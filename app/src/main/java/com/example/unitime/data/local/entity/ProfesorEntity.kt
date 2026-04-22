package com.example.unitime.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profesores")
data class ProfesorEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "correo")
    val correo: String = "",

    @ColumnInfo(name = "cubiculo")
    val cubiculo: String = "",

    @ColumnInfo(name = "facultad")
    val facultad: String = ""
)