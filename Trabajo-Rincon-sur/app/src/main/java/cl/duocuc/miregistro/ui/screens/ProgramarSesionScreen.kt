package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.accesos
import cl.duocuc.miregistro.model.colaboradores
import cl.duocuc.miregistro.ui.components.BotonAtras
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramarSesionScreen(
    onGuardar: (String, String, String, String, String, String) -> Result<Unit>,
    onVolver: () -> Unit
) {
    var acceso by remember { mutableStateOf("A4") }
    var fecha by remember { mutableStateOf("2026-09-15") }
    var horaInicio by remember { mutableStateOf("13:00") }
    var horaTermino by remember { mutableStateOf("13:50") }
    var observador by remember { mutableStateOf("C-05") }

    var dropdownAccesoAbierto by remember { mutableStateOf(false) }
    var dropdownObservadorAbierto by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var guardadoExitoso by remember { mutableStateOf(false) }

    val accesosDisponibles = accesos.map { it.codigo }
    val observadoresDisponibles = colaboradores.filter { it.codigoPuesto == "P-04" || it.codigoPuesto == "P-03" }

    val duracionMin = calcularDuracion(horaInicio, horaTermino)
    val duracionValida = duracionMin != null && duracionMin in 30..60
    val fechaValida = fecha.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))
    val formularioValido = duracionValida && fechaValida

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BotonAtras(onClick = onVolver)   // ← PRIMERA LÍNEA del Column
            Text("Programar Sesiones")
            Text("Programar Sesión", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Duración obligatoria: entre 30 y 60 minutos (CA-3 HU-04)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            ExposedDropdownMenuBox(
                expanded = dropdownAccesoAbierto,
                onExpandedChange = { dropdownAccesoAbierto = !dropdownAccesoAbierto }
            ) {
                OutlinedTextField(
                    value = acceso,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Acceso") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownAccesoAbierto) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownAccesoAbierto,
                    onDismissRequest = { dropdownAccesoAbierto = false }
                ) {
                    accesosDisponibles.forEach { a ->
                        val info = accesos.find { it.codigo == a }
                        DropdownMenuItem(
                            text = { Text("$a - ${info?.nombre ?: ""}") },
                            onClick = { acceso = a; dropdownAccesoAbierto = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it; errorMensaje = null },
                label = { Text("Fecha (AAAA-MM-DD)") },
                isError = fecha.isNotBlank() && !fechaValida,
                supportingText = {
                    if (fecha.isNotBlank() && !fechaValida) Text("Formato inválido. Usa AAAA-MM-DD")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it; errorMensaje = null },
                    label = { Text("Hora inicio") },
                    placeholder = { Text("HH:mm") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = horaTermino,
                    onValueChange = { horaTermino = it; errorMensaje = null },
                    label = { Text("Hora término") },
                    placeholder = { Text("HH:mm") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            if (duracionMin != null) {
                val colorDuracion = if (duracionValida) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                Text(
                    text = "Duración: $duracionMin min ${if (duracionValida) "✓" else "(debe estar entre 30 y 60)"}",
                    color = colorDuracion,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            ExposedDropdownMenuBox(
                expanded = dropdownObservadorAbierto,
                onExpandedChange = { dropdownObservadorAbierto = !dropdownObservadorAbierto }
            ) {
                OutlinedTextField(
                    value = observador,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Observador") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownObservadorAbierto) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownObservadorAbierto,
                    onDismissRequest = { dropdownObservadorAbierto = false }
                ) {
                    observadoresDisponibles.forEach { colab ->
                        DropdownMenuItem(
                            text = { Text("${colab.codigo} - ${colab.nombre}") },
                            onClick = { observador = colab.codigo; dropdownObservadorAbierto = false }
                        )
                    }
                }
            }

            errorMensaje?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Medium)
                }
            }

            if (guardadoExitoso) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓ Sesión programada correctamente", color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    errorMensaje = null
                    guardadoExitoso = false
                    val nuevoNumero = "S-${(100..999).random()}"
                    val resultado = onGuardar(nuevoNumero, acceso, fecha, horaInicio, horaTermino, observador)
                    resultado.fold(
                        onSuccess = { guardadoExitoso = true },
                        onFailure = { errorMensaje = it.message ?: "Error desconocido" }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = formularioValido
            ) {
                Text("PROGRAMAR SESIÓN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Volver al Menú")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun calcularDuracion(inicio: String, termino: String): Int? {
    val partesInicio = inicio.split(":")
    val partesTermino = termino.split(":")
    if (partesInicio.size != 2 || partesTermino.size != 2) return null

    val hIni = partesInicio[0].toIntOrNull() ?: return null
    val mIni = partesInicio[1].toIntOrNull() ?: return null
    val hFin = partesTermino[0].toIntOrNull() ?: return null
    val mFin = partesTermino[1].toIntOrNull() ?: return null

    if (hIni !in 0..23 || mIni !in 0..59) return null
    if (hFin !in 0..23 || mFin !in 0..59) return null

    val totalIni = hIni * 60 + mIni
    val totalFin = hFin * 60 + mFin
    if (totalFin <= totalIni) return null
    return totalFin - totalIni
}