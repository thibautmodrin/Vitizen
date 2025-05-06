package com.vitizen.app.domain.repository

import com.vitizen.app.data.local.entity.ParcelleEntity
import kotlinx.coroutines.flow.Flow

interface IParcelleRepository {
    fun getAllParcelles(): Flow<List<ParcelleEntity>>
    suspend fun getParcelleById(id: Long): ParcelleEntity?
    suspend fun addParcelle(parcelle: ParcelleEntity): Long
    suspend fun updateParcelle(parcelle: ParcelleEntity)
    suspend fun deleteParcelle(parcelle: ParcelleEntity)
} 