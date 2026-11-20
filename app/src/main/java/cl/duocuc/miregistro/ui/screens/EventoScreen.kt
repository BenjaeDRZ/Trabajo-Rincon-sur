package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoScreen(
    onGuardar: (String, String, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var fecha by remember { mutableStateOf("2026-09-15") }
    var tipo by remember { mutableStateOf("Clima") }
    var acceso by remember { mutableStateOf("Todos") }
    var descripcion by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    val tipos = listOf("Clima", "Feriado", "Evento en el mall", "Cierre de acceso", "Campaña comercial", "Otro")
    val accesos = listOf("Todos", "A1", "A2", "A3", "A4")

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Registrar Evento del Día", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Anota anomalías que afecten el flujo (lluvia, cierres, etc.)", color = MaterialTheme.colorScheme.onBackground)

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            var expandidoTipo by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expandidoTipo, onExpandedChange = { expandidoTipo = !expandidoTipo }) {
                OutlinedTextField(
                    value = tipo, onValueChange = {}, readOnly = true,
                    label = { Text("Tipo de Evento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoTipo) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandidoTipo, onDismissRequest = { expandidoTipo = false }) {
                    tipos.forEach { t ->
                        DropdownMenuItem(text = { Text(t) }, onClick = { tipo = t; expandidoTipo = false })
                    }
                }
            }

            var expandidoAcceso by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expandidoAcceso, onExpandedChange = { expandidoAcceso = !expandidoAcceso }) {
                OutlinedTextField(
                    value = acceso, onValueChange = {}, readOnly = true,
                    label = { Text("Acceso Afectado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoAcceso) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandidoAcceso, onDismissRequest = { expandidoAcceso = false }) {
                    accesos.forEach { a ->
                        DropdownMenuItem(text = { Text(a) }, onClick = { acceso = a; expandidoAcceso = false })
                    }
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción breve") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (mensaje != null) {
                Text(mensaje!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (descripcion.isBlank()) {
                        mensaje = "La descripción es obligatoria (RN-16)"
                    } else {
                        onGuardar(fecha, tipo, acceso, descripcion)
                        mensaje = "¡Evento guardado con éxito!"
                        descripcion = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("GUARDAR EVENTO", fontSize = 16.sp)
            }

            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Volver al Menú")
            }
        }
    }
}