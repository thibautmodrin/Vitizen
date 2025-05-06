package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.presentation.components.CertificationSelector
import com.vitizen.app.presentation.components.MultiSelectDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationsGeneralesForm(
    informationsId: Long? = null,
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val modeCultureOptions = listOf("Conventionnel", "Bio", "HVE", "Zéro phyto")

    var nomDomaine by remember { mutableStateOf("") }
    var modeCulture by remember { mutableStateOf("") }
    var certifications by remember { mutableStateOf<List<String>>(emptyList()) }
    var surfaceTotale by remember { mutableStateOf("") }
    var codePostal by remember { mutableStateOf("") }

    var nomDomaineError by remember { mutableStateOf(false) }
    var modeCultureError by remember { mutableStateOf(false) }
    var surfaceTotaleError by remember { mutableStateOf(false) }
    var codePostalError by remember { mutableStateOf(false) }
    var surfaceTotaleErrorMessage by remember { mutableStateOf("") }

    var modeCultureExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(informationsId) {
        informationsId?.let { id ->
            viewModel.getInformationsGeneralesById(id)?.let { informations ->
                nomDomaine = informations.nomDomaine
                modeCulture = informations.modeCulture
                certifications = informations.certifications
                surfaceTotale = informations.surfaceTotale.toString()
                codePostal = informations.codePostal
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (informationsId == null) "Ajouter des informations" else "Modifier les informations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nomDomaine,
                onValueChange = { nomDomaine = it; nomDomaineError = false },
                label = { Text("Nom du domaine") },
                isError = nomDomaineError,
                supportingText = { if (nomDomaineError) Text("Le nom du domaine est requis") },
                modifier = Modifier.fillMaxWidth()
            )

            // Mode de culture
            MultiSelectDropdown(
                label = "Mode de culture",
                options = modeCultureOptions,
                selected = if (modeCulture.isBlank()) emptyList() else listOf(modeCulture),
                onSelectionChange = { newSelection ->
                    modeCulture = newSelection.firstOrNull() ?: ""
                    modeCultureError = false
                },
                optionToString = { it },
                modifier = Modifier.fillMaxWidth()
            )

            // Certifications - Menu déroulant avec checkboxes
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Certifications",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                CertificationSelector(
                    selected = certifications,
                    onChange = { certifications = it }
                )
            }

            OutlinedTextField(
                value = surfaceTotale,
                onValueChange = { 
                    surfaceTotale = it
                    surfaceTotaleError = false
                    surfaceTotaleErrorMessage = ""
                },
                label = { Text("Surface totale (ha)") },
                isError = surfaceTotaleError,
                supportingText = { 
                    if (surfaceTotaleError) {
                        Text(surfaceTotaleErrorMessage)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = codePostal,
                onValueChange = { codePostal = it; codePostalError = false },
                label = { Text("Code postal") },
                isError = codePostalError,
                supportingText = { if (codePostalError) Text("Le code postal est requis") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    var hasError = false
                    if (nomDomaine.isBlank()) {
                        nomDomaineError = true
                        hasError = true
                    }
                    if (modeCulture.isBlank()) {
                        modeCultureError = true
                        hasError = true
                    }
                    if (surfaceTotale.isBlank()) {
                        surfaceTotaleError = true
                        surfaceTotaleErrorMessage = "La surface totale est requise"
                        hasError = true
                    } else {
                        try {
                            val surfaceValue = surfaceTotale.toFloat()
                            if (surfaceValue <= 0) {
                                surfaceTotaleError = true
                                surfaceTotaleErrorMessage = "La surface doit être supérieure à 0"
                                hasError = true
                            }
                        } catch (e: NumberFormatException) {
                            surfaceTotaleError = true
                            surfaceTotaleErrorMessage = "Veuillez entrer un nombre valide"
                            hasError = true
                        }
                    }
                    if (codePostal.isBlank()) {
                        codePostalError = true
                        hasError = true
                    }

                    if (!hasError) {
                        try {
                            val surfaceValue = surfaceTotale.toFloat()
                            if (informationsId == null) {
                                viewModel.addInformationsGenerales(
                                    nomDomaine = nomDomaine,
                                    modeCulture = modeCulture,
                                    certifications = certifications,
                                    surfaceTotale = surfaceValue,
                                    codePostal = codePostal
                                )
                            } else {
                                viewModel.updateInformationsGenerales(
                                    id = informationsId,
                                    nomDomaine = nomDomaine,
                                    modeCulture = modeCulture,
                                    surfaceTotale = surfaceValue,
                                    codePostal = codePostal,
                                    certifications = certifications
                                )
                            }
                            onNavigateBack()
                        } catch (e: NumberFormatException) {
                            surfaceTotaleError = true
                            surfaceTotaleErrorMessage = "Veuillez entrer un nombre valide"
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (informationsId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 