package com.vitizen.app.domain.model

data class Parcelle(
    val id: String = "",
    val name: String = "",
    val surface: Double = 0.0,
    val cepage: String = "",
    val typeConduite: String = "",
    val largeur: Double = 0.0,
    val hauteur: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) 