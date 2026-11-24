package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.Sesion
import cl.duocuc.miregistro.ui.components.BotonAtras

@Composable
fun HistorialScreen(sesiones: List<Sesion>, onVolver: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            BotonAtras(onClick = onVolver)
            Text("Historial de Sesiones", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            if (sesiones.isEmpty()) {
                Text("No hay sesiones registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sesiones) { sesion ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Sesión: ${sesion.numero}", fontWeight = FontWeight.Bold)
                                Text("Acceso: ${sesion.codigoAcceso} | Fecha: ${sesion.fecha}")
                                Text("Estado: ${sesion.estado}", color = if(sesion.estado == "VALIDADA") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}