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
import androidx.compose.foundation.layout.width // for scaling fix
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
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
                    onDoubleTap = { finish() },
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
private fun rememberFitTextSize(
    text: String,
    textStyle: TextStyle,
    minFontSize: Float = 10f,
    maxInitialFontSize: Float, // Max starting point for the search
    maxWidth: Float,
    maxHeight: Float
): Float {
    val textMeasurer = rememberTextMeasurer()

    // Remember the result, keyed by all inputs that affect the calculation
    return remember(text, textStyle, minFontSize, maxInitialFontSize, maxWidth, maxHeight) {
        if (text.isBlank()) {
            return@remember maxInitialFontSize
        }

        // --- THE CORRECTED LOGIC IS HERE ---
        // We define a local function for clarity and to allow for simple 'return' statements.
        // This is the cleanest way to avoid lambda return ambiguity.
        fun isOverflowing(fontSize: Float): Boolean {
            val style = textStyle.copy(fontSize = fontSize.sp)

            // First, check if any single word is wider than the container.
            val anyWordIsTooWide = text.split(Regex("\\s+")).any { word ->
                if (word.isEmpty()) false
                else textMeasurer.measure(text = word, style = style).size.width > maxWidth
            }

            if (anyWordIsTooWide) {
                return true // A word is too long, so the font size is invalid.
            }

            // If words fit, check the whole block.
            val layoutResult = textMeasurer.measure(
                text = text,
                style = style,
                constraints = Constraints(maxWidth = maxWidth.toInt(), maxHeight = maxHeight.toInt())
            )

            // Return the final overflow status
            return layoutResult.hasVisualOverflow || layoutResult.size.height > maxHeight
        }
        // --- END OF CORRECTED LOGIC ---


        // Binary search for the optimal font size (This part remains the same)
        var low = minFontSize
        var high = maxInitialFontSize
        var bestSize = low

        // Check if the smallest possible font size already overflows. If so, use it.
        if (isOverflowing(low)) {
            return@remember low
        }

        while (low <= high) {
            val mid = (low + high) / 2
            if (isOverflowing(mid)) {
                high = mid - 0.1f // Font is too big, search in the lower half
            } else {
                bestSize = mid   // Font fits, it's a candidate. Try for a bigger size.
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
    maxFontSize: Int, // This is the user-defined max from settings for Lowkey mode
    onDoubleTap: () -> Unit,
    onTripleTap: () -> Unit
) {
    HideSystemBars()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val padding = 16.dp

    val screenWidthPx = with(density) { (configuration.screenWidthDp.dp - (padding * 2)).toPx() }
    val screenHeightPx = with(density) { (configuration.screenHeightDp.dp - (padding * 2)).toPx() }

    val textStyle = LocalTextStyle.current.copy(
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Default,
        lineHeight = 1.15.em,
        platformStyle = androidx.compose.ui.text.PlatformTextStyle(
            includeFontPadding = false
        ),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        ),
        lineBreak = LineBreak.Simple,
        hyphens = Hyphens.None
    )

    // 1. Calculate the ABSOLUTE maximum font size that can fit the screen.
    val maxFitFontSizeValue = rememberFitTextSize(
        text = text,
        textStyle = textStyle,
        maxInitialFontSize = 300f,
        maxWidth = screenWidthPx,
        maxHeight = screenHeightPx
    )

    // 2. Determine the initial font size based on the mode.
    val initialFontSizeValue = remember(mode, text, maxFitFontSizeValue, defaultFontSize, maxFontSize) {
        if (mode == "Blast") {
            maxFitFontSizeValue
        } else { // Lowkey mode
            val userLimitedMax = min(maxFitFontSizeValue, maxFontSize.toFloat())
            defaultFontSize.toFloat().coerceAtMost(userLimitedMax)
        }
    }

    var dynamicFontSize by remember { mutableStateOf(initialFontSizeValue.sp) }

    LaunchedEffect(initialFontSizeValue) {
        dynamicFontSize = initialFontSizeValue.sp
    }

    // 3. Determine the zoom gesture's ceiling by respecting BOTH the screen limit AND the user setting.
    val maxAllowedZoomSize = min(maxFitFontSizeValue, maxFontSize.toFloat())

    val gestureModifier = if (mode == "Lowkey") {
        Modifier.pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                val newSizeValue = dynamicFontSize.value * zoom
                // Use the new, correctly calculated ceiling for the zoom.
                dynamicFontSize = newSizeValue.coerceIn(10f, maxAllowedZoomSize).sp
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
                style = textStyle.copy(fontSize = dynamicFontSize)
            )
        }
    }
}