package com.example.projeto.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// A calculadora usa sempre o tema escuro, com as cores definidas em Color.kt
private val EsquemaDeCores = darkColorScheme(
    primary = CorOperador,
    secondary = CorIgual,
    background = CorFundo,
    surface = CorPainel,
    onBackground = CorTexto,
    onSurface = CorTexto,
    error = CorErro
)

@Composable
fun ProjetoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaDeCores,
        typography = Typography,
        content = content
    )
}
