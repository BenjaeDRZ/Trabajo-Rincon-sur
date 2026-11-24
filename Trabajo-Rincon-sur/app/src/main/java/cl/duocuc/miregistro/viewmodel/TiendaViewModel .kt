package cl.duocuc.miregistro.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.TiendaDbHelper
import cl.duocuc.miregistro.data.remote.CambioEstadoDto
import cl.duocuc.miregistro.data.remote.LoginRequestDto
import cl.duocuc.miregistro.data.remote.RegistroDto
import cl.duocuc.miregistro.data.remote.RetrofitClient
import cl.duocuc.miregistro.data.remote.SesionDto
import cl.duocuc.miregistro.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import cl.duocuc.miregistro.data.remote.ReportesRetrofitInstance
import cl.duocuc.miregistro.model.Meta
import kotlinx.coroutines.launch
import cl.duocuc.miregistro.model.ConteoApi
import cl.duocuc.miregistro.data.remote.AnaliticaRetrofitInstance
import cl.duocuc.miregistro.model.TransaccionApi



class TiendaViewModel(application: Application) : AndroidViewModel(application) {

    private val _mensajeMeta = MutableStateFlow<String?>(null)
    val mensajeMeta: StateFlow<String?> = _mensajeMeta

    fun guardarMeta(mes: String, metaConversion: Double) {
        viewModelScope.launch {
            try {
                _mensajeMeta.value = "Guardando..."
                val nuevaMeta = Meta(mes = mes, metaConversion = metaConversion)
                ReportesRetrofitInstance.api.crearMeta(nuevaMeta)
                _mensajeMeta.value = "✅ ¡Meta guardada exitosamente en MySQL!"
            } catch (e: Exception) {
                _mensajeMeta.value = "❌ Error: ${e.message}"
            }
        }
    }


    fun importarConteosSimulados() {
        viewModelScope.launch {
            try {
                _mensajeMeta.value = "Importando conteos..."

                val listaConteos = listOf(
                    ConteoApi(
                        fecha = "2026-09-15",
                        acceso = "A1",
                        bloque = "13:00",
                        cantidad = 200
                    ),
                    ConteoApi(
                        fecha = "2026-09-15",
                        acceso = "A4",
                        bloque = "13:00",
                        cantidad = 500
                    ),
                    ConteoApi(
                        fecha = "2026-09-15",
                        acceso = "A3",
                        bloque = "14:00",
                        cantidad = 150
                    )
                )

                val respuesta = withContext(Dispatchers.IO) {
                    AnaliticaRetrofitInstance.api
                        .cargarConteos(listaConteos)
                        .string()
                }

                _mensajeMeta.value = "✅ $respuesta"

            } catch (e: Exception) {
                _mensajeMeta.value = "❌ Error: ${e.message}"
                Log.e(TAG, "Error importando conteos", e)
            }
        }
    }

    fun importarTransaccionesSimuladas() {
        viewModelScope.launch {
            try {
                _mensajeMeta.value = "Importando transacciones..."

                val listaTransacciones = listOf(
                    TransaccionApi(
                        acceso = "A1",
                        bloque = "13:00",
                        cantidad = 35,
                        fecha = "2026-09-15"
                    ),
                    TransaccionApi(
                        acceso = "A4",
                        bloque = "13:00",
                        cantidad = 52,
                        fecha = "2026-09-15"
                    ),
                    TransaccionApi(
                        acceso = "A3",
                        bloque = "14:00",
                        cantidad = 18,
                        fecha = "2026-09-15"
                    )
                )

                val respuesta = withContext(Dispatchers.IO) {
                    AnaliticaRetrofitInstance.api
                        .cargarTransacciones(listaTransacciones)
                        .string()
                }

                _mensajeMeta.value = "✅ $respuesta"

            } catch (e: Exception) {
                _mensajeMeta.value = "❌ Error: ${e.message}"
                Log.e(TAG, "Error importando transacciones", e)
            }
        }
    }



    private val dbHelper = TiendaDbHelper(application.applicationContext)

    private val _listaAccesos = MutableStateFlow(accesos)
    val listaAccesos: StateFlow<List<Acceso>> = _listaAccesos

    private val _sesionActual = MutableStateFlow<Sesion?>(null)
    val sesionActual: StateFlow<Sesion?> = _sesionActual

    private val _registros = MutableStateFlow<List<RegistroPersona>>(emptyList())
    val registros: StateFlow<List<RegistroPersona>> = _registros

    
    private val _servidorConectado = MutableStateFlow(false)
    val servidorConectado: StateFlow<Boolean> = _servidorConectado

