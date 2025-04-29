package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.InformationsGeneralesDao
import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InformationsGeneralesRepository @Inject constructor(
    private val dao: InformationsGeneralesDao
) {
    fun getAll(): Flow<List<InformationsGeneralesEntity>> = dao.getAll()

    suspend fun getById(id: Long): InformationsGeneralesEntity? = dao.getById(id)

    suspend fun insert(informations: InformationsGeneralesEntity): Long = dao.insert(informations)

    suspend fun update(informations: InformationsGeneralesEntity) = dao.update(informations)

    suspend fun delete(informations: InformationsGeneralesEntity) = dao.delete(informations)

    suspend fun deleteAll() = dao.deleteAll()
} 