package com.vitizen.app.data.local.dao

import androidx.room.*
import com.vitizen.app.data.local.entity.ParcelleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParcelleDao {
    @Query("SELECT * FROM parcelles")
    fun getAllParcelles(): Flow<List<ParcelleEntity>>

    @Query("SELECT * FROM parcelles WHERE id = :id")
    suspend fun getParcelleById(id: Long): ParcelleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParcelle(parcelle: ParcelleEntity): Long

    @Update
    suspend fun updateParcelle(parcelle: ParcelleEntity)

    @Delete
    suspend fun deleteParcelle(parcelle: ParcelleEntity)
}