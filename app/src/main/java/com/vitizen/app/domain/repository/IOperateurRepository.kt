package com.vitizen.app.domain.repository

import com.vitizen.app.data.local.entity.OperateurEntity
import kotlinx.coroutines.flow.Flow

interface IOperateurRepository {
    fun getAll(): Flow<List<OperateurEntity>>
    suspend fun getById(id: Long): OperateurEntity?
    suspend fun insert(operateur: OperateurEntity): Long
    suspend fun update(operateur: OperateurEntity)
    suspend fun delete(operateur: OperateurEntity)
    suspend fun deleteAll()
} 