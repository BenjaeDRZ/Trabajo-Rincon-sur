package cl.duocuc.miregistro.model

data class ConteoApi(
    val id: Long? = null,
    val fecha: String,
    val acceso: String,
    val bloque: String,
    val cantidad: Int
)