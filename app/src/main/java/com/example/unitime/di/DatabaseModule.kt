package com.example.unitime.di

import android.content.Context
import androidx.room.Room
import com.example.unitime.data.local.UniTimeDatabase
import com.example.unitime.data.local.dao.ClaseDao
import com.example.unitime.data.local.dao.TareaDao
import com.example.unitime.data.repository.ClaseRepositoryImpl
import com.example.unitime.data.repository.TareaRepositoryImpl
import com.example.unitime.domain.repository.ClaseRepository
import com.example.unitime.domain.repository.TareaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): UniTimeDatabase {
        return Room.databaseBuilder(
            context,
            UniTimeDatabase::class.java,
            UniTimeDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideClaseDao(database: UniTimeDatabase): ClaseDao =
        database.claseDao()

    @Provides
    @Singleton
    fun provideTareaDao(database: UniTimeDatabase): TareaDao =
        database.tareaDao()

    // Conecta la interfaz con su implementación concreta
    @Provides
    @Singleton
    fun provideClaseRepository(
        claseDao: ClaseDao
    ): ClaseRepository = ClaseRepositoryImpl(claseDao)

    @Provides
    @Singleton
    fun provideTareaRepository(
        tareaDao: TareaDao
    ): TareaRepository = TareaRepositoryImpl(tareaDao)
}