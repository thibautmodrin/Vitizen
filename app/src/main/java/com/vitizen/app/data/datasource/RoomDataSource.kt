package com.vitizen.app.data.datasource

import com.vitizen.app.data.local.dao.UserDao
import com.vitizen.app.data.local.entity.UserEntity
import com.vitizen.app.domain.model.User
import javax.inject.Inject

class RoomDataSource @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun insertUser(user: User) {
        val userEntity = UserEntity(
            uid = user.uid,
            email = user.email,
            role = user.role,
            isEmailVerified = user.isEmailVerified
        )
        userDao.insertUser(userEntity)
    }

    suspend fun getUserByUid(uid: String): User? {
        return userDao.getUser(uid)?.toUser()
    }

    suspend fun getLastUser(): User? {
        val userEntity = userDao.getLastUser()
        return userEntity?.toUser()
    }

    suspend fun clearUsers() {
        userDao.deleteAllUsers()
    }

    private fun UserEntity.toUser(): User {
        return User(
            uid = uid,
            email = email,
            role = role,
            isEmailVerified = isEmailVerified
        )
    }
} 