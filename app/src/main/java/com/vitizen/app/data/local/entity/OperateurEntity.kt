package com.vitizen.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vitizen.app.data.local.converter.StringListConverter

@Entity(tableName = "operateurs")
@TypeConverters(StringListConverter::class)
data class OperateurEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val disponibleWeekend: Boolean,
    val diplomes: List<String>,
    val materielMaitrise: List<String>
) 