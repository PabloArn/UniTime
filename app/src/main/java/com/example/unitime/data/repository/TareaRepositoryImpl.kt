package com.example.unitime.data.repository

import com.example.unitime.data.local.dao.TareaDao
import com.example.unitime.data.local.entity.TareaEntity
import com.example.unitime.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TareaRepositoryImpl @Inject constructor(
    private val tareaDao: TareaDao
) : TareaRepository {

    override fun getAllTareas(): Flow<List<TareaEntity>> =
        tareaDao.getAllTareas()

    override fun getTareasPendientes(): Flow<List<TareaEntity>> =
        tareaDao.getTareasPendientes()

    override fun getTareasByClase(claseId: Long): Flow<List<TareaEntity>> =
        tareaDao.getTareasByClase(claseId)

    override fun getTareasUrgentes(hoy: Long, manana: Long): Flow<List<TareaEntity>> =
        tareaDao.getTareasUrgentes(hoy, manana)

    override suspend fun getTareasParaManana(inicioDia: Long, finDia: Long): List<TareaEntity> =
        tareaDao.getTareasParaManana(inicioDia, finDia)

    override suspend fun getTareaById(id: Long): TareaEntity? =
        tareaDao.getTareaById(id)

    override suspend fun insertTarea(tarea: TareaEntity): Long =
        tareaDao.insertTarea(tarea)

    override suspend fun updateTarea(tarea: TareaEntity) =
        tareaDao.updateTarea(tarea)

    override suspend fun deleteTarea(tarea: TareaEntity) =
        tareaDao.deleteTarea(tarea)

    override suspend fun deleteTareasByClase(claseId: Long) =
        tareaDao.deleteTareasByClase(claseId)
}