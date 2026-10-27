package cl.duocuc.miregistro.data

import android.content.Context
import androidx.core.content.edit
import cl.duocuc.miregistro.model.ModoTema

class TemaPreferencias(context: Context) {

    private val prefs = context.getSharedPreferences("tema", Context.MODE_PRIVATE)

    companion object {
        private const val CLAVE_MODO = "modo_tema"
    }

    fun guardar(modo: ModoTema) {
        prefs.edit { putString(CLAVE_MODO, modo.name) }
    }

    
    fun leer(): ModoTema =
        ModoTema.entries.find { it.name == prefs.getString(CLAVE_MODO, null) } ?: ModoTema.SISTEMA
}
