package cl.duocuc.miregistro.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.model.ModoTema
import cl.duocuc.miregistro.ui.screens.*
import cl.duocuc.miregistro.viewmodel.TiendaViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AppNavegacion(
    modoTema: ModoTema,
    onCambiarModoTema: (ModoTema) -> Unit
) {
    val navController = rememberNavController()
    val tiendaViewModel: TiendaViewModel = viewModel()

    var colaboradorLogueado by remember { mutableStateOf<Colaborador?>(null) }

    val sesionActual by tiendaViewModel.sesionActual.collectAsStateWithLifecycle()
    val registros by tiendaViewModel.registros.collectAsStateWithLifecycle()

    var sesionParaRevisar by remember { mutableStateOf(tiendaViewModel.obtenerSesionParaRevisar()) }
    var registrosParaRevisar by remember { mutableStateOf(tiendaViewModel.obtenerRegistrosDeSesion(sesionParaRevisar?.numero ?: "")) }

    var fechaConsulta by remember { mutableStateOf("2026-09-15") }
    var metricasDia by remember { mutableStateOf(tiendaViewModel.obtenerMetricasDia(fechaConsulta)) }

    NavHost(navController = navController, startDestination = Ruta.Login.ruta) {

        composable(Ruta.Login.ruta) {
            LoginScreen(
                onLoginExitoso = { colaborador ->
                    colaboradorLogueado = colaborador
                    navController.navigate(Ruta.MenuPrincipal.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                iniciarSesion = tiendaViewModel::iniciarSesion,
                modoTema = modoTema,
                onCambiarModoTema = onCambiarModoTema
            )
        }



        composable(Ruta.MenuPrincipal.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
                var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

                fun cerrarSesion() {
                    colaboradorLogueado = null
                    navController.navigate(Ruta.Login.ruta) {
                        popUpTo(Ruta.MenuPrincipal.ruta) { inclusive = true }
                    }
                }

                BackHandler { mostrarDialogoCerrarSesion = true }

                MenuPrincipalScreen(
                    colaborador = colab,
                    onAtras = { mostrarDialogoCerrarSesion = true },
                    onVerMetas = { navController.navigate(Ruta.Metas.ruta) },
                    onImportarConteos = { navController.navigate(Ruta.ImportarConteos.ruta) },
                    onImportarTransacciones = { navController.navigate(Ruta.ImportarTransacciones.ruta) },
                    onVerHistorial = { navController.navigate(Ruta.Historial.ruta) },
                    onVerColaboradores = { navController.navigate(Ruta.Colaboradores.ruta) },

                    onVerObservador = {
                        tiendaViewModel.cargarSesionObservador(colab.codigo)
                        navController.navigate(Ruta.Observador.ruta)

                        tiendaViewModel.sincronizarConServidor {
                            if (tiendaViewModel.sesionActual.value == null) {
                                tiendaViewModel.cargarSesionObservador(colab.codigo)
                            }
                        }
                    },
                    onVerSupervisor = {
                        sesionParaRevisar = tiendaViewModel.obtenerSesionParaRevisar()
                        registrosParaRevisar = tiendaViewModel.obtenerRegistrosDeSesion(sesionParaRevisar?.numero ?: "")
                        navController.navigate(Ruta.Supervisor.ruta)

                        tiendaViewModel.sincronizarConServidor {
                            sesionParaRevisar = tiendaViewModel.obtenerSesionParaRevisar()
                            registrosParaRevisar = tiendaViewModel.obtenerRegistrosDeSesion(sesionParaRevisar?.numero ?: "")
                        }
                    },
                    onVerIndicadores = {
                        metricasDia = tiendaViewModel.obtenerMetricasDia(fechaConsulta)
                        navController.navigate(Ruta.Indicadores.ruta)

                        tiendaViewModel.cargarMetricasDia(fechaConsulta) { metricasDia = it }
                    },
                    onVerEventos = { navController.navigate(Ruta.Eventos.ruta) },
                    onProgramarSesion = { navController.navigate(Ruta.ProgramarSesion.ruta) },
                    onVerUbicacion = { navController.navigate(Ruta.Ubicacion.ruta) },
                    onCerrarSesion = { mostrarDialogoCerrarSesion = true }
                )

                if (mostrarDialogoCerrarSesion) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoCerrarSesion = false },
                        title = { Text("¿Cerrar sesión?") },
                        text = { Text("¿Deseas retroceder y cerrar sesión?") },
                        confirmButton = {
                            TextButton(onClick = {
                                mostrarDialogoCerrarSesion = false
                                cerrarSesion()
                            }) { Text("Sí, cerrar sesión") }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }



        composable(Ruta.Observador.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
                var mostrarDialogoSalir by remember { mutableStateOf(false) }

                BackHandler { mostrarDialogoSalir = true }

                ObservadorScreen(
                    colaborador = colab,
                    sesion = sesionActual,
                    registros = registros,
                    onAtras = { mostrarDialogoSalir = true },
                    onRegistrar = { clasificacion, edad, compania, compras, destino ->
                        tiendaViewModel.registrarPersona(clasificacion, edad, compania, compras, destino)
                    },
                    onAnularUltimo = { tiendaViewModel.anularUltimoRegistro() },
                    onFinalizarSesion = {
                        tiendaViewModel.finalizarSesionActual()
                        navController.navigateUp()
                    }
                )

                if (mostrarDialogoSalir) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoSalir = false },
                        title = { Text("¿Volver al menú?") },
                        text = {
                            Text(
                                "Los registros ya están guardados.\n\n" +
                                        "La sesión quedará EN CURSO y podrás retomarla más tarde " +
                                        "volviendo a entrar a \"Módulo Observador\".\n\n" +
                                        "¿Deseas volver al menú principal?"
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                mostrarDialogoSalir = false
                                navController.navigateUp()
                            }) { Text("Sí, volver") }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDialogoSalir = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }


        composable(Ruta.Metas.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
                // RN-19: solo P-01 y P-02 pueden acceder a la pantalla de metas
                if (colab.codigoPuesto == "P-01" || colab.codigoPuesto == "P-02") {
                    val mensaje by tiendaViewModel.mensajeMeta.collectAsStateWithLifecycle()

                    MetaScreen(
                        colaborador = colab,
                        onGuardar = { mes, meta ->
                            tiendaViewModel.guardarMeta(mes, meta)
                        },
                        mensaje = mensaje,
                        onVolver = {
                            navController.navigateUp()
                        },
                        onIrConteos = {
                            navController.navigate(Ruta.ImportarConteos.ruta)
                        },
                        onIrTransacciones = {
                            navController.navigate(Ruta.ImportarTransacciones.ruta)
                        }
                    )
                } else {
                    // Defensa en profundidad: si un perfil sin permiso llega, lo expulsamos
                    LaunchedEffect(Unit) { navController.navigateUp() }
                }
            }
        }

        composable(Ruta.ImportarConteos.ruta) {
            val mensaje by tiendaViewModel.mensajeMeta.collectAsStateWithLifecycle()

            ImportarConteosScreen(
                mensaje = mensaje,
                onImportar = {
                    tiendaViewModel.importarConteosSimulados()
                },
                onVolver = {
                    navController.navigateUp()
                }
            )
        }

        composable(Ruta.ImportarTransacciones.ruta) {
            val mensaje by tiendaViewModel.mensajeMeta.collectAsStateWithLifecycle()

            ImportarTransaccionesScreen(
                mensaje = mensaje,
                onImportar = {
                    tiendaViewModel.importarTransaccionesSimuladas()
                },
                onVolver = {
                    navController.navigateUp()
                }
            )
        }


        composable(Ruta.Historial.ruta) {
            val sesiones by tiendaViewModel.historial.collectAsStateWithLifecycle()

            LaunchedEffect(Unit) { tiendaViewModel.cargarHistorial() }

            HistorialScreen(
                sesiones = sesiones,
                onVolver = { navController.navigateUp() }
            )
        }

        composable(Ruta.Colaboradores.ruta) {
            ColaboradoresScreen(onVolver = { navController.navigateUp() })
        }



        composable(Ruta.Supervisor.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
                BackHandler { navController.navigateUp() }

                SupervisorScreen(
                    supervisorLogueado = colab,
                    sesion = sesionParaRevisar,
                    registros = registrosParaRevisar,
                    onValidar = { esRechazo, motivo ->
                        val exito = tiendaViewModel.validarSesion(
                            numeroSesion = sesionParaRevisar?.numero ?: "",
                            codigoSupervisor = colab.codigo,
                            esRechazo = esRechazo,
                            motivo = motivo
                        )
                        if (exito) navController.navigateUp()
                    },
                    onVolver = { navController.navigateUp() }
                )
            }
        }



        composable(Ruta.ProgramarSesion.ruta) {
            BackHandler { navController.navigateUp() }

            ProgramarSesionScreen(
                onGuardar = { numero, acceso, fecha, inicio, termino, observador ->
                    tiendaViewModel.programarSesion(numero, acceso, fecha, inicio, termino, observador)
                },
                onVolver = { navController.navigateUp() }
            )
        }



        composable(Ruta.Indicadores.ruta) {
            BackHandler { navController.navigateUp() }

            IndicadoresScreen(
                metricas = metricasDia,
                fechaActual = fechaConsulta,
                onFechaCambiada = { nuevaFecha ->
                    fechaConsulta = nuevaFecha
                    metricasDia = tiendaViewModel.obtenerMetricasDia(nuevaFecha)
                    tiendaViewModel.cargarMetricasDia(nuevaFecha) { metricasDia = it }
                },
                onVolver = { navController.navigateUp() }
            )
        }



        composable(Ruta.Eventos.ruta) {
            BackHandler { navController.navigateUp() }

            EventoScreen(
                onGuardar = { fecha, tipo, acceso, descripcion ->
                    tiendaViewModel.guardarEvento(fecha, tipo, acceso, descripcion)
                },
                onVolver = { navController.navigateUp() }
            )
        }



        composable(Ruta.Ubicacion.ruta) {
            BackHandler { navController.navigateUp() }
            UbicacionScreen(onVolver = { navController.navigateUp() })
        }
    }
}