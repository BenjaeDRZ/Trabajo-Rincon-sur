package cl.duocuc.miregistro.model

data class RegistroPersona(
    val numero: String,             
    val sesion: String,             
    val hora: String,               
    val clasificacion: String,      
    val rangoEtario: String,        
    val compania: String,           
    val traeCompras: Boolean,
    val accesoDestino: String? = null, 
    var anulado: Boolean = false
)