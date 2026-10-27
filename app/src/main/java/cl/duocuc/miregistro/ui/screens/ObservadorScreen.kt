package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.model.RegistroPersona
import cl.duocuc.miregistro.model.Sesion

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ObservadorScreen(
    colaborador: Colaborador,
    sesion: Sesion?,
    registros: List<RegistroPersona>,
    onRegistrar: (String, String, String, Boolean, String?) -> Unit,
    onAnularUltimo: () -> Unit,
    onFinalizarSesion: () -> Unit
) {
    var clasificacion by remember { mutableStateOf("") }
    var rangoEtario by remember { mutableStateOf("") }
    var compania by remember { mutableStateOf("") }
    var traeCompras by remember { mutableStateOf<Boolean?>(null) }
    var accesoDestino by remember { mutableStateOf<String?>(null) }

    val isTransito = clasificacion == "TRANSITO"
    val formularioValido = clasificacion.isNotEmpty() && rangoEtario.isNotEmpty() &&
            compania.isNotEmpty() && traeCompras != null &&
            (!isTransito || accesoDestino != null)

    if (sesion == null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "No hay sesión activa\n\nPídele a tu supervisor que programe una sesión.",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Sesión: ${sesion.numero} | Acceso: ${sesion.codigoAcceso}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Observador: ${colaborador.nombre} (${colaborador.codigo})", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Registros: ${registros.size}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                }
            }

            Text("1. Clasificación", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipGrande(texto = "Tránsito", seleccionado = clasificacion == "TRANSITO") { clasificacion = "TRANSITO" }
                ChipGrande(texto = "Visita", seleccionado = clasificacion == "VISITA") { clasificacion = "VISITA"; accesoDestino = null }
                ChipGrande(texto = "No visto", seleccionado = clasificacion == "NO_DETERMINADO") { clasificacion = "NO_DETERMINADO" }
            }

            Text("2. Edad Aproximada", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipGrande(texto = "< 18", seleccionado = rangoEtario == "MENOR_18") { rangoEtario = "MENOR_18" }
                ChipGrande(texto = "18-25", seleccionado = rangoEtario == "18_25") { rangoEtario = "18_25" }
                ChipGrande(texto = "26-40", seleccionado = rangoEtario == "26_40") { rangoEtario = "26_40" }
                ChipGrande(texto = "41-60", seleccionado = rangoEtario == "41_60") { rangoEtario = "41_60" }
                ChipGrande(texto = "61+", seleccionado = rangoEtario == "61_MAS") { rangoEtario = "61_MAS" }
            }

            Text("3. Compañía", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipGrande(texto = "Solo", seleccionado = compania == "SOLO") { compania = "SOLO" }
                ChipGrande(texto = "Pareja", seleccionado = compania == "PAREJA") { compania = "PAREJA" }
                ChipGrande(texto = "Con niños", seleccionado = compania == "CON_NINOS") { compania = "CON_NINOS" }
                ChipGrande(texto = "Grupo", seleccionado = compania == "GRUPO") { compania = "GRUPO" }
            }

            Text("4. ¿Trae compras de otra tienda?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipGrande(texto = "Sí", seleccionado = traeCompras == true) { traeCompras = true }
                ChipGrande(texto = "No", seleccionado = traeCompras == false) { traeCompras = false }
            }

            if (isTransito) {
                Text("5. ¿Hacia qué acceso se dirige?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("A1", "A2", "A3", "A4").forEach { acceso ->
                        if (acceso != sesion.codigoAcceso) {
                            ChipGrande(texto = acceso, seleccionado = accesoDestino == acceso) { accesoDestino = acceso }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onRegistrar(clasificacion, rangoEtario, compania, traeCompras!!, accesoDestino)
                    clasificacion = ""
                    rangoEtario = ""
                    compania = ""
                    traeCompras = null
                    accesoDestino = null
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(72.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("REGISTRAR PERSONA", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onAnularUltimo,
                    enabled = registros.isNotEmpty(),
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Text("Anular", fontSize = 16.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onFinalizarSesion,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("Finalizar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipGrande(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = {
            Text(
                text = texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
            )
        },
        shape = RoundedCornerShape(8.dp)
    )
}