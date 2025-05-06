package com.vitizen.app.domain.repository

import com.vitizen.app.data.local.entity.PulverisateurEntity
import kotlinx.coroutines.flow.Flow

interface IPulverisateurRepository {
    fun getAllPulverisateurs(): Flow<List<PulverisateurEntity>>
    suspend fun getPulverisateurById(id: Long): PulverisateurEntity?
    suspend fun addPulverisateur(pulverisateur: PulverisateurEntity): Long
    suspend fun updatePulverisateur(pulverisateur: PulverisateurEntity)
    suspend fun deletePulverisateur(pulverisateur: PulverisateurEntity)
} 