package com.vitizen.app.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.ParametresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    viewModel: ParametresViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("Profil") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil"
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { /* TODO: Naviguer vers le profil */ }) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Voir le profil"
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("Notifications") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications"
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = uiState.notificationsEnabled,
                            onCheckedChange = { viewModel.toggleNotifications(it) }
                        )
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = { Text("Déconnexion") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Déconnexion"
                        )
                    },
                    modifier = Modifier.clickable { viewModel.logout() }
                )
            }
        }
    }
} 