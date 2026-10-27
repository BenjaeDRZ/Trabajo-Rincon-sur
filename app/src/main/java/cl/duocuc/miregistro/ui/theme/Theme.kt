package cl.duocuc.miregistro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cl.duocuc.miregistro.model.ModoTema

private val EsquemaClaro = lightColorScheme(
    primary = Primario,
    onPrimary = Blanco,
    primaryContainer = PrimarioClaro,
    onPrimaryContainer = PrimarioOscuro,

    secondary = Secundario,
    onSecondary = Blanco,
    secondaryContainer = PrimarioClaro,
    onSecondaryContainer = PrimarioOscuro,

    tertiary = Acento,
    onTertiary = TextoOscuro,

    background = FondoClaro,
    onBackground = TextoOscuro,

    surface = SuperficieClara,
    onSurface = TextoOscuro,
    surfaceVariant = PrimarioClaro,
    onSurfaceVariant = PrimarioOscuro,

    error = Color(0xFFBA1A1A)
)

private val EsquemaOscuro = darkColorScheme(
    primary = PrimarioClaro,
    onPrimary = PrimarioOscuro,
    primaryContainer = Primario,
    onPrimaryContainer = Blanco,

    secondary = Secundario,
    onSecondary = Blanco,
    secondaryContainer = PrimarioOscuro,
    onSecondaryContainer = PrimarioClaro,

    tertiary = Acento,
    onTertiary = TextoOscuro,

    background = FondoOscuro,
    onBackground = TextoClaro,

    surface = SuperficieOscura,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieOscura,
    onSurfaceVariant = TextoClaro,

    error = Color(0xFFCF6679)
)

private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun MiRegistroTheme(
    modoTema: ModoTema = ModoTema.SISTEMA,
    content: @Composable () -> Unit
) {
    val darkTheme = when (modoTema) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.SISTEMA -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Tipografia,
        shapes = Formas,
        content = content
    )
}