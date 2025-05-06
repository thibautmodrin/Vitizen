package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.Pulverisateur
import kotlinx.coroutines.flow.Flow

interface IPulverisateurRepository {
    fun getAllPulverisateurs(): Flow<List<Pulverisateur>>
    suspend fun getPulverisateurById(id: Long): Pulverisateur?
    suspend fun addPulverisateur(pulverisateur: Pulverisateur): Long
    suspend fun updatePulverisateur(pulverisateur: Pulverisateur)
    suspend fun deletePulverisateur(pulverisateur: Pulverisateur)
} 