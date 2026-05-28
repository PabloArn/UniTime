package com.example.unitime.presentation.schedule

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.unitime.data.local.entity.ClaseEntity
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarClaseScreen(
    navController: NavController,
    viewModel: HorarioViewModel = hiltViewModel(),
    claseId: Long? = null
) {
    val contexto = LocalContext.current
    val estadoUi = viewModel.estadoUi

    var nombre by remember { mutableStateOf("") }
    var edificio by remember { mutableStateOf("") }
    var salon by remember { mutableStateOf("") }

    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }

    var diasSeleccionados by remember { mutableStateOf(setOf<String>()) }
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")

    // --- LÓGICA DEL RELOJ NATIVO (CU-12) ---
    val calendario = Calendar.getInstance()
    val horaActual = calendario.get(Calendar.HOUR_OF_DAY)
    val minutoActual = calendario.get(Calendar.MINUTE)

    LaunchedEffect(key1 = claseId) {
        if (claseId != null) {
            val claseExistente = viewModel.obtenerClasePorId(claseId)
            if (claseExistente != null) {
                nombre = claseExistente.nombre
                salon = claseExistente.salon
                edificio = claseExistente.edificio
                horaInicio = claseExistente.horaInicio
                horaFin = claseExistente.horaFin

                // Convertimos el texto "Lunes, Martes" de vuelta a una lista para la interfaz
                if (claseExistente.diasDeLaSemana.isNotBlank()) {
                    diasSeleccionados = claseExistente.diasDeLaSemana.split(", ").toSet()
                }
            }
        }
    }
    // Función reutilizable para abrir el reloj
    fun mostrarReloj(alSeleccionar: (String) -> Unit) {
        TimePickerDialog(
            contexto,
            { _, hora, minuto ->
                // Formateamos la hora para asegurarnos de que SIEMPRE tenga el formato "HH:MM" (ej. 09:05)
                val tiempoFormateado = String.format(Locale.getDefault(), "%02d:%02d", hora, minuto)
                alSeleccionar(tiempoFormateado)
            },
            horaActual,
            minutoActual,
            true // true para formato de 24 horas
        ).show()
    }

    LaunchedEffect(estadoUi) {
        when (estadoUi) {
            is HorarioUiState.Exito -> {
                Toast.makeText(contexto, "Clase guardada correctamente", Toast.LENGTH_SHORT).show()
                viewModel.reiniciarEstado()
                navController.popBackStack()
            }
            is HorarioUiState.Error -> {
                Toast.makeText(contexto, estadoUi.mensaje, Toast.LENGTH_LONG).show()
                viewModel.reiniciarEstado()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Clase") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de la materia") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = edificio,
                    onValueChange = { edificio = it },
                    label = { Text("Edificio (Opcional)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = salon,
                    onValueChange = { salon = it },
                    label = { Text("Salón") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // --- CAMPOS DE HORA ACTUALIZADOS PARA ABRIR EL RELOJ ---
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { },
                    label = { Text("Hora Inicio") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { mostrarReloj { horaInicio = it } }, // Al hacer clic, abre el reloj
                    enabled = false, // Lo deshabilitamos para que no puedan escribir a mano
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = { },
                    label = { Text("Hora Fin") },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { mostrarReloj { horaFin = it } }, // Al hacer clic, abre el reloj
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Text("Días de la semana", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                diasSemana.forEach { dia ->
                    val inicial = dia.take(1)
                    val seleccionado = diasSeleccionados.contains(dia)

                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            diasSeleccionados = if (seleccionado) {
                                diasSeleccionados - dia
                            } else {
                                diasSeleccionados + dia
                            }
                        },
                        label = { Text(inicial) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && horaInicio.isNotBlank() && horaFin.isNotBlank() && diasSeleccionados.isNotEmpty()) {
                        val nuevaClase = ClaseEntity(
                            id = claseId ?: 0L, // IMPORTANTE: Si es edición usa el ID, si es nueva le pone 0
                            nombre = nombre,
                            edificio = edificio,
                            salon = salon,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            diasDeLaSemana = diasSeleccionados.joinToString(separator = ", ")
                        )

                        // LA DECISIÓN: ¿Actualizar o Crear?
                        if (claseId != null) {
                            viewModel.actualizarClase(nuevaClase)
                        } else {
                            viewModel.guardarClase(nuevaClase)
                        }

                        // Regresa al menú principal después de guardar
                        navController.popBackStack()
                    } else {
                        Toast.makeText(contexto, "Llena los campos obligatorios y un día", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                // El texto del botón cambia automáticamente
                Text(if (claseId != null) "Actualizar Clase" else "Guardar Clase")
            }
        }
    }
}