package com.example.shoutless

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.shoutless.ui.theme.ShoutlessTheme
import kotlin.math.min

class DisplayActivity : ComponentActivity() {

    private val text by lazy { intent.getStringExtra(EXTRA_TEXT) ?: "" }
    private val mode by lazy { intent.getStringExtra(EXTRA_MODE) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPreferences = getSharedPreferences("shoutless_prefs", MODE_PRIVATE)
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
            ShoutlessTheme {
                HideSystemBars()
                DisplayScreen(text = text, mode = mode, defaultFontSize = defaultFontSize, maxFontSize = maxFontSize) {
                    finish()
                }
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
private fun HideSystemBars() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            onDispose {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
}

@Composable
fun DisplayScreen(text: String, mode: String, defaultFontSize: Int, maxFontSize: Int, onDoubleTap: () -> Unit) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val padding = 16.dp

    val screenWidthPx = with(density) { (configuration.screenWidthDp.dp - (padding * 2)).toPx() }
    val screenHeightPx = with(density) { (configuration.screenHeightDp.dp - (padding * 2)).toPx() }

    val textMeasurer = rememberTextMeasurer()
    val textStyleFromTheme = LocalTextStyle.current

    val maxFitFontSize = remember(text, screenWidthPx, screenHeightPx, textStyleFromTheme) {
        if (text.isBlank()) {
            maxFontSize.sp
        } else {
            var size = with(density) { screenHeightPx.toSp() }
            while (size.value > 1f) {
                val style = textStyleFromTheme.copy(fontSize = size, textAlign = TextAlign.Center, lineHeight = size * 1.2f)
                if (!isTextOverflowing(text, style, screenWidthPx, screenHeightPx, textMeasurer)) {
                    break
                }
                size = (size.value * 0.95f).sp
            }
            size
        }
    }

    val initialFontSize = remember(text, screenWidthPx, screenHeightPx, mode, textStyleFromTheme, defaultFontSize) {
        if (text.isBlank()) {
            30.sp
        } else if (mode == "Blast") {
            maxFitFontSize
        } else { // Lowkey mode
            var size = defaultFontSize.sp
            while (size.value > 1f) {
                val style = textStyleFromTheme.copy(fontSize = size, textAlign = TextAlign.Center, lineHeight = size * 1.2f)
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

    val finalTextStyle = textStyleFromTheme.copy(
        fontSize = dynamicFontSize,
        textAlign = TextAlign.Center,
        lineHeight = dynamicFontSize * 1.2f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) { detectTapGestures(onDoubleTap = { onDoubleTap() }) }
            .then(gestureModifier)
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = finalTextStyle,
            modifier = Modifier.graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
        )
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
