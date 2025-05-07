package com.vitizen.app.presentation.screen.home.section.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.data.local.entity.ParcelleEntity
import com.vitizen.app.domain.model.Parcelle
import kotlinx.coroutines.launch
import com.vitizen.app.presentation.components.OsmMapPicker
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelleForm(
    parcelle: Parcelle? = null,
    onSave: (Parcelle) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(parcelle?.name ?: "") }
    var surface by remember { mutableStateOf(parcelle?.surface?.toString() ?: "") }
    var cepage by remember { mutableStateOf(parcelle?.cepage ?: "") }
    var typeConduite by remember { mutableStateOf(parcelle?.typeConduite ?: "") }
    var largeur by remember { mutableStateOf(parcelle?.largeur?.toString() ?: "") }
    var hauteur by remember { mutableStateOf(parcelle?.hauteur?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (parcelle == null) "Nouvelle parcelle" else "Modifier la parcelle") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la parcelle") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = surface,
                    onValueChange = { surface = it },
                    label = { Text("Surface (ha)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cepage,
                    onValueChange = { cepage = it },
                    label = { Text("Cépage") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = typeConduite,
                    onValueChange = { typeConduite = it },
                    label = { Text("Type de conduite") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = largeur,
                    onValueChange = { largeur = it },
                    label = { Text("Largeur (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hauteur,
                    onValueChange = { hauteur = it },
                    label = { Text("Hauteur (m)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Parcelle(
                            id = parcelle?.id ?: UUID.randomUUID().toString(),
                            name = name,
                            surface = surface.toDoubleOrNull() ?: 0.0,
                            cepage = cepage,
                            typeConduite = typeConduite,
                            largeur = largeur.toDoubleOrNull() ?: 0.0,
                            hauteur = hauteur.toDoubleOrNull() ?: 0.0,
                            latitude = parcelle?.latitude ?: 0.0,
                            longitude = parcelle?.longitude ?: 0.0
                        )
                    )
                }
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
} 