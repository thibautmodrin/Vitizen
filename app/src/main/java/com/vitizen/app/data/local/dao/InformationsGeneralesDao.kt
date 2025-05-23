package com.vitizen.app.data.local.dao

import androidx.room.*
import com.vitizen.app.data.local.entity.InformationsGeneralesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InformationsGeneralesDao {
    @Query("SELECT * FROM informations_generales")
    fun getAll(): Flow<List<InformationsGeneralesEntity>>

    @Query("SELECT * FROM informations_generales WHERE id = :id")
    suspend fun getById(id: Long): InformationsGeneralesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(informationsGenerales: InformationsGeneralesEntity): Long

    @Update
    suspend fun update(informationsGenerales: InformationsGeneralesEntity)

    @Delete
    suspend fun delete(informationsGenerales: InformationsGeneralesEntity)

    @Query("DELETE FROM informations_generales")
    suspend fun deleteAll()
} 