package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.model.RegistroPersona
import cl.duocuc.miregistro.model.Sesion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorScreen(
    supervisorLogueado: Colaborador,
    sesion: Sesion?,
    registros: List<RegistroPersona>,
    onValidar: (Boolean, String?) -> Unit, 
    onVolver: () -> Unit
) {
    var mostrarDialogoRechazo by remember { mutableStateOf(false) }
    var motivoRechazo by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    if (sesion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay sesiones para revisar", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Revisión de Sesión", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Sesión: ${sesion.numero}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Acceso: ${sesion.codigoAcceso} | Fecha: ${sesion.fecha}")
                    Text("Observador: ${sesion.observador}")
                    Text("Estado actual: ${sesion.estado}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total registros: ${registros.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (mensajeError != null) {
                Text(mensajeError!!, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            
            Button(
                onClick = {
                    if (registros.size < 30) {
                        mensajeError = "Error: Mínimo 30 registros para validar (RN-10)."
                    } else if (sesion.observador == supervisorLogueado.codigo) {
                        mensajeError = "Error: No puedes validar tu propia sesión (RN-10)."
                    } else {
                        onValidar(false, null)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("VALIDAR SESIÓN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            
            OutlinedButton(
                onClick = {
                    if (sesion.observador == supervisorLogueado.codigo) {
                        mensajeError = "Error: No puedes rechazar tu propia sesión (RN-10)."
                    } else {
                        mostrarDialogoRechazo = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("RECHAZAR SESIÓN", fontSize = 18.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Menú")
            }
        }
    }

    
    if (mostrarDialogoRechazo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRechazo = false },
            title = { Text("Motivo del rechazo") },
            text = {
                OutlinedTextField(
                    value = motivoRechazo,
                    onValueChange = { motivoRechazo = it },
                    label = { Text("Escribe el motivo...") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (motivoRechazo.isNotBlank()) {
                        onValidar(true, motivoRechazo)
                        mostrarDialogoRechazo = false
                    }
                }) {
                    Text("Confirmar Rechazo", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoRechazo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}