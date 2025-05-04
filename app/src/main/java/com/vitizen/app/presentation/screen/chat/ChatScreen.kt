package com.vitizen.app.presentation.screen.chat

import android.Manifest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onDismiss: () -> Unit
) {
    var showPermissionDialog by remember { mutableStateOf(false) }
    val recordAudioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) {
        if (!recordAudioPermission.status.isGranted) {
            showPermissionDialog = true
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission d'enregistrement audio") },
            text = { Text("L'application a besoin de la permission d'enregistrement audio pour la reconnaissance vocale.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        recordAudioPermission.launchPermissionRequest()
                    }
                ) {
                    Text("Autoriser")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    ChatDialog(
        viewModel = viewModel,
        onDismiss = onDismiss
    )
} 