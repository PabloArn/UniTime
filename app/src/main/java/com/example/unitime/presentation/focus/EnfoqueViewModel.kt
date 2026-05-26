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

    var esperandoPosicion by mutableStateOf(false)
        private set

    private var temporizadorJob: Job? = null

    // Escudo de tiempo para estabilizar el celular
    private var tiempoInmunidad: Long = 0

    fun iniciarPreparacion(minutos: Int) {
        if (minutos <= 0) return
        tiempoRestanteEnSegundos = minutos * 60
        esperandoPosicion = true
        estaEnfocado = false
        alarmaActivada = false
    }

    fun confirmarPosicion() {
        esperandoPosicion = false
        estaEnfocado = true

        // NUENA LÍNEA: Apagamos la alarma para que el sonido se detenga
        // y la UI regrese a la pantalla del cronómetro
        alarmaActivada = false

        // Le damos 2.5 segundos de inmunidad para que el estudiante saque la mano
        // y el celular deje de temblar en la mesa antes de "armar" la alarma.
        tiempoInmunidad = System.currentTimeMillis() + 2500L

        temporizadorJob?.cancel()
        temporizadorJob = viewModelScope.launch {
            while (tiempoRestanteEnSegundos > 0 && estaEnfocado) {
                delay(1000L)
                tiempoRestanteEnSegundos--
            }
            if (tiempoRestanteEnSegundos == 0) {
                detenerEnfoque()
            }
        }
    }

    fun detenerEnfoque() {
        estaEnfocado = false
        esperandoPosicion = false
        alarmaActivada = false
        tiempoRestanteEnSegundos = 0
        temporizadorJob?.cancel()
    }

    fun penalizarPorLevantar() {
        // Solo lo castigamos si YA PASÓ el tiempo de inmunidad
        if (estaEnfocado && !alarmaActivada && System.currentTimeMillis() > tiempoInmunidad) {
            alarmaActivada = true
            temporizadorJob?.cancel()
        }
    }
}