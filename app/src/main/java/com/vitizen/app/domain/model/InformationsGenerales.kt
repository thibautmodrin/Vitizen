package com.vitizen.app.domain.model

data class InformationsGenerales(
    val id: Long = 0,
    val nomDomaine: String,
    val modeCulture: String,
    val certifications: List<String>,
    val surfaceTotale: Float,
    val codePostal: String
) 