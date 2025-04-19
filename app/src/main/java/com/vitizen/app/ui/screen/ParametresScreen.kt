package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
    viewModel: ParametresViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val scrollState = rememberScrollState()

    var domaine by remember { mutableStateOf(uiState.domaine) }
    var fonction by remember { mutableStateOf(uiState.fonction) }
    var surfaceBlanc by remember { mutableStateOf(uiState.surfaceBlanc) }
    var surfaceRouge by remember { mutableStateOf(uiState.surfaceRouge) }
    var pulverisateur by remember { mutableStateOf(uiState.pulverisateur) }
    var typeTraitement by remember { mutableStateOf(uiState.typeTraitement) }

    LaunchedEffect(uiState) {
        if (!isEditing) {
            domaine = uiState.domaine
            fonction = uiState.fonction
            surfaceBlanc = uiState.surfaceBlanc
            surfaceRouge = uiState.surfaceRouge
            pulverisateur = uiState.pulverisateur
            typeTraitement = uiState.typeTraitement
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramètres du domaine") },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, "Modifier")
                        }
                    } else {
                        IconButton(
                            onClick = {
                                viewModel.saveParametres(
                                    domaine = domaine,
                                    fonction = fonction,
                                    surfaceBlanc = surfaceBlanc,
                                    surfaceRouge = surfaceRouge,
                                    pulverisateur = pulverisateur,
                                    typeTraitement = typeTraitement
                                )
                            }
                        ) {
                            Icon(Icons.Default.Save, "Enregistrer")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = domaine,
                onValueChange = { domaine = it },
                label = { Text("Nom du Domaine") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                readOnly = !isEditing
            )

            OutlinedTextField(
                value = fonction,
                onValueChange = { fonction = it },
                label = { Text("Fonction") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing,
                readOnly = !isEditing
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = surfaceBlanc,
                    onValueChange = { surfaceBlanc = it },
                    label = { Text("Surface Blanc (ha)") },
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    readOnly = !isEditing
                )

                OutlinedTextField(
                    value = surfaceRouge,
                    onValueChange = { surfaceRouge = it },
                    label = { Text("Surface Rouge (ha)") },
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    readOnly = !isEditing
                )
            }

            Text(
                text = "Type de pulvérisateur",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pulverisateur,
                    onValueChange = { pulverisateur = it },
                    label = { Text("Modèle") },
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    readOnly = !isEditing
                )
            }

            Text(
                text = "Type de traitement",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = typeTraitement,
                    onValueChange = { typeTraitement = it },
                    label = { Text("Type") },
                    modifier = Modifier.weight(1f),
                    enabled = isEditing,
                    readOnly = !isEditing
                )
            }
        }
    }
} 