    private val _historial = MutableStateFlow<List<Sesion>>(emptyList())
    val historial: StateFlow<List<Sesion>> = _historial

    private val operacionApi get() = RetrofitClient.operacion

    init {
        dbHelper.crearSesionDePrueba()
        sincronizarConServidor()
        cargarHistorial()
    }

    fun validarColaborador(codigo: String): Colaborador? =
        colaboradores.find { it.codigo == codigo }

    /**
     * Login contra ms-auth. Si el microservicio no responde (sin red / apagado),
     * se usa la lista local del prototipo (contraseña 12345) para no bloquear el trabajo en terreno.
     * onResultado(colaborador, mensajeError)
     */
    fun iniciarSesion(codigo: String, contrasena: String, onResultado: (Colaborador?, String?) -> Unit) {
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                try {
                    val resp = RetrofitClient.auth.login(LoginRequestDto(codigo, contrasena))
                    _servidorConectado.value = true
                    when {
                        resp.isSuccessful && resp.body() != null -> resp.body()!!.aModelo() to null
                        resp.code() == 401 -> null to "Código o contraseña incorrectos"
                        else -> loginLocal(codigo, contrasena)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "ms-auth no disponible, se usa login local: ${e.message}")
                    _servidorConectado.value = false
                    loginLocal(codigo, contrasena)
                }
            }
            onResultado(resultado.first, resultado.second)
        }
    }

    private fun loginLocal(codigo: String, contrasena: String): Pair<Colaborador?, String?> {
        val colaborador = validarColaborador(codigo) ?: return null to "Código no encontrado"
        if (contrasena != CONTRASENA_LOCAL) return null to "Contraseña incorrecta"
        return colaborador to null
    }

    fun cargarSesionObservador(codigoObservador: String) {
        val sesion = dbHelper.obtenerSesionProgramada(codigoObservador)
        _sesionActual.value = sesion
        _registros.value = if (sesion != null) dbHelper.obtenerRegistrosPorSesion(sesion.numero) else emptyList()
    }







    
    private fun enviarAlServidor(descripcion: String, accion: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                accion()
                _servidorConectado.value = true
            } catch (e: Exception) {
                _servidorConectado.value = false
                Log.w(TAG, "No se pudo $descripcion (queda pendiente para la próxima sincronización): ${e.message}")
            }
        }
    }

    /**
     * Sube a ms-operacion lo que solo existe localmente y baja lo que hay en el servidor
     * (sesiones programadas por otros dispositivos, validaciones, etc.).
     */
    fun sincronizarConServidor(onListo: () -> Unit = {}) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val remotas = operacionApi.listarSesiones().mapNotNull { it.aModelo() }.associateBy { it.numero }
                    val locales = dbHelper.obtenerTodasLasSesiones().associateBy { it.numero }

                    for (local in locales.values) {
                        val remota = remotas[local.numero]
                        if (remota == null || nivelEstado(local.estado) > nivelEstado(remota.estado)) {
                            operacionApi.guardarSesion(SesionDto.de(local))
                        }
                        val regsLocales = dbHelper.obtenerRegistrosPorSesion(local.numero)
                        val regsRemotos = if (remota == null) 0 else operacionApi.registrosDeSesion(local.numero).size
                        if (regsLocales.size > regsRemotos) {
                            regsLocales.forEach { operacionApi.guardarRegistro(RegistroDto.de(it)) }
                        }
                    }

                    for (remota in remotas.values) {
                        val local = locales[remota.numero]
                        if (local == null || nivelEstado(remota.estado) > nivelEstado(local.estado)) {
                            dbHelper.guardarSesionDesdeServidor(remota)
                        }
                        if (remota.estado != "PROGRAMADA") {
                            val regsRemotos = operacionApi.registrosDeSesion(remota.numero).mapNotNull { it.aModelo() }
                            if (regsRemotos.size > dbHelper.obtenerRegistrosPorSesion(remota.numero).size) {
                                dbHelper.reemplazarRegistrosDeSesion(remota.numero, regsRemotos)
                            }
                        }
                    }
                    _servidorConectado.value = true
                } catch (e: Exception) {
                    _servidorConectado.value = false
                    Log.w(TAG, "Sincronización con ms-operacion falló, se trabaja offline: ${e.message}")
                }
            }
            onListo()
        }
    }

    private fun nivelEstado(estado: String?): Int = when (estado) {
        "PROGRAMADA" -> 0
        "EN_CURSO" -> 1
        "FINALIZADA" -> 2
        "VALIDADA", "RECHAZADA" -> 3
        else -> -1
    }

    /**
     * Trae desde ms-analitica los ingresos por acceso de la fecha, los guarda localmente
     * y luego entrega las métricas calculadas. Si el servidor no responde, usa lo local.
     */
    fun cargarMetricasDia(fecha: String, onListo: (Map<String, Any>) -> Unit) {
        viewModelScope.launch {
            val metricas = withContext(Dispatchers.IO) {
                try {
                    val porAcceso = RetrofitClient.analitica.ingresosPorAcceso(fecha)
                    if (porAcceso.isNotEmpty()) dbHelper.reemplazarConteos(fecha, porAcceso)
                    _servidorConectado.value = true
                } catch (e: Exception) {
                    _servidorConectado.value = false
                    Log.w(TAG, "ms-analitica no disponible, se usan conteos locales: ${e.message}")
                }
                obtenerMetricasDia(fecha)
            }
            onListo(metricas)
        }
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
        enviarAlServidor("enviar la sesión $numero") { operacionApi.guardarSesion(SesionDto.de(nuevaSesion)) }
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
        enviarAlServidor("enviar el registro $nuevoNumero") { operacionApi.guardarRegistro(RegistroDto.de(nuevoRegistro)) }
    }

    fun anularUltimoRegistro() {
        val listaActual = _registros.value.toMutableList()
        if (listaActual.isNotEmpty()) {
            val ultimo = listaActual.removeAt(listaActual.size - 1)
            _registros.value = listaActual
            dbHelper.anularRegistro(ultimo.numero)
            enviarAlServidor("anular el registro ${ultimo.numero}") {
                operacionApi.anularRegistro(ultimo.sesion, ultimo.numero)
            }
        }
    }

    fun finalizarSesionActual() {
        val sesion = _sesionActual.value ?: return
        dbHelper.actualizarEstadoSesion(sesion.numero, "FINALIZADA")
        enviarEstado(sesion.numero, "FINALIZADA", null, null)
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
            val motivoFinal = motivo ?: "Sin motivo"
            dbHelper.actualizarEstadoSesion(numeroSesion, "RECHAZADA", codigoSupervisor, motivoFinal)
            enviarEstado(numeroSesion, "RECHAZADA", codigoSupervisor, motivoFinal)
            true
        } else {
            val registrosValidos = dbHelper.obtenerRegistrosPorSesion(numeroSesion).count { !it.anulado }
            if (registrosValidos < 30) return false // RN-10
            dbHelper.actualizarEstadoSesion(numeroSesion, "VALIDADA", codigoSupervisor)
            enviarEstado(numeroSesion, "VALIDADA", codigoSupervisor, null)
            true
        }
    }

    
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

    
    private fun enviarEstado(numero: String, estado: String, validador: String?, motivo: String?) {
        enviarAlServidor("actualizar el estado de $numero") {
            val resp = operacionApi.actualizarEstado(numero, CambioEstadoDto(estado, validador, motivo))
            if (resp.code() == 404) {
                dbHelper.obtenerSesion(numero)?.let { operacionApi.guardarSesion(SesionDto.de(it)) }
            } else if (!resp.isSuccessful) {
                throw IllegalStateException("HTTP ${resp.code()}")
            }
        }
    }

    /**
     * Carga las sesiones para la pantalla de historial.
     * Intenta traerlas del backend y las guarda en SQLite. Si falla, usa lo local.
     * Siempre termina leyendo de SQLite, que es la fuente de la UI.
     */
    fun cargarHistorial() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val remotas = operacionApi.listarSesiones().mapNotNull { it.aModelo() }
                    remotas.forEach { dbHelper.guardarSesionDesdeServidor(it) }
                    _servidorConectado.value = true
                } catch (e: Exception) {
                    _servidorConectado.value = false
                    Log.w(TAG, "Historial offline, se usa SQLite local: ${e.message}")
                }
            }
            _historial.value = withContext(Dispatchers.IO) {
                dbHelper.obtenerTodasLasSesiones()
            }
            Log.d(TAG, "Historial cargado: ${_historial.value.size} sesiones")
        }
    }

    companion object {
        private const val TAG = "TiendaViewModel"
        
        private const val CONTRASENA_LOCAL = "12345"
    }
}
