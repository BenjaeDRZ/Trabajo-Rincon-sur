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
    onCerrarSesion: () -> Unit
) {
    val perfil = colaborador.codigoPuesto
    val puedeObservar = perfil == "P-04" || perfil == "P-03"
    val puedeSupervisar = perfil == "P-01" || perfil == "P-02" || perfil == "P-03"
    val puedeVerIndicadores = perfil == "P-01" || perfil == "P-02" || perfil == "P-03"
    val puedeVerEventos = perfil == "P-02" || perfil == "P-03"

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenido(a)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(colaborador.nombre, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text("Perfil: $perfil", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            if (puedeObservar) {
                Button(onClick = onVerObservador, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Módulo Observador", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (puedeSupervisar) {
                Button(onClick = onVerSupervisor, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Módulo Supervisor", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (puedeVerIndicadores) {
                Button(onClick = onVerIndicadores, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Módulo Indicadores", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (puedeVerEventos) {
                Button(onClick = onVerEventos, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("Registrar Eventos", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Cerrar Sesión", fontSize = 16.sp)
            }
        }
    }
}