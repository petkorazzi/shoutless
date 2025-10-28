package com.example.shoutless

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    SettingsScreen(
        defaultFontSize = defaultFontSize,
        maxFontSize = maxFontSize,
        forceBrightness = forceBrightness,
        forceLandscape = forceLandscape,
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
    onDefaultFontSizeChange: (Float) -> Unit,
    onMaxFontSizeChange: (Float) -> Unit,
    onForceBrightnessChange: (Boolean) -> Unit,
    onForceLandscapeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lowkey Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lowkey Settings", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Default Font Size Slider
                    Text("Default Volume: ${defaultFontSize.toInt()}", style = MaterialTheme.typography.bodyLarge)
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
                            steps = 7, // (50-10) / 5 = 8 sections, so 7 steps
                            modifier = Modifier.weight(1f)
                        )
                        Text("50", style = MaterialTheme.typography.labelSmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Max Font Size Slider
                    Text("Max Volume: ${maxFontSize.toInt()}", style = MaterialTheme.typography.bodyLarge)
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

            // Blast Settings
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Blast Settings", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Force Max Brightness Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Force Max Brightness", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = forceBrightness,
                            onCheckedChange = onForceBrightnessChange
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Force Landscape Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Force Landscape", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = forceLandscape,
                            onCheckedChange = onForceLandscapeChange
                        )
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
            onDefaultFontSizeChange = {},
            onMaxFontSizeChange = {},
            onForceBrightnessChange = {},
            onForceLandscapeChange = {}
        )
    }
}
