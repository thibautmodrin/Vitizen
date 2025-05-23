package com.vitizen.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "informations_generales")
data class InformationsGeneralesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nomDomaine: String,
    val modeCulture: String,
    val certifications: List<String>,
    val surfaceTotale: Float,
    val codePostal: String
) 