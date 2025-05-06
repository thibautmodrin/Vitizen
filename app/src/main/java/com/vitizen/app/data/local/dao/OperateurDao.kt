package com.vitizen.app.data.local.dao

import androidx.room.*
import com.vitizen.app.data.local.entity.OperateurEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OperateurDao {
    @Query("SELECT * FROM operateurs")
    fun getAllOperateurs(): Flow<List<OperateurEntity>>

    @Query("SELECT * FROM operateurs WHERE id = :id")
    suspend fun getOperateurById(id: Long): OperateurEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperateur(operateur: OperateurEntity): Long

    @Update
    suspend fun updateOperateur(operateur: OperateurEntity)

    @Delete
    suspend fun deleteOperateur(operateur: OperateurEntity)

    @Query("DELETE FROM operateurs")
    suspend fun deleteAll()
} 