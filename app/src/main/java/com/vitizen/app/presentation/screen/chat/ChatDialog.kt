package com.vitizen.app.presentation.screen.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vitizen.app.domain.model.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatDialog(
    viewModel: ChatViewModel,
    onDismiss: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val state = viewModel.state
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Défilement automatique basé sur le contenu du dernier message
    LaunchedEffect(state.messages.lastOrNull()?.message) {
        try {
            if (state.messages.isNotEmpty()) {
                // Défilement immédiat à chaque mise à jour du contenu
                listState.scrollToItem(state.messages.lastIndex)
            }
        } catch (e: Exception) {
            // Ignorer les erreurs de défilement
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
                // En-tête du chat avec bouton de réinitialisation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vitizen Assistant",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    IconButton(
                        onClick = { viewModel.resetChat() },
                        modifier = Modifier.padding(start = 8.dp),
                        enabled = !state.isLoading && !state.isTyping
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Réinitialiser le chat",
                            tint = if (state.isLoading || state.isTyping) 
                                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.38f)
                            else 
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Zone des messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.messages) { message ->
                        MessageItem(message)
                    }
                    if (state.isTyping) {
                        item { TypingIndicator() }
                    }
                }

                // Message d'erreur
                AnimatedVisibility(
                    visible = state.error != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    state.error?.let { errorMessage ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                // Zone de saisie
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Posez votre question...") },
                            enabled = !state.isLoading,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.onMessageChanged(messageText)
                                    viewModel.sendMessage()
                                    messageText = ""
                                }
                            },
                            enabled = !state.isLoading,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (!state.isLoading) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.surfaceVariant
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
                                    Icons.Default.Send,
                                    contentDescription = "Envoyer",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
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
                modifier = Modifier.padding(12.dp),
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
    val dots = remember { mutableStateOf(".") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dots.value = when (dots.value) {
                "." -> ".."
                ".." -> "..."
                else -> "."
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Vitizen est en train d'écrire${dots.value}",
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

