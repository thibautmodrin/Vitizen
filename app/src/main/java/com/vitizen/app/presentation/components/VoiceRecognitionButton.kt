package com.vitizen.app.presentation.components

import android.Manifest
import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.vitizen.app.presentation.screen.chat.VoiceRecognitionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceRecognitionButton(
    viewModel: VoiceRecognitionViewModel,
    modifier: Modifier = Modifier,
    onTranscriptionComplete: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val state by viewModel.state.collectAsState()

    val scale by animateFloatAsState(
        targetValue = if (state.isRecording) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            state.isProcessing -> MaterialTheme.colorScheme.tertiary
            state.isRecording -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        label = "backgroundColor"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            state.isProcessing -> MaterialTheme.colorScheme.onTertiary
            state.isRecording -> MaterialTheme.colorScheme.onError
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "iconColor"
    )

    IconButton(
        onClick = {
            when (permissionState.status) {
                is PermissionStatus.Granted -> {
                    scope.launch {
                        if (state.isRecording) {
                            viewModel.stopRecording()
                        } else {
                            viewModel.startRecording()
                        }
                    }
                }
                is PermissionStatus.Denied -> {
                    permissionState.launchPermissionRequest()
                }
            }
        },
        enabled = !state.isProcessing,
        modifier = modifier
            .clip(CircleShape)
            .background(color = backgroundColor)
            .scale(scale)
    ) {
        if (state.isProcessing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = iconColor
            )
        } else {
            Icon(
                imageVector = if (state.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (state.isRecording) "Arrêter l'enregistrement" else "Démarrer l'enregistrement",
                tint = iconColor
            )
        }
    }

    // Observer le texte transcrit
    LaunchedEffect(state.transcribedText) {
        state.transcribedText?.let { text ->
            if (text.isNotBlank()) {
                onTranscriptionComplete(text)
            }
        }
    }
}