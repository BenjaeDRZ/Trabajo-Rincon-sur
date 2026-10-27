package cl.duocuc.miregistro.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duocuc.miregistro.model.Rol
import cl.duocuc.miregistro.model.Usuario

class UsuarioDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "miregistro.db"
        
        
        
        private const val VERSION_BD = 2

        private const val TABLA_ROLES = "roles"
        private const val ROL_ID = "id_rol"
        private const val ROL_NOMBRE = "nombre_rol"

        private const val TABLA_USUARIOS = "usuarios"
        private const val USU_ID = "id_usuario"
        private const val USU_NOMBRE = "nombre"
        private const val USU_CORREO = "correo"
        private const val USU_CIUDAD = "ciudad"
        private const val USU_ROL = "id_rol"
        
        private const val USU_AVATAR = "avatar"

        private const val SQL_CREAR_ROLES =
            "CREATE TABLE $TABLA_ROLES (" +
                "$ROL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ROL_NOMBRE TEXT NOT NULL UNIQUE)"

        private const val SQL_CREAR_USUARIOS =
            "CREATE TABLE $TABLA_USUARIOS (" +
                "$USU_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USU_NOMBRE TEXT NOT NULL, " +
                "$USU_CORREO TEXT NOT NULL, " +
                "$USU_CIUDAD TEXT NOT NULL, " +
                
                
                "$USU_AVATAR BLOB, " +
                "$USU_ROL INTEGER NOT NULL REFERENCES $TABLA_ROLES($ROL_ID))"

        
        
        private const val SQL_AGREGAR_AVATAR =
            "ALTER TABLE $TABLA_USUARIOS ADD COLUMN $USU_AVATAR BLOB"

        private const val SQL_PRECARGAR_ROLES =
            "INSERT INTO $TABLA_ROLES ($ROL_ID, $ROL_NOMBRE) VALUES " +
                "(1, 'Administrador'), (2, 'Docente'), (3, 'Estudiante')"

        private const val SQL_PRECARGAR_USUARIOS =
            "INSERT INTO $TABLA_USUARIOS ($USU_NOMBRE, $USU_CORREO, $USU_CIUDAD, $USU_ROL) VALUES " +
                "('Camila Rojas', 'camila@duocuc.cl', 'Santiago', 1), " +
                "('Diego Muñoz', 'diego@duocuc.cl', 'Valparaíso', 2), " +
                "('Valentina Soto', 'valentina@duocuc.cl', 'Concepción', 3), " +
                "('Matías Pérez', 'matias@duocuc.cl', 'Temuco', 3), " +
                "('Fernanda Díaz', 'fernanda@duocuc.cl', 'Santiago', 3)"

        private const val SQL_SELECT_USUARIOS =
            "SELECT u.*, r.$ROL_NOMBRE AS $ROL_NOMBRE " +
                "FROM $TABLA_USUARIOS u " +
                "INNER JOIN $TABLA_ROLES r ON u.$USU_ROL = r.$ROL_ID"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_ROLES)
        db.execSQL(SQL_CREAR_USUARIOS)
        db.execSQL(SQL_PRECARGAR_ROLES)
        db.execSQL(SQL_PRECARGAR_USUARIOS)
    }

    
    
    
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(SQL_AGREGAR_AVATAR)
        }
        
    }

    fun listarRoles(): List<Rol> {
        val roles = mutableListOf<Rol>()
        readableDatabase.rawQuery(
            "SELECT $ROL_ID, $ROL_NOMBRE FROM $TABLA_ROLES ORDER BY $ROL_ID", null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                roles.add(
                    Rol(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(ROL_ID)),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE))
                    )
                )
            }
        }
        return roles
    }

    fun insertarUsuario(usuario: Usuario): Long =
        writableDatabase.insert(TABLA_USUARIOS, null, valoresDe(usuario))

    fun listarUsuarios(): List<Usuario> =
        consultarUsuarios("$SQL_SELECT_USUARIOS ORDER BY u.$USU_NOMBRE", null)

    fun buscarUsuarios(texto: String): List<Usuario> {
        val patron = "%$texto%"
        return consultarUsuarios(
            "$SQL_SELECT_USUARIOS WHERE u.$USU_NOMBRE LIKE ? OR u.$USU_CORREO LIKE ? " +
                "ORDER BY u.$USU_NOMBRE",
            arrayOf(patron, patron)
        )
    }

    fun buscarPorId(id: Long): Usuario? =
        consultarUsuarios("$SQL_SELECT_USUARIOS WHERE u.$USU_ID = ?", arrayOf(id.toString()))
            .firstOrNull()

    fun actualizarUsuario(usuario: Usuario): Int =
        writableDatabase.update(
            TABLA_USUARIOS, valoresDe(usuario), "$USU_ID = ?", arrayOf(usuario.id.toString())
        )

    fun eliminarUsuario(id: Long): Int =
        writableDatabase.delete(TABLA_USUARIOS, "$USU_ID = ?", arrayOf(id.toString()))

    
    
    
    fun actualizarAvatar(id: Long, avatar: ByteArray): Int {
        val valores = ContentValues().apply { put(USU_AVATAR, avatar) }
        return writableDatabase.update(TABLA_USUARIOS, valores, "$USU_ID = ?", arrayOf(id.toString()))
    }

    private fun valoresDe(usuario: Usuario) = ContentValues().apply {
        put(USU_NOMBRE, usuario.nombre)
        put(USU_CORREO, usuario.correo)
        put(USU_CIUDAD, usuario.ciudad)
        put(USU_ROL, usuario.rolId)
    }

    private fun consultarUsuarios(sql: String, args: Array<String>?): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        readableDatabase.rawQuery(sql, args).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(leerUsuario(cursor))
            }
        }
        return lista
    }

    private fun leerUsuario(cursor: Cursor) = Usuario(
        nombre = cursor.getString(cursor.getColumnIndexOrThrow(USU_NOMBRE)),
        correo = cursor.getString(cursor.getColumnIndexOrThrow(USU_CORREO)),
        ciudad = cursor.getString(cursor.getColumnIndexOrThrow(USU_CIUDAD)),
        id = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ID)),
        rolId = cursor.getLong(cursor.getColumnIndexOrThrow(USU_ROL)),
        rolNombre = cursor.getString(cursor.getColumnIndexOrThrow(ROL_NOMBRE)),
        
        
        avatar = cursor.getColumnIndexOrThrow(USU_AVATAR).let { columna ->
            if (cursor.isNull(columna)) null else cursor.getBlob(columna)
        }
    )
}
