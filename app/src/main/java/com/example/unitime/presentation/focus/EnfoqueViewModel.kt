package com.example.unitime.presentation.focus

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnfoqueViewModel @Inject constructor() : ViewModel() {

    var tiempoRestanteEnSegundos by mutableStateOf(0)
        private set

    var estaEnfocado by mutableStateOf(false)
        private set

    var alarmaActivada by mutableStateOf(false)
        private set

    private var temporizadorJob: Job? = null

    fun iniciarEnfoque(minutos: Int) {
        if (minutos <= 0) return
        tiempoRestanteEnSegundos = minutos * 60
        estaEnfocado = true
        alarmaActivada = false

        temporizadorJob?.cancel()
        temporizadorJob = viewModelScope.launch {
            while (tiempoRestanteEnSegundos > 0 && estaEnfocado) {
                delay(1000L) // Esperamos 1 segundo
                tiempoRestanteEnSegundos--
            }
            // Si termina el tiempo natural
            if (tiempoRestanteEnSegundos == 0) {
                detenerEnfoque()
            }
        }
    }

    fun detenerEnfoque() {
        estaEnfocado = false
        alarmaActivada = false
        tiempoRestanteEnSegundos = 0
        temporizadorJob?.cancel()
    }

    // Esta función la llamará el SensorManager cuando detecte que levantaron el teléfono
    fun penalizarPorLevantar() {
        if (estaEnfocado && !alarmaActivada) {
            alarmaActivada = true
            temporizadorJob?.cancel() // Pausamos el tiempo como castigo
        }
    }
}