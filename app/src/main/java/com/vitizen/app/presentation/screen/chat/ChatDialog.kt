package com.vitizen.app.presentation.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vitizen.app.domain.model.ChatMessage
import com.vitizen.app.presentation.screen.chat.components.VoiceInputButton
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDialog(
    viewModel: ChatViewModel,
    onDismiss: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Défilement automatique à chaque nouveau message
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            scope.launch {
                try {
                    // Attendre que la liste soit prête
                    delay(100)
                    // Forcer le défilement vers le dernier élément
                    listState.animateScrollToItem(state.messages.lastIndex)
                } catch (e: Exception) {
                    // En cas d'erreur, essayer un défilement immédiat
                    listState.scrollToItem(state.messages.lastIndex)
                }
            }
        }
    }

    // Observer le contenu du dernier message avec debounce
    LaunchedEffect(state.lastMessageContent) {
        if (state.messages.isNotEmpty() && state.lastMessageContent != null) {
            scope.launch {
                try {
                    delay(150) // Augmenté pour réduire la fréquence des mises à jour
                    listState.animateScrollToItem(state.messages.lastIndex)
                } catch (e: Exception) {
                    listState.scrollToItem(state.messages.lastIndex)
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // En-tête avec statut de connexion et bouton reset
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vitizen Assistant",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (state.connectionStatus) {
                                is ChatState.ConnectionStatus.Connected -> Icons.Default.Wifi
                                is ChatState.ConnectionStatus.Connecting -> Icons.Default.Wifi
                                else -> Icons.Default.WifiOff
                            },
                            contentDescription = "Statut de connexion",
                            tint = when (state.connectionStatus) {
                                is ChatState.ConnectionStatus.Connected -> MaterialTheme.colorScheme.primary
                                is ChatState.ConnectionStatus.Connecting -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.error
                            }
                        )
                        IconButton(
                            onClick = { viewModel.resetChat() },
                            enabled = !state.isLoading && !state.isTyping,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (!state.isLoading && !state.isTyping)
                                        MaterialTheme.colorScheme.surfaceVariant
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Réinitialiser la conversation",
                                tint = if (!state.isLoading && !state.isTyping)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = state.messages,
                        key = { it.id }
                    ) { message ->
                        MessageItem(message = message)
                    }
                    if (state.isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                }

                // Zone de saisie
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text("Votre message...") },
                        enabled = !state.isLoading && state.connectionStatus is ChatState.ConnectionStatus.Connected,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    VoiceInputButton(
                        isListening = state.isListening,
                        onStartListening = { viewModel.toggleVoiceInput() },
                        onStopListening = { viewModel.toggleVoiceInput() },
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.onMessageChanged(messageText)
                                viewModel.sendMessage()
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank() && !state.isLoading && 
                                 state.connectionStatus is ChatState.ConnectionStatus.Connected,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (messageText.isNotBlank() && !state.isLoading && 
                                    state.connectionStatus is ChatState.ConnectionStatus.Connected) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Envoyer",
                                tint = if (messageText.isNotBlank() && 
                                         state.connectionStatus is ChatState.ConnectionStatus.Connected) 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(4.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text(
                text = message.message,
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                color = if (message.isUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 100.dp)
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(8.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

