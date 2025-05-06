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

    override fun getAllOperateurs(): Flow<List<Operateur>> {
        return operateurDao.getAllOperateurs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getOperateurById(id: Long): Operateur? {
        return operateurDao.getOperateurById(id)?.toDomain()
    }

    override suspend fun addOperateur(operateur: Operateur): Long {
        return operateurDao.insertOperateur(operateur.toEntity())
    }

    override suspend fun updateOperateur(operateur: Operateur) {
        operateurDao.updateOperateur(operateur.toEntity())
    }

    override suspend fun deleteOperateur(operateur: Operateur) {
        operateurDao.deleteOperateur(operateur.toEntity())
    }

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