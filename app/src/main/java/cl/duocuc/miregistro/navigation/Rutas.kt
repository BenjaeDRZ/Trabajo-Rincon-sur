package cl.duocuc.miregistro.navigation

sealed class Ruta(val ruta: String) {
    // Rutas originales de tu plantilla
    data object Formulario : Ruta("formulario")
    data object Perfil : Ruta("perfil")
    data object Usuarios : Ruta("usuarios")
    data object Editar : Ruta("editar/{id}") {
        fun crear(id: Long) = "editar/$id"
    }

    // Rutas de la app Rincón Sur
    data object Login : Ruta("login")
    data object MenuPrincipal : Ruta("menu_principal")
    data object Observador : Ruta("observador")
    data object Supervisor : Ruta("supervisor")
    data object Indicadores : Ruta("indicadores")
    data object Eventos : Ruta("eventos")
}