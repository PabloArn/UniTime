package com.example.unitime.presentation.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.data.local.entity.TareaEntity
import com.example.unitime.domain.repository.ClaseRepository
import com.example.unitime.domain.repository.TareaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val claseRepository: ClaseRepository //Necesitamos las clases para el menú desplegable (CU-02)
) : ViewModel() {

    // Lista reactiva de todas las clases (para que el alumno pueda elegir a qué materia pertenece la tarea)
    val clasesDisponibles: StateFlow<List<ClaseEntity>> = claseRepository
        .getAllClases()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Lista reactiva de tareas pendientes (Para el CU-03 y CU-08)
    val tareasPendientes: StateFlow<List<TareaEntity>> = tareaRepository
        .getTareasPendientes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var estadoUi by mutableStateOf<TareaUiState>(TareaUiState.Inactivo)
        private set

    //CU-02: Agregar tarea vinculada
    fun guardarTarea(titulo: String, descripcion: String, fechaEntrega: Long?, prioridad: String, claseId: Long?) {
        viewModelScope.launch {
            if (titulo.isBlank()) {
                estadoUi = TareaUiState.Error("El título no puede estar vacío")
                return@launch
            }

            // FA-01: Si no hay fecha, asignamos la de "mañana" por defecto
            val fechaFinal = fechaEntrega ?: obtenerFechaManana()

            val nuevaTarea = TareaEntity(
                titulo = titulo,
                descripcion = descripcion,
                fechaEntrega = fechaFinal,
                prioridad = prioridad,
                claseId = claseId // Si es null, será una tarea general sin materia
            )

            try {
                tareaRepository.insertTarea(nuevaTarea)
                estadoUi = TareaUiState.Exito
            } catch (e: Exception) {
                // Ex-01: Error al guardar
                estadoUi = TareaUiState.Error("Error al guardar la tarea en la base de datos")
            }
        }
    }

    //CU-03: Marcar tarea completada
    fun alternarEstadoTarea(tarea: TareaEntity, estaCompletada: Boolean) {
        viewModelScope.launch {
            val tareaActualizada = tarea.copy(completada = estaCompletada)
            try {
                tareaRepository.updateTarea(tareaActualizada)
            } catch (e: Exception) {
                estadoUi = TareaUiState.Error("Error al actualizar el estado")
            }
        }
    }

    // --- CU-16: Eliminar tarea ---
    fun eliminarTarea(tarea: TareaEntity) {
        viewModelScope.launch {
            try {
                tareaRepository.deleteTarea(tarea)
            } catch (e: Exception) {
                estadoUi = TareaUiState.Error("Error al eliminar la tarea")
            }
        }
    }

    fun reiniciarEstado() {
        estadoUi = TareaUiState.Inactivo
    }

    // Helper para obtener el timestamp de mañana (Flujo alternativo CU-02)
    private fun obtenerFechaManana(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return calendar.timeInMillis
    }
}

sealed class TareaUiState {
    object Inactivo : TareaUiState()
    object Exito : TareaUiState()
    data class Error(val mensaje: String) : TareaUiState()
}