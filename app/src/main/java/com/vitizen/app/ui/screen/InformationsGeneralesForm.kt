package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.ParametresViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationsGeneralesForm(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    infoId: Long? = null
) {
    val scope = rememberCoroutineScope()
    var nomDomaine by remember { mutableStateOf("") }
    var modeCulture by remember { mutableStateOf("") }
    var surfaceTotale by remember { mutableStateOf("") }
    var codePostal by remember { mutableStateOf("") }
    var certifications by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(infoId) {
        infoId?.let { id ->
            viewModel.getInformationsGeneralesById(id)?.let { info ->
                nomDomaine = info.nomDomaine
                modeCulture = info.modeCulture
                surfaceTotale = info.surfaceTotale.toString()
                codePostal = info.codePostal
                certifications = info.certifications
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (infoId == null) "Ajouter des informations" else "Modifier les informations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nomDomaine,
                onValueChange = { nomDomaine = it },
                label = { Text("Nom du domaine") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = modeCulture,
                onValueChange = { modeCulture = it },
                label = { Text("Mode de culture") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = surfaceTotale,
                onValueChange = { surfaceTotale = it },
                label = { Text("Surface totale (ha)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = codePostal,
                onValueChange = { codePostal = it },
                label = { Text("Code postal") },
                modifier = Modifier.fillMaxWidth()
            )

            // TODO: Ajouter les champs pour les certifications

            Button(
                onClick = {
                    scope.launch {
                        if (infoId == null) {
                            viewModel.addInformationsGenerales(
                                nomDomaine = nomDomaine,
                                modeCulture = modeCulture,
                                surfaceTotale = surfaceTotale.toFloatOrNull() ?: 0f,
                                codePostal = codePostal,
                                certifications = certifications
                            )
                        } else {
                            viewModel.updateInformationsGenerales(
                                id = infoId,
                                nomDomaine = nomDomaine,
                                modeCulture = modeCulture,
                                surfaceTotale = surfaceTotale.toFloatOrNull() ?: 0f,
                                codePostal = codePostal,
                                certifications = certifications
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (infoId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 