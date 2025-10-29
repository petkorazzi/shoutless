package com.example.shoutless

import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoutless.ui.theme.ShoutlessTheme
import com.example.shoutless.util.HideSystemBars

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
fun SettingsRoute() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("shoutless_prefs", Context.MODE_PRIVATE) }

    var defaultFontSize by remember { mutableStateOf(sharedPreferences.getInt("lowkey_default_font_size", 30).toFloat()) }
    var maxFontSize by remember { mutableStateOf(sharedPreferences.getInt("lowkey_max_font_size", 150).toFloat()) }
    var forceBrightness by remember { mutableStateOf(sharedPreferences.getBoolean("blast_force_brightness", false)) }
    var forceLandscape by remember { mutableStateOf(sharedPreferences.getBoolean("blast_force_landscape", false)) }

    var clapback1Label by remember { mutableStateOf(sharedPreferences.getString("clapback1_label", "yeah") ?: "yeah") }
    var clapback1Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback1_hidden", "yeah") ?: "yeah") }
    var clapback2Label by remember { mutableStateOf(sharedPreferences.getString("clapback2_label", "nah") ?: "nah") }
    var clapback2Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback2_hidden", "nah") ?: "nah") }
    var clapback3Label by remember { mutableStateOf(sharedPreferences.getString("clapback3_label", "ty") ?: "ty") }
    var clapback3Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback3_hidden", "thank you") ?: "thank you") }
    var clapback4Label by remember { mutableStateOf(sharedPreferences.getString("clapback4_label", "brb") ?: "brb") }
    var clapback4Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback4_hidden", "be right back") ?: "be right back") }

    HideSystemBars()

    SettingsScreen(
        defaultFontSize = defaultFontSize,
        maxFontSize = maxFontSize,
        forceBrightness = forceBrightness,
        forceLandscape = forceLandscape,
        clapback1Label = clapback1Label,
        clapback1Hidden = clapback1Hidden,
        clapback2Label = clapback2Label,
        clapback2Hidden = clapback2Hidden,
        clapback3Label = clapback3Label,
        clapback3Hidden = clapback3Hidden,
        clapback4Label = clapback4Label,
        clapback4Hidden = clapback4Hidden,
        onDefaultFontSizeChange = {
            defaultFontSize = it
            sharedPreferences.edit().putInt("lowkey_default_font_size", it.toInt()).apply()
            if (it > maxFontSize) {
                maxFontSize = it
                sharedPreferences.edit().putInt("lowkey_max_font_size", it.toInt()).apply()
            }
        },
        onMaxFontSizeChange = {
            maxFontSize = it
            sharedPreferences.edit().putInt("lowkey_max_font_size", it.toInt()).apply()
        },
        onForceBrightnessChange = {
            forceBrightness = it
            sharedPreferences.edit().putBoolean("blast_force_brightness", it).apply()
        },
        onForceLandscapeChange = {
            forceLandscape = it
            sharedPreferences.edit().putBoolean("blast_force_landscape", it).apply()
        },
        onClapback1LabelChange = {
            clapback1Label = it
            sharedPreferences.edit().putString("clapback1_label", it).apply()
        },
        onClapback1HiddenChange = {
            clapback1Hidden = it
            sharedPreferences.edit().putString("clapback1_hidden", it).apply()
        },
        onClapback2LabelChange = {
            clapback2Label = it
            sharedPreferences.edit().putString("clapback2_label", it).apply()
        },
        onClapback2HiddenChange = {
            clapback2Hidden = it
            sharedPreferences.edit().putString("clapback2_hidden", it).apply()
        },
        onClapback3LabelChange = {
            clapback3Label = it
            sharedPreferences.edit().putString("clapback3_label", it).apply()
        },
        onClapback3HiddenChange = {
            clapback3Hidden = it
            sharedPreferences.edit().putString("clapback3_hidden", it).apply()
        },
        onClapback4LabelChange = {
            clapback4Label = it
            sharedPreferences.edit().putString("clapback4_label", it).apply()
        },
        onClapback4HiddenChange = {
            clapback4Hidden = it
            sharedPreferences.edit().putString("clapback4_hidden", it).apply()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    defaultFontSize: Float,
    maxFontSize: Float,
    forceBrightness: Boolean,
    forceLandscape: Boolean,
    clapback1Label: String,
    clapback1Hidden: String,
    clapback2Label: String,
    clapback2Hidden: String,
    clapback3Label: String,
    clapback3Hidden: String,
    clapback4Label: String,
    clapback4Hidden: String,
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

    Scaffold {
        paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Lowkey Settings
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

                        // Default Font Size Slider
                        Text("default volume: ${defaultFontSize.toInt()}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("10", style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = defaultFontSize,
                                onValueChange = onDefaultFontSizeChange,
                                valueRange = 10f..50f,
                                modifier = Modifier.weight(1f)
                            )
                            Text("50", style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Max Font Size Slider
                        Text("max volume: ${maxFontSize.toInt()}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(defaultFontSize.toInt().toString(), style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = maxFontSize,
                                onValueChange = onMaxFontSizeChange,
                                valueRange = defaultFontSize..150f,
                                modifier = Modifier.weight(1f)
                            )
                            Text("150", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            item {
                // Blast Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("BLAST", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Force Max Brightness Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("force max brightness", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                            Switch(
                                checked = forceBrightness,
                                onCheckedChange = onForceBrightnessChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                    checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Force Landscape Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("force landscape", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                            Switch(
                                checked = forceLandscape,
                                onCheckedChange = onForceLandscapeChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                    checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }
            item {
                // Clapback Button Settings
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

                        // Button 1
                        Text("spark 1", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                        OutlinedTextField(value = clapback1Label, onValueChange = onClapback1LabelChange, label = { Text("label") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                        OutlinedTextField(value = clapback1Hidden, onValueChange = onClapback1HiddenChange, label = { Text("msg txt") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Button 2
                        Text("spark 2", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                        OutlinedTextField(value = clapback2Label, onValueChange = onClapback2LabelChange, label = { Text("label") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                        OutlinedTextField(value = clapback2Hidden, onValueChange = onClapback2HiddenChange, label = { Text("msg txt") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Button 3
                        Text("spark 3", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                        OutlinedTextField(value = clapback3Label, onValueChange = onClapback3LabelChange, label = { Text("label") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                        OutlinedTextField(value = clapback3Hidden, onValueChange = onClapback3HiddenChange, label = { Text("msg txt") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Button 4
                        Text("spark 4", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                        OutlinedTextField(value = clapback4Label, onValueChange = onClapback4LabelChange, label = { Text("label") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                        OutlinedTextField(value = clapback4Hidden, onValueChange = onClapback4HiddenChange, label = { Text("msg txt") }, modifier = Modifier.fillMaxWidth(), colors = clapbackTextFieldColors)
                    }
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
            defaultFontSize = 30f,
            maxFontSize = 120f,
            forceBrightness = true,
            forceLandscape = false,
            clapback1Label = "yeah",
            clapback1Hidden = "yeah",
            clapback2Label = "nah",
            clapback2Hidden = "nah",
            clapback3Label = "ty",
            clapback3Hidden = "thank you",
            clapback4Label = "brb",
            clapback4Hidden = "be right back",
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
