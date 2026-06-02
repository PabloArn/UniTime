package com.example.unitime.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://dftrhnvwjuzhbqauhwfg.supabase.co", // Pega tu URL aquí (empieza con https://)
            supabaseKey = "sb_publishable_oBiixUd0KNQZAjtHMnpW5g_wboJwL84"      // Pega tu llave larguísima aquí (la sb_publishable...)
        ) {
            install(Auth) // Instalamos el módulo de Autenticación
        }
    }
}