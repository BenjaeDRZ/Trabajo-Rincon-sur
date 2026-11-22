package cl.duocuc.miregistro.ui.components



import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import cl.duocuc.miregistro.R

@Composable
fun BotonAtras(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_volver),
            contentDescription = "Volver",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}