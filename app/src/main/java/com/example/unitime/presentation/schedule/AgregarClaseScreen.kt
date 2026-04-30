package com.example.unitime.presentation.schedule

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.unitime.data.local.entity.ClaseEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarClaseScreen(
    navController: NavController,
    viewModel: HorarioViewModel = hiltViewModel()
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

    // Observamos los cambios del ViewModel para saber si redirigir o mostrar error
    LaunchedEffect(estadoUi) {
        when (estadoUi) {
            is HorarioUiState.Exito -> {
                Toast.makeText(contexto, "Clase guardada correctamente", Toast.LENGTH_SHORT).show()
                viewModel.reiniciarEstado()
                navController.popBackStack() // Regresamos al horario
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it },
                    label = { Text("Hora Inicio (ej. 14:30)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = { horaFin = it },
                    label = { Text("Hora Fin (ej. 16:00)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
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
                            nombre = nombre,
                            edificio = edificio,
                            salon = salon,
                            horaInicio = horaInicio,
                            horaFin = horaFin,
                            diasDeLaSemana = diasSeleccionados.joinToString(",")
                        )
                        viewModel.guardarClase(nuevaClase)
                    } else {
                        Toast.makeText(contexto, "Llena los campos obligatorios y un día", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Clase")
            }
        }
    }
}