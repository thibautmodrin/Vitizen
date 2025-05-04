package com.vitizen.app.presentation.components

import android.Manifest
import androidx.compose.animation.core.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.vitizen.app.presentation.screen.chat.VoiceRecognitionViewModel
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceRecognitionButton(
    viewModel: VoiceRecognitionViewModel,
    modifier: Modifier = Modifier,
    onTranscriptionComplete: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Gestion des permissions
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    
    // Vérifier les permissions au démarrage
    LaunchedEffect(Unit) {
        viewModel.onPermissionResult(permissionState.status == PermissionStatus.Granted)
    }

    // Animation du niveau audio avec limitation de l'alpha
    val audioLevelAnim by animateFloatAsState(
        targetValue = state.audioLevel.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    // Animation de pulsation
    val scale by animateFloatAsState(
        targetValue = if (state.isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(state.transcribedText) {
        state.transcribedText?.let { text ->
            if (text.isNotBlank()) {
                onTranscriptionComplete(text)
            }
        }
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(
                when {
                    state.isProcessing -> MaterialTheme.colorScheme.secondary
                    state.isRecording -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        // Indicateur de niveau audio avec alpha limité
        if (state.isRecording) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.error.copy(
                            alpha = audioLevelAnim.coerceIn(0f, 1f)
                        )
                    )
            )
        }

        IconButton(
            onClick = {
                if (permissionState.status != PermissionStatus.Granted) {
                    scope.launch {
                        permissionState.launchPermissionRequest()
                    }
                } else {
                    if (state.isRecording) {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording()
                    }
                }
            },
            enabled = !state.isProcessing,
            modifier = Modifier.size(56.dp)
        ) {
            when {
                state.isProcessing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                state.isRecording -> {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Arrêter l'enregistrement",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Démarrer l'enregistrement",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
} 