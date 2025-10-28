package com.example.shoutless

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoutless.ui.theme.ShoutlessTheme
import com.example.shoutless.util.HideSystemBars
import com.example.shoutless.util.glow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            ShoutlessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    HideSystemBars()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            // Top section with header, tagline, and icons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header and tagline
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shadow = Shadow(color = MaterialTheme.colorScheme.secondary, blurRadius = 20f)
                                )
                            ) {
                                append("shout")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    shadow = Shadow(color = MaterialTheme.colorScheme.primary, blurRadius = 20f)
                                )
                            ) {
                                append("less")
                            }
                        },
                        style = MaterialTheme.typography.displayLarge
                    )

                    val taglines = context.resources.getStringArray(R.array.taglines)
                    val tagline = remember { taglines.random() }

                    Text(
                        text = tagline,
                        modifier = Modifier.offset(y = (-8).dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            shadow = Shadow(color = MaterialTheme.colorScheme.onBackground, blurRadius = 10f)
                        )
                    )
                }

                // Icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModeButton(
                        iconId = R.drawable.ic_lowkey,
                        text = "lowkey",
                        fontWeight = FontWeight.Light,
                        contentDescription = "Lowkey Mode",
                        glowColor = MaterialTheme.colorScheme.primary,
                        textBlurRadius = 25f,
                        onClick = {
                            if (text.isBlank()) {
                                val messages = context.resources.getStringArray(R.array.toast_messages)
                                scope.launch {
                                    snackbarHostState.showSnackbar(messages.random())
                                }
                            } else {
                                val intent = DisplayActivity.newIntent(context, text, "Lowkey")
                                context.startActivity(intent)
                            }
                        }
                    )
                    ModeButton(
                        iconId = R.drawable.ic_blast,
                        text = "BLAST",
                        fontWeight = FontWeight.Bold,
                        contentDescription = "Blast Mode",
                        glowColor = MaterialTheme.colorScheme.secondary,
                        textBlurRadius = 10f,
                        onClick = {
                            if (text.isBlank()) {
                                val messages = context.resources.getStringArray(R.array.toast_messages)
                                scope.launch {
                                    snackbarHostState.showSnackbar(messages.random())
                                }
                            } else {
                                val intent = DisplayActivity.newIntent(context, text, "Blast")
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }

            // Clapback Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .glow(
                            color = MaterialTheme.colorScheme.tertiary,
                            radius = 15.dp,
                            shape = RoundedCornerShape(24.dp),
                            alpha = 0.5f
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            val intent = Intent(context, ClapbackActivity::class.java)
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = "Clapback Mode",
                            modifier = Modifier.size(57.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "clapback",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.offset(y = (-7).dp)
                        )
                    }
                }
            }

            // Bottom section with text field
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 0.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .glow(
                            color = MaterialTheme.colorScheme.secondary,
                            radius = 10.dp,
                            shape = RoundedCornerShape(24.dp),
                            alpha = 0.5f
                        ),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text("Enter text") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                if (text.isNotEmpty()) {
                    IconButton(
                        onClick = { text = "" },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Rounded.Clear, contentDescription = "Clear text")
                    }
                }
            }
        }
    }
}

@Composable
fun ModeButton(
    @DrawableRes iconId: Int,
    text: String,
    fontWeight: FontWeight,
    contentDescription: String,
    glowColor: Color,
    textBlurRadius: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .glow(
                color = glowColor,
                radius = 15.dp,
                shape = RoundedCornerShape(32.dp),
                alpha = 0.5f
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(32.dp)
            )
            .border(
                width = 2.dp,
                color = glowColor,
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription,
                modifier = Modifier.size(100.dp),
                tint = glowColor
            )
            Text(
                text = text,
                color = glowColor,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = fontWeight,
                    shadow = Shadow(color = glowColor, blurRadius = textBlurRadius)
                ),
                modifier = Modifier.offset(y = (-12).dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeScreenPreview() {
    ShoutlessTheme {
        HomeScreen()
    }
}
