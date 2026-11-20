package cl.duocuc.miregistro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duocuc.miregistro.data.TiendaDbHelper
import cl.duocuc.miregistro.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class TiendaViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = TiendaDbHelper(application.applicationContext)

    private val _listaAccesos = MutableStateFlow(accesos)
    val listaAccesos: StateFlow<List<Acceso>> = _listaAccesos

    private val _sesionActual = MutableStateFlow<Sesion?>(null)
    val sesionActual: StateFlow<Sesion?> = _sesionActual

    private val _registros = MutableStateFlow<List<RegistroPersona>>(emptyList())
    val registros: StateFlow<List<RegistroPersona>> = _registros

    init {
        dbHelper.crearSesionDePrueba()
    }

    fun validarColaborador(codigo: String): Colaborador? =
        colaboradores.find { it.codigo == codigo }

    fun cargarSesionObservador(codigoObservador: String) {
        val sesion = dbHelper.obtenerSesionProgramada(codigoObservador)
        _sesionActual.value = sesion
        _registros.value = if (sesion != null) dbHelper.obtenerRegistrosPorSesion(sesion.numero) else emptyList()
    }

    /**
     * RF-04 + RN-09 + duración 30-60 min (CA-3 HU-04).
     * Devuelve Result.success si todo OK, Result.failure con mensaje si no.
     */
    fun programarSesion(
        numero: String, acceso: String, fecha: String,
        horaInicio: String, horaTermino: String, observador: String
    ): Result<Unit> {
        val minInicio = minutosDesdeMedianoche(horaInicio)
        val minTermino = minutosDesdeMedianoche(horaTermino)
        if (minInicio == null || minTermino == null) {
            return Result.failure(Exception("Formato de hora inválido (HH:mm)"))
        }
        val duracion = minTermino - minInicio
        if (duracion < 30 || duracion > 60) {
            return Result.failure(Exception("La sesión debe durar entre 30 y 60 minutos (CA-3 HU-04)"))
        }
        if (existeSolape(acceso, fecha, minInicio, minTermino)) {
            return Result.failure(Exception("Ya existe una sesión en $acceso entre $horaInicio y $horaTermino (RN-09)"))
        }
        val nuevaSesion = Sesion(numero, acceso, fecha, horaInicio, horaTermino, observador, "PROGRAMADA")
        dbHelper.insertarSesion(nuevaSesion)
        return Result.success(Unit)
    }

    private fun minutosDesdeMedianoche(hora: String): Int? {
        val partes = hora.split(":")
        if (partes.size != 2) return null
        val h = partes[0].toIntOrNull() ?: return null
        val m = partes[1].toIntOrNull() ?: return null
        if (h !in 0..23 || m !in 0..59) return null
        return h * 60 + m
    }

    private fun existeSolape(acceso: String, fecha: String, minInicio: Int, minTermino: Int): Boolean {
        val cursor = dbHelper.db.rawQuery(
            "SELECT horaInicioProg, horaTerminoProg FROM sesiones WHERE codigoAcceso = ? AND fecha = ? AND estado != 'RECHAZADA'",
            arrayOf(acceso, fecha)
        )
        while (cursor.moveToNext()) {
            val ini = minutosDesdeMedianoche(cursor.getString(0)) ?: continue
            val fin = minutosDesdeMedianoche(cursor.getString(1)) ?: continue
            if (minInicio < fin && minTermino > ini) {
                cursor.close()
                return true
            }
        }
        cursor.close()
        return false
    }

    fun registrarPersona(clasificacion: String, rangoEtario: String, compania: String, traeCompras: Boolean, accesoDestino: String? = null) {
        val sesion = _sesionActual.value ?: return
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val nuevoNumero = "R-%04d".format(_registros.value.size + 1)

        val nuevoRegistro = RegistroPersona(nuevoNumero, sesion.numero, horaActual, clasificacion, rangoEtario, compania, traeCompras, accesoDestino)
        _registros.value = _registros.value + nuevoRegistro
        dbHelper.insertarRegistro(nuevoRegistro)
    }

    fun anularUltimoRegistro() {
        val listaActual = _registros.value.toMutableList()
        if (listaActual.isNotEmpty()) {
            val ultimo = listaActual.removeAt(listaActual.size - 1)
            _registros.value = listaActual
            dbHelper.anularRegistro(ultimo.numero)
        }
    }

    fun finalizarSesionActual() {
        val sesion = _sesionActual.value ?: return
        dbHelper.actualizarEstadoSesion(sesion.numero, "FINALIZADA")
        _sesionActual.value = null
    }

    fun obtenerSesionParaRevisar(): Sesion? = dbHelper.obtenerUltimaSesionFinalizada()

    fun obtenerRegistrosDeSesion(numeroSesion: String): List<RegistroPersona> =
        dbHelper.obtenerRegistrosPorSesion(numeroSesion)

    /**
     * RF-07 + RN-10 + RN-11.
     */
    fun validarSesion(numeroSesion: String, codigoSupervisor: String, esRechazo: Boolean, motivo: String? = null): Boolean {
        val sesion = dbHelper.obtenerSesion(numeroSesion) ?: return false
        if (sesion.estado != "FINALIZADA") return false
        if (sesion.observador == codigoSupervisor) return false // RN-11

        return if (esRechazo) {
            dbHelper.actualizarEstadoSesion(numeroSesion, "RECHAZADA", codigoSupervisor, motivo ?: "Sin motivo")
            true
        } else {
            val registrosValidos = dbHelper.obtenerRegistrosPorSesion(numeroSesion).count { !it.anulado }
            if (registrosValidos < 30) return false // RN-10
            dbHelper.actualizarEstadoSesion(numeroSesion, "VALIDADA", codigoSupervisor)
            true
        }
    }

    /** RN-13: proporción de tránsito (excluye NO_DETERMINADO del denominador) */
    fun calcularProporcionTransito(registros: List<RegistroPersona>): Double {
        val validos = registros.filter { !it.anulado && it.clasificacion != "NO_DETERMINADO" }
        if (validos.isEmpty()) return 0.0
        val transito = validos.count { it.clasificacion == "TRANSITO" }
        return transito.toDouble() / validos.size * 100
    }

    fun calcularPerfilTransito(registros: List<RegistroPersona>): Map<String, String> {
        val transito = registros.filter { it.clasificacion == "TRANSITO" && !it.anulado }
        if (transito.isEmpty()) return mapOf(
            "edades" to "Sin datos", "compania" to "Sin datos", "compras" to "Sin datos"
        )
        val total = transito.size.toDouble()
        val edades = transito.groupingBy { it.rangoEtario }.eachCount()
            .mapValues { (it.value / total * 100).roundToInt() }
        val edadesTexto = edades.entries.joinToString(", ") { "${it.key}: ${it.value}%" }
        val compania = transito.groupingBy { it.compania }.eachCount()
            .mapValues { (it.value / total * 100).roundToInt() }
        val companiaTexto = compania.entries.joinToString(", ") { "${it.key}: ${it.value}%" }
        val conCompras = transito.count { it.traeCompras }
        val porcentajeCompras = (conCompras / total * 100).roundToInt()
        return mapOf(
            "edades" to edadesTexto,
            "compania" to companiaTexto,
            "compras" to "$porcentajeCompras% trae compras de otra tienda"
        )
    }

    /**
     * RF-09 + RF-10 + RN-12 + RN-13 + RN-14 + CA-3 HU-10.
     * Devuelve estado: MEDIDO / ESTIMADO / SIN_INFO.
     */
    fun obtenerMetricasDia(fecha: String): Map<String, Any> {
        val ingresos = dbHelper.obtenerIngresosTotales(fecha)
        val transacciones = dbHelper.obtenerTransaccionesTotales(fecha)
        val ingresosPorAcceso = dbHelper.obtenerIngresosPorAcceso(fecha)

        val sesionValidada = dbHelper.obtenerSesionValidadaPorFecha(fecha)

        var propTransito = 0.0
        var estadoTransito = "SIN_INFO"
        var fuenteTransito = "Sin información"
        var registrosBase: List<RegistroPersona> = emptyList()

        if (sesionValidada != null) {
            registrosBase = dbHelper.obtenerRegistrosPorSesion(sesionValidada.numero)
            propTransito = calcularProporcionTransito(registrosBase)
            estadoTransito = "MEDIDO"
            fuenteTransito = "Sesión ${sesionValidada.numero}"
        } else {
            val promedio = dbHelper.promedioTransitoUltimas4Semanas(fecha, "A4")
            if (promedio != null) {
                propTransito = promedio
                estadoTransito = "ESTIMADO"
                fuenteTransito = "Promedio últimas 4 semanas"
            }
        }

        val conversion = if (ingresos > 0) (transacciones.toDouble() / ingresos * 100) else 0.0
        val transitoEstimado = (ingresos * (propTransito / 100)).roundToInt()
        val flujoSinTransito = ingresos - transitoEstimado
        val conversionSinTransito = if (flujoSinTransito > 0)
            (transacciones.toDouble() / flujoSinTransito * 100) else 0.0

        val perfilTransito = calcularPerfilTransito(registrosBase)

        return mapOf(
            "ingresos" to ingresos,
            "transacciones" to transacciones,
            "conversion" to conversion,
            "propTransito" to propTransito,
            "transitoEstimado" to transitoEstimado,
            "flujoSinTransito" to flujoSinTransito,
            "conversionSinTransito" to conversionSinTransito,
            "porAcceso" to ingresosPorAcceso,
            "perfil" to perfilTransito,
            "estadoTransito" to estadoTransito,
            "fuenteTransito" to fuenteTransito
        )
    }

    fun guardarEvento(fecha: String, tipo: String, acceso: String, descripcion: String) {
        dbHelper.insertarEvento(fecha, tipo, acceso, descripcion)
    }
}