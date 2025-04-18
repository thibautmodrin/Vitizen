package com.vitizen.app.data.local.dao

import androidx.room.*
import com.vitizen.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY uid DESC LIMIT 1")
    suspend fun getLastUser(): UserEntity?

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
} 