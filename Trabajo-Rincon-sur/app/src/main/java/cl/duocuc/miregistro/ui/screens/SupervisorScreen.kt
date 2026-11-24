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
import cl.duocuc.miregistro.ui.components.BotonAtras
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

    val esPropiaSesion = sesion?.observador == supervisorLogueado.codigo
    val cumpleMinimo = registros.count { !it.anulado } >= 30

    if (sesion == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay sesiones finalizadas para revisar", fontSize = 20.sp)
        }
        return
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BotonAtras(onClick = onVolver)   // ← PRIMERA LÍNEA del Column
            Text("Menu del Supervisor")
            Text("Revisión de Sesión", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Sesión: ${sesion.numero}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Acceso: ${sesion.codigoAcceso} | Fecha: ${sesion.fecha}")
                    Text("Observador: ${sesion.observador}")
                    Text("Estado: ${sesion.estado}", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Registros válidos: ${registros.count { !it.anulado }} / mínimo 30 (RN-10)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cumpleMinimo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    mensajeError = when {
                        esPropiaSesion -> "No puedes validar tu propia sesión (RN-11)"
                        !cumpleMinimo -> "Mínimo 30 registros válidos para validar (RN-10)"
                        else -> { onValidar(false, null); null }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !esPropiaSesion && cumpleMinimo
            ) {
                Text("VALIDAR SESIÓN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    if (esPropiaSesion) {
                        mensajeError = "No puedes rechazar tu propia sesión (RN-11)"
                    } else {
                        mostrarDialogoRechazo = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !esPropiaSesion
            ) {
                Text("RECHAZAR SESIÓN", fontSize = 18.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Menú")
            }
        }
    }

    if (mostrarDialogoRechazo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRechazo = false },
            title = { Text("Motivo del rechazo (RN-16)") },
            text = {
                OutlinedTextField(
                    value = motivoRechazo,
                    onValueChange = { motivoRechazo = it },
                    label = { Text("Escribe el motivo...") },
                    isError = motivoRechazo.isBlank()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValidar(true, motivoRechazo.trim())
                        mostrarDialogoRechazo = false
                        motivoRechazo = ""
                    },
                    enabled = motivoRechazo.isNotBlank()
                ) {
                    Text("Confirmar Rechazo", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoRechazo = false; motivoRechazo = "" }) {
                    Text("Cancelar")
                }
            }
        )
    }
}