package com.vitizen.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultiSelectDropdown(
    label: String,
    options: List<T>,
    selected: List<T>,
    onSelectionChange: (List<T>) -> Unit,
    optionToString: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = if (selected.isEmpty()) "Aucune" else selected.joinToString(", ") { optionToString(it) },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { 
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = option in selected,
                                onCheckedChange = null // géré par DropdownMenuItem
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(optionToString(option))
                        }
                    },
                    onClick = {
                        val newSelection = if (option in selected)
                            selected - option
                        else
                            selected + option
                        onSelectionChange(newSelection)
                    }
                )
            }
        }
    }
} 