package com.example.unitime.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    // Variables para controlar el estado en la pantalla
    private val _estadoLogin = MutableStateFlow<AuthState>(AuthState.Inactivo)
    val estadoLogin: StateFlow<AuthState> = _estadoLogin

    fun iniciarSesion(correo: String, contrasena: String) {
        viewModelScope.launch {
            _estadoLogin.value = AuthState.Cargando
            try {
                // Intenta iniciar sesión
                supabase.auth.signInWith(Email) {
                    email = correo
                    password = contrasena
                }
                _estadoLogin.value = AuthState.Exito
            } catch (e: Exception) {
                // Si falla (por ejemplo, el usuario no existe), intentamos registrarlo automáticamente
                registrarUsuario(correo, contrasena)
            }
        }
    }

    private suspend fun registrarUsuario(correo: String, contrasena: String) {
        try {
            supabase.auth.signUpWith(Email) {
                email = correo
                password = contrasena
            }
            _estadoLogin.value = AuthState.Exito
        } catch (e: Exception) {
            _estadoLogin.value = AuthState.Error(e.message ?: "Error desconocido")
        }
    }

    fun reiniciarEstado() {
        _estadoLogin.value = AuthState.Inactivo
    }
}

// Estados posibles de la autenticación
sealed class AuthState {
    object Inactivo : AuthState()
    object Cargando : AuthState()
    object Exito : AuthState()
    data class Error(val mensaje: String) : AuthState()
}