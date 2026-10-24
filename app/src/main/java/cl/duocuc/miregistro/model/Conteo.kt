package cl.duocuc.miregistro.model

data class Conteo(
    val codigoAcceso: String,
    val fecha: String,
    val bloque: Int, // 0 al 10
    val cantidad: Int
)