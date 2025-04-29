package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.TreatmentDao
import com.vitizen.app.data.local.entity.TreatmentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TreatmentRepository @Inject constructor(
    private val treatmentDao: TreatmentDao
) {
    fun getAllTreatments(): Flow<List<TreatmentEntity>> = treatmentDao.getAll()

    suspend fun getTreatmentById(id: Long): TreatmentEntity? = treatmentDao.getById(id)

    suspend fun insertTreatment(treatment: TreatmentEntity): Long = treatmentDao.insert(treatment)

    suspend fun updateTreatment(treatment: TreatmentEntity) = treatmentDao.update(treatment)

    suspend fun deleteTreatment(treatment: TreatmentEntity) = treatmentDao.delete(treatment)
} 