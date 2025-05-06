package com.vitizen.app.data.repository

import com.vitizen.app.data.local.dao.PulverisateurDao
import com.vitizen.app.data.local.entity.PulverisateurEntity
import com.vitizen.app.domain.model.Pulverisateur
import com.vitizen.app.domain.repository.IPulverisateurRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PulverisateurRepository @Inject constructor(
    private val pulverisateurDao: PulverisateurDao
) : IPulverisateurRepository {

    override fun getAllPulverisateurs(): Flow<List<Pulverisateur>> {
        return pulverisateurDao.getAllPulverisateurs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPulverisateurById(id: Long): Pulverisateur? {
        return pulverisateurDao.getPulverisateurById(id)?.toDomain()
    }

    override suspend fun addPulverisateur(pulverisateur: Pulverisateur): Long {
        return pulverisateurDao.insertPulverisateur(pulverisateur.toEntity())
    }

    override suspend fun updatePulverisateur(pulverisateur: Pulverisateur) {
        pulverisateurDao.updatePulverisateur(pulverisateur.toEntity())
    }

    override suspend fun deletePulverisateur(pulverisateur: Pulverisateur) {
        pulverisateurDao.deletePulverisateur(pulverisateur.toEntity())
    }

    private fun PulverisateurEntity.toDomain(): Pulverisateur {
        return Pulverisateur(
            id = id,
            nomMateriel = nomMateriel,
            modeDeplacement = modeDeplacement,
            nombreRampes = nombreRampes,
            nombreBusesParRampe = nombreBusesParRampe,
            typeBuses = typeBuses,
            pressionPulverisation = pressionPulverisation,
            debitParBuse = debitParBuse,
            anglePulverisation = anglePulverisation,
            largeurTraitement = largeurTraitement,
            plageVitesseAvancementMin = plageVitesseAvancementMin,
            plageVitesseAvancementMax = plageVitesseAvancementMax,
            volumeTotalCuve = volumeTotalCuve
        )
    }

    private fun Pulverisateur.toEntity(): PulverisateurEntity {
        return PulverisateurEntity(
            id = id,
            nomMateriel = nomMateriel,
            modeDeplacement = modeDeplacement,
            nombreRampes = nombreRampes,
            nombreBusesParRampe = nombreBusesParRampe,
            typeBuses = typeBuses,
            pressionPulverisation = pressionPulverisation,
            debitParBuse = debitParBuse,
            anglePulverisation = anglePulverisation,
            largeurTraitement = largeurTraitement,
            plageVitesseAvancementMin = plageVitesseAvancementMin,
            plageVitesseAvancementMax = plageVitesseAvancementMax,
            volumeTotalCuve = volumeTotalCuve
        )
    }
} 