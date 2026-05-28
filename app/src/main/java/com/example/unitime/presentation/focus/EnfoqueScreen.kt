package com.example.unitime.presentation.focus

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.RingtoneManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnfoqueScreen(
    navController: NavController,
    viewModel: EnfoqueViewModel = hiltViewModel()
) {
    val contexto = LocalContext.current
    var inputMinutos by remember { mutableStateOf("25") }

    val ringtone = remember {
        val uriAlarma = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        RingtoneManager.getRingtone(contexto, uriAlarma)
    }

    DisposableEffect(Unit) {
        val sensorManager = contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                val z = event.values[2]

                if ((viewModel.esperandoPosicion || viewModel.alarmaActivada) && z < -7.0f) {
                    viewModel.confirmarPosicion()
                }
                else if (viewModel.estaEnfocado && !viewModel.alarmaActivada && z > -5.0f) {
                    viewModel.penalizarPorLevantar()
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorListener, acelerometro, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(sensorListener)
            if (ringtone.isPlaying) ringtone.stop()
        }
    }

    LaunchedEffect(viewModel.alarmaActivada) {
        if (viewModel.alarmaActivada) {
            ringtone.play()
        } else {
            if (ringtone.isPlaying) ringtone.stop()
        }
    }

    val colorFondo = when {
        viewModel.alarmaActivada -> MaterialTheme.colorScheme.errorContainer
        viewModel.esperandoPosicion -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.background
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modo Enfoque") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.detenerEnfoque()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (viewModel.esperandoPosicion) {
                Text(
                    text = "Gira el teléfono y ponlo sobre la mesa",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("El temporizador iniciará automáticamente cuando esté boca abajo.")
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedButton(onClick = { viewModel.detenerEnfoque() }) {
                    Text("Cancelar")
                }
            }
            else if (!viewModel.estaEnfocado && !viewModel.alarmaActivada) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = inputMinutos,
                    onValueChange = { inputMinutos = it },
                    label = { Text("Minutos de estudio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.iniciarPreparacion(inputMinutos.toIntOrNull() ?: 0) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Empezar a Estudiar")
                }
            }
            else if (viewModel.alarmaActivada) {
                Text("¡CONCÉNTRATE!", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Se pausó tu tiempo por mirar el teléfono.")
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.detenerEnfoque() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Rendirse y salir")
                }
            }
            else {
                val minutos = viewModel.tiempoRestanteEnSegundos / 60
                val segundos = viewModel.tiempoRestanteEnSegundos % 60
                Text(
                    text = String.format("%02d:%02d", minutos, segundos),
                    fontSize = 80.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Celular boca abajo. ¡No lo toques!")
                Spacer(modifier = Modifier.height(48.dp))
                OutlinedButton(onClick = { viewModel.detenerEnfoque() }) {
                    Text("Cancelar temporizador")
                }
            }
        }
    }
}