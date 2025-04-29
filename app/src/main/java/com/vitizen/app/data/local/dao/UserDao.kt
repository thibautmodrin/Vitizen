package com.vitizen.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitizen.app.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUser(uid: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY uid DESC LIMIT 1")
    suspend fun getLastUser(): UserEntity?

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}