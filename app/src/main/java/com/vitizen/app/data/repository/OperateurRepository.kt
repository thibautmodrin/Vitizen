package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.OperateurDao
import com.vitizen.app.data.local.entity.OperateurEntity
import com.vitizen.app.domain.model.Operateur
import com.vitizen.app.domain.repository.IOperateurRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OperateurRepository @Inject constructor(
    private val operateurDao: OperateurDao
) : IOperateurRepository {

    override fun getAll(): Flow<List<Operateur>> =
        operateurDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getById(id: Long): Operateur? =
        operateurDao.getById(id)?.toDomain()

    override suspend fun insert(operateur: Operateur): Long =
        operateurDao.insert(operateur.toEntity())

    override suspend fun update(operateur: Operateur) =
        operateurDao.update(operateur.toEntity())

    override suspend fun delete(operateur: Operateur) =
        operateurDao.delete(operateur.toEntity())

    private fun OperateurEntity.toDomain(): Operateur {
        return Operateur(
            id = id,
            nom = nom,
            disponibleWeekend = disponibleWeekend,
            diplomes = diplomes,
            materielMaitrise = materielMaitrise
        )
    }

    private fun Operateur.toEntity(): OperateurEntity {
        return OperateurEntity(
            id = id,
            nom = nom,
            disponibleWeekend = disponibleWeekend,
            diplomes = diplomes,
            materielMaitrise = materielMaitrise
        )
    }
} 