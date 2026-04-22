package com.example.unitime.data.local.dao

import androidx.room.*
import com.example.unitime.data.local.entity.ClaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClaseDao {

    // Inserta una clase y reemplaza si ya existe el mismo id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClase(clase: ClaseEntity): Long

    @Update
    suspend fun updateClase(clase: ClaseEntity)

    @Delete
    suspend fun deleteClase(clase: ClaseEntity)

    // Observa todas las clases en tiempo real (Flow reactivo)
    @Query("SELECT * FROM clases ORDER BY hora_inicio ASC")
    fun getAllClases(): Flow<List<ClaseEntity>>

    // Busca clases de un día específico
    @Query("SELECT * FROM clases WHERE dias_de_la_semana LIKE '%' || :dia || '%' ORDER BY hora_inicio ASC")
    fun getClasesByDia(dia: String): Flow<List<ClaseEntity>>

    // Para validación de empalme
    @Query("SELECT * FROM clases WHERE dias_de_la_semana LIKE '%' || :dia || '%' AND id != :excludeId")
    suspend fun getClasesByDiaParaValidacion(dia: String, excludeId: Long = 0): List<ClaseEntity>

    @Query("SELECT * FROM clases WHERE id = :id")
    suspend fun getClaseById(id: Long): ClaseEntity?
}