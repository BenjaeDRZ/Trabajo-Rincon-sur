package cl.duocuc.miregistro.model

data class RegistroPersona(
    val numero: String,             // R-0301
    val sesion: String,             // S-0003
    val hora: String,               // "17:01"
    val clasificacion: String,      // TRANSITO, VISITA, NO_DETERMINADO
    val rangoEtario: String,        // MENOR_18, 18_25, 26_40, 41_60, 61_MAS
    val compania: String,           // SOLO, PAREJA, CON_NINOS, GRUPO
    val traeCompras: Boolean,
    val accesoDestino: String? = null, // Solo si es TRANSITO
    var anulado: Boolean = false
)