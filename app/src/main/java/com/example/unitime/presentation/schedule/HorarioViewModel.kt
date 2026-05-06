package com.example.unitime.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.domain.repository.ClaseRepository
import com.example.unitime.domain.usecase.clase.GuardarClaseUseCase
import com.example.unitime.domain.usecase.clase.ValidarEmpalmeUseCase
import com.example.unitime.domain.usecase.clase.EliminarClaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@HiltViewModel
class HorarioViewModel @Inject constructor(
    private val repository: ClaseRepository,
    private val guardarClaseUseCase: GuardarClaseUseCase,
    private val validarEmpalmeUseCase: ValidarEmpalmeUseCase,
    private val eliminarClaseUseCase: EliminarClaseUseCase
) : ViewModel() {

    // Exponemos la lista de clases como un StateFlow para que la UI pueda observarla
    val clases: StateFlow<List<ClaseEntity>> = repository.getAllClases()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var estadoUi by mutableStateOf<HorarioUiState>(HorarioUiState.Inactivo)
        private set

    fun guardarClase(clase: ClaseEntity) {
        viewModelScope.launch {
            val dias = clase.diasDeLaSemana.split(",")
            val conflicto = validarEmpalmeUseCase(
                diasNueva = dias,
                horaInicio = clase.horaInicio,
                horaFin = clase.horaFin,
                excludeId = clase.id
            )

            estadoUi = if (conflicto != null) {
                HorarioUiState.Error("Esta clase choca con: ${conflicto.nombre}")
            } else {
                guardarClaseUseCase(clase)
                HorarioUiState.Exito
            }
        }
    }

    fun eliminarClase(clase: ClaseEntity) {
        viewModelScope.launch {
            eliminarClaseUseCase(clase)
        }
    }

    fun reiniciarEstado() {
        estadoUi = HorarioUiState.Inactivo
    }
}

sealed class HorarioUiState {
    object Inactivo : HorarioUiState()
    object Exito : HorarioUiState()
    data class Error(val mensaje: String) : HorarioUiState()
}