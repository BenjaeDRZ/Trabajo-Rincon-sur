
package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.ui.components.BotonAtras

@Composable
fun MetaScreen(
    onGuardar: (String, Double) -> Unit,
    mensaje: String?,
    onVolver: () -> Unit,
    onIrConteos: () -> Unit,
    onIrTransacciones: () -> Unit
) {
    var mes by remember { mutableStateOf("2026-11") }
    var valorMeta by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BotonAtras(onClick = onVolver)

            Text(
                "Registrar Meta Mensual",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = mes,
                onValueChange = { mes = it },
                label = { Text("Mes (AAAA-MM)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valorMeta,
                onValueChange = { valorMeta = it },
                label = { Text("Meta de conversión (%)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val meta = valorMeta.toDoubleOrNull()
                    if (meta != null && mes.isNotBlank()) {
                        onGuardar(mes, meta)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("GUARDAR META", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider()

            Text(
                "Importación de datos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onIrConteos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("IMPORTAR CONTEOS")
            }

            Button(
                onClick = onIrTransacciones,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("IMPORTAR TRANSACCIONES")
            }

            if (mensaje != null) {
                Text(
                    mensaje,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
