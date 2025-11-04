package com.example.shoutless

import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoutless.ui.theme.ShoutlessTheme
import com.example.shoutless.util.HideSystemBars
import kotlin.math.roundToInt

class SettingsActivity : ComponentActivity() {
    companion object {
        const val PREFS_NAME = "shoutless_prefs"
        const val KEY_DEFAULT_FONT_SIZE = "lowkey_default_font_size"
        const val KEY_MAX_FONT_SIZE = "lowkey_max_font_size"
        const val KEY_FORCE_BRIGHTNESS = "blast_force_brightness"
        const val KEY_FORCE_LANDSCAPE = "blast_force_landscape"
        const val KEY_CLAPBACK1_LABEL = "clapback1_label"
        const val KEY_CLAPBACK1_HIDDEN = "clapback1_hidden"
        const val KEY_CLAPBACK2_LABEL = "clapback2_label"
        const val KEY_CLAPBACK2_HIDDEN = "clapback2_hidden"
        const val KEY_CLAPBACK3_LABEL = "clapback3_label"
        const val KEY_CLAPBACK3_HIDDEN = "clapback3_hidden"
        const val KEY_CLAPBACK4_LABEL = "clapback4_label"
        const val KEY_CLAPBACK4_HIDDEN = "clapback4_hidden"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            ShoutlessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsRoute()
                }
            }
        }
    }
}

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            application = LocalContext.current.applicationContext as Application,
            prefs = LocalContext.current.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE)
        )
    )
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val tagline by settingsViewModel.randomTagline.collectAsState()

    HideSystemBars()

    SettingsScreen(
        uiState = uiState,
        tagline = tagline,
        onDefaultFontSizeChange = settingsViewModel::updateDefaultFontSize,
        onMaxFontSizeChange = settingsViewModel::updateMaxFontSize,
        onForceBrightnessChange = settingsViewModel::updateForceBrightness,
        onForceLandscapeChange = settingsViewModel::updateForceLandscape,
        onClapback1LabelChange = settingsViewModel::updateClapback1Label,
        onClapback1HiddenChange = settingsViewModel::updateClapback1Hidden,
        onClapback2LabelChange = settingsViewModel::updateClapback2Label,
        onClapback2HiddenChange = settingsViewModel::updateClapback2Hidden,
        onClapback3LabelChange = settingsViewModel::updateClapback3Label,
        onClapback3HiddenChange = settingsViewModel::updateClapback3Hidden,
        onClapback4LabelChange = settingsViewModel::updateClapback4Label,
        onClapback4HiddenChange = settingsViewModel::updateClapback4Hidden,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClapbackSettings(
    title: String,
    labelValue: String,
    onLabelChange: (String) -> Unit,
    hiddenValue: String,
    onHiddenChange: (String) -> Unit,
    colors: TextFieldColors
) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
    OutlinedTextField(value = labelValue, onValueChange = onLabelChange, label = { Text("label") }, modifier = Modifier.fillMaxWidth(), colors = colors)
    OutlinedTextField(value = hiddenValue, onValueChange = onHiddenChange, label = { Text("msg txt") }, modifier = Modifier.fillMaxWidth(), colors = colors)
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    tagline: String,
    onDefaultFontSizeChange: (Float) -> Unit,
    onMaxFontSizeChange: (Float) -> Unit,
    onForceBrightnessChange: (Boolean) -> Unit,
    onForceLandscapeChange: (Boolean) -> Unit,
    onClapback1LabelChange: (String) -> Unit,
    onClapback1HiddenChange: (String) -> Unit,
    onClapback2LabelChange: (String) -> Unit,
    onClapback2HiddenChange: (String) -> Unit,
    onClapback3LabelChange: (String) -> Unit,
    onClapback3HiddenChange: (String) -> Unit,
    onClapback4LabelChange: (String) -> Unit,
    onClapback4HiddenChange: (String) -> Unit,
) {
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val clapbackTextFieldColors = TextFieldDefaults.colors(
        focusedLabelColor = tertiaryColor,
        cursorColor = tertiaryColor,
        selectionColors = TextSelectionColors(handleColor = tertiaryColor, backgroundColor = tertiaryColor.copy(alpha = 0.4f)),
        unfocusedIndicatorColor = tertiaryColor,
        focusedIndicatorColor = tertiaryColor,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.tertiary,
                                shadow = Shadow(color = MaterialTheme.colorScheme.tertiary, blurRadius = 20f)
                            )
                        ) {
                            append("settings")
                        }
                    },
                    style = MaterialTheme.typography.displayLarge,
                )

                Text(
                    text = tagline,
                    modifier = Modifier.offset(y = (-8).dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        shadow = Shadow(color = MaterialTheme.colorScheme.onBackground, blurRadius = 10f)
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("lowkey", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("default volume: ${uiState.defaultFontSize.roundToInt()}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("10", style = MaterialTheme.typography.labelSmall)
                        Slider(
                            value = uiState.defaultFontSize,
                            onValueChange = onDefaultFontSizeChange,
                            valueRange = 10f..50f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("50", style = MaterialTheme.typography.labelSmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("max volume: ${uiState.maxFontSize.roundToInt()}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(uiState.defaultFontSize.roundToInt().toString(), style = MaterialTheme.typography.labelSmall)
                        Slider(
                            value = uiState.maxFontSize,
                            onValueChange = onMaxFontSizeChange,
                            valueRange = uiState.defaultFontSize..150f,
                            modifier = Modifier.weight(1f)
                        )
                        Text("150", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("BLAST", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("force max brightness", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                        Switch(
                            checked = uiState.forceBrightness,
                            onCheckedChange = onForceBrightnessChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("force landscape", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                        Switch(
                            checked = uiState.forceLandscape,
                            onCheckedChange = onForceLandscapeChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("clapback", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(16.dp))

                    ClapbackSettings(
                        title = "spark 1",
                        labelValue = uiState.clapback1Label,
                        onLabelChange = onClapback1LabelChange,
                        hiddenValue = uiState.clapback1Hidden,
                        onHiddenChange = onClapback1HiddenChange,
                        colors = clapbackTextFieldColors
                    )
                    ClapbackSettings(
                        title = "spark 2",
                        labelValue = uiState.clapback2Label,
                        onLabelChange = onClapback2LabelChange,
                        hiddenValue = uiState.clapback2Hidden,
                        onHiddenChange = onClapback2HiddenChange,
                        colors = clapbackTextFieldColors
                    )
                    ClapbackSettings(
                        title = "spark 3",
                        labelValue = uiState.clapback3Label,
                        onLabelChange = onClapback3LabelChange,
                        hiddenValue = uiState.clapback3Hidden,
                        onHiddenChange = onClapback3HiddenChange,
                        colors = clapbackTextFieldColors
                    )
                    ClapbackSettings(
                        title = "spark 4",
                        labelValue = uiState.clapback4Label,
                        onLabelChange = onClapback4LabelChange,
                        hiddenValue = uiState.clapback4Hidden,
                        onHiddenChange = onClapback4HiddenChange,
                        colors = clapbackTextFieldColors
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ShoutlessTheme {
        SettingsScreen(
            uiState = SettingsUiState(),
            tagline = "do it different",
            onDefaultFontSizeChange = {},
            onMaxFontSizeChange = {},
            onForceBrightnessChange = {},
            onForceLandscapeChange = {},
            onClapback1LabelChange = {},
            onClapback1HiddenChange = {},
            onClapback2LabelChange = {},
            onClapback2HiddenChange = {},
            onClapback3LabelChange = {},
            onClapback3HiddenChange = {},
            onClapback4LabelChange = {},
            onClapback4HiddenChange = {}
        )
    }
}
