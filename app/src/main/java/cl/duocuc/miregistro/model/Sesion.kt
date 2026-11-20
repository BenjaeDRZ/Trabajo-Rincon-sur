package cl.duocuc.miregistro.model

data class Sesion(
    val numero: String,             
    val codigoAcceso: String,       
    val fecha: String,              
    val horaInicioProg: String,     
    val horaTerminoProg: String,    
    val observador: String,         
    var estado: String,             
    var validador: String? = null,
    var motivoRechazo: String? = null
)