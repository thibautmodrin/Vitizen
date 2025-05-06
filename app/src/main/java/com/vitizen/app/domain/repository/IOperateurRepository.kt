package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.Operateur
import kotlinx.coroutines.flow.Flow

interface IOperateurRepository {
    fun getAllOperateurs(): Flow<List<Operateur>>
    suspend fun getOperateurById(id: Long): Operateur?
    suspend fun addOperateur(operateur: Operateur): Long
    suspend fun updateOperateur(operateur: Operateur)
    suspend fun deleteOperateur(operateur: Operateur)
} 