package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.InformationsGenerales
import kotlinx.coroutines.flow.Flow

interface IInformationsGeneralesRepository {
    fun getAll(): Flow<List<InformationsGenerales>>
    suspend fun getById(id: Long): InformationsGenerales?
    suspend fun insert(informations: InformationsGenerales): Long
    suspend fun update(informations: InformationsGenerales)
    suspend fun delete(informations: InformationsGenerales)
    suspend fun deleteAll()
} 