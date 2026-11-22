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
import cl.duocuc.miregistro.ui.components.BotonAtras

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

    val estadoTransito = metricas["estadoTransito"] as? String ?: "SIN_INFO"
    val fuenteTransito = metricas["fuenteTransito"] as? String ?: ""

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BotonAtras(onClick = onVolver)   // ← PRIMERA LÍNEA del Column
            Text("Indicadores")
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

            // --- Flujo y ventas ---
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
                        val conv = metricas["conversion"] as? Double ?: 0.0
                        Text(String.format(Locale.US, "%.1f %%", conv), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // --- Ingresos por acceso ---
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

            // --- Análisis de tránsito ---
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Análisis de Tránsito", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)

                    // Chip de estado (medido / estimado / sin info)
                    val (etiquetaEstado, colorEstado) = when (estadoTransito) {
                        "MEDIDO" -> "✓ Medido" to MaterialTheme.colorScheme.primary
                        "ESTIMADO" -> "≈ Estimado" to MaterialTheme.colorScheme.tertiary
                        else -> "⚠ Sin información" to MaterialTheme.colorScheme.outline
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estado del dato:", fontWeight = FontWeight.Bold)
                        AssistChip(
                            onClick = {},
                            label = { Text(etiquetaEstado, color = colorEstado, fontWeight = FontWeight.Bold) }
                        )
                    }
                    Text("Fuente: $fuenteTransito", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Proporción Tránsito (RN-13):")
                        val prop = metricas["propTransito"] as? Double ?: 0.0
                        Text(String.format(Locale.US, "%.1f %%", prop), fontWeight = FontWeight.Bold)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Tránsito Estimado (RN-14):")
                        Text("${metricas["transitoEstimado"]} pers.", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Clientes Reales:", fontWeight = FontWeight.Bold)
                        Text("${metricas["flujoSinTransito"]} pers.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Conversión Real (RN-15):", fontWeight = FontWeight.Bold)
                        val convReal = metricas["conversionSinTransito"] as? Double ?: 0.0
                        Text(String.format(Locale.US, "%.1f %%", convReal), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // --- Perfil del tránsito ---
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