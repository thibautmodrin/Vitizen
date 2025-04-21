package com.vitizen.app.data.dao

import androidx.room.*
import com.vitizen.app.data.entity.PulverisateurEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PulverisateurDao {
    @Query("SELECT * FROM pulverisateurs ORDER BY nomMateriel ASC")
    fun getAllPulverisateurs(): Flow<List<PulverisateurEntity>>

    @Query("SELECT * FROM pulverisateurs WHERE id = :id")
    suspend fun getPulverisateurById(id: Long): PulverisateurEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPulverisateur(pulverisateur: PulverisateurEntity): Long

    @Update
    suspend fun updatePulverisateur(pulverisateur: PulverisateurEntity)

    @Delete
    suspend fun deletePulverisateur(pulverisateur: PulverisateurEntity)
} 