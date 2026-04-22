package com.example.unitime.data.repository

import com.example.unitime.data.local.dao.ClaseDao
import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.domain.repository.ClaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClaseRepositoryImpl @Inject constructor(
    private val claseDao: ClaseDao
) : ClaseRepository {

    override fun getAllClases(): Flow<List<ClaseEntity>> =
        claseDao.getAllClases()

    override fun getClasesByDia(dia: String): Flow<List<ClaseEntity>> =
        claseDao.getClasesByDia(dia)

    override suspend fun getClaseById(id: Long): ClaseEntity? =
        claseDao.getClaseById(id)

    override suspend fun insertClase(clase: ClaseEntity): Long =
        claseDao.insertClase(clase)

    override suspend fun updateClase(clase: ClaseEntity) =
        claseDao.updateClase(clase)

    override suspend fun deleteClase(clase: ClaseEntity) =
        claseDao.deleteClase(clase)

    override suspend fun getClasesByDiaParaValidacion(
        dia: String,
        excludeId: Long
    ): List<ClaseEntity> =
        claseDao.getClasesByDiaParaValidacion(dia, excludeId)
}