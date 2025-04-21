package com.vitizen.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "treatments")
data class Treatment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Date,
    val type: String,
    val product: String,
    val surface: Double,
    val comment: String?,
    val createdAt: Date = Date()
) 