package com.example.unitime.presentation.tasks

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.unitime.data.local.entity.ClaseEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.unitime.data.local.entity.TareaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTareaScreen(
    navController: NavController,
    viewModel: TareaViewModel = hiltViewModel(),
    tareaId: Long? = null
) {
    val contexto = LocalContext.current
    val estadoUi = viewModel.estadoUi

    // Obtenemos las clases disponibles para el menú desplegable (CU-02)
    val clasesDisponibles by viewModel.clasesDisponibles.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("MEDIA") } // CU-22: Prioridad por defecto

    // Variables para la fecha de entrega
    var fechaSeleccionada by remember { mutableStateOf<Long?>(null) }
    var textoFecha by remember { mutableStateOf("") }

    // Variables para el menú desplegable de clases (CU-02)
    var claseSeleccionada by remember { mutableStateOf<ClaseEntity?>(null) }
    var expandirMenuClases by remember { mutableStateOf(false) }

    //CU-10: Seleccionar fecha de entrega (DatePicker)
    val calendario = Calendar.getInstance()
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    LaunchedEffect(tareaId) {
        if (tareaId != null) {
            val tareaExistente = viewModel.obtenerTareaPorId(tareaId)
            if (tareaExistente != null) {
                // Asigna tus variables de estado aquí (ejemplo:)
                titulo = tareaExistente.titulo
                descripcion = tareaExistente.descripcion
                // etc...
            }
        }
    }

    fun mostrarCalendario() {
        val datePickerDialog = DatePickerDialog(
            contexto,
            { _, anio, mes, dia ->
                calendario.set(anio, mes, dia)
                fechaSeleccionada = calendario.timeInMillis
                textoFecha = formatoFecha.format(calendario.time)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        // Bloqueamos fechas pasadas (FA-01 del CU-10)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    LaunchedEffect(estadoUi) {
        when (estadoUi) {
            is TareaUiState.Exito -> {
                Toast.makeText(contexto, "Tarea guardada correctamente", Toast.LENGTH_SHORT).show()
                viewModel.reiniciarEstado()
                navController.popBackStack()
            }
            is TareaUiState.Error -> {
                Toast.makeText(contexto, estadoUi.mensaje, Toast.LENGTH_LONG).show()
                viewModel.reiniciarEstado()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Tarea") },
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
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la tarea") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Selector de Fecha (CU-10) [cite: 12]
            OutlinedTextField(
                value = textoFecha,
                onValueChange = { },
                label = { Text("Fecha de entrega") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarCalendario() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Menú desplegable para seleccionar la materia vinculada (CU-02)
            ExposedDropdownMenuBox(
                expanded = expandirMenuClases,
                onExpandedChange = { expandirMenuClases = !expandirMenuClases }
            ) {
                OutlinedTextField(
                    value = claseSeleccionada?.nombre ?: "Sin clase (General)",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Materia vinculada") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMenuClases) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandirMenuClases,
                    onDismissRequest = { expandirMenuClases = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sin clase (General)") },
                        onClick = {
                            claseSeleccionada = null
                            expandirMenuClases = false
                        }
                    )
                    clasesDisponibles.forEach { clase ->
                        DropdownMenuItem(
                            text = { Text(clase.nombre) },
                            onClick = {
                                claseSeleccionada = clase
                                expandirMenuClases = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (tareaId != null) {
                        // ACTUALIZAR: Creamos el objeto con el ID existente para sobreescribir
                        val tareaActualizada = TareaEntity(
                            id = tareaId,
                            titulo = titulo,
                            descripcion = descripcion,
                            fechaEntrega = fechaSeleccionada ?: 0L,
                            prioridad = prioridad,
                            claseId = claseSeleccionada?.id,
                            completada = false // Asumimos que al editar sigue pendiente
                        )
                        viewModel.actualizarTarea(tareaActualizada)
                    } else {
                        // GUARDAR NUEVA: Dejamos tu código original exactamente como lo tenías
                        viewModel.guardarTarea(
                            titulo = titulo,
                            descripcion = descripcion,
                            fechaEntrega = fechaSeleccionada ?: 0L,
                            prioridad = prioridad,
                            claseId = claseSeleccionada?.id
                        )
                    }

                    // Regresa a la pantalla principal
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                // El texto del botón cambia dependiendo de si estamos editando o creando
                Text(if (tareaId != null) "Actualizar Tarea" else "Guardar Tarea")
            }
        }
    }
}