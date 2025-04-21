package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.ParametresViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperateurForm(
    viewModel: ParametresViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    operateurId: Long? = null
) {
    var nom by remember { mutableStateOf("") }
    var disponibleWeekend by remember { mutableStateOf(false) }
    var diplomes by remember { mutableStateOf(listOf<String>()) }
    var materielMaitrise by remember { mutableStateOf(listOf<String>()) }


    LaunchedEffect(operateurId) {
        operateurId?.let { id ->
            viewModel.getOperateurById(id)?.let { operateur ->
                nom = operateur.nom
                disponibleWeekend = operateur.disponibleWeekend
                diplomes = operateur.diplomes
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
                onValueChange = { nom = it },
                label = { Text("Nom de l'opérateur") },
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

            Text("Diplômes", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            diplomes.forEachIndexed { index, diplome ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = diplome,
                        onValueChange = { newValue ->
                            val newDiplomes = diplomes.toMutableList()
                            newDiplomes[index] = newValue
                            diplomes = newDiplomes
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val newDiplomes = diplomes.toMutableList()
                            newDiplomes.removeAt(index)
                            diplomes = newDiplomes
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { diplomes = diplomes + "" },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter un diplôme")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Matériels maîtrisés", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            materielMaitrise.forEachIndexed { index, materiel ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = materiel,
                        onValueChange = { newValue ->
                            val newMateriels = materielMaitrise.toMutableList()
                            newMateriels[index] = newValue
                            materielMaitrise = newMateriels
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            val newMateriels = materielMaitrise.toMutableList()
                            newMateriels.removeAt(index)
                            materielMaitrise = newMateriels
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { materielMaitrise = materielMaitrise + "" },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ajouter un matériel")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (operateurId == null) {
                        viewModel.addOperateur(
                            nom = nom,
                            disponibleWeekend = disponibleWeekend,
                            diplomes = diplomes.filter { it.isNotBlank() },
                            materielMaitrise = materielMaitrise.filter { it.isNotBlank() }
                        )
                    } else {
                        viewModel.updateOperateur(
                            id = operateurId,
                            nom = nom,
                            disponibleWeekend = disponibleWeekend,
                            diplomes = diplomes.filter { it.isNotBlank() },
                            materielMaitrise = materielMaitrise.filter { it.isNotBlank() }
                        )
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nom.isNotBlank()
            ) {
                Text(if (operateurId == null) "Ajouter" else "Modifier")
            }
        }
    }
} 