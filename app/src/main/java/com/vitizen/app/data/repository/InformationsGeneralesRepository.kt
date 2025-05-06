package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.InformationsGeneralesDao
import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import com.vitizen.app.domain.repository.IInformationsGeneralesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InformationsGeneralesRepository @Inject constructor(
    private val informationsGeneralesDao: InformationsGeneralesDao
) : IInformationsGeneralesRepository {
    override fun getAll(): Flow<List<InformationsGeneralesEntity>> = informationsGeneralesDao.getAll()

    override suspend fun getById(id: Long): InformationsGeneralesEntity? = informationsGeneralesDao.getById(id)

    override suspend fun insert(informations: InformationsGeneralesEntity): Long = informationsGeneralesDao.insert(informations)

    override suspend fun update(informations: InformationsGeneralesEntity) = informationsGeneralesDao.update(informations)

    override suspend fun delete(informations: InformationsGeneralesEntity) = informationsGeneralesDao.delete(informations)

    override suspend fun deleteAll() = informationsGeneralesDao.deleteAll()
} 