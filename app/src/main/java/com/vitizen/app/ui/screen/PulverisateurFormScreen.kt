package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.ParametresViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulverisateurFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: ParametresViewModel = hiltViewModel(),
    initialPulverisateur: ParametresViewModel.PulverisateurInfo? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // État local pour le formulaire
    var nom by remember { mutableStateOf(initialPulverisateur?.nom ?: "") }
    var typePulverisateur by remember { mutableStateOf(initialPulverisateur?.typePulverisateur) }
    var typePulverisation by remember { mutableStateOf(initialPulverisateur?.typePulverisation) }
    var modeleMarque by remember { mutableStateOf(initialPulverisateur?.modeleMarque ?: "") }
    var pression by remember { mutableStateOf(initialPulverisateur?.pression ?: "") }
    var debit by remember { mutableStateOf(initialPulverisateur?.debit ?: "") }
    var uniteDebit by remember { mutableStateOf(initialPulverisateur?.uniteDebit ?: ParametresViewModel.UniteDebit.L_MIN) }
    var nombreRangs by remember { mutableStateOf(initialPulverisateur?.nombreRangs ?: "") }
    var volumeBacPrincipal by remember { mutableStateOf(initialPulverisateur?.volumeBacPrincipal ?: "") }
    var volumeBacSecondaire by remember { mutableStateOf(initialPulverisateur?.volumeBacSecondaire ?: "") }
    var volumeBacRinçage by remember { mutableStateOf(initialPulverisateur?.volumeBacRinçage ?: "") }
    var largeurRampe by remember { mutableStateOf(initialPulverisateur?.largeurRampe ?: "") }
    var surfaceMoyenne by remember { mutableStateOf(initialPulverisateur?.surfaceMoyenne ?: "") }
    var systemeRinçage by remember { mutableStateOf(initialPulverisateur?.systemeRinçage) }
    var systemeGPS by remember { mutableStateOf(initialPulverisateur?.systemeGPS) }
    var typeBuse by remember { mutableStateOf(initialPulverisateur?.typeBuse) }
    var nombreBuses by remember { mutableStateOf(initialPulverisateur?.nombreBuses ?: "") }
    var anglePulverisation by remember { mutableStateOf(initialPulverisateur?.anglePulverisation ?: "") }
    var codeCouleurISO by remember { mutableStateOf(initialPulverisateur?.codeCouleurISO) }

    // États pour les menus déroulants
    var typePulverisateurExpanded by remember { mutableStateOf(false) }
    var typePulverisationExpanded by remember { mutableStateOf(false) }
    var uniteDebitExpanded by remember { mutableStateOf(false) }
    var systemeRincageExpanded by remember { mutableStateOf(false) }
    var systemeGPSExpanded by remember { mutableStateOf(false) }
    var typeBuseExpanded by remember { mutableStateOf(false) }
    var codeCouleurISOExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (initialPulverisateur == null) "Ajouter un pulvérisateur" else "Modifier le pulvérisateur") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (nom.isBlank()) {
                                Toast.makeText(context, "Le nom du pulvérisateur est obligatoire pour enregistrer", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            
                            val pulverisateur = if (initialPulverisateur != null) {
                                // Mise à jour : on garde le même objet avec les nouvelles valeurs
                                initialPulverisateur.copy(
                                    nom = nom,
                                    typePulverisateur = typePulverisateur,
                                    typePulverisation = typePulverisation,
                                    modeleMarque = modeleMarque,
                                    pression = pression,
                                    debit = debit,
                                    uniteDebit = uniteDebit,
                                    nombreRangs = nombreRangs,
                                    volumeBacPrincipal = volumeBacPrincipal,
                                    volumeBacSecondaire = volumeBacSecondaire,
                                    volumeBacRinçage = volumeBacRinçage,
                                    largeurRampe = largeurRampe,
                                    surfaceMoyenne = surfaceMoyenne,
                                    systemeRinçage = systemeRinçage,
                                    systemeGPS = systemeGPS,
                                    typeBuse = typeBuse,
                                    nombreBuses = nombreBuses,
                                    anglePulverisation = anglePulverisation,
                                    codeCouleurISO = codeCouleurISO
                                )
                            } else {
                                // Création : nouveau pulvérisateur
                                ParametresViewModel.PulverisateurInfo(
                                    nom = nom,
                                    typePulverisateur = typePulverisateur,
                                    typePulverisation = typePulverisation,
                                    modeleMarque = modeleMarque,
                                    pression = pression,
                                    debit = debit,
                                    uniteDebit = uniteDebit,
                                    nombreRangs = nombreRangs,
                                    volumeBacPrincipal = volumeBacPrincipal,
                                    volumeBacSecondaire = volumeBacSecondaire,
                                    volumeBacRinçage = volumeBacRinçage,
                                    largeurRampe = largeurRampe,
                                    surfaceMoyenne = surfaceMoyenne,
                                    systemeRinçage = systemeRinçage,
                                    systemeGPS = systemeGPS,
                                    typeBuse = typeBuse,
                                    nombreBuses = nombreBuses,
                                    anglePulverisation = anglePulverisation,
                                    codeCouleurISO = codeCouleurISO
                                )
                            }

                            if (initialPulverisateur != null) {
                                viewModel.updatePulverisateur(initialPulverisateur.nom, pulverisateur)
                            } else {
                                viewModel.addPulverisateur(pulverisateur)
                            }
                            onNavigateBack()
                        }
                    ) {
                        Icon(Icons.Default.Save, "Enregistrer")
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
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom du pulvérisateur") },
                modifier = Modifier.fillMaxWidth()
            )

            // Type de pulvérisateur
            ExposedDropdownMenuBox(
                expanded = typePulverisateurExpanded,
                onExpandedChange = { typePulverisateurExpanded = it }
            ) {
                OutlinedTextField(
                    value = typePulverisateur?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Type de pulvérisateur") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typePulverisateurExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = typePulverisateurExpanded,
                    onDismissRequest = { typePulverisateurExpanded = false }
                ) {
                    ParametresViewModel.TypePulverisateur.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = { 
                                typePulverisateur = type
                                typePulverisateurExpanded = false
                            }
                        )
                    }
                }
            }

            // Type de pulvérisation
            ExposedDropdownMenuBox(
                expanded = typePulverisationExpanded,
                onExpandedChange = { typePulverisationExpanded = it }
            ) {
                OutlinedTextField(
                    value = typePulverisation?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Type de pulvérisation") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typePulverisationExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = typePulverisationExpanded,
                    onDismissRequest = { typePulverisationExpanded = false }
                ) {
                    ParametresViewModel.TypePulverisation.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = { 
                                typePulverisation = type
                                typePulverisationExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = modeleMarque,
                onValueChange = { modeleMarque = it },
                label = { Text("Modèle & Marque") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pression,
                onValueChange = { pression = it },
                label = { Text("Pression (bar)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = debit,
                    onValueChange = { debit = it },
                    label = { Text("Débit") },
                    modifier = Modifier.weight(1f)
                )
                ExposedDropdownMenuBox(
                    expanded = uniteDebitExpanded,
                    onExpandedChange = { uniteDebitExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uniteDebit.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Unité") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uniteDebitExpanded) },
                        modifier = Modifier.weight(1f).menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = uniteDebitExpanded,
                        onDismissRequest = { uniteDebitExpanded = false }
                    ) {
                        ParametresViewModel.UniteDebit.values().forEach { unite ->
                            DropdownMenuItem(
                                text = { Text(unite.name) },
                                onClick = { 
                                    uniteDebit = unite
                                    uniteDebitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = nombreRangs,
                onValueChange = { nombreRangs = it },
                label = { Text("Nombre de rangs traités") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = largeurRampe,
                onValueChange = { largeurRampe = it },
                label = { Text("Largeur de rampe (m)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = surfaceMoyenne,
                onValueChange = { surfaceMoyenne = it },
                label = { Text("Surface moyenne traitée (ha)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Système de rinçage
            ExposedDropdownMenuBox(
                expanded = systemeRincageExpanded,
                onExpandedChange = { systemeRincageExpanded = it }
            ) {
                OutlinedTextField(
                    value = systemeRinçage?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Système de rinçage") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = systemeRincageExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = systemeRincageExpanded,
                    onDismissRequest = { systemeRincageExpanded = false }
                ) {
                    ParametresViewModel.SystemeRinçage.values().forEach { systeme ->
                        DropdownMenuItem(
                            text = { Text(systeme.name) },
                            onClick = { 
                                systemeRinçage = systeme
                                systemeRincageExpanded = false
                            }
                        )
                    }
                }
            }

            // Système GPS
            ExposedDropdownMenuBox(
                expanded = systemeGPSExpanded,
                onExpandedChange = { systemeGPSExpanded = it }
            ) {
                OutlinedTextField(
                    value = systemeGPS?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Système GPS") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = systemeGPSExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = systemeGPSExpanded,
                    onDismissRequest = { systemeGPSExpanded = false }
                ) {
                    ParametresViewModel.SystemeGPS.values().forEach { systeme ->
                        DropdownMenuItem(
                            text = { Text(systeme.name) },
                            onClick = { 
                                systemeGPS = systeme
                                systemeGPSExpanded = false
                            }
                        )
                    }
                }
            }

            // Type de buse
            ExposedDropdownMenuBox(
                expanded = typeBuseExpanded,
                onExpandedChange = { typeBuseExpanded = it }
            ) {
                OutlinedTextField(
                    value = typeBuse?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Type de buse") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeBuseExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = typeBuseExpanded,
                    onDismissRequest = { typeBuseExpanded = false }
                ) {
                    ParametresViewModel.TypeBuse.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = { 
                                typeBuse = type
                                typeBuseExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = nombreBuses,
                onValueChange = { nombreBuses = it },
                label = { Text("Nombre de buses") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = anglePulverisation,
                onValueChange = { anglePulverisation = it },
                label = { Text("Angle de pulvérisation (°)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Code couleur ISO
            ExposedDropdownMenuBox(
                expanded = codeCouleurISOExpanded,
                onExpandedChange = { codeCouleurISOExpanded = it }
            ) {
                OutlinedTextField(
                    value = codeCouleurISO?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Code couleur ISO") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = codeCouleurISOExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = codeCouleurISOExpanded,
                    onDismissRequest = { codeCouleurISOExpanded = false }
                ) {
                    ParametresViewModel.CodeCouleurISO.values().forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code.name) },
                            onClick = { 
                                codeCouleurISO = code
                                codeCouleurISOExpanded = false
                            }
                        )
                    }
                }
            }

            // Volumes des bacs
            OutlinedTextField(
                value = volumeBacPrincipal,
                onValueChange = { volumeBacPrincipal = it },
                label = { Text("Volume bac principal (L)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = volumeBacSecondaire,
                onValueChange = { volumeBacSecondaire = it },
                label = { Text("Volume bac secondaire (L)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = volumeBacRinçage,
                onValueChange = { volumeBacRinçage = it },
                label = { Text("Volume bac rinçage (L)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
} 