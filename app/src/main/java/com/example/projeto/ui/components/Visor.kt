package com.example.projeto.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projeto.ui.theme.CorErro
import com.example.projeto.ui.theme.CorTexto
import com.example.projeto.ui.theme.CorTextoSecundario
import com.example.projeto.ui.theme.ProjetoTheme

/**
 * Visor: linha pequena com a última conta e texto grande com o que está na tela.
 * Não guarda estado nenhum, só mostra o que recebe.
 */
@Composable
fun Visor(
    expressao: String,
    texto: String,
    erro: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = expressao,
            color = CorTextoSecundario,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = texto,
            color = if (erro) CorErro else CorTexto,
            fontSize = tamanhoDaFonte(texto),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Quanto mais longo o texto, menor a fonte, para caber no visor. */
private fun tamanhoDaFonte(texto: String): TextUnit = when {
    texto.length <= 8 -> 64.sp
    texto.length <= 12 -> 48.sp
    texto.length <= 18 -> 34.sp
    texto.length <= 26 -> 24.sp
    else -> 18.sp
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1115)
@Composable
fun VisorPreview() {
    ProjetoTheme {
        Visor(expressao = "2 + 3 =", texto = "5", erro = false)
    }
}
