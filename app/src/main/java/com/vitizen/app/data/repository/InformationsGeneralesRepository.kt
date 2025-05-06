package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.InformationsGeneralesDao
import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import com.vitizen.app.domain.model.InformationsGenerales
import com.vitizen.app.domain.repository.IInformationsGeneralesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InformationsGeneralesRepository @Inject constructor(
    private val informationsGeneralesDao: InformationsGeneralesDao
) : IInformationsGeneralesRepository {

    override fun getAll(): Flow<List<InformationsGenerales>> =
        informationsGeneralesDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getById(id: Long): InformationsGenerales? =
        informationsGeneralesDao.getById(id)?.toDomain()

    override suspend fun insert(informations: InformationsGenerales): Long =
        informationsGeneralesDao.insert(informations.toEntity())

    override suspend fun update(informations: InformationsGenerales) =
        informationsGeneralesDao.update(informations.toEntity())

    override suspend fun delete(informations: InformationsGenerales) =
        informationsGeneralesDao.delete(informations.toEntity())

    override suspend fun deleteAll() = informationsGeneralesDao.deleteAll()

    private fun InformationsGeneralesEntity.toDomain(): InformationsGenerales {
        return InformationsGenerales(
            id = id,
            nomDomaine = nomDomaine,
            modeCulture = modeCulture,
            certifications = certifications,
            surfaceTotale = surfaceTotale,
            codePostal = codePostal
        )
    }

    private fun InformationsGenerales.toEntity(): InformationsGeneralesEntity {
        return InformationsGeneralesEntity(
            id = id,
            nomDomaine = nomDomaine,
            modeCulture = modeCulture,
            certifications = certifications,
            surfaceTotale = surfaceTotale,
            codePostal = codePostal
        )
    }
} 