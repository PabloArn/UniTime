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
class ScheduleViewModel @Inject constructor(
    private val repository: ClaseRepository,
    private val guardarClaseUseCase: GuardarClaseUseCase,
    private val validarEmpalmeUseCase: ValidarEmpalmeUseCase
) : ViewModel() {

    // Lista reactiva de todas las clases
    val clases: StateFlow<List<ClaseEntity>> = repository
        .getAllClases()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Estado del resultado al guardar
    var uiState: ScheduleUiState = ScheduleUiState.Idle
        private set

    fun guardarClase(clase: ClaseEntity) {
        viewModelScope.launch {
            // Validar empalme antes de guardar (CU-19)
            val dias = clase.diasDeLaSemana.split(",")
            val conflicto = validarEmpalmeUseCase(
                diasNueva = dias,
                horaInicio = clase.horaInicio,
                horaFin = clase.horaFin,
                excludeId = clase.id
            )

            uiState = if (conflicto != null) {
                ScheduleUiState.Error(
                    "Esta clase se empalma con: ${conflicto.nombre}"
                )
            } else {
                guardarClaseUseCase(clase)
                ScheduleUiState.Success
            }
        }
    }

    fun resetState() {
        uiState = ScheduleUiState.Idle
    }
}

sealed class ScheduleUiState {
    object Idle : ScheduleUiState()
    object Success : ScheduleUiState()
    data class Error(val mensaje: String) : ScheduleUiState()
}