package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.ParcelleDao
import com.vitizen.app.data.local.entity.ParcelleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParcelleRepository @Inject constructor(
    private val parcelleDao: ParcelleDao
) {
    fun getAllParcelles(): Flow<List<ParcelleEntity>> = parcelleDao.getAllParcelles()

    suspend fun getParcelleById(id: Long): ParcelleEntity? = parcelleDao.getParcelleById(id)

    suspend fun addParcelle(parcelle: ParcelleEntity): Long = parcelleDao.insertParcelle(parcelle)

    suspend fun updateParcelle(parcelle: ParcelleEntity) = parcelleDao.updateParcelle(parcelle)

    suspend fun deleteParcelle(parcelle: ParcelleEntity) = parcelleDao.deleteParcelle(parcelle)
} 