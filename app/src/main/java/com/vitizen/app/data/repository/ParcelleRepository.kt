package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.ParcelleDao
import com.vitizen.app.data.local.entity.ParcelleEntity
import com.vitizen.app.domain.model.Parcelle
import com.vitizen.app.domain.repository.IParcelleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParcelleRepository @Inject constructor(
    private val parcelleDao: ParcelleDao
) : IParcelleRepository {

    override fun getAllParcelles(): Flow<List<Parcelle>> {
        return parcelleDao.getAllParcelles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getParcelleById(id: String): Parcelle? {
        return parcelleDao.getParcelleById(id)?.toDomain()
    }

    override suspend fun addParcelle(parcelle: Parcelle) {
        parcelleDao.insertParcelle(parcelle.toEntity())
    }

    override suspend fun updateParcelle(parcelle: Parcelle) {
        parcelleDao.updateParcelle(parcelle.toEntity())
    }

    override suspend fun deleteParcelle(parcelle: Parcelle) {
        parcelleDao.deleteParcelle(parcelle.toEntity())
    }

    private fun ParcelleEntity.toDomain(): Parcelle {
        return Parcelle(
            id = id,
            name = name,
            surface = surface,
            cepage = cepage,
            typeConduite = typeConduite,
            largeur = largeur,
            hauteur = hauteur,
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun Parcelle.toEntity(): ParcelleEntity {
        return ParcelleEntity(
            id = id,
            name = name,
            surface = surface,
            cepage = cepage,
            typeConduite = typeConduite,
            largeur = largeur,
            hauteur = hauteur,
            latitude = latitude,
            longitude = longitude
        )
    }
} 