package cl.duocuc.miregistro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duocuc.miregistro.data.TiendaDbHelper
import cl.duocuc.miregistro.model.Acceso
import cl.duocuc.miregistro.model.Colaborador
import cl.duocuc.miregistro.model.RegistroPersona
import cl.duocuc.miregistro.model.Sesion
import cl.duocuc.miregistro.model.accesos
import cl.duocuc.miregistro.model.colaboradores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TiendaViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = TiendaDbHelper(application.applicationContext)

    private val _listaAccesos = MutableStateFlow(accesos)
    val listaAccesos: StateFlow<List<Acceso>> = _listaAccesos

    private val _listaColaboradores = MutableStateFlow(colaboradores)
    val listaColaboradores: StateFlow<List<Colaborador>> = _listaColaboradores

    private val _sesionActual = MutableStateFlow<Sesion?>(
        Sesion("S-0001", "A4", "2026-09-15", "13:05", "13:50", "C-05", "EN_CURSO")
    )
    val sesionActual: StateFlow<Sesion?> = _sesionActual

    private val _registros = MutableStateFlow<List<RegistroPersona>>(emptyList())
    val registros: StateFlow<List<RegistroPersona>> = _registros

    init {
        dbHelper.crearSesionDePrueba()
    }

    fun validarColaborador(codigo: String): Colaborador? {
        return colaboradores.find { it.codigo == codigo }
    }

    fun registrarPersona(clasificacion: String, rangoEtario: String, compania: String, traeCompras: Boolean, accesoDestino: String? = null) {
        val sesion = _sesionActual.value ?: return
        val horaActual = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val nuevoId = _registros.value.size + 1
        val nuevoNumero = "R-%04d".format(nuevoId)

        val nuevoRegistro = RegistroPersona(nuevoNumero, sesion.numero, horaActual, clasificacion, rangoEtario, compania, traeCompras, accesoDestino)
        _registros.value = _registros.value + nuevoRegistro
        dbHelper.insertarRegistro(nuevoRegistro)
    }

    fun anularUltimoRegistro() {
        val listaActual = _registros.value.toMutableList()
        if (listaActual.isNotEmpty()) {
            listaActual.removeAt(listaActual.size - 1)
            _registros.value = listaActual
        }
    }

    fun finalizarSesionActual() {
        val sesion = _sesionActual.value ?: return
        val sesionFinalizada = sesion.copy(estado = "FINALIZADA")
        _sesionActual.value = sesionFinalizada
        dbHelper.actualizarEstadoSesion(sesion.numero, "FINALIZADA")
    }

    fun obtenerSesionParaRevisar(): Sesion? {
        return dbHelper.obtenerSesion("S-0001")
    }

    fun obtenerRegistrosDeSesion(numeroSesion: String): List<RegistroPersona> {
        return dbHelper.obtenerRegistrosPorSesion(numeroSesion)
    }

    fun validarSesion(numeroSesion: String, codigoSupervisor: String, esRechazo: Boolean, motivo: String? = null): Boolean {
        val sesion = dbHelper.obtenerSesion(numeroSesion) ?: return false
        val registrosSesion = dbHelper.obtenerRegistrosPorSesion(numeroSesion)

        if (sesion.observador == codigoSupervisor) return false

        if (esRechazo) {
            dbHelper.actualizarEstadoSesion(numeroSesion, "RECHAZADA", codigoSupervisor, motivo ?: "Sin motivo")
            return true
        } else {
            if (registrosSesion.size < 30) return false // ⚠️ Cámbialo a 3 para probar rápido
            dbHelper.actualizarEstadoSesion(numeroSesion, "VALIDADA", codigoSupervisor)
            return true
        }
    }

    fun calcularProporcionTransito(registros: List<RegistroPersona>): Double {
        if (registros.isEmpty()) return 0.0
        val validos = registros.filter { !it.anulado }
        val transito = validos.count { it.clasificacion == "TRANSITO" }
        val noDet = validos.count { it.clasificacion == "NO_DETERMINADO" }
        val denominador = validos.size - noDet
        return if (denominador == 0) 0.0 else (transito.toDouble() / denominador * 100)
    }

    fun obtenerMetricasDia(fecha: String, sesionEvaluada: String): Map<String, Any> {
        val ingresos = dbHelper.obtenerIngresosTotales(fecha)
        val transacciones = dbHelper.obtenerTransaccionesTotales(fecha)
        val registros = dbHelper.obtenerRegistrosPorSesion(sesionEvaluada)

        val conversion = if (ingresos > 0) (transacciones.toDouble() / ingresos * 100) else 0.0
        val propTransito = calcularProporcionTransito(registros)
        val transitoEstimado = Math.round(ingresos * (propTransito / 100)).toInt()
        val flujoSinTransito = ingresos - transitoEstimado
        val conversionSinTransito = if (flujoSinTransito > 0) (transacciones.toDouble() / flujoSinTransito * 100) else 0.0

        return mapOf(
            "ingresos" to ingresos, "transacciones" to transacciones,
            "conversion" to conversion, "propTransito" to propTransito,
            "transitoEstimado" to transitoEstimado, "flujoSinTransito" to flujoSinTransito,
            "conversionSinTransito" to conversionSinTransito
        )
    }

    // --- EVENTOS (FASE 5) ---
    fun guardarEvento(fecha: String, tipo: String, acceso: String, descripcion: String) {
        dbHelper.insertarEvento(fecha, tipo, acceso, descripcion)
    }

    fun obtenerEventos(): List<Map<String, String>> {
        return dbHelper.obtenerEventos()
    }
}