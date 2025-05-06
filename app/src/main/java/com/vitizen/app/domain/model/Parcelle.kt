package com.vitizen.app.domain.model

data class Parcelle(
    val id: Long = 0,
    val nom: String,
    val surface: Float,
    val cepage: String,
    val anneePlantation: Int,
    val typeConduite: String,
    val largeurInterrang: Float?,
    val hauteurFeuillage: Float?,
    val accessibleMateriel: List<String>,
    val zoneSensible: Boolean,
    val zoneHumide: Boolean,
    val drainage: Boolean,
    val enherbement: Boolean,
    val pente: String,
    val typeSol: String,
    val inondable: Boolean,
    val latitude: Double?,
    val longitude: Double?
) 