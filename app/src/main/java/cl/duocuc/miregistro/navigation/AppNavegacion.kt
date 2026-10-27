package cl.duocuc.miregistro.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.model.ModoTema
import cl.duocuc.miregistro.ui.screens.*
import cl.duocuc.miregistro.viewmodel.TiendaViewModel

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
                validarCodigo = tiendaViewModel::validarColaborador,
                modoTema = modoTema,
                onCambiarModoTema = onCambiarModoTema
            )
        }

        composable(Ruta.MenuPrincipal.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
                MenuPrincipalScreen(
                    colaborador = colab,
                    onVerObservador = {
                        tiendaViewModel.cargarSesionObservador(colab.codigo)
                        navController.navigate(Ruta.Observador.ruta)
                    },
                    onVerSupervisor = {
                        sesionParaRevisar = tiendaViewModel.obtenerSesionParaRevisar()
                        registrosParaRevisar = tiendaViewModel.obtenerRegistrosDeSesion(sesionParaRevisar?.numero ?: "")
                        navController.navigate(Ruta.Supervisor.ruta)
                    },
                    onVerIndicadores = {
                        metricasDia = tiendaViewModel.obtenerMetricasDia(fechaConsulta)
                        navController.navigate(Ruta.Indicadores.ruta)
                    },
                    onVerEventos = { navController.navigate(Ruta.Eventos.ruta) },
                    onProgramarSesion = { navController.navigate(Ruta.ProgramarSesion.ruta) },
                    onVerUbicacion = { navController.navigate(Ruta.Ubicacion.ruta) },
                    onCerrarSesion = {
                        colaboradorLogueado = null
                        navController.navigate(Ruta.Login.ruta) {
                            popUpTo(Ruta.MenuPrincipal.ruta) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Ruta.Observador.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
                ObservadorScreen(
                    colaborador = colab,
                    sesion = sesionActual,
                    registros = registros,
                    onRegistrar = { clasificacion, edad, compania, compras, destino ->
                        tiendaViewModel.registrarPersona(clasificacion, edad, compania, compras, destino)
                    },
                    onAnularUltimo = { tiendaViewModel.anularUltimoRegistro() },
                    onFinalizarSesion = {
                        tiendaViewModel.finalizarSesionActual()
                        navController.navigateUp()
                    }
                )
            }
        }

        composable(Ruta.Supervisor.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
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
            ProgramarSesionScreen(
                onGuardar = { numero, acceso, fecha, inicio, termino, observador ->
                    tiendaViewModel.programarSesion(numero, acceso, fecha, inicio, termino, observador)
                    navController.navigateUp()
                },
                onVolver = { navController.navigateUp() }
            )
        }

        composable(Ruta.Indicadores.ruta) {
            IndicadoresScreen(
                metricas = metricasDia,
                fechaActual = fechaConsulta,
                onFechaCambiada = { nuevaFecha ->
                    fechaConsulta = nuevaFecha
                    metricasDia = tiendaViewModel.obtenerMetricasDia(nuevaFecha)
                },
                onVolver = { navController.navigateUp() }
            )
        }

        composable(Ruta.Eventos.ruta) {
            EventoScreen(
                onGuardar = { fecha, tipo, acceso, descripcion ->
                    tiendaViewModel.guardarEvento(fecha, tipo, acceso, descripcion)
                },
                onVolver = { navController.navigateUp() }
            )
        }

        composable(Ruta.Ubicacion.ruta) {
            UbicacionScreen(onVolver = { navController.navigateUp() })
        }

    }
}