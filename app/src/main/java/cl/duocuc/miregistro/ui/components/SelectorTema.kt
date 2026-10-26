package cl.duocuc.miregistro.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.model.ModoTema

// Ícono discreto que abre un menú con las opciones de apariencia
@Composable
fun SelectorTema(
    modoActual: ModoTema,
    onCambiar: (ModoTema) -> Unit,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { abierto = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_tema),
                contentDescription = stringResource(R.string.cd_tema),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            ModoTema.entries.forEach { modo ->
                DropdownMenuItem(
                    text = { Text(modo.etiqueta) },
                    onClick = {
                        onCambiar(modo)
                        abierto = false
                    },
                    trailingIcon = {
                        if (modo == modoActual) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}
