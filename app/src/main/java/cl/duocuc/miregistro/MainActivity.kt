package cl.duocuc.miregistro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cl.duocuc.miregistro.data.TemaPreferencias
import cl.duocuc.miregistro.model.ModoTema
import cl.duocuc.miregistro.navigation.AppNavegacion
import cl.duocuc.miregistro.ui.theme.MiRegistroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val temaPreferencias = TemaPreferencias(applicationContext)

        setContent {
            var modoTema by rememberSaveable { mutableStateOf(temaPreferencias.leer()) }

            MiRegistroTheme(modoTema = modoTema) {
                AppNavegacion(
                    modoTema = modoTema,
                    onCambiarModoTema = { nuevo ->
                        modoTema = nuevo
                        temaPreferencias.guardar(nuevo)
                    }
                )
            }
        }
    }
}