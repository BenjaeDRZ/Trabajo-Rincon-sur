package cl.duocuc.miregistro.data.remote

import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.model.RegistroPersona
import cl.duocuc.miregistro.model.Sesion

// Objetos que viajan en JSON hacia/desde los microservicios.
// Todos los campos son nullable para tolerar respuestas incompletas del servidor.

data class LoginRequestDto(val codigo: String, val contrasena: String)

data class ColaboradorDto(
    val codigo: String? = null,
    val nombre: String? = null,
    val rut: String? = null,
    val cargo: String? = null,
    val codigoPuesto: String? = null,
    val email: String? = null,
    val telefono: String? = null
) {
    fun aModelo() = Colaborador(
        codigo = codigo.orEmpty(),
        nombre = nombre.orEmpty(),
        rut = rut.orEmpty(),
        cargo = cargo.orEmpty(),
        codigoPuesto = codigoPuesto.orEmpty(),
        email = email.orEmpty(),
        telefono = telefono.orEmpty()
    )
}

data class SesionDto(
    val numero: String? = null,
    val codigoAcceso: String? = null,
    val fecha: String? = null,
    val horaInicioProg: String? = null,
    val horaTerminoProg: String? = null,
    val observador: String? = null,
    val estado: String? = null,
    val validador: String? = null,
    val motivoRechazo: String? = null
) {
    fun aModelo(): Sesion? {
        if (numero == null) return null
        return Sesion(
            numero = numero,
            codigoAcceso = codigoAcceso.orEmpty(),
            fecha = fecha.orEmpty(),
            horaInicioProg = horaInicioProg.orEmpty(),
            horaTerminoProg = horaTerminoProg.orEmpty(),
            observador = observador.orEmpty(),
            estado = estado ?: "PROGRAMADA",
            validador = validador,
            motivoRechazo = motivoRechazo
        )
    }

    companion object {
        fun de(s: Sesion) = SesionDto(
            s.numero, s.codigoAcceso, s.fecha, s.horaInicioProg, s.horaTerminoProg,
            s.observador, s.estado, s.validador, s.motivoRechazo
        )
    }
}

data class CambioEstadoDto(val estado: String, val validador: String?, val motivoRechazo: String?)

data class RegistroDto(
    val numero: String? = null,
    val sesion: String? = null,
    val hora: String? = null,
    val clasificacion: String? = null,
    val rangoEtario: String? = null,
    val compania: String? = null,
    val traeCompras: Boolean? = null,
    val accesoDestino: String? = null,
    val anulado: Boolean? = null
) {
    fun aModelo(): RegistroPersona? {
        if (numero == null || sesion == null) return null
        return RegistroPersona(
            numero = numero,
            sesion = sesion,
            hora = hora.orEmpty(),
            clasificacion = clasificacion.orEmpty(),
            rangoEtario = rangoEtario.orEmpty(),
            compania = compania.orEmpty(),
            traeCompras = traeCompras ?: false,
            accesoDestino = accesoDestino,
            anulado = anulado ?: false
        )
    }

    companion object {
        fun de(r: RegistroPersona) = RegistroDto(
            r.numero, r.sesion, r.hora, r.clasificacion, r.rangoEtario,
            r.compania, r.traeCompras, r.accesoDestino, r.anulado
        )
    }
}
