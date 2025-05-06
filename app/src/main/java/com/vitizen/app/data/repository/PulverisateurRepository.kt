package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.PulverisateurDao
import com.vitizen.app.data.local.entity.PulverisateurEntity
import com.vitizen.app.domain.repository.IPulverisateurRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PulverisateurRepository @Inject constructor(
    private val pulverisateurDao: PulverisateurDao
) : IPulverisateurRepository {
    override fun getAllPulverisateurs(): Flow<List<PulverisateurEntity>> {
        return pulverisateurDao.getAllPulverisateurs()
    }

    override suspend fun getPulverisateurById(id: Long): PulverisateurEntity? {
        return pulverisateurDao.getPulverisateurById(id)
    }

    override suspend fun addPulverisateur(pulverisateur: PulverisateurEntity): Long {
        return pulverisateurDao.insertPulverisateur(pulverisateur)
    }

    override suspend fun updatePulverisateur(pulverisateur: PulverisateurEntity) {
        pulverisateurDao.updatePulverisateur(pulverisateur)
    }

    override suspend fun deletePulverisateur(pulverisateur: PulverisateurEntity) {
        pulverisateurDao.deletePulverisateur(pulverisateur)
    }
} 