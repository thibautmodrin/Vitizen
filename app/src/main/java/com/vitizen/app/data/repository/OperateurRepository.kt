package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.OperateurDao
import com.vitizen.app.data.local.entity.OperateurEntity
import com.vitizen.app.domain.repository.IOperateurRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OperateurRepository @Inject constructor(
    private val operateurDao: OperateurDao
) : IOperateurRepository {
    override fun getAll(): Flow<List<OperateurEntity>> = operateurDao.getAll()

    override suspend fun getById(id: Long): OperateurEntity? = operateurDao.getById(id)

    override suspend fun insert(operateur: OperateurEntity): Long = operateurDao.insert(operateur)

    override suspend fun update(operateur: OperateurEntity) = operateurDao.update(operateur)

    override suspend fun delete(operateur: OperateurEntity) = operateurDao.delete(operateur)

    override suspend fun deleteAll() = operateurDao.deleteAll()
} 