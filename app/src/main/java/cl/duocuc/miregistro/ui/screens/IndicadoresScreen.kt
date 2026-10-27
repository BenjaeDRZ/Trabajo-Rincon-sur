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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicadoresScreen(
    metricas: Map<String, Any>,
    fechaActual: String,
    onFechaCambiada: (String) -> Unit,
    onVolver: () -> Unit
) {
    var fechaInput by remember { mutableStateOf(fechaActual) }
    val porAcceso = metricas["porAcceso"] as? Map<*, *> ?: emptyMap<Any, Any>()
    val perfil = metricas["perfil"] as? Map<*, *> ?: emptyMap<Any, Any>()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Indicadores Históricos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = fechaInput,
                    onValueChange = { fechaInput = it },
                    label = { Text("Fecha (AAAA-MM-DD)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onFechaCambiada(fechaInput) }, modifier = Modifier.height(56.dp)) {
                    Text("Buscar")
                }
            }

            Text("Resumen del día: $fechaActual", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Flujo y Ventas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Ingresos totales:")
                        Text("${metricas["ingresos"]}", fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Boletas (Transacciones):")
                        Text("${metricas["transacciones"]}", fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Conversión Bruta (RN-12):")
                        val conv = metricas["conversion"] as Double
                        Text(String.format(Locale.US, "%.1f %%", conv), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingresos por Acceso", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    listOf("A1", "A2", "A3", "A4").forEach { acceso ->
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Acceso $acceso:")
                            Text("${porAcceso[acceso] ?: 0} pers.", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Análisis de Tránsito", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Proporción Tránsito (RN-13):")
                        val prop = metricas["propTransito"] as Double
                        Text(String.format(Locale.US, "%.1f %%", prop), fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Tránsito Estimado (RN-14):")
                        Text("${metricas["transitoEstimado"]} pers.", fontWeight = FontWeight.Bold)
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Clientes Reales:", fontWeight = FontWeight.Bold)
                        Text("${metricas["flujoSinTransito"]} pers.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Conversión Real (RN-15):", fontWeight = FontWeight.Bold)
                        val convReal = metricas["conversionSinTransito"] as Double
                        Text(String.format(Locale.US, "%.1f %%", convReal), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Perfil del Tránsito (RF-11)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Edades: ${perfil["edades"]}", fontSize = 14.sp)
                    Text("Compañía: ${perfil["compania"]}", fontSize = 14.sp)
                    Text("Compras: ${perfil["compras"]}", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Volver al Menú", fontSize = 16.sp)
            }
        }
    }
}