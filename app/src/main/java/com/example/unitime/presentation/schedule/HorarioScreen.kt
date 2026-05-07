package com.example.unitime.presentation.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.unitime.data.local.entity.TareaEntity
import com.example.unitime.presentation.navigation.Rutas
import com.example.unitime.presentation.tasks.TareaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioScreen(
    navController: NavController,
    horarioViewModel: HorarioViewModel = hiltViewModel(),
    tareaViewModel: TareaViewModel = hiltViewModel()
) {
    val clases by horarioViewModel.clases.collectAsState(initial = emptyList())
    val tareas by tareaViewModel.tareasPendientes.collectAsState(initial = emptyList())

    // Variables para diálogos de eliminación
    var claseParaBorrar by remember { mutableStateOf<ClaseEntity?>(null) }
    var tareaParaBorrar by remember { mutableStateOf<TareaEntity?>(null) } // NUEVO: Para el CU-16

    var mostrarMenuAgregar by remember { mutableStateOf(false) }

    // Días y Pestañas
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    val diaActual = remember {
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> "Lunes"
        }
    }

    var diaSeleccionado by remember { mutableStateOf(diaActual) }
    var tabSeleccionada by remember { mutableStateOf(0) }

    // NUEVO: Variable para saber qué materia quiere filtrar el estudiante (CU-14)
    // Si es null, significa que quiere ver "Todas" las materias.
    var filtroMateriaId by remember { mutableStateOf<Long?>(null) }

    // --- LÓGICA DE FILTROS ---
    val clasesFiltradas = clases.filter { it.diasDeLaSemana.contains(diaSeleccionado) }

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

        val coincideDia = diaDeLaTarea == diaSeleccionado
        // Validamos si la tarea pertenece a la materia que seleccionó el usuario (CU-14)
        val coincideMateria = filtroMateriaId == null || tarea.claseId == filtroMateriaId

        coincideDia && coincideMateria
    }

    // --- DIÁLOGOS DE CONFIRMACIÓN ---

    // Diálogo para borrar Clases
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

    // NUEVO: Diálogo para borrar Tareas (CU-16)
    tareaParaBorrar?.let { tarea ->
        AlertDialog(
            onDismissRequest = { tareaParaBorrar = null },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Estás seguro de eliminar permanentemente la tarea '${tarea.titulo}'?") },
            confirmButton = {
                TextButton(onClick = { tareaViewModel.eliminarTarea(tarea); tareaParaBorrar = null }) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { tareaParaBorrar = null }) { Text("Cancelar") } }
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
            ScrollableTabRow(
                selectedTabIndex = diasSemana.indexOf(diaSeleccionado),
                edgePadding = 8.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                diasSemana.forEachIndexed { index, dia ->
                    Tab(selected = diaSeleccionado == dia, onClick = { diaSeleccionado = dia }, text = { Text(dia) })
                }
            }

            TabRow(selectedTabIndex = tabSeleccionada) {
                Tab(selected = tabSeleccionada == 0, onClick = { tabSeleccionada = 0 }, text = { Text("📚 Clases (${clasesFiltradas.size})") })
                Tab(selected = tabSeleccionada == 1, onClick = { tabSeleccionada = 1 }, text = { Text("📝 Tareas (${tareasFiltradas.size})") })
            }

            if (tabSeleccionada == 0) {
                // --- VISTA DE CLASES ---
                if (clasesFiltradas.isEmpty()) {
                    EstadoVacio("No tienes clases este día.")
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(clasesFiltradas) { clase ->
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
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
                // --- VISTA DE TAREAS ---
                Column {
                    // NUEVO: Carrusel de filtros por materia (CU-14)
                    // Solo lo mostramos si el estudiante tiene clases registradas
                    if (clases.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = filtroMateriaId == null,
                                    onClick = { filtroMateriaId = null },
                                    label = { Text("Todas") }
                                )
                            }
                            items(clases) { clase ->
                                FilterChip(
                                    selected = filtroMateriaId == clase.id,
                                    onClick = { filtroMateriaId = clase.id },
                                    label = { Text(clase.nombre) }
                                )
                            }
                        }
                    }

                    if (tareasFiltradas.isEmpty()) {
                        EstadoVacio(if (filtroMateriaId == null) "¡Día libre! No hay tareas urgentes para este día." else "No hay tareas pendientes para esta materia.")
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
                                        Checkbox(
                                            checked = tarea.completada,
                                            onCheckedChange = { estaCompletada -> tareaViewModel.alternarEstadoTarea(tarea, estaCompletada) }
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
                                        // NUEVO: Botón de eliminar tarea (CU-16)
                                        IconButton(onClick = { tareaParaBorrar = tarea }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Borrar Tarea", tint = MaterialTheme.colorScheme.error)
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
}

@Composable
fun EstadoVacio(mensaje: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = mensaje, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}