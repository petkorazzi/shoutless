package com.example.shoutless

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoutless.ui.theme.ShoutlessTheme
import com.example.shoutless.util.HideSystemBars
import com.example.shoutless.util.glow

class ClapbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoutlessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClapbackScreen(onFinishActivity = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClapbackScreen(modifier: Modifier = Modifier, onFinishActivity: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("shoutless_prefs", Context.MODE_PRIVATE) }

    var selectedMode by remember { mutableStateOf(sharedPreferences.getString("last_display_mode", "Lowkey") ?: "Lowkey") }

    val clapback1Label by remember { mutableStateOf(sharedPreferences.getString("clapback1_label", "yeah") ?: "yeah") }
    val clapback1Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback1_hidden", "yeah") ?: "yeah") }
    val clapback2Label by remember { mutableStateOf(sharedPreferences.getString("clapback2_label", "nah") ?: "nah") }
    val clapback2Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback2_hidden", "nah") ?: "nah") }
    val clapback3Label by remember { mutableStateOf(sharedPreferences.getString("clapback3_label", "ty") ?: "ty") }
    val clapback3Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback3_hidden", "thank you") ?: "thank you") }
    val clapback4Label by remember { mutableStateOf(sharedPreferences.getString("clapback4_label", "brb") ?: "brb") }
    val clapback4Hidden by remember { mutableStateOf(sharedPreferences.getString("clapback4_hidden", "be right back") ?: "be right back") }

    var customText1 by remember { mutableStateOf(sharedPreferences.getString("custom_text_1", "On my way!") ?: "On my way!") }
    var customText2 by remember { mutableStateOf(sharedPreferences.getString("custom_text_2", "Sounds good!") ?: "Sounds good!") }
    var showEditDialog by remember { mutableStateOf(false) }
    var textToEditId by remember { mutableStateOf(0) }
    var number by remember { mutableStateOf(1) }

    fun launchDisplay(text: String) {
        val intent = DisplayActivity.newIntent(context, text, selectedMode)
        context.startActivity(intent)
        onFinishActivity()
    }

    HideSystemBars()

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val buttonTextColor = if (selectedMode == "Lowkey") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            val buttonBorderColor = if (selectedMode == "Lowkey") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shadow = Shadow(color = MaterialTheme.colorScheme.tertiary, blurRadius = 20f)
                                )
                            ) {
                                append("clap")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    shadow = Shadow(color = MaterialTheme.colorScheme.primary, blurRadius = 20f)
                                )
                            ) {
                                append("back")
                            }
                        },
                        style = MaterialTheme.typography.displayLarge
                    )

                    // Tagline
                    val taglines = context.resources.getStringArray(R.array.clapback_taglines)
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

                // Segmented Toggle
                SingleChoiceSegmentedButtonRow(modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 32.dp)) {
                    SegmentedButton(
                        modifier = if (selectedMode == "Lowkey") Modifier.glow(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 15.dp,
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            alpha = 0.5f
                        ) else Modifier,
                        selected = selectedMode == "Lowkey",
                        onClick = { selectedMode = "Lowkey" },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.primary,
                            activeContentColor = MaterialTheme.colorScheme.onPrimary,
                            activeBorderColor = MaterialTheme.colorScheme.primary,
                            inactiveContainerColor = MaterialTheme.colorScheme.surface,
                            inactiveContentColor = MaterialTheme.colorScheme.primary,
                            inactiveBorderColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("lowkey", style = MaterialTheme.typography.labelLarge)
                    }
                    SegmentedButton(
                        modifier = if (selectedMode == "Blast") Modifier.glow(
                            color = MaterialTheme.colorScheme.secondary,
                            radius = 15.dp,
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            alpha = 0.5f
                        ) else Modifier,
                        selected = selectedMode == "Blast",
                        onClick = { selectedMode = "Blast" },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = MaterialTheme.colorScheme.secondary,
                            activeContentColor = MaterialTheme.colorScheme.onSecondary,
                            activeBorderColor = MaterialTheme.colorScheme.secondary,
                            inactiveContainerColor = MaterialTheme.colorScheme.surface,
                            inactiveContentColor = MaterialTheme.colorScheme.secondary,
                            inactiveBorderColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("BLAST", style = MaterialTheme.typography.labelLarge)
                    }
                }


                // Buttons
                Column(modifier = Modifier.padding(top = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ClapbackButton(text = clapback1Label, textColor = buttonTextColor, borderColor = buttonBorderColor, onClick = { launchDisplay(clapback1Hidden) })
                        ClapbackButton(text = clapback2Label, textColor = buttonTextColor, borderColor = buttonBorderColor, onClick = { launchDisplay(clapback2Hidden) })
                    }
                    Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ClapbackButton(text = clapback3Label, textColor = buttonTextColor, borderColor = buttonBorderColor, onClick = { launchDisplay(clapback3Hidden) })
                        ClapbackButton(text = clapback4Label, textColor = buttonTextColor, borderColor = buttonBorderColor, onClick = { launchDisplay(clapback4Hidden) })
                    }
                }

                // Custom Text Buttons
                Column(
                    modifier = Modifier.padding(top = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomTextButton(
                        text = customText1,
                        borderColor = buttonBorderColor,
                        onClick = { launchDisplay(customText1) },
                        onEditClick = {
                            textToEditId = 1
                            showEditDialog = true
                        }
                    )
                    CustomTextButton(
                        text = customText2,
                        borderColor = buttonBorderColor,
                        onClick = { launchDisplay(customText2) },
                        onEditClick = {
                            textToEditId = 2
                            showEditDialog = true
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                NumberPicker(
                    value = number,
                    onValueChange = { if (it >= 1) number = it },
                    borderColor = buttonBorderColor,
                    onNumberClick = { launchDisplay(number.toString()) }
                )
            }
        }

        if (showEditDialog) {
            val currentText = when (textToEditId) {
                1 -> customText1
                2 -> customText2
                else -> ""
            }
            EditCustomTextDialog(
                currentText = currentText,
                onDismiss = { showEditDialog = false },
                onConfirm = {
                    if (textToEditId == 1) {
                        customText1 = it
                        sharedPreferences.edit().putString("custom_text_1", it).apply()
                    } else {
                        customText2 = it
                        sharedPreferences.edit().putString("custom_text_2", it).apply()
                    }
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun ClapbackButton(
    text: String,
    textColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .glow(
                color = borderColor,
                radius = 15.dp,
                shape = RoundedCornerShape(24.dp),
                alpha = 0.5f
            )
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall.copy(
                shadow = Shadow(color = textColor, blurRadius = 15f)
            )
        )
    }
}

@Composable
fun CustomTextButton(
    text: String,
    borderColor: Color,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .glow(
                color = borderColor,
                radius = 15.dp,
                shape = RoundedCornerShape(16.dp),
                alpha = 0.5f
            )
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Edit text",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.clickable(onClick = onEditClick)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun EditCustomTextDialog(
    currentText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Text") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("New text") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    borderColor: Color,
    onNumberClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.8f)
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        val buttonHeight = 50.dp
        val cornerRadius = 16.dp
        val horizontalOffset = 60.dp

        val minusInteractionSource = remember { MutableInteractionSource() }
        val isMinusPressed by minusInteractionSource.collectIsPressedAsState()
        val animatedMinusGlowAlpha by animateFloatAsState(
            targetValue = if (isMinusPressed) 0.5f else 0f,
            label = "minusGlowAlpha"
        )

        val plusInteractionSource = remember { MutableInteractionSource() }
        val isPlusPressed by plusInteractionSource.collectIsPressedAsState()
        val animatedPlusGlowAlpha by animateFloatAsState(
            targetValue = if (isPlusPressed) 0.5f else 0f,
            label = "plusGlowAlpha"
        )

        // Minus Button
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -horizontalOffset)
                .size(width = 70.dp, height = buttonHeight)
                .glow(
                    color = MaterialTheme.colorScheme.tertiary,
                    radius = 15.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    alpha = animatedMinusGlowAlpha
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary), RoundedCornerShape(cornerRadius))
                .clickable(
                    interactionSource = minusInteractionSource,
                    indication = null, // Disable ripple
                    onClick = { onValueChange(value - 1) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("-", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        }

        // Plus Button
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = horizontalOffset)
                .size(width = 70.dp, height = buttonHeight)
                .glow(
                    color = MaterialTheme.colorScheme.tertiary,
                    radius = 15.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    alpha = animatedPlusGlowAlpha
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary), RoundedCornerShape(cornerRadius))
                .clickable(
                    interactionSource = plusInteractionSource,
                    indication = null, // Disable ripple
                    onClick = { onValueChange(value + 1) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        }

        // Value
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 90.dp, height = 60.dp)
                .glow(
                    color = borderColor,
                    radius = 15.dp,
                    shape = RoundedCornerShape(cornerRadius),
                    alpha = 0.5f
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(3.dp, borderColor), RoundedCornerShape(cornerRadius))
                .clickable(onClick = onNumberClick),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "numberChangeAnimation"
            ) { targetValue ->
                Text(targetValue.toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ClapbackScreenPreview() {
    ShoutlessTheme {
        ClapbackScreen(onFinishActivity = {})
    }
}
