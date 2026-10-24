package cl.duocuc.miregistro.model

data class Sesion(
    val numero: String,             // S-0001
    val codigoAcceso: String,       // A4
    val fecha: String,              // "2026-09-15"
    val horaInicioProg: String,     // "13:05"
    val horaTerminoProg: String,    // "13:50"
    val observador: String,         // C-05
    var estado: String,             // PROGRAMADA, EN_CURSO, FINALIZADA, VALIDADA, RECHAZADA
    var validador: String? = null,
    var motivoRechazo: String? = null
)