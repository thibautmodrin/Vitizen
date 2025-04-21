package com.vitizen.app.data.dao

import androidx.room.*
import com.vitizen.app.data.entity.TreatmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TreatmentDao {
    @Query("SELECT * FROM treatments")
    fun getAll(): Flow<List<TreatmentEntity>>

    @Query("SELECT * FROM treatments WHERE id = :id")
    suspend fun getById(id: Long): TreatmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(treatment: TreatmentEntity): Long

    @Update
    suspend fun update(treatment: TreatmentEntity)

    @Delete
    suspend fun delete(treatment: TreatmentEntity)

    @Query("DELETE FROM treatments")
    suspend fun deleteAll()
} 