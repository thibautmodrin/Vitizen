package com.vitizen.app.ui.screen

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
import com.vitizen.app.ui.viewmodel.ParametresViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulverisateurForm(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    pulverisateurId: Long? = null
) {
    var nomMateriel by remember { mutableStateOf("") }
    var modeDeplacement by remember { mutableStateOf("") }
    var nombreRampes by remember { mutableStateOf("") }
    var nombreBusesParRampe by remember { mutableStateOf("") }
    var typeBuses by remember { mutableStateOf("") }
    var pressionPulverisation by remember { mutableStateOf("") }
    var debitParBuse by remember { mutableStateOf("") }
    var anglePulverisation by remember { mutableStateOf("") }
    var largeurTraitement by remember { mutableStateOf("") }
    var plageVitesseAvancementMin by remember { mutableStateOf("") }
    var plageVitesseAvancementMax by remember { mutableStateOf("") }
    var volumeTotalCuve by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(pulverisateurId) {
        pulverisateurId?.let { id ->
            viewModel.getPulverisateurById(id)?.let { pulverisateur ->
                nomMateriel = pulverisateur.nomMateriel
                modeDeplacement = pulverisateur.modeDeplacement
                nombreRampes = pulverisateur.nombreRampes?.toString() ?: ""
                nombreBusesParRampe = pulverisateur.nombreBusesParRampe?.toString() ?: ""
                typeBuses = pulverisateur.typeBuses
                pressionPulverisation = pulverisateur.pressionPulverisation?.toString() ?: ""
                debitParBuse = pulverisateur.debitParBuse?.toString() ?: ""
                anglePulverisation = pulverisateur.anglePulverisation?.toString() ?: ""
                largeurTraitement = pulverisateur.largeurTraitement?.toString() ?: ""
                plageVitesseAvancementMin = pulverisateur.plageVitesseAvancementMin?.toString() ?: ""
                plageVitesseAvancementMax = pulverisateur.plageVitesseAvancementMax?.toString() ?: ""
                volumeTotalCuve = pulverisateur.volumeTotalCuve?.toString() ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (pulverisateurId == null) "Ajouter un pulvérisateur" else "Modifier le pulvérisateur") },
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
                value = nomMateriel,
                onValueChange = { nomMateriel = it },
                label = { Text("Nom du matériel") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = modeDeplacement,
                onValueChange = { modeDeplacement = it },
                label = { Text("Mode de déplacement") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreRampes,
                onValueChange = { nombreRampes = it },
                label = { Text("Nombre de rampes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombreBusesParRampe,
                onValueChange = { nombreBusesParRampe = it },
                label = { Text("Nombre de buses par rampe") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = typeBuses,
                onValueChange = { typeBuses = it },
                label = { Text("Type de buses") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pressionPulverisation,
                onValueChange = { pressionPulverisation = it },
                label = { Text("Pression de pulvérisation (bar)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = debitParBuse,
                onValueChange = { debitParBuse = it },
                label = { Text("Débit par buse (l/min)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = anglePulverisation,
                onValueChange = { anglePulverisation = it },
                label = { Text("Angle de pulvérisation (°)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = largeurTraitement,
                onValueChange = { largeurTraitement = it },
                label = { Text("Largeur de traitement (m)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plageVitesseAvancementMin,
                onValueChange = { plageVitesseAvancementMin = it },
                label = { Text("Vitesse d'avancement min (km/h)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = plageVitesseAvancementMax,
                onValueChange = { plageVitesseAvancementMax = it },
                label = { Text("Vitesse d'avancement max (km/h)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = volumeTotalCuve,
                onValueChange = { volumeTotalCuve = it },
                label = { Text("Volume total de la cuve (l)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (pulverisateurId == null) {
                            viewModel.addPulverisateur(
                                nomMateriel = nomMateriel,
                                modeDeplacement = modeDeplacement,
                                nombreRampes = nombreRampes.toIntOrNull(),
                                nombreBusesParRampe = nombreBusesParRampe.toIntOrNull(),
                                typeBuses = typeBuses,
                                pressionPulverisation = pressionPulverisation.toFloatOrNull(),
                                debitParBuse = debitParBuse.toFloatOrNull(),
                                anglePulverisation = anglePulverisation.toIntOrNull(),
                                largeurTraitement = largeurTraitement.toFloatOrNull(),
                                plageVitesseAvancementMin = plageVitesseAvancementMin.toFloatOrNull(),
                                plageVitesseAvancementMax = plageVitesseAvancementMax.toFloatOrNull(),
                                volumeTotalCuve = volumeTotalCuve.toIntOrNull()
                            )
                        } else {
                            viewModel.updatePulverisateur(
                                id = pulverisateurId,
                                nomMateriel = nomMateriel,
                                modeDeplacement = modeDeplacement,
                                nombreRampes = nombreRampes.toIntOrNull(),
                                nombreBusesParRampe = nombreBusesParRampe.toIntOrNull(),
                                typeBuses = typeBuses,
                                pressionPulverisation = pressionPulverisation.toFloatOrNull(),
                                debitParBuse = debitParBuse.toFloatOrNull(),
                                anglePulverisation = anglePulverisation.toIntOrNull(),
                                largeurTraitement = largeurTraitement.toFloatOrNull(),
                                plageVitesseAvancementMin = plageVitesseAvancementMin.toFloatOrNull(),
                                plageVitesseAvancementMax = plageVitesseAvancementMax.toFloatOrNull(),
                                volumeTotalCuve = volumeTotalCuve.toIntOrNull()
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nomMateriel.isNotBlank() && modeDeplacement.isNotBlank() && typeBuses.isNotBlank()
            ) {
                Text(if (pulverisateurId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 