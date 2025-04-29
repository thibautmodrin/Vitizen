package com.vitizen.app.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificationSelector(
    selected: List<String>,
    onChange: (List<String>) -> Unit
) {
    val options = listOf("AB", "HVE", "Terra Vitis", "Demeter", "Zéro Résidu de Pesticide")
    
    MultiSelectDropdown(
        label = "Certifications",
        options = options,
        selected = selected,
        onSelectionChange = onChange,
        optionToString = { it }
    )
} 