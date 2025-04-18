package com.vitizen.app.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vitizen.app.ui.theme.*

@Composable
fun CitizenScaffold(
    snackbarHostState: SnackbarHostState,
    messageType: MessageType = MessageType.INFO,
    modifier: Modifier = Modifier,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Scaffold(
        modifier = modifier,
        snackbarHost = { 
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = when (messageType) {
                            MessageType.ERROR -> VitizenError
                            MessageType.SUCCESS -> VitizenGreen
                            MessageType.INFO -> VitizenGreenLight
                        },
                        contentColor = when (messageType) {
                            MessageType.ERROR -> VitizenSurface
                            MessageType.SUCCESS -> VitizenSurface
                            MessageType.INFO -> VitizenGreenDark
                        },
                        actionColor = when (messageType) {
                            MessageType.ERROR -> VitizenSurface
                            MessageType.SUCCESS -> VitizenSurface
                            MessageType.INFO -> VitizenGreenDark
                        },
                        dismissActionContentColor = when (messageType) {
                            MessageType.ERROR -> VitizenSurface.copy(alpha = 0.7f)
                            MessageType.SUCCESS -> VitizenSurface.copy(alpha = 0.7f)
                            MessageType.INFO -> VitizenGreenDark.copy(alpha = 0.7f)
                        }
                    )
                }
            )
        },
        content = content
    )
} 