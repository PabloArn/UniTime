package com.example.unitime.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = ClaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["clase_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clase_id")]
)
data class TareaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "titulo")
    val titulo: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String = "",

    // Fecha como epoch en milisegundos para comparar fácilmente
    @ColumnInfo(name = "fecha_entrega")
    val fechaEntrega: Long,

    // "ALTA", "MEDIA", "BAJA"
    @ColumnInfo(name = "prioridad")
    val prioridad: String = "MEDIA",

    @ColumnInfo(name = "completada")
    val completada: Boolean = false,

    @ColumnInfo(name = "clase_id")
    val claseId: Long? = null,

    // UUID del Worker para cancelarlo en edición/eliminación
    @ColumnInfo(name = "worker_id")
    val workerId: String? = null
)