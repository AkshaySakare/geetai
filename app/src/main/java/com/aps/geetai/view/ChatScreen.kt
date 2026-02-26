package com.aps.geetai.ui

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aps.geetai.BackgroundMode
import com.aps.geetai.model.ChatMessage
import com.aps.geetai.model.Mood
import com.aps.geetai.model.Sender
import com.aps.geetai.viewmodel.ChatUiState

/**
 * VIEW LAYER — Root screen composable
 *

 * @param uiState        Current state from the ViewModel
 * @param onUserMessage  Callback when the user submits a typed message
 * @param onMoodSelected Callback when the user taps a mood chip
 * @param onDismissError Callback when the user dismisses the error snackbar
 * @param onFinish       Callback to finish the Activity (swipe-down gesture)
 */
@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onUserMessage: (String) -> Unit,
    onMoodSelected: (Mood) -> Unit,
    onDismissError: () -> Unit,
    onFinish: () -> Unit
) {
    // ----- Local UI-only state (not business state — fine to keep here) -----
    var currentMode by remember { mutableStateOf(BackgroundMode.STAR_STATIC) }
    var rippleCenter by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current

    // Continuously advance shader time
    val time = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            time.animateTo(time.value + 0.3f, tween(30))
        }
    }

    // Load AGSL shader for the selected background mode
    val shader = remember(currentMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RuntimeShader(
                context.resources.openRawResource(currentMode.shaderRes)
                    .readBytes().decodeToString()
            )
        } else {
            error("AGSL shaders require Android 13+")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Double-tap → cycle background shader
            .pointerInput(currentMode) {
                detectTapGestures(
                    onDoubleTap = {
                        val modes = BackgroundMode.values()
                        currentMode = modes[(modes.indexOf(currentMode) + 1) % modes.size]
                    },
                    onTap = { offset -> rippleCenter = offset }
                )
            }
            // Swipe down → exit
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 100f) onFinish()
                }
            }
    ) {
        // ── 🎨 Shader Background ────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            shader.setFloatUniform("time", time.value)
            shader.setFloatUniform("resolution", size.width, size.height)
            if (currentMode == BackgroundMode.RIPPEL) {
                shader.setFloatUniform("rippleCenter", rippleCenter.x, rippleCenter.y)
            }
            drawRect(ShaderBrush(shader))
        }

        // ── 💬 Chat UI ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            MoodSelector(
                selectedMood = uiState.selectedMood,
                onMoodSelected = onMoodSelected
            )

            if (uiState.isLoading) {
                LoadingIndicator()
            }

            MessageList(
                messages = uiState.messages,
                modifier = Modifier.weight(1f)
            )

            MessageInputRow(onSend = onUserMessage)
        }

        // ── ⚠️ Error Snackbar ───────────────────────────────────────────────
        uiState.error?.let { errorMsg ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = onDismissError) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            ) { Text(errorMsg) }
        }
    }
}

// ── Sub-composables ─────────────────────────────────────────────────────────

/**
 * Horizontal scrollable row of mood chips.
 * Highlights the currently selected mood.
 */
@Composable
private fun MoodSelector(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(Mood.values()) { mood ->
            val isSelected = mood == selectedMood
            Button(
                onClick = { onMoodSelected(mood) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xAA1a73e8) else Color(0x33000000)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(mood.chipLabel, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

/**
 * Reverse-sorted message list — latest message at the bottom.
 * Bot and user bubbles use different background tints.
 */
@Composable
private fun MessageList(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true
    ) {
        items(
            items = messages.reversed(),
            key = { it.id }
        ) { msg ->
            val bubbleColor = when (msg.sender) {
                Sender.USER -> Color(0x88003366)   // dark blue tint
                Sender.BOT  -> Color(0x66000000)   // dark grey tint
            }
            val alignment = when (msg.sender) {
                Sender.USER -> Alignment.End
                Sender.BOT  -> Alignment.Start
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = alignment
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier
                        .background(bubbleColor, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Text field + Send button row at the bottom of the screen.
 * Input state is local to this composable — it is transient UI state,
 * not application state, so it doesn't belong in the ViewModel.
 */
@Composable
private fun MessageInputRow(onSend: (String) -> Unit) {
    var userInput by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x55000000), RoundedCornerShape(12.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type your message...", color = Color(0xAAFFFFFF)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor  = Color.Transparent,
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
                cursorColor             = Color.White,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                if (userInput.isNotBlank()) {
                    onSend(userInput)
                    userInput = ""
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = Color.White
            )
        ) {
            Text("Send")
        }
    }
}
