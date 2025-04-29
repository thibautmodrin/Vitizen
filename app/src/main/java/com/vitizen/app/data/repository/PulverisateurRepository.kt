package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.PulverisateurDao
import com.vitizen.app.data.local.entity.PulverisateurEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PulverisateurRepository @Inject constructor(
    private val pulverisateurDao: PulverisateurDao
) {
    fun getAllPulverisateurs(): Flow<List<PulverisateurEntity>> {
        return pulverisateurDao.getAllPulverisateurs()
    }

    suspend fun getPulverisateurById(id: Long): PulverisateurEntity? {
        return pulverisateurDao.getPulverisateurById(id)
    }

    suspend fun addPulverisateur(pulverisateur: PulverisateurEntity): Long {
        return pulverisateurDao.insertPulverisateur(pulverisateur)
    }

    suspend fun updatePulverisateur(pulverisateur: PulverisateurEntity) {
        pulverisateurDao.updatePulverisateur(pulverisateur)
    }

    suspend fun deletePulverisateur(pulverisateur: PulverisateurEntity) {
        pulverisateurDao.deletePulverisateur(pulverisateur)
    }
} 