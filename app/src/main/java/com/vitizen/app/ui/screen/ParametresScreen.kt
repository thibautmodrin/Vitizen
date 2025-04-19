package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.ParametresViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.platform.LocalContext
import com.vitizen.app.services.FirstConnectionManager
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParametresScreen(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateToPulverisateurForm: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var isGeneralInfoEditing by remember { mutableStateOf(false) }
    var isMaterialEditing by remember { mutableStateOf(false) }
    var pulverisateurToDelete by remember { mutableStateOf<String?>(null) }
    var isGeneralInfoExpanded by remember { mutableStateOf(false) }
    var isMaterialExpanded by remember { mutableStateOf(true) }
    var showFirstConnectionDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // État local pour les champs
    var domaine by remember(uiState) { mutableStateOf(uiState.domaine) }
    var fonction by remember(uiState) { mutableStateOf(uiState.fonction) }
    var surfaceBlanc by remember(uiState) { mutableStateOf(uiState.surfaceBlanc) }
    var surfaceRouge by remember(uiState) { mutableStateOf(uiState.surfaceRouge) }
    var typeTraitement by remember(uiState) { mutableStateOf(uiState.typeTraitement) }
    
    // Synchroniser les valeurs avec l'état du ViewModel quand on n'est pas en mode édition
    LaunchedEffect(uiState, isGeneralInfoEditing) {
        if (!isGeneralInfoEditing) {
            domaine = uiState.domaine
            fonction = uiState.fonction
            surfaceBlanc = uiState.surfaceBlanc
            surfaceRouge = uiState.surfaceRouge
            typeTraitement = uiState.typeTraitement
        }
    }

    // Effet pour gérer la première connexion
    LaunchedEffect(Unit) {
        val isFirst = FirstConnectionManager.isFirstConnection(context)
        Log.d("ParametresScreen", "isFirstConnection: $isFirst")
        if (isFirst) {
            isGeneralInfoExpanded = true
            isGeneralInfoEditing = true
            showFirstConnectionDialog = true
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

    // Boîte de dialogue de première connexion
    if (showFirstConnectionDialog) {
        AlertDialog(
            onDismissRequest = { 
                showFirstConnectionDialog = false
                FirstConnectionManager.setFirstConnectionDone(context)
            },
            title = { Text("Bienvenue") },
            text = { 
                Text("Avant de commencer, il est essentiel de renseigner le plus d'informations possible afin de permettre à l'application de proposer une aide optimale. Merci de détailler chaque champ demandé.")
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showFirstConnectionDialog = false
                        FirstConnectionManager.setFirstConnectionDone(context)
                    }
                ) {
                    Text("Compris")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Column(
            modifier = Modifier
                .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section Informations générales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { isGeneralInfoExpanded = !isGeneralInfoExpanded }
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Informations générales",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isGeneralInfoExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isGeneralInfoExpanded) "Replier" else "Déplier"
                                )
                                if (isGeneralInfoExpanded) {
                                    IconButton(
                                        onClick = {
                                            if (isGeneralInfoEditing) {
                                                viewModel.saveParametres(
                                                    domaine = domaine,
                                                    fonction = fonction,
                                                    surfaceBlanc = surfaceBlanc,
                                                    surfaceRouge = surfaceRouge,
                                                    typeTraitement = typeTraitement,
                                                    pulverisateurs = uiState.pulverisateurs
                                                )
                                            }
                                            isGeneralInfoEditing = !isGeneralInfoEditing
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isGeneralInfoEditing) Icons.Default.Save else Icons.Default.Edit,
                                            contentDescription = if (isGeneralInfoEditing) "Sauvegarder" else "Modifier"
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = isGeneralInfoExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = domaine,
                                    onValueChange = { domaine = it },
                                    label = { Text("Nom du Domaine") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isGeneralInfoEditing,
                                    readOnly = !isGeneralInfoEditing
                                )

                                OutlinedTextField(
                                    value = fonction,
                                    onValueChange = { fonction = it },
                                    label = { Text("Fonction de l'agent") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isGeneralInfoEditing,
                                    readOnly = !isGeneralInfoEditing
                                )

                                OutlinedTextField(
                                    value = surfaceBlanc,
                                    onValueChange = { surfaceBlanc = it },
                                    label = { Text("Surface Blanc") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isGeneralInfoEditing,
                                    readOnly = !isGeneralInfoEditing
                                )

                                OutlinedTextField(
                                    value = surfaceRouge,
                                    onValueChange = { surfaceRouge = it },
                                    label = { Text("Surface Rouge") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isGeneralInfoEditing,
                                    readOnly = !isGeneralInfoEditing
                                )

                                OutlinedTextField(
                                    value = typeTraitement,
                                    onValueChange = { typeTraitement = it },
                                    label = { Text("Type de traitement") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isGeneralInfoEditing,
                                    readOnly = !isGeneralInfoEditing
                                )
                            }
                        }
                    }
                }

                // Section Matériel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { isMaterialExpanded = !isMaterialExpanded }
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Matériel",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMaterialExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isMaterialExpanded) "Replier" else "Déplier"
                                )
                                if (isMaterialExpanded) {
                                    IconButton(
                                        onClick = {
                                            if (isMaterialEditing) {
                                                viewModel.saveParametres(
                                                    domaine = uiState.domaine,
                                                    fonction = uiState.fonction,
                                                    surfaceBlanc = uiState.surfaceBlanc,
                                                    surfaceRouge = uiState.surfaceRouge,
                                                    typeTraitement = uiState.typeTraitement,
                                                    pulverisateurs = uiState.pulverisateurs
                                                )
                                            }
                                            isMaterialEditing = !isMaterialEditing
                                        }
                                    ) {
                        Icon(
                                            imageVector = if (isMaterialEditing) Icons.Default.Save else Icons.Default.Edit,
                                            contentDescription = if (isMaterialEditing) "Sauvegarder" else "Modifier"
                                        )
                                    }
                                    if (isMaterialEditing) {
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
                        }

                        AnimatedVisibility(
                            visible = isMaterialExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                uiState.pulverisateurs.forEach { pulverisateur ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { 
                                            if (isMaterialEditing) {
                                                val nomPulverisateur = pulverisateur.nom.takeIf { it.isNotBlank() } ?: "new"
                                                onNavigateToPulverisateurForm(nomPulverisateur)
                                            }
                                        },
                                        enabled = isMaterialEditing,
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isMaterialEditing) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isMaterialEditing) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
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
                                            
                                            if (isMaterialEditing) {
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
                    }
                }
            }
        }
    )
} 