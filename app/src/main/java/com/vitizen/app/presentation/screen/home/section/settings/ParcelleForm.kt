package com.vitizen.app.presentation.screen.home.section.settings

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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelleForm(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    parcelleId: Long? = null
) {
    var nom by remember { mutableStateOf("") }
    var surface by remember { mutableStateOf("") }
    var cepage by remember { mutableStateOf("") }
    var anneePlantation by remember { mutableStateOf("") }
    var typeConduite by remember { mutableStateOf("") }
    var largeurInterrang by remember { mutableStateOf("") }
    var hauteurFeuillage by remember { mutableStateOf("") }
    var accessibleMateriel by remember { mutableStateOf(listOf<String>()) }
    var zoneSensible by remember { mutableStateOf(false) }
    var zoneHumide by remember { mutableStateOf(false) }
    var drainage by remember { mutableStateOf(false) }
    var enherbement by remember { mutableStateOf(false) }
    var pente by remember { mutableStateOf("") }
    var typeSol by remember { mutableStateOf("") }
    var inondable by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(parcelleId) {
        parcelleId?.let { id ->
            viewModel.getParcelleById(id)?.let { parcelle ->
                nom = parcelle.nom
                surface = parcelle.surface.toString()
                cepage = parcelle.cepage
                anneePlantation = parcelle.anneePlantation.toString()
                typeConduite = parcelle.typeConduite
                largeurInterrang = parcelle.largeurInterrang?.toString() ?: ""
                hauteurFeuillage = parcelle.hauteurFeuillage?.toString() ?: ""
                accessibleMateriel = parcelle.accessibleMateriel
                zoneSensible = parcelle.zoneSensible
                zoneHumide = parcelle.zoneHumide
                drainage = parcelle.drainage
                enherbement = parcelle.enherbement
                pente = parcelle.pente
                typeSol = parcelle.typeSol
                inondable = parcelle.inondable
                latitude = parcelle.latitude?.toString() ?: ""
                longitude = parcelle.longitude?.toString() ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (parcelleId == null) "Ajouter une parcelle" else "Modifier la parcelle") },
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
                onValueChange = { nom = it },
                label = { Text("Nom de la parcelle *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && nom.isBlank(),
                supportingText = {
                    if (showErrors && nom.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = surface,
                onValueChange = { surface = it },
                label = { Text("Surface (ha) *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && surface.isBlank(),
                supportingText = {
                    if (showErrors && surface.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = cepage,
                onValueChange = { cepage = it },
                label = { Text("Cépage *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && cepage.isBlank(),
                supportingText = {
                    if (showErrors && cepage.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = anneePlantation,
                onValueChange = { anneePlantation = it },
                label = { Text("Année de plantation *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && anneePlantation.isBlank(),
                supportingText = {
                    if (showErrors && anneePlantation.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = typeConduite,
                onValueChange = { typeConduite = it },
                label = { Text("Type de conduite *") },
                modifier = Modifier.fillMaxWidth(),
                isError = showErrors && typeConduite.isBlank(),
                supportingText = {
                    if (showErrors && typeConduite.isBlank()) {
                        Text("Ce champ est obligatoire", color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = largeurInterrang,
                onValueChange = { largeurInterrang = it },
                label = { Text("Largeur interrang (m)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = hauteurFeuillage,
                onValueChange = { hauteurFeuillage = it },
                label = { Text("Hauteur du feuillage (m)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TODO: Ajouter un composant pour sélectionner les matériels accessibles

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = zoneSensible,
                    onCheckedChange = { zoneSensible = it },
                    modifier = Modifier.weight(1f)
                )
                Text("Zone sensible", modifier = Modifier.weight(3f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = zoneHumide,
                    onCheckedChange = { zoneHumide = it },
                    modifier = Modifier.weight(1f)
                )
                Text("Zone humide", modifier = Modifier.weight(3f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = drainage,
                    onCheckedChange = { drainage = it },
                    modifier = Modifier.weight(1f)
                )
                Text("Drainage", modifier = Modifier.weight(3f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = enherbement,
                    onCheckedChange = { enherbement = it },
                    modifier = Modifier.weight(1f)
                )
                Text("Enherbement", modifier = Modifier.weight(3f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TODO: Ajouter un composant pour sélectionner la pente

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = typeSol,
                onValueChange = { typeSol = it },
                label = { Text("Type de sol") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    checked = inondable,
                    onCheckedChange = { inondable = it },
                    modifier = Modifier.weight(1f)
                )
                Text("Inondable", modifier = Modifier.weight(3f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "* Champs obligatoires",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = {
                    if (nom.isBlank() || surface.isBlank() || cepage.isBlank() || 
                        anneePlantation.isBlank() || typeConduite.isBlank()) {
                        showErrors = true
                    } else {
                        scope.launch {
                            if (parcelleId == null) {
                                viewModel.addParcelle(
                                    nom = nom,
                                    surface = surface.toFloat(),
                                    cepage = cepage,
                                    anneePlantation = anneePlantation.toInt(),
                                    typeConduite = typeConduite,
                                    largeurInterrang = largeurInterrang.toFloatOrNull(),
                                    hauteurFeuillage = hauteurFeuillage.toFloatOrNull(),
                                    accessibleMateriel = accessibleMateriel,
                                    zoneSensible = zoneSensible,
                                    zoneHumide = zoneHumide,
                                    drainage = drainage,
                                    enherbement = enherbement,
                                    pente = pente,
                                    typeSol = typeSol,
                                    inondable = inondable,
                                    latitude = latitude.toDoubleOrNull(),
                                    longitude = longitude.toDoubleOrNull()
                                )
                            } else {
                                viewModel.updateParcelle(
                                    id = parcelleId,
                                    nom = nom,
                                    surface = surface.toFloat(),
                                    cepage = cepage,
                                    anneePlantation = anneePlantation.toInt(),
                                    typeConduite = typeConduite,
                                    largeurInterrang = largeurInterrang.toFloatOrNull(),
                                    hauteurFeuillage = hauteurFeuillage.toFloatOrNull(),
                                    accessibleMateriel = accessibleMateriel,
                                    zoneSensible = zoneSensible,
                                    zoneHumide = zoneHumide,
                                    drainage = drainage,
                                    enherbement = enherbement,
                                    pente = pente,
                                    typeSol = typeSol,
                                    inondable = inondable,
                                    latitude = latitude.toDoubleOrNull(),
                                    longitude = longitude.toDoubleOrNull()
                                )
                            }
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (parcelleId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 