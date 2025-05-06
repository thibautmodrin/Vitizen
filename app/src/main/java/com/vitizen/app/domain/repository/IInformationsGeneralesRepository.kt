package com.vitizen.app.domain.repository

import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import kotlinx.coroutines.flow.Flow

interface IInformationsGeneralesRepository {
    fun getAll(): Flow<List<InformationsGeneralesEntity>>
    suspend fun getById(id: Long): InformationsGeneralesEntity?
    suspend fun insert(informations: InformationsGeneralesEntity): Long
    suspend fun update(informations: InformationsGeneralesEntity)
    suspend fun delete(informations: InformationsGeneralesEntity)
    suspend fun deleteAll()
} 