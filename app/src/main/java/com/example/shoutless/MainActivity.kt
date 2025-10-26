package com.example.shoutless

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.shoutless.ui.theme.Poppins
import com.example.shoutless.ui.theme.ShoutlessTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoutlessTheme {
                HideSystemBars()
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

@Composable
private fun HideSystemBars() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            onDispose {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
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
                modifier = Modifier.weight(0.55f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header and tagline
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 48.sp,
                                    shadow = Shadow(color = MaterialTheme.colorScheme.secondary, blurRadius = 20f)
                                )
                            ) {
                                append("shout")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 48.sp,
                                    shadow = Shadow(color = MaterialTheme.colorScheme.primary, blurRadius = 20f)
                                )
                            ) {
                                append("less")
                            }
                        }
                    )

                    val taglines = context.resources.getStringArray(R.array.taglines)
                    val tagline = remember { taglines.random() }

                    Text(
                        text = tagline,
                        modifier = Modifier.padding(top = 8.dp),
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
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
                        iconId = R.drawable.icon_lowkey,
                        contentDescription = "Lowkey Mode",
                        glowColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            if (text.isBlank()) {
                                val messages = context.resources.getStringArray(R.array.toast_messages)
                                toastMessage = messages.random()
                                showToast = true
                            } else {
                                val intent = DisplayActivity.newIntent(context, text, "Lowkey")
                                context.startActivity(intent)
                            }
                        }
                    )
                    ModeButton(
                        iconId = R.drawable.icon_blast,
                        contentDescription = "Blast Mode",
                        glowColor = MaterialTheme.colorScheme.secondary,
                        onClick = {
                            if (text.isBlank()) {
                                val messages = context.resources.getStringArray(R.array.toast_messages)
                                toastMessage = messages.random()
                                showToast = true
                            } else {
                                val intent = DisplayActivity.newIntent(context, text, "Blast")
                                context.startActivity(intent)
                            }
                        }
                    )
                }
            }

            // Bottom section with text field
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 12.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(
                            elevation = 24.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = MaterialTheme.colorScheme.secondary
                        ),
                    shape = RoundedCornerShape(24.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = Color.DarkGray,
                        focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    ),
                    placeholder = { Text("Enter text", color = Color.Gray) }
                )
                if (text.isNotEmpty()) {
                    IconButton(
                        onClick = { text = "" },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear text", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        IconButton(
            onClick = {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        ComposableToast(message = toastMessage, visible = showToast, modifier = Modifier.align(Alignment.BottomCenter)) {
            showToast = false
        }
    }
}

@Composable
fun ComposableToast(message: String, visible: Boolean, modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.padding(bottom = 50.dp)
    ) {
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = MaterialTheme.colorScheme.primary
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyle(fontSize = 16.sp)
            )
        }
    }

    if (visible) {
        LaunchedEffect(Unit) {
            delay(3000)
            onDismiss()
        }
    }
}

@Composable
fun ModeButton(
    @DrawableRes iconId: Int,
    contentDescription: String,
    glowColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(144.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = glowColor
                ),
            tint = Color.Unspecified // Use this to render the PNG with its own colors
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeScreenPreview() {
    ShoutlessTheme {
        HomeScreen()
    }
}
