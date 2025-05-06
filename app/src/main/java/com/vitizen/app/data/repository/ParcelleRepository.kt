package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.ParcelleDao
import com.vitizen.app.data.local.entity.ParcelleEntity
import com.vitizen.app.domain.repository.IParcelleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParcelleRepository @Inject constructor(
    private val parcelleDao: ParcelleDao
) : IParcelleRepository {
    override fun getAllParcelles(): Flow<List<ParcelleEntity>> = parcelleDao.getAllParcelles()

    override suspend fun getParcelleById(id: Long): ParcelleEntity? = parcelleDao.getParcelleById(id)

    override suspend fun addParcelle(parcelle: ParcelleEntity): Long = parcelleDao.insertParcelle(parcelle)

    override suspend fun updateParcelle(parcelle: ParcelleEntity) = parcelleDao.updateParcelle(parcelle)

    override suspend fun deleteParcelle(parcelle: ParcelleEntity) = parcelleDao.deleteParcelle(parcelle)
} 