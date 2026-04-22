package com.example.unitime.domain.repository

import com.example.unitime.data.local.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

interface TareaRepository {

    fun getAllTareas(): Flow<List<TareaEntity>>

    fun getTareasPendientes(): Flow<List<TareaEntity>>

    fun getTareasByClase(claseId: Long): Flow<List<TareaEntity>>

    fun getTareasUrgentes(hoy: Long, manana: Long): Flow<List<TareaEntity>>

    suspend fun getTareasParaManana(inicioDia: Long, finDia: Long): List<TareaEntity>

    suspend fun getTareaById(id: Long): TareaEntity?

    suspend fun insertTarea(tarea: TareaEntity): Long

    suspend fun updateTarea(tarea: TareaEntity)

    suspend fun deleteTarea(tarea: TareaEntity)

    suspend fun deleteTareasByClase(claseId: Long)
}