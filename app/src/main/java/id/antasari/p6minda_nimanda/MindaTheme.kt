package id.antasari.p6minda_nimanda

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MindaColorScheme = lightColorScheme()
// Bisa dikustom nanti (pink / purple / green tab)

@Composable
fun MindaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MindaColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
