package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.ui.components.BotonAtras
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun MenuPrincipalScreen(
    colaborador: Colaborador,
    onAtras: () -> Unit,
    onVerObservador: () -> Unit,
    onVerSupervisor: () -> Unit,
    onVerIndicadores: () -> Unit,
    onVerEventos: () -> Unit,
    onProgramarSesion: () -> Unit,
    onVerUbicacion: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVerMetas: () -> Unit,
    onImportarConteos: () -> Unit, // NUEVO
    onImportarTransacciones: () -> Unit, // NUEVO
    onVerHistorial: () -> Unit, // NUEVO
    onVerColaboradores: () -> Unit // NUEVO
) {
    val perfil = colaborador.codigoPuesto

    val puedeObservar      = perfil == "P-04"
    val puedeSupervisar    = perfil == "P-03"
    val puedeProgramar     = perfil == "P-03"
    val puedeVerIndicadores = perfil == "P-01" || perfil == "P-02" || perfil == "P-03"
    val puedeVerEventos    = perfil == "P-02" || perfil == "P-03"
    val puedeVerMetas      = perfil == "P-01" || perfil == "P-02"
    val puedeVerHistorial  = perfil == "P-01" || perfil == "P-02" || perfil == "P-03"
    val puedeVerColabs     = perfil == "P-01" || perfil == "P-02"

    val nombreRol = when (perfil) {
        "P-01" -> "Jefa de tienda"
        "P-02" -> "Subgerente comercial"
        "P-03" -> "Supervisor/a de piso"
        "P-04" -> "Colaborador observador"
        else -> "Colaborador"
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            BotonAtras(
                onClick = onAtras,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bienvenido(a)", style = MaterialTheme.typography.titleMedium)
                Text(colaborador.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(nombreRol, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                if (puedeObservar) {
                    Button(onClick = onVerObservador, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Módulo Observador") }
                    Spacer(Modifier.height(8.dp))
                }
                if (puedeSupervisar) {
                    Button(onClick = onVerSupervisor, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Módulo Supervisor") }
                    Spacer(Modifier.height(8.dp))
                }
                if (puedeProgramar) {
                    Button(onClick = onProgramarSesion, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Programar Sesión") }
                    Spacer(Modifier.height(8.dp))
                }
                if (puedeVerIndicadores) {
                    Button(onClick = onVerIndicadores, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Módulo Indicadores") }
                    Spacer(Modifier.height(8.dp))
                }
                if (puedeVerEventos) {
                    Button(onClick = onVerEventos, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Registrar Eventos") }
                    Spacer(Modifier.height(8.dp))
                }
                if (puedeVerMetas) {
                    Button(onClick = onVerMetas, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Registrar Meta de Conversión") }
                    Spacer(Modifier.height(8.dp))
                }

                if (perfil == "P-02") {
                    Button(onClick = onImportarConteos, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text("Importar Conteos (Sistema)")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onImportarTransacciones, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text("Importar Transacciones (Cajas)")
                    }
                    Spacer(Modifier.height(8.dp))
                }

                if (puedeVerHistorial) {
                    Button(onClick = onVerHistorial, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text("Historial de Sesiones")
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (puedeVerColabs) {
                    Button(onClick = onVerColaboradores, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text("Ver Colaboradores")
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Button(onClick = onVerUbicacion, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Ubicación GPS / Maps")
                }
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}