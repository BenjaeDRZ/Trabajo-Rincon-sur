package cl.duocuc.miregistro.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duocuc.miregistro.model.RegistroPersona
import cl.duocuc.miregistro.model.Sesion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TiendaDbHelper(context: Context) :
    SQLiteOpenHelper(context, "rinconsur.db", null, 3) {

    val db: SQLiteDatabase get() = readableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE sesiones (
                numero TEXT PRIMARY KEY, codigoAcceso TEXT, fecha TEXT,
                horaInicioProg TEXT, horaTerminoProg TEXT, observador TEXT,
                estado TEXT, validador TEXT, motivoRechazo TEXT
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE registros (
                id INTEGER PRIMARY KEY AUTOINCREMENT, numero TEXT, sesion TEXT,
                hora TEXT, clasificacion TEXT, rangoEtario TEXT, compania TEXT,
                traeCompras INTEGER, accesoDestino TEXT, anulado INTEGER
            )
        """.trimIndent())
        db.execSQL("CREATE TABLE conteos (fecha TEXT, codigoAcceso TEXT, cantidad INTEGER)")
        db.execSQL("CREATE TABLE transacciones (fecha TEXT, cantidad INTEGER)")
        db.execSQL("""
            CREATE TABLE eventos (
                id INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, tipo TEXT, accesoAfectado TEXT, descripcion TEXT
            )
        """.trimIndent())

        db.execSQL("INSERT INTO conteos VALUES ('2026-09-15', 'A1', 2040)")
        db.execSQL("INSERT INTO conteos VALUES ('2026-09-15', 'A2', 1105)")
        db.execSQL("INSERT INTO conteos VALUES ('2026-09-15', 'A3', 730)")
        db.execSQL("INSERT INTO conteos VALUES ('2026-09-15', 'A4', 1860)")
        db.execSQL("INSERT INTO transacciones VALUES ('2026-09-15', 497)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS sesiones")
        db.execSQL("DROP TABLE IF EXISTS registros")
        db.execSQL("DROP TABLE IF EXISTS conteos")
        db.execSQL("DROP TABLE IF EXISTS transacciones")
        db.execSQL("DROP TABLE IF EXISTS eventos")
        onCreate(db)
    }

    fun insertarSesion(sesion: Sesion) {
        val values = ContentValues().apply {
            put("numero", sesion.numero); put("codigoAcceso", sesion.codigoAcceso)
            put("fecha", sesion.fecha); put("horaInicioProg", sesion.horaInicioProg)
            put("horaTerminoProg", sesion.horaTerminoProg); put("observador", sesion.observador)
            put("estado", sesion.estado)
        }
        writableDatabase.insert("sesiones", null, values)
    }

    fun actualizarEstadoSesion(numero: String, estado: String, validador: String? = null, motivo: String? = null) {
        val values = ContentValues().apply {
            put("estado", estado); put("validador", validador); put("motivoRechazo", motivo)
        }
        writableDatabase.update("sesiones", values, "numero = ?", arrayOf(numero))
    }

    fun obtenerSesion(numero: String): Sesion? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM sesiones WHERE numero = ?", arrayOf(numero))
        return if (cursor.moveToFirst()) {
            Sesion(
                numero = cursor.getString(cursor.getColumnIndexOrThrow("numero")),
                codigoAcceso = cursor.getString(cursor.getColumnIndexOrThrow("codigoAcceso")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                horaInicioProg = cursor.getString(cursor.getColumnIndexOrThrow("horaInicioProg")),
                horaTerminoProg = cursor.getString(cursor.getColumnIndexOrThrow("horaTerminoProg")),
                observador = cursor.getString(cursor.getColumnIndexOrThrow("observador")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                validador = cursor.getString(cursor.getColumnIndexOrThrow("validador")),
                motivoRechazo = cursor.getString(cursor.getColumnIndexOrThrow("motivoRechazo"))
            ).also { cursor.close() }
        } else null
    }

    fun insertarRegistro(registro: RegistroPersona) {
        val values = ContentValues().apply {
            put("numero", registro.numero); put("sesion", registro.sesion)
            put("hora", registro.hora); put("clasificacion", registro.clasificacion)
            put("rangoEtario", registro.rangoEtario); put("compania", registro.compania)
            put("traeCompras", if (registro.traeCompras) 1 else 0)
            put("accesoDestino", registro.accesoDestino); put("anulado", 0)
        }
        writableDatabase.insert("registros", null, values)
    }

    fun anularRegistro(numeroRegistro: String) {
        val values = ContentValues().apply { put("anulado", 1) }
        writableDatabase.update("registros", values, "numero = ?", arrayOf(numeroRegistro))
    }

    fun obtenerRegistrosPorSesion(sesion: String): List<RegistroPersona> {
        val lista = mutableListOf<RegistroPersona>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM registros WHERE sesion = ? AND anulado = 0", arrayOf(sesion))
        while (cursor.moveToNext()) {
            lista.add(
                RegistroPersona(
                    numero = cursor.getString(cursor.getColumnIndexOrThrow("numero")),
                    sesion = cursor.getString(cursor.getColumnIndexOrThrow("sesion")),
                    hora = cursor.getString(cursor.getColumnIndexOrThrow("hora")),
                    clasificacion = cursor.getString(cursor.getColumnIndexOrThrow("clasificacion")),
                    rangoEtario = cursor.getString(cursor.getColumnIndexOrThrow("rangoEtario")),
                    compania = cursor.getString(cursor.getColumnIndexOrThrow("compania")),
                    traeCompras = cursor.getInt(cursor.getColumnIndexOrThrow("traeCompras")) == 1,
                    accesoDestino = cursor.getString(cursor.getColumnIndexOrThrow("accesoDestino"))
                )
            )
        }
        cursor.close()
        return lista
    }

    fun crearSesionDePrueba() {
        if (obtenerSesion("S-0001") == null) {
            insertarSesion(Sesion("S-0001", "A4", "2026-09-15", "13:05", "13:50", "C-05", "FINALIZADA"))
        }
    }

    fun obtenerIngresosTotales(fecha: String): Int {
        val cursor = readableDatabase.rawQuery("SELECT SUM(cantidad) FROM conteos WHERE fecha = ?", arrayOf(fecha))
        var total = 0
        if (cursor.moveToFirst()) total = cursor.getInt(0)
        cursor.close()
        return total
    }

    fun obtenerTransaccionesTotales(fecha: String): Int {
        val cursor = readableDatabase.rawQuery("SELECT SUM(cantidad) FROM transacciones WHERE fecha = ?", arrayOf(fecha))
        var total = 0
        if (cursor.moveToFirst()) total = cursor.getInt(0)
        cursor.close()
        return total
    }

    fun insertarEvento(fecha: String, tipo: String, acceso: String, descripcion: String) {
        val values = ContentValues().apply {
            put("fecha", fecha); put("tipo", tipo); put("accesoAfectado", acceso); put("descripcion", descripcion)
        }
        writableDatabase.insert("eventos", null, values)
    }

    fun obtenerIngresosPorAcceso(fecha: String): Map<String, Int> {
        val mapa = mutableMapOf<String, Int>()
        val cursor = readableDatabase.rawQuery("SELECT codigoAcceso, SUM(cantidad) FROM conteos WHERE fecha = ? GROUP BY codigoAcceso", arrayOf(fecha))
        while (cursor.moveToNext()) {
            mapa[cursor.getString(0)] = cursor.getInt(1)
        }
        cursor.close()
        return mapa
    }

    fun obtenerSesionValidadaPorFecha(fecha: String): Sesion? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM sesiones WHERE fecha = ? AND estado = 'VALIDADA' LIMIT 1", arrayOf(fecha))
        return if (cursor.moveToFirst()) {
            Sesion(
                numero = cursor.getString(cursor.getColumnIndexOrThrow("numero")),
                codigoAcceso = cursor.getString(cursor.getColumnIndexOrThrow("codigoAcceso")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                horaInicioProg = cursor.getString(cursor.getColumnIndexOrThrow("horaInicioProg")),
                horaTerminoProg = cursor.getString(cursor.getColumnIndexOrThrow("horaTerminoProg")),
                observador = cursor.getString(cursor.getColumnIndexOrThrow("observador")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                validador = cursor.getString(cursor.getColumnIndexOrThrow("validador")),
                motivoRechazo = cursor.getString(cursor.getColumnIndexOrThrow("motivoRechazo"))
            ).also { cursor.close() }
        } else null
    }

    fun obtenerSesionProgramada(observador: String): Sesion? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM sesiones WHERE observador = ? AND estado IN ('PROGRAMADA', 'EN_CURSO') LIMIT 1", arrayOf(observador))
        return if (cursor.moveToFirst()) {
            Sesion(
                numero = cursor.getString(cursor.getColumnIndexOrThrow("numero")),
                codigoAcceso = cursor.getString(cursor.getColumnIndexOrThrow("codigoAcceso")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                horaInicioProg = cursor.getString(cursor.getColumnIndexOrThrow("horaInicioProg")),
                horaTerminoProg = cursor.getString(cursor.getColumnIndexOrThrow("horaTerminoProg")),
                observador = cursor.getString(cursor.getColumnIndexOrThrow("observador")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                validador = cursor.getString(cursor.getColumnIndexOrThrow("validador")),
                motivoRechazo = cursor.getString(cursor.getColumnIndexOrThrow("motivoRechazo"))
            ).also { cursor.close() }
        } else null
    }

    fun obtenerUltimaSesionFinalizada(): Sesion? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM sesiones WHERE estado = 'FINALIZADA' ORDER BY rowid DESC LIMIT 1", null)
        return if (cursor.moveToFirst()) {
            Sesion(
                numero = cursor.getString(cursor.getColumnIndexOrThrow("numero")),
                codigoAcceso = cursor.getString(cursor.getColumnIndexOrThrow("codigoAcceso")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                horaInicioProg = cursor.getString(cursor.getColumnIndexOrThrow("horaInicioProg")),
                horaTerminoProg = cursor.getString(cursor.getColumnIndexOrThrow("horaTerminoProg")),
                observador = cursor.getString(cursor.getColumnIndexOrThrow("observador")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                validador = cursor.getString(cursor.getColumnIndexOrThrow("validador")),
                motivoRechazo = cursor.getString(cursor.getColumnIndexOrThrow("motivoRechazo"))
            ).also { cursor.close() }
        } else null
    }




    /**
     * Devuelve el siguiente número de sesión secuencial: "S-0001", "S-0002", ...
     */
    fun siguienteNumeroSesion(): String {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM sesiones", null)
        val total = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return "S-%04d".format(total + 1)
    }

    /**
     * RN-14: promedio de proporción de tránsito de las últimas 4 semanas,
     * filtrando por mismo acceso y tipo de día (laboral / fin de semana).
     * Devuelve null si no hay sesiones válidas.
     */
    fun promedioTransitoUltimas4Semanas(fechaConsulta: String, codigoAcceso: String): Double? {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val fecha = try { formato.parse(fechaConsulta) } catch (e: Exception) { return null } ?: return null

        val cal = Calendar.getInstance().apply { time = fecha }
        val diaSemana = cal.get(Calendar.DAY_OF_WEEK)
        val esFinDeSemana = diaSemana == Calendar.SATURDAY || diaSemana == Calendar.SUNDAY

        val desde = Calendar.getInstance().apply {
            time = fecha
            add(Calendar.DAY_OF_YEAR, -28)
        }
        val desdeStr = formato.format(desde.time)

        val cursor = readableDatabase.rawQuery(
            "SELECT numero, fecha FROM sesiones " +
                    "WHERE codigoAcceso = ? AND estado = 'VALIDADA' " +
                    "AND fecha BETWEEN ? AND ?",
            arrayOf(codigoAcceso, desdeStr, fechaConsulta)
        )

        val numerosSesiones = mutableListOf<String>()
        while (cursor.moveToNext()) {
            val fechaSesion = cursor.getString(1)
            val fechaParse = try { formato.parse(fechaSesion) } catch (e: Exception) { continue } ?: continue
            val calSesion = Calendar.getInstance().apply { time = fechaParse }
            val diaSes = calSesion.get(Calendar.DAY_OF_WEEK)
            val esFinSes = diaSes == Calendar.SATURDAY || diaSes == Calendar.SUNDAY
            if (esFinSes == esFinDeSemana) {
                numerosSesiones.add(cursor.getString(0))
            }
        }
        cursor.close()

        if (numerosSesiones.isEmpty()) return null

        var sumaProporciones = 0.0
        var conDatos = 0
        for (numSesion in numerosSesiones) {
            val regs = obtenerRegistrosPorSesion(numSesion)
            val validos = regs.filter { !it.anulado && it.clasificacion != "NO_DETERMINADO" }
            if (validos.isNotEmpty()) {
                val transito = validos.count { it.clasificacion == "TRANSITO" }
                sumaProporciones += transito.toDouble() / validos.size * 100
                conDatos++
            }
        }

        return if (conDatos == 0) null else sumaProporciones / conDatos
    }




    private fun leerSesion(cursor: Cursor) = Sesion(
        numero = cursor.getString(cursor.getColumnIndexOrThrow("numero")),
        codigoAcceso = cursor.getString(cursor.getColumnIndexOrThrow("codigoAcceso")),
        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
        horaInicioProg = cursor.getString(cursor.getColumnIndexOrThrow("horaInicioProg")),
        horaTerminoProg = cursor.getString(cursor.getColumnIndexOrThrow("horaTerminoProg")),
        observador = cursor.getString(cursor.getColumnIndexOrThrow("observador")),
        estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
        validador = cursor.getString(cursor.getColumnIndexOrThrow("validador")),
        motivoRechazo = cursor.getString(cursor.getColumnIndexOrThrow("motivoRechazo"))
    )

    fun obtenerTodasLasSesiones(): List<Sesion> {
        val lista = mutableListOf<Sesion>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM sesiones", null)
        while (cursor.moveToNext()) lista.add(leerSesion(cursor))
        cursor.close()
        return lista
    }

    
    fun guardarSesionDesdeServidor(sesion: Sesion) {
        val values = ContentValues().apply {
            put("numero", sesion.numero); put("codigoAcceso", sesion.codigoAcceso)
            put("fecha", sesion.fecha); put("horaInicioProg", sesion.horaInicioProg)
            put("horaTerminoProg", sesion.horaTerminoProg); put("observador", sesion.observador)
            put("estado", sesion.estado); put("validador", sesion.validador)
            put("motivoRechazo", sesion.motivoRechazo)
        }
        writableDatabase.insertWithOnConflict("sesiones", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    
    fun reemplazarRegistrosDeSesion(sesion: String, registros: List<RegistroPersona>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("registros", "sesion = ?", arrayOf(sesion))
            registros.forEach { r ->
                val values = ContentValues().apply {
                    put("numero", r.numero); put("sesion", r.sesion)
                    put("hora", r.hora); put("clasificacion", r.clasificacion)
                    put("rangoEtario", r.rangoEtario); put("compania", r.compania)
                    put("traeCompras", if (r.traeCompras) 1 else 0)
                    put("accesoDestino", r.accesoDestino); put("anulado", if (r.anulado) 1 else 0)
                }
                db.insert("registros", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    
    fun reemplazarConteos(fecha: String, porAcceso: Map<String, Int>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("conteos", "fecha = ?", arrayOf(fecha))
            porAcceso.forEach { (acceso, cantidad) ->
                val values = ContentValues().apply {
                    put("fecha", fecha); put("codigoAcceso", acceso); put("cantidad", cantidad)
                }
                db.insert("conteos", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
