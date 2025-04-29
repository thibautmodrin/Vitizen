package com.vitizen.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vitizen.app.data.local.converter.StringListConverter

@Entity(tableName = "parcelles")
@TypeConverters(StringListConverter::class)
data class ParcelleEntity(
    @PrimaryKey(autoGenerate = true)
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