package com.example.unitime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clases")
data class ClaseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "profesor_id")
    val profesorId: Long? = null,

    @ColumnInfo(name = "edificio")
    val edificio: String = "",

    @ColumnInfo(name = "salon")
    val salon: String = "",

    // Hora en formato "HH:mm", ej: "14:30"
    @ColumnInfo(name = "hora_inicio")
    val horaInicio: String,

    @ColumnInfo(name = "hora_fin")
    val horaFin: String,

    // Días separados por coma, ej: "LUNES,MIERCOLES,VIERNES"
    @ColumnInfo(name = "dias_de_la_semana")
    val diasDeLaSemana: String,

    // Color ARGB como Int, ej: Color.Blue.toArgb()
    @ColumnInfo(name = "color")
    val color: Int = 0xFF6650A4.toInt()
)