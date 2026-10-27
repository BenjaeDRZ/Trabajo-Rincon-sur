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

    fun validarColaborador(codigo: String): Colaborador? {
        return colaboradores.find { it.codigo == codigo }
    }

    fun cargarSesionObservador(codigoObservador: String) {
        val sesion = dbHelper.obtenerSesionProgramada(codigoObservador)
        _sesionActual.value = sesion
        _registros.value = if (sesion != null) dbHelper.obtenerRegistrosPorSesion(sesion.numero) else emptyList()
    }

    fun programarSesion(numero: String, acceso: String, fecha: String, horaInicio: String, horaTermino: String, observador: String) {
        val nuevaSesion = Sesion(numero, acceso, fecha, horaInicio, horaTermino, observador, "EN_CURSO")
        dbHelper.insertarSesion(nuevaSesion)
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

    fun obtenerSesionParaRevisar(): Sesion? {
        return dbHelper.obtenerUltimaSesionFinalizada()
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
            if (registrosSesion.size < 3) return false
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

    fun calcularPerfilTransito(registros: List<RegistroPersona>): Map<String, String> {
        val transito = registros.filter { it.clasificacion == "TRANSITO" && !it.anulado }
        if (transito.isEmpty()) return mapOf("edades" to "Sin datos", "compania" to "Sin datos", "compras" to "Sin datos")

        val total = transito.size.toDouble()

        val edades = transito.groupingBy { it.rangoEtario }.eachCount().mapValues { (it.value / total * 100).toInt() }
        val edadesTexto = edades.entries.joinToString(", ") { "${it.key}: ${it.value}%" }

        val compania = transito.groupingBy { it.compania }.eachCount().mapValues { (it.value / total * 100).toInt() }
        val companiaTexto = compania.entries.joinToString(", ") { "${it.key}: ${it.value}%" }

        val conCompras = transito.count { it.traeCompras }
        val porcentajeCompras = (conCompras / total * 100).toInt()

        return mapOf(
            "edades" to edadesTexto,
            "compania" to companiaTexto,
            "compras" to "$porcentajeCompras% trae compras de otra tienda"
        )
    }

    fun obtenerMetricasDia(fecha: String): Map<String, Any> {
        val ingresos = dbHelper.obtenerIngresosTotales(fecha)
        val transacciones = dbHelper.obtenerTransaccionesTotales(fecha)
        val ingresosPorAcceso = dbHelper.obtenerIngresosPorAcceso(fecha)

        val sesion = dbHelper.obtenerSesionValidadaPorFecha(fecha) ?: dbHelper.obtenerSesion("S-0001")
        val registros = if (sesion != null) dbHelper.obtenerRegistrosPorSesion(sesion.numero) else emptyList()

        val conversion = if (ingresos > 0) (transacciones.toDouble() / ingresos * 100) else 0.0
        val propTransito = calcularProporcionTransito(registros)
        val transitoEstimado = Math.round(ingresos * (propTransito / 100)).toInt()
        val flujoSinTransito = ingresos - transitoEstimado
        val conversionSinTransito = if (flujoSinTransito > 0) (transacciones.toDouble() / flujoSinTransito * 100) else 0.0
        val perfilTransito = calcularPerfilTransito(registros)

        return mapOf(
            "ingresos" to ingresos, "transacciones" to transacciones, "conversion" to conversion,
            "propTransito" to propTransito, "transitoEstimado" to transitoEstimado,
            "flujoSinTransito" to flujoSinTransito, "conversionSinTransito" to conversionSinTransito,
            "porAcceso" to ingresosPorAcceso, "perfil" to perfilTransito
        )
    }

    fun guardarEvento(fecha: String, tipo: String, acceso: String, descripcion: String) {
        dbHelper.insertarEvento(fecha, tipo, acceso, descripcion)
    }
}