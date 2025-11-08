package io.petkorazzi.shoutless

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
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.petkorazzi.shoutless.ui.theme.ShoutlessTheme
import io.petkorazzi.shoutless.util.HideSystemBars
import io.petkorazzi.shoutless.util.glow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.text.font.FontStyle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            ShoutlessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(mainViewModel = viewModel(viewModelStoreOwner = this))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, mainViewModel: MainViewModel) {
    val text by mainViewModel.text.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // START: Refreshable Tagline Logic
    val taglines: Array<String> = stringArrayResource(id = R.array.taglines)
    var tagline by remember(taglines) { mutableStateOf(taglines.random()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                tagline = taglines.random()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // END: Refreshable Tagline Logic
    val toastMessages: Array<String> = stringArrayResource(id = R.array.toast_messages)

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
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text(
                        text = data.visuals.message,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
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

                    // Old tagline stuff
                    // val taglines = context.resources.getStringArray(R.array.taglines)
                    // val tagline = remember { taglines.random() }

                    Text(
                        text = tagline,
                        modifier = Modifier.offset(y = (-8).dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
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
                        iconId = R.drawable.ic_lowkey,
                        text = "lowkey",
                        fontWeight = FontWeight.Light,
                        contentDescription = "Lowkey Mode",
                        glowColor = MaterialTheme.colorScheme.primary,
                        textBlurRadius = 25f,
                        onClick = {
                            if (text.isBlank()) {
                                scope.launch {
                                    withTimeoutOrNull(2000) {
                                        snackbarHostState.showSnackbar(toastMessages.random())
                                    }
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
                                scope.launch {
                                    withTimeoutOrNull(2000) {
                                        snackbarHostState.showSnackbar(toastMessages.random())
                                    }
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
                        .size(100.dp)
                        .glow(
                            color = MaterialTheme.colorScheme.tertiary,
                            radius = 15.dp,
                            shape = RoundedCornerShape(24.dp),
                            alpha = 0.5f
                        )
                        .clip(RoundedCornerShape(24.dp))
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
                            // imageVector = Icons.Rounded.Replay,
                            painter = painterResource(id = R.drawable.quick_phrases_24px),
                            contentDescription = "Clapback Mode",
                            modifier = Modifier.size(66.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "clapback",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.offset(y = (-8).dp)
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
                    onValueChange = { mainViewModel.onTextChange(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .glow(
                            color = MaterialTheme.colorScheme.secondary,
                            radius = 10.dp,
                            shape = RoundedCornerShape(24.dp),
                            alpha = 0.5f
                        )
                        .clip(RoundedCornerShape(24.dp)),
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
                        onClick = { mainViewModel.onTextChange("") },
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
            .clip(RoundedCornerShape(32.dp))
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