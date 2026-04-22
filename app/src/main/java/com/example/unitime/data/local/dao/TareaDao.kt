package com.example.unitime.data.local.dao

import androidx.room.*
import com.example.unitime.data.local.entity.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarea(tarea: TareaEntity): Long

    @Update
    suspend fun updateTarea(tarea: TareaEntity)

    @Delete
    suspend fun deleteTarea(tarea: TareaEntity)

    // Todas las tareas ordenadas por fecha
    @Query("SELECT * FROM tareas ORDER BY fecha_entrega ASC")
    fun getAllTareas(): Flow<List<TareaEntity>>

    // Solo pendientes
    @Query("SELECT * FROM tareas WHERE completada = 0 ORDER BY fecha_entrega ASC")
    fun getTareasPendientes(): Flow<List<TareaEntity>>

    // Tareas de una clase específica
    @Query("SELECT * FROM tareas WHERE clase_id = :claseId ORDER BY fecha_entrega ASC")
    fun getTareasByClase(claseId: Long): Flow<List<TareaEntity>>

    // Entregas urgentes: hoy y mañana
    @Query("SELECT * FROM tareas WHERE completada = 0 AND fecha_entrega BETWEEN :hoy AND :manana ORDER BY fecha_entrega ASC")
    fun getTareasUrgentes(hoy: Long, manana: Long): Flow<List<TareaEntity>>

    // Para el Worker de notificaciones
    @Query("SELECT * FROM tareas WHERE completada = 0 AND fecha_entrega BETWEEN :inicioDia AND :finDia")
    suspend fun getTareasParaManana(inicioDia: Long, finDia: Long): List<TareaEntity>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun getTareaById(id: Long): TareaEntity?

    // Borrado en cascada manual cuando se elimina una clase (CU-07)
    @Query("DELETE FROM tareas WHERE clase_id = :claseId")
    suspend fun deleteTareasByClase(claseId: Long)
}