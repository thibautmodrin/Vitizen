package com.vitizen.app.data.dao

import androidx.room.*
import com.vitizen.app.data.entity.OperateurEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperateurDao {
    @Query("SELECT * FROM operateurs")
    fun getAll(): Flow<List<OperateurEntity>>

    @Query("SELECT * FROM operateurs WHERE id = :id")
    suspend fun getById(id: Long): OperateurEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operateur: OperateurEntity): Long

    @Update
    suspend fun update(operateur: OperateurEntity)

    @Delete
    suspend fun delete(operateur: OperateurEntity)

    @Query("DELETE FROM operateurs")
    suspend fun deleteAll()
} 