package com.example.unitime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.unitime.data.local.dao.ClaseDao
import com.example.unitime.data.local.dao.TareaDao
import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.data.local.entity.ProfesorEntity
import com.example.unitime.data.local.entity.TareaEntity

@Database(
    entities = [
        ClaseEntity::class,
        TareaEntity::class,
        ProfesorEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UniTimeDatabase : RoomDatabase() {

    abstract fun claseDao(): ClaseDao
    abstract fun tareaDao(): TareaDao

    companion object {
        const val DATABASE_NAME = "unitime_db"
    }
}