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
import cl.duocuc.miregistro.ui.screens.*
import cl.duocuc.miregistro.viewmodel.TiendaViewModel
import cl.duocuc.miregistro.viewmodel.UsuariosViewModel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    val usuariosViewModel: UsuariosViewModel = viewModel()
    val tiendaViewModel: TiendaViewModel = viewModel()

    val usuarioActual by usuariosViewModel.usuarioActual.collectAsStateWithLifecycle()
    val usuarios by usuariosViewModel.usuarios.collectAsStateWithLifecycle()
    val filtro by usuariosViewModel.filtro.collectAsStateWithLifecycle()

    var colaboradorLogueado by remember { mutableStateOf<Colaborador?>(null) }

    val sesionActual by tiendaViewModel.sesionActual.collectAsStateWithLifecycle()
    val registros by tiendaViewModel.registros.collectAsStateWithLifecycle()

    var sesionParaRevisar by remember { mutableStateOf(tiendaViewModel.obtenerSesionParaRevisar()) }
    var registrosParaRevisar by remember { mutableStateOf(tiendaViewModel.obtenerRegistrosDeSesion(sesionParaRevisar?.numero ?: "")) }
    var metricasDia by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }

    NavHost(navController = navController, startDestination = Ruta.Login.ruta) {

        composable(Ruta.Login.ruta) {
            LoginScreen(
                onLoginExitoso = { colaborador ->
                    colaboradorLogueado = colaborador
                    navController.navigate(Ruta.MenuPrincipal.ruta) {
                        popUpTo(Ruta.Login.ruta) { inclusive = true }
                    }
                },
                validarCodigo = tiendaViewModel::validarColaborador
            )
        }

        composable(Ruta.MenuPrincipal.ruta) {
            val colab = colaboradorLogueado
            if (colab != null) {
                MenuPrincipalScreen(
                    colaborador = colab,
                    onVerObservador = { navController.navigate(Ruta.Observador.ruta) },
                    onVerSupervisor = {
                        tiendaViewModel.finalizarSesionActual()
                        sesionParaRevisar = tiendaViewModel.obtenerSesionParaRevisar()
                        registrosParaRevisar = tiendaViewModel.obtenerRegistrosDeSesion(sesionParaRevisar?.numero ?: "")
                        navController.navigate(Ruta.Supervisor.ruta)
                    },
                    onVerIndicadores = {
                        metricasDia = tiendaViewModel.obtenerMetricasDia("2026-09-15", "S-0001")
                        navController.navigate(Ruta.Indicadores.ruta)
                    },
                    onVerEventos = {
                        navController.navigate(Ruta.Eventos.ruta)
                    },
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
            ObservadorScreen(
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

        composable(Ruta.Indicadores.ruta) {
            IndicadoresScreen(
                metricas = metricasDia,
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

        // --- PANTALLAS DE TU PLANTILLA ORIGINAL ---
        composable(Ruta.Formulario.ruta) {
            FormularioScreen(
                onRegistroExitoso = { usuario ->
                    usuariosViewModel.registrar(usuario)
                    navController.navigate(Ruta.Perfil.ruta)
                },
                usuarioActual = usuarioActual,
                onVerMiPerfil = { navController.navigate(Ruta.Perfil.ruta) },
                onVerUsuarios = { navController.navigate(Ruta.Usuarios.ruta) },
                onCerrarSesion = { usuariosViewModel.cerrarSesion() }
            )
        }

        composable(Ruta.Perfil.ruta) {
            PerfilScreen(
                usuario = usuarioActual,
                onVolver = { navController.navigateUp() },
                onFotoTomada = usuariosViewModel::cambiarAvatarDesdeCamara,
                onImagenElegida = usuariosViewModel::cambiarAvatarDesdeGaleria
            )
        }

        composable(Ruta.Usuarios.ruta) {
            UsuariosScreen(
                usuarios = usuarios,
                onVolver = { navController.navigateUp() },
                onEditar = { id -> navController.navigate(Ruta.Editar.crear(id)) },
                filtro = filtro,
                onBuscar = usuariosViewModel::buscar,
                onEliminar = usuariosViewModel::eliminar
            )
        }

        composable(
            route = Ruta.Editar.ruta,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entrada ->
            val id = entrada.arguments?.getLong("id") ?: 0L
            val usuario = remember(id) { usuariosViewModel.obtener(id) }
            if (usuario != null) {
                EditarScreen(
                    usuario = usuario,
                    roles = usuariosViewModel.roles,
                    onVolver = { navController.navigateUp() },
                    onGuardar = { editado ->
                        usuariosViewModel.actualizar(editado)
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}