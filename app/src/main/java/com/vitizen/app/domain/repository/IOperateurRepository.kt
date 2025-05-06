package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.Operateur
import kotlinx.coroutines.flow.Flow

interface IOperateurRepository {
    fun getAll(): Flow<List<Operateur>>
    suspend fun getById(id: Long): Operateur?
    suspend fun insert(operateur: Operateur): Long
    suspend fun update(operateur: Operateur)
    suspend fun delete(operateur: Operateur)
} 