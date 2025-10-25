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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoutless.ui.theme.ShoutlessTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoutlessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("shoutless_prefs", Context.MODE_PRIVATE) }
    var defaultFontSize by remember { mutableStateOf(sharedPreferences.getInt("lowkey_default_font_size", 30).toFloat()) }
    var maxFontSize by remember { mutableStateOf(sharedPreferences.getInt("lowkey_max_font_size", 150).toFloat()) }
    var forceBrightness by remember { mutableStateOf(sharedPreferences.getBoolean("blast_force_brightness", false)) }
    var forceLandscape by remember { mutableStateOf(sharedPreferences.getBoolean("blast_force_landscape", false)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Default Font Size Slider
        Text("lowkey default volume: ${defaultFontSize.toInt()}", color = Color.White, fontSize = 20.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text("10", color = Color.White)
            Slider(
                value = defaultFontSize,
                onValueChange = {
                    defaultFontSize = it
                    sharedPreferences.edit().putInt("lowkey_default_font_size", it.toInt()).apply()
                    if (it > maxFontSize) {
                        maxFontSize = it
                        sharedPreferences.edit().putInt("lowkey_max_font_size", it.toInt()).apply()
                    }
                },
                valueRange = 10f..50f,
                steps = 7, // (50-10) / 5 = 8 sections, so 7 steps
                modifier = Modifier.weight(1f)
            )
            Text("50", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Max Font Size Slider
        Text("lowkey max volume: ${maxFontSize.toInt()}", color = Color.White, fontSize = 20.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(defaultFontSize.toInt().toString(), color = Color.White)
            Slider(
                value = maxFontSize,
                onValueChange = {
                    maxFontSize = it
                    sharedPreferences.edit().putInt("lowkey_max_font_size", it.toInt()).apply()
                },
                valueRange = defaultFontSize..150f,
                modifier = Modifier.weight(1f)
            )
            Text("150", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Force Max Brightness Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Force Max Brightness (Blast)", color = Color.White, fontSize = 20.sp)
            Switch(
                checked = forceBrightness,
                onCheckedChange = {
                    forceBrightness = it
                    sharedPreferences.edit().putBoolean("blast_force_brightness", it).apply()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Force Landscape Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Force Landscape (Blast)", color = Color.White, fontSize = 20.sp)
            Switch(
                checked = forceLandscape,
                onCheckedChange = {
                    forceLandscape = it
                    sharedPreferences.edit().putBoolean("blast_force_landscape", it).apply()
                }
            )
        }
    }
}
