package com.example.shoutless

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoutless.ui.theme.ShoutlessDisplayTheme
import com.example.shoutless.util.HideSystemBars
import kotlin.math.min
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DisplayActivity : ComponentActivity() {

    private val text by lazy { intent.getStringExtra(EXTRA_TEXT) ?: "" }
    private val mode by lazy { intent.getStringExtra(EXTRA_MODE) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("shoutless_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putString("last_display_mode", mode).apply()
        val defaultFontSize = sharedPreferences.getInt("lowkey_default_font_size", 30)
        val maxFontSize = sharedPreferences.getInt("lowkey_max_font_size", 150)
        val forceBrightness = sharedPreferences.getBoolean("blast_force_brightness", false)
        val forceLandscape = sharedPreferences.getBoolean("blast_force_landscape", false)

        if (mode == "Blast" && forceLandscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        if (mode == "Blast" && forceBrightness) {
            window.attributes = window.attributes.apply {
                screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            }
        }

        setContent {
            ShoutlessDisplayTheme {
                DisplayScreen(
                    text = text,
                    mode = mode,
                    defaultFontSize = defaultFontSize,
                    maxFontSize = maxFontSize,
                    onDoubleTap = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onTripleTap = {
                        val intent = Intent(this, ClapbackActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_MODE = "EXTRA_MODE"

        fun newIntent(context: Context, text: String, mode: String): Intent {
            return Intent(context, DisplayActivity::class.java).apply {
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_MODE, mode)
            }
        }
    }
}

@Composable
fun DisplayScreen(
    text: String,
    mode: String,
    defaultFontSize: Int,
    maxFontSize: Int,
    onDoubleTap: () -> Unit,
    onTripleTap: () -> Unit
) {
    HideSystemBars()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val padding = 16.dp

    val screenWidthPx = with(density) { (configuration.screenWidthDp.dp - (padding * 2)).toPx() }
    val screenHeightPx = with(density) { (configuration.screenHeightDp.dp - (padding * 2)).toPx() }

    val textMeasurer = rememberTextMeasurer()

    val maxFitFontSize = remember(text, screenWidthPx, screenHeightPx) {
        if (text.isBlank()) {
            maxFontSize.sp
        } else {
            var size = with(density) { screenHeightPx.toSp() }
            while (size.value > 1f) {
                val style = TextStyle(fontSize = size, textAlign = TextAlign.Center, lineHeight = size * 1.2f)
                if (!isTextOverflowing(text, style, screenWidthPx, screenHeightPx, textMeasurer)) {
                    break
                }
                size = (size.value * 0.95f).sp
            }
            size
        }
    }

    val initialFontSize = remember(text, screenWidthPx, screenHeightPx, mode, defaultFontSize) {
        if (text.isBlank()) {
            30.sp
        } else if (mode == "Blast") {
            maxFitFontSize
        } else { // Lowkey mode
            var size = defaultFontSize.sp
            while (size.value > 1f) {
                val style = TextStyle(fontSize = size, textAlign = TextAlign.Center, lineHeight = size * 1.2f)
                if (!isTextOverflowing(text, style, screenWidthPx, screenHeightPx, textMeasurer)) {
                    break
                }
                size = (size.value * 0.95f).sp
            }
            size
        }
    }

    var dynamicFontSize by remember { mutableStateOf(initialFontSize) }

    LaunchedEffect(initialFontSize) {
        dynamicFontSize = initialFontSize
    }

    val gestureModifier = if (mode == "Lowkey") {
        Modifier.pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                val newSizeValue = dynamicFontSize.value * zoom
                val maxAllowedSize = min(maxFontSize.toFloat(), maxFitFontSize.value)
                dynamicFontSize = newSizeValue.coerceIn(10f, maxAllowedSize).sp
            }
        }
    } else {
        Modifier
    }

    val scope = rememberCoroutineScope()
    var tapCount by remember { mutableStateOf(0) }
    var tapJob: Job? by remember { mutableStateOf(null) }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            tapJob?.cancel()
                            tapCount++
                            tapJob = scope.launch {
                                delay(300L)
                                when (tapCount) {
                                    2 -> onDoubleTap()
                                    3 -> onTripleTap()
                                }
                                tapCount = 0
                            }
                        }
                    )
                }
                .then(gestureModifier)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                style = LocalTextStyle.current.copy(
                    fontSize = dynamicFontSize,
                    textAlign = TextAlign.Center,
                    lineHeight = dynamicFontSize * 1.2f
                ),
                modifier = Modifier.graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
            )
        }
    }
}

fun isTextOverflowing(
    text: String,
    style: TextStyle,
    screenWidthPx: Float,
    screenHeightPx: Float,
    textMeasurer: TextMeasurer
): Boolean {
    val words = text.split(Regex("\\s+"))
    val anyWordIsTooWide = words.any { word -> textMeasurer.measure(word, style).size.width > screenWidthPx }
    val measuredHeight = textMeasurer.measure(text, style, constraints = Constraints(maxWidth = screenWidthPx.toInt())).size.height
    val textIsTooTall = measuredHeight > screenHeightPx
    return anyWordIsTooWide || textIsTooTall
}
