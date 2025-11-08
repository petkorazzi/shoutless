package io.petkorazzi.shoutless

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.petkorazzi.shoutless.R
import io.petkorazzi.shoutless.ui.theme.ShoutlessDisplayTheme
import io.petkorazzi.shoutless.util.HideSystemBars
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
                    onDoubleTap = { finish() },
                    onTripleTap = {
                        val intent = Intent(this, ClapbackActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onHomeClick = {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    },
                    onQuickPhrasesClick = {
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
private fun rememberFitTextSize(
    text: String,
    textStyle: TextStyle,
    minFontSize: Float = 10f,
    maxInitialFontSize: Float,
    maxWidth: Float,
    maxHeight: Float
): Float {
    val textMeasurer = rememberTextMeasurer()

    return remember(text, textStyle, minFontSize, maxInitialFontSize, maxWidth, maxHeight) {
        if (text.isBlank()) {
            return@remember maxInitialFontSize
        }
        val words = text.split(Regex("\\s+"))
        fun isOverflowing(fontSize: Float): Boolean {
            val style = textStyle.copy(fontSize = fontSize.sp)
            for (word in words) {
                val wordWidth = textMeasurer.measure(
                    text = word,
                    style = style,
                    softWrap = false
                ).size.width
                if (wordWidth > maxWidth) {
                    return true
                }
            }
            val layoutResult = textMeasurer.measure(
                text = text,
                style = style,
                constraints = Constraints(maxWidth = maxWidth.toInt())
            )
            return layoutResult.size.height > maxHeight
        }
        var low = minFontSize
        var high = maxInitialFontSize
        var bestSize = low
        if (isOverflowing(low)) {
            return@remember low
        }
        while (low <= high) {
            val mid = (low + high) / 2
            if (isOverflowing(mid)) {
                high = mid - 0.1f
            } else {
                bestSize = mid
                low = mid + 0.1f
            }
        }
        bestSize
    }
}


@Composable
fun DisplayScreen(
    text: String,
    mode: String,
    defaultFontSize: Int,
    maxFontSize: Int,
    onDoubleTap: () -> Unit,
    onTripleTap: () -> Unit,
    onHomeClick: () -> Unit,
    onQuickPhrasesClick: () -> Unit,
) {
    HideSystemBars()
    val configuration = LocalConfiguration.current
    val padding = 16.dp

    val textStyle = LocalTextStyle.current.copy(
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Default,
        lineHeight = 1.15.em,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        ),
        lineBreak = LineBreak.Simple,
        hyphens = Hyphens.None
    )

    val scope = rememberCoroutineScope()
    var tapCount by remember { mutableStateOf(0) }
    var tapJob: Job? by remember { mutableStateOf(null) }

    Scaffold { scaffoldPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding) // Apply padding from Scaffold
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
                .padding(padding), // Apply our own content padding
            contentAlignment = Alignment.Center
        ) {
            // This is the key: we now use the constraints provided by BoxWithConstraints.
            // These are the REAL available pixels after all paddings are applied.
            val maxWidthPx = this.constraints.maxWidth.toFloat()
            val maxHeightPx = this.constraints.maxHeight.toFloat()

            // All the logic that depends on size is now INSIDE the box that knows its own size.
            val maxFitFontSizeValue = rememberFitTextSize(
                text = text,
                textStyle = textStyle,
                maxInitialFontSize = 300f,
                maxWidth = maxWidthPx,
                maxHeight = maxHeightPx
            )

            val initialLowkeyFontSize = remember(maxFitFontSizeValue, defaultFontSize, maxFontSize) {
                val userLimitedMax = min(maxFitFontSizeValue, maxFontSize.toFloat())
                defaultFontSize.toFloat().coerceAtMost(userLimitedMax)
            }

            var lowkeyDynamicFontSize by remember { mutableStateOf(initialLowkeyFontSize.sp) }

            LaunchedEffect(initialLowkeyFontSize) {
                lowkeyDynamicFontSize = initialLowkeyFontSize.sp
            }

            val finalFontSize = if (mode == "Blast") {
                maxFitFontSizeValue.sp
            } else {
                lowkeyDynamicFontSize
            }

            val gestureModifier = if (mode == "Lowkey") {
                Modifier.pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        val newSizeValue = lowkeyDynamicFontSize.value * zoom
                        val maxAllowedZoomSize = min(maxFitFontSizeValue, maxFontSize.toFloat())
                        lowkeyDynamicFontSize = newSizeValue.coerceIn(10f, maxAllowedZoomSize).sp
                    }
                }
            } else {
                Modifier
            }

            // The Text and Icons are now placed inside a separate Box that has the gesture modifier
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(gestureModifier),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = textStyle.copy(fontSize = finalFontSize)
                )

                val orientation = configuration.orientation
                val iconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                val (homeIconAlignment, quickPhrasesAlignment) = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Pair(Alignment.TopStart, Alignment.BottomStart)
                } else {
                    Pair(Alignment.BottomStart, Alignment.BottomEnd)
                }
                IconButton(
                    onClick = onHomeClick,
                    modifier = Modifier.align(homeIconAlignment)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Home,
                        contentDescription = "Go Home",
                        tint = iconColor
                    )
                }
                IconButton(
                    onClick = onQuickPhrasesClick,
                    modifier = Modifier.align(quickPhrasesAlignment)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.quick_phrases_24px),
                        contentDescription = "Quick Phrases",
                        tint = iconColor
                    )
                }
            }
        }
    }
}