package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.model.ModoTema
import cl.duocuc.miregistro.ui.components.SelectorTema
import cl.duocuc.miregistro.ui.components.BotonAtras
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginExitoso: (Colaborador) -> Unit,
    validarCodigo: (String) -> Colaborador?,
    modoTema: ModoTema,
    onCambiarModoTema: (ModoTema) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Contraseña fija para todos los colaboradores (prototipo)
    val CONTRASENA_CORRECTA = "12345"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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

                // --- Campo: código de colaborador ---
                OutlinedTextField(
                    value = codigo,
                    onValueChange = {
                        codigo = it
                        error = null
                    },
                    label = { Text("Código de colaborador") },
                    isError = error != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- Campo: contraseña ---
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                        error = null
                    },
                    label = { Text("Contraseña") },
                    isError = error != null,
                    singleLine = true,
                    visualTransformation = if (contrasenaVisible) {
                        androidx.compose.ui.text.input.VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        TextButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                            Text(
                                text = if (contrasenaVisible) "Ocultar" else "Ver",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Botón ingresar ---
                Button(
                    onClick = {
                        val codigoLimpio = codigo.trim().uppercase()
                        when {
                            codigoLimpio.isBlank() -> error = "Ingresa tu código de colaborador"
                            contrasena.isBlank() -> error = "Ingresa tu contraseña"
                            contrasena != CONTRASENA_CORRECTA -> error = "Contraseña incorrecta"
                            else -> {
                                val colaborador = validarCodigo(codigoLimpio)
                                if (colaborador != null) {
                                    onLoginExitoso(colaborador)
                                } else {
                                    error = "Código no encontrado"
                                }
                            }
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

            SelectorTema(
                modoActual = modoTema,
                onCambiar = onCambiarModoTema,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp)
            )
        }
    }
}