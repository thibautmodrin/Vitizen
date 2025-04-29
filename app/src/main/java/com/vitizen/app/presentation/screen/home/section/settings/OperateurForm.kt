package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.presentation.components.MultiSelectDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperateurForm(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    operateurId: Long? = null
) {
    var nom by remember { mutableStateOf("") }
    var nomError by remember { mutableStateOf(false) }
    var disponibleWeekend by remember { mutableStateOf(false) }
    var certiPhyto by remember { mutableStateOf(false) }
    var materielMaitrise by remember { mutableStateOf(listOf<String>()) }

    val pulverisateurs by viewModel.pulverisateurs.collectAsState()
    val materielOptions = remember(pulverisateurs) {
        if (pulverisateurs.isEmpty()) {
            listOf("Aucun")
        } else {
            pulverisateurs.map { it.nomMateriel }
        }
    }

    LaunchedEffect(operateurId) {
        operateurId?.let { id ->
            viewModel.getOperateurById(id)?.let { operateur ->
                nom = operateur.nom
                disponibleWeekend = operateur.disponibleWeekend
                certiPhyto = operateur.diplomes.contains("CertiPhyto")
                materielMaitrise = operateur.materielMaitrise
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (operateurId == null) "Ajouter un opérateur" else "Modifier l'opérateur") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nom,
                onValueChange = { 
                    nom = it
                    nomError = false
                },
                label = { Text("Nom de l'opérateur") },
                isError = nomError,
                supportingText = {
                    if (nomError) {
                        Text("Champ requis")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Disponible le week-end")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = disponibleWeekend,
                    onCheckedChange = { disponibleWeekend = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CertiPhyto")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = certiPhyto,
                    onCheckedChange = { certiPhyto = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Matériel maîtrisé
            MultiSelectDropdown(
                label = "Matériel maîtrisé",
                options = materielOptions,
                selected = materielMaitrise,
                onSelectionChange = { materielMaitrise = it },
                optionToString = { it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (nom.isBlank()) {
                        nomError = true
                        return@Button
                    }
                    
                    if (operateurId == null) {
                        viewModel.addOperateur(
                            nom = nom,
                            disponibleWeekend = disponibleWeekend,
                            diplomes = if (certiPhyto) listOf("CertiPhyto") else emptyList(),
                            materielMaitrise = materielMaitrise
                        )
                    } else {
                        viewModel.updateOperateur(
                            id = operateurId,
                            nom = nom,
                            disponibleWeekend = disponibleWeekend,
                            diplomes = if (certiPhyto) listOf("CertiPhyto") else emptyList(),
                            materielMaitrise = materielMaitrise
                        )
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (operateurId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 