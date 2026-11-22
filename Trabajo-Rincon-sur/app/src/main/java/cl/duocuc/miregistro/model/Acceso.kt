package cl.duocuc.miregistro.model

data class Acceso(
    val codigo: String,
    val nombre: String,
    val ubicacion: String
)

val accesos = listOf(
    Acceso("A1", "Acceso Principal", "Frente al pasillo central del mall"),
    Acceso("A2", "Acceso Lateral", "Costado del sector patio de comidas"),
    Acceso("A3", "Acceso Estacionamiento", "Conecta con el estacionamiento subterráneo"),
    Acceso("A4", "Acceso Conexión", "Comunica con la tienda vecina de mejoramiento del hogar")
)