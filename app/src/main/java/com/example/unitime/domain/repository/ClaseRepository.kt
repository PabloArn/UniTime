package com.example.unitime.domain.repository

import com.example.unitime.data.local.entity.ClaseEntity
import kotlinx.coroutines.flow.Flow

interface ClaseRepository {

    fun getAllClases(): Flow<List<ClaseEntity>>

    fun getClasesByDia(dia: String): Flow<List<ClaseEntity>>

    suspend fun getClaseById(id: Long): ClaseEntity?

    suspend fun insertClase(clase: ClaseEntity): Long

    suspend fun updateClase(clase: ClaseEntity)

    suspend fun deleteClase(clase: ClaseEntity)

    // Para validación de empalme
    suspend fun getClasesByDiaParaValidacion(
        dia: String,
        excludeId: Long = 0
    ): List<ClaseEntity>
}