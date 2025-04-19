package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.ParametresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateToPulverisateurForm: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var pulverisateurToDelete by remember { mutableStateOf<String?>(null) }
    
    // État local pour les champs
    var domaine by remember(uiState) { mutableStateOf(uiState.domaine) }
    var fonction by remember(uiState) { mutableStateOf(uiState.fonction) }
    var surfaceBlanc by remember(uiState) { mutableStateOf(uiState.surfaceBlanc) }
    var surfaceRouge by remember(uiState) { mutableStateOf(uiState.surfaceRouge) }
    var typeTraitement by remember(uiState) { mutableStateOf(uiState.typeTraitement) }
    
    // Synchroniser les valeurs avec l'état du ViewModel quand on n'est pas en mode édition
    LaunchedEffect(uiState, isEditing) {
        if (!isEditing) {
            domaine = uiState.domaine
            fonction = uiState.fonction
            surfaceBlanc = uiState.surfaceBlanc
            surfaceRouge = uiState.surfaceRouge
            typeTraitement = uiState.typeTraitement
        }
    }

    // Boîte de dialogue de confirmation
    if (pulverisateurToDelete != null) {
        AlertDialog(
            onDismissRequest = { pulverisateurToDelete = null },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce pulvérisateur ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        pulverisateurToDelete?.let { viewModel.removePulverisateur(it) }
                        pulverisateurToDelete = null
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pulverisateurToDelete = null }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Informations générales
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Informations générales",
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    viewModel.saveParametres(
                                        domaine = domaine,
                                        fonction = fonction,
                                        surfaceBlanc = surfaceBlanc,
                                        surfaceRouge = surfaceRouge,
                                        typeTraitement = typeTraitement,
                                        pulverisateurs = uiState.pulverisateurs
                                    )
                                }
                                isEditing = !isEditing
                            }
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Sauvegarder" else "Modifier"
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = domaine,
                        onValueChange = { domaine = it },
                        label = { Text("Nom du Domaine") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )
                }

                item {
                    OutlinedTextField(
                        value = fonction,
                        onValueChange = { fonction = it },
                        label = { Text("Fonction de l'agent") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )
                }

                item {
                    OutlinedTextField(
                        value = surfaceBlanc,
                        onValueChange = { surfaceBlanc = it },
                        label = { Text("Surface Blanc") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )
                }

                item {
                    OutlinedTextField(
                        value = surfaceRouge,
                        onValueChange = { surfaceRouge = it },
                        label = { Text("Surface Rouge") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )
                }

                item {
                    OutlinedTextField(
                        value = typeTraitement,
                        onValueChange = { typeTraitement = it },
                        label = { Text("Type de traitement") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        readOnly = !isEditing
                    )
                }

                // Section Matériel
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Matériel",
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (isEditing) {
                            IconButton(
                                onClick = { onNavigateToPulverisateurForm("new") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ajouter un pulvérisateur"
                                )
                            }
                        }
                    }
                }

                items(uiState.pulverisateurs) { pulverisateur ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { 
                            if (isEditing) {
                                val nomPulverisateur = pulverisateur.nom.takeIf { it.isNotBlank() } ?: "new"
                                onNavigateToPulverisateurForm(nomPulverisateur)
                            }
                        },
                        enabled = isEditing,
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEditing) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isEditing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = pulverisateur.nom.takeIf { it.isNotBlank() } ?: "Nouveau pulvérisateur",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Type: ${pulverisateur.typePulverisateur?.name ?: "Non défini"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Modèle: ${pulverisateur.modeleMarque.takeIf { it.isNotBlank() } ?: "Non défini"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Pression: ${pulverisateur.pression.takeIf { it.isNotBlank() } ?: "Non définie"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            if (isEditing) {
                                IconButton(
                                    onClick = { pulverisateurToDelete = pulverisateur.nom },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
} 