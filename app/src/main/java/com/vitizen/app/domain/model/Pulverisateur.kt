package com.vitizen.app.domain.model

data class Pulverisateur(
    val id: Long = 0,
    val nomMateriel: String,
    val modeDeplacement: String,
    val nombreRampes: Int?,
    val nombreBusesParRampe: Int?,
    val typeBuses: String,
    val pressionPulverisation: Float?,
    val debitParBuse: Float?,
    val anglePulverisation: Int?,
    val largeurTraitement: Float?,
    val plageVitesseAvancementMin: Float?,
    val plageVitesseAvancementMax: Float?,
    val volumeTotalCuve: Int?
) 