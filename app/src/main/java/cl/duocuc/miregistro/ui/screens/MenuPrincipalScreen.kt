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

@Composable
fun MenuPrincipalScreen(
    colaborador: Colaborador,
    onVerObservador: () -> Unit,
    onVerSupervisor: () -> Unit,
    onVerIndicadores: () -> Unit,
    onVerEventos: () -> Unit,
    onProgramarSesion: () -> Unit,
    onVerUbicacion: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val perfil = colaborador.codigoPuesto

    // ==========================================================
    // PERMISOS POR PERFIL (RN-19)
    // P-01: Jefa de tienda         → Indicadores
    // P-02: Subgerente comercial   → Indicadores + Eventos
    // P-03: Supervisor/a de piso   → Supervisor + Programar Sesión + Eventos + Indicadores
    // P-04: Colaborador observador → Observador
    // ==========================================================

    val puedeObservar      = perfil == "P-04"
    val puedeSupervisar    = perfil == "P-03"
    val puedeProgramar     = perfil == "P-03"
    val puedeVerIndicadores = perfil == "P-01" || perfil == "P-02" || perfil == "P-03"
    val puedeVerEventos    = perfil == "P-02" || perfil == "P-03"
    val puedeVerUbicacion  = true // Todos pueden ver la ubicación de la tienda

    // Etiqueta descriptiva del rol (para mostrar en pantalla)
    val nombreRol = when (perfil) {
        "P-01" -> "Jefa de tienda"
        "P-02" -> "Subgerente comercial"
        "P-03" -> "Supervisor/a de piso"
        "P-04" -> "Colaborador observador"
        else -> "Colaborador"
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido(a)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(colaborador.nombre, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(nombreRol, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Código: ${colaborador.codigo}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // --- Módulo Observador (solo P-04) ---
            if (puedeObservar) {
                Button(onClick = onVerObservador, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Módulo Observador", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Módulo Supervisor (solo P-03) ---
            if (puedeSupervisar) {
                Button(onClick = onVerSupervisor, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Módulo Supervisor", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Programar Sesión (solo P-03) ---
            if (puedeProgramar) {
                Button(onClick = onProgramarSesion, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Programar Sesión", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Indicadores (P-01, P-02, P-03) ---
            if (puedeVerIndicadores) {
                Button(onClick = onVerIndicadores, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Módulo Indicadores", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Eventos (P-02, P-03) ---
            if (puedeVerEventos) {
                Button(onClick = onVerEventos, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Registrar Eventos", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Ubicación (todos) ---
            if (puedeVerUbicacion) {
                Button(
                    onClick = onVerUbicacion,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Ubicación GPS / Maps", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Cerrar Sesión", fontSize = 16.sp)
            }
        }
    }
}