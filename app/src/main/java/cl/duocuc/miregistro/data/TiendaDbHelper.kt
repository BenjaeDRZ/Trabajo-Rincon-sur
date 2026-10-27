package cl.duocuc.miregistro.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duocuc.miregistro.model.RegistroPersona
import cl.duocuc.miregistro.model.Sesion

class TiendaDbHelper(context: Context) :
    SQLiteOpenHelper(context, "rinconsur.db", null, 3) {

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
}