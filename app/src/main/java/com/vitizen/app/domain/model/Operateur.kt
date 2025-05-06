package com.vitizen.app.domain.model

data class Operateur(
    val id: Long = 0,
    val nom: String,
    val disponibleWeekend: Boolean,
    val diplomes: List<String>,
    val materielMaitrise: List<String>
) 