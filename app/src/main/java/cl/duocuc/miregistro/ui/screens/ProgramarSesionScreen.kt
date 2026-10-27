package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgramarSesionScreen(onGuardar: (String, String, String, String, String, String) -> Unit, onVolver: () -> Unit) {
    var acceso by remember { mutableStateOf("A4") }
    var fecha by remember { mutableStateOf("2026-09-15") }
    var inicio by remember { mutableStateOf("13:00") }
    var termino by remember { mutableStateOf("14:00") }
    var observador by remember { mutableStateOf("C-05") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Programar Sesión", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(value = acceso, onValueChange = { acceso = it }, label = { Text("Acceso (A1-A4)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = inicio, onValueChange = { inicio = it }, label = { Text("Hora Inicio (HH:mm)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = termino, onValueChange = { termino = it }, label = { Text("Hora Término (HH:mm)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = observador, onValueChange = { observador = it }, label = { Text("Observador (Ej: C-05)") }, modifier = Modifier.fillMaxWidth())

            Button(onClick = {
                val nuevoNumero = "S-00${(10..99).random()}"
                onGuardar(nuevoNumero, acceso, fecha, inicio, termino, observador)
            }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("ASIGNAR SESIÓN")
            }
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Volver")
            }
        }
    }
}