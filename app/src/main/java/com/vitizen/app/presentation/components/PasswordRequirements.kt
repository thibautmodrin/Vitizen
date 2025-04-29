package com.vitizen.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PasswordRequirements(
    password: String,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    // Ne pas afficher si le champ n'est pas focus et que le mot de passe est vide
    if (!isFocused && password.isBlank()) {
        return
    }

    val minLength = 6
    val hasMinLength = password.length >= minLength
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }

    // Vérifier si toutes les exigences sont satisfaites
    val allRequirementsMet = hasMinLength && hasUpperCase && hasLowerCase && hasDigit

    // Ne pas afficher si toutes les exigences sont satisfaites
    if (allRequirementsMet) {
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Le mot de passe doit contenir :",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        RequirementRow(
            isValid = hasMinLength,
            text = "Au moins $minLength caractères"
        )
        RequirementRow(
            isValid = hasUpperCase,
            text = "Au moins une majuscule"
        )
        RequirementRow(
            isValid = hasLowerCase,
            text = "Au moins une minuscule"
        )
        RequirementRow(
            isValid = hasDigit,
            text = "Au moins un chiffre"
        )
    }
}

@Composable
private fun RequirementRow(
    isValid: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
            contentDescription = if (isValid) "Validé" else "Non validé",
            tint = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
} 