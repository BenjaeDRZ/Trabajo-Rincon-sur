package cl.duocuc.miregistro.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun UbicacionScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    var coordenadas by remember { mutableStateOf<String?>(null) }

    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    coordenadas = "${location.latitude}, ${location.longitude}"
                } else {
                    coordenadas = "Buscando señal GPS... (Asegúrate de tener el GPS encendido)"
                }
            } catch (e: SecurityException) {
                coordenadas = "Error al obtener ubicación."
            }
        } else {
            coordenadas = "Permiso de GPS denegado."
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ubicación de la Tienda", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))

            Text("Coordenadas actuales:", color = MaterialTheme.colorScheme.onBackground)
            Text(coordenadas ?: "No obtenidas", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                coordenadas = "${location.latitude}, ${location.longitude}"
                            } else {
                                coordenadas = "Buscando señal GPS..."
                            }
                        } catch (e: SecurityException) {}
                    } else {
                        launcherPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Obtener mi GPS")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=-33.4489,-70.6693")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    context.startActivity(mapIntent)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Abrir en Google Maps")
            }

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Volver")
            }
        }
    }
}