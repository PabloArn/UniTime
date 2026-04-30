package com.example.unitime.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.domain.repository.ClaseRepository
import com.example.unitime.domain.usecase.clase.GuardarClaseUseCase
import com.example.unitime.domain.usecase.clase.ValidarEmpalmeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HorarioViewModel @Inject constructor(
    private val repository: ClaseRepository,
    private val guardarClaseUseCase: GuardarClaseUseCase,
    private val validarEmpalmeUseCase: ValidarEmpalmeUseCase
) : ViewModel() {

    // Lista reactiva de todas las clases que se mostrarán en la UI
    val clases: StateFlow<List<ClaseEntity>> = repository
        .getAllClases()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado visual para saber si se guardó bien o hubo error
    var estadoUi: HorarioUiState = HorarioUiState.Inactivo
        private set

    fun guardarClase(clase: ClaseEntity) {
        viewModelScope.launch {
            // Separamos los días para poder validarlos uno por uno (CU-19)
            val dias = clase.diasDeLaSemana.split(",")
            val conflicto = validarEmpalmeUseCase(
                diasNueva = dias,
                horaInicio = clase.horaInicio,
                horaFin = clase.horaFin,
                excludeId = clase.id
            )

            estadoUi = if (conflicto != null) {
                // Si hay empalme, bloqueamos el guardado y avisamos
                HorarioUiState.Error("Esta clase choca con: ${conflicto.nombre}")
            } else {
                // Si el horario está libre, insertamos en la base de datos
                guardarClaseUseCase(clase)
                HorarioUiState.Exito
            }
        }
    }

    fun reiniciarEstado() {
        estadoUi = HorarioUiState.Inactivo
    }
}

// Representa los estados posibles al intentar guardar una materia
sealed class HorarioUiState {
    object Inactivo : HorarioUiState()
    object Exito : HorarioUiState()
    data class Error(val mensaje: String) : HorarioUiState()
}