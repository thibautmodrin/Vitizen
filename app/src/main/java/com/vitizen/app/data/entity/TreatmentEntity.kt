package com.vitizen.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "treatments")
data class TreatmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String?,
    val doctor: String,
    val notes: String?,
    val isCompleted: Boolean = false
) 