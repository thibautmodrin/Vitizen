package com.vitizen.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vitizen.app.data.local.converter.GeoPointListConverter
import org.osmdroid.util.GeoPoint

@Entity(tableName = "parcelles")
@TypeConverters(GeoPointListConverter::class)
data class ParcelleEntity(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val surface: Double = 0.0,
    val cepage: String = "",
    val typeConduite: String = "",
    val largeur: Double = 0.0,
    val hauteur: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val polygonPoints: List<GeoPoint> = emptyList()
) 