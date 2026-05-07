package com.example.unitime.presentation.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.unitime.data.local.entity.ClaseEntity
import com.example.unitime.presentation.navigation.Rutas
import com.example.unitime.presentation.tasks.TareaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HorarioScreen(
    navController: NavController,
    horarioViewModel: HorarioViewModel = hiltViewModel(),
    tareaViewModel: TareaViewModel = hiltViewModel() // Inyectamos las tareas
) {
    // Recolectamos datos de ambas bases de datos
    val clases by horarioViewModel.clases.collectAsState(initial = emptyList())
    val tareas by tareaViewModel.tareasPendientes.collectAsState(initial = emptyList())

    var claseParaBorrar by remember { mutableStateOf<ClaseEntity?>(null) }
    var mostrarMenuAgregar by remember { mutableStateOf(false) }

    //Lógica de tares y materias por pestaña
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")

    // Intentamos que el día seleccionado por defecto sea el día actual
    val diaActual = remember {
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> "Lunes" // Si es domingo, mostramos el lunes por defecto
        }
    }

    var diaSeleccionado by remember { mutableStateOf(diaActual) }
    var tabSeleccionada by remember { mutableStateOf(0) } // 0 = Clases, 1 = Tareas

    val clasesFiltradas = clases.filter { it.diasDeLaSemana.contains(diaSeleccionado) }

    // Filtramos las tareas para que solo muestre las que su fecha de entrega cae en el día seleccionado
    val tareasFiltradas = tareas.filter { tarea ->
        val cal = Calendar.getInstance().apply { timeInMillis = tarea.fechaEntrega }
        val diaDeLaTarea = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> "Domingo"
        }
        diaDeLaTarea == diaSeleccionado
    }

    // Diálogo para borrar clases (CU-07)
    claseParaBorrar?.let { clase ->
        AlertDialog(
            onDismissRequest = { claseParaBorrar = null },
            title = { Text("Eliminar clase") },
            text = { Text("¿Estás seguro de eliminar '${clase.nombre}'?") },
            confirmButton = {
                TextButton(onClick = { horarioViewModel.eliminarClase(clase); claseParaBorrar = null }) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { claseParaBorrar = null }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { mostrarMenuAgregar = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Opciones de agregar")
                }
                DropdownMenu(expanded = mostrarMenuAgregar, onDismissRequest = { mostrarMenuAgregar = false }) {
                    DropdownMenuItem(text = { Text("📝 Nueva Tarea") }, onClick = { mostrarMenuAgregar = false; navController.navigate(Rutas.AGREGAR_TAREA) })
                    DropdownMenuItem(text = { Text("📚 Nueva Materia") }, onClick = { mostrarMenuAgregar = false; navController.navigate(Rutas.AGREGAR_CLASE) })
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Selector de días mediante scroll
            ScrollableTabRow(
                selectedTabIndex = diasSemana.indexOf(diaSeleccionado),
                edgePadding = 8.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                diasSemana.forEachIndexed { index, dia ->
                    Tab(
                        selected = diaSeleccionado == dia,
                        onClick = { diaSeleccionado = dia },
                        text = { Text(dia) }
                    )
                }
            }

            TabRow(selectedTabIndex = tabSeleccionada) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    text = { Text("📚 Clases (${clasesFiltradas.size})") }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = { tabSeleccionada = 1 },
                    text = { Text("📝 Tareas (${tareasFiltradas.size})") }
                )
            }

            if (tabSeleccionada == 0) {
                //Vista de las clases
                if (clasesFiltradas.isEmpty()) {
                    EstadoVacio("No tienes clases este día.")
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(clasesFiltradas) { clase ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(clase.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("⏰ ${clase.horaInicio} - ${clase.horaFin}")
                                        Text("📍 Salón: ${clase.salon} | Edificio: ${clase.edificio}")
                                    }
                                    IconButton(onClick = { claseParaBorrar = clase }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //Vista de las tareas
                if (tareasFiltradas.isEmpty()) {
                    EstadoVacio("¡Día libre! No hay tareas urgentes para este día.")
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tareasFiltradas) { tarea ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Checkbox para completar tareas (CU-03)
                                    Checkbox(
                                        checked = tarea.completada,
                                        onCheckedChange = { estaCompletada ->
                                            tareaViewModel.alternarEstadoTarea(tarea, estaCompletada)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(tarea.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        if (tarea.descripcion.isNotBlank()) {
                                            Text(tarea.descripcion, style = MaterialTheme.typography.bodySmall)
                                        }
                                        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        Text("📅 Entrega: ${formato.format(tarea.fechaEntrega)}", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Componente reutilizable para cuando no hay datos
@Composable
fun EstadoVacio(mensaje: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = mensaje, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}