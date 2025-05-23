package com.vitizen.app.domain.repository

import com.vitizen.app.domain.model.Parcelle
import kotlinx.coroutines.flow.Flow

interface IParcelleRepository {
    fun getAllParcelles(): Flow<List<Parcelle>>
    suspend fun getParcelleById(id: String): Parcelle?
    suspend fun addParcelle(parcelle: Parcelle)
    suspend fun updateParcelle(parcelle: Parcelle)
    suspend fun deleteParcelle(parcelle: Parcelle)
} 