package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.Colaborador

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginExitoso: (Colaborador) -> Unit,
    validarCodigo: (String) -> Colaborador?
) {
    var codigo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // Surface asegura que el fondo sea el correcto según el tema (claro u oscuro)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título con color explícito del tema
            Text(
                text = "Rincón Sur",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Segmentación de Flujo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Ingresa tu código (Ej: C-01)",
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it; error = null },
                label = { Text("Código de colaborador") },
                isError = error != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val colaborador = validarCodigo(codigo.trim().uppercase())
                    if (colaborador != null) {
                        onLoginExitoso(colaborador)
                    } else {
                        error = "Código no encontrado"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("INGRESAR", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}