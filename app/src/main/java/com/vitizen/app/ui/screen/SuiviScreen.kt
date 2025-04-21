package com.vitizen.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitizen.app.ui.viewmodel.SuiviViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuiviScreen(
    viewModel: SuiviViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.treatments) { treatment ->
            TreatmentCard(
                treatment = treatment,
                onDelete = { viewModel.showDeleteConfirmation(treatment.id) }
            )
        }
    }

    // Boîte de dialogue de confirmation
    if (uiState.treatmentToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmation() },
            title = { Text("Confirmation de suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce traitement ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        uiState.treatmentToDelete?.let { id ->
                            viewModel.deleteTreatment(id)
                            viewModel.hideDeleteConfirmation()
                        }
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteConfirmation() }
                ) {
                    Text("Annuler")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TreatmentCard(
    treatment: SuiviViewModel.TreatmentCardState,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = treatment.date,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Type: ${treatment.type}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Produit: ${treatment.product}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Surface: ${treatment.surface} ha",
                style = MaterialTheme.typography.bodyMedium
            )

            treatment.comment?.let { comment ->
                if (comment.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Commentaire: $comment",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
} 