package com.example.projeto.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projeto.ui.theme.CorOperador
import com.example.projeto.ui.theme.CorPainel
import com.example.projeto.ui.theme.CorTextoSecundario
import com.example.projeto.ui.theme.ProjetoTheme

/** Barra do topo: botão "Histórico/Fechar" e, com o painel aberto, "Limpar histórico". */
@Composable
fun BarraHistorico(
    aberto: Boolean,
    onAlternar: () -> Unit,
    onLimpar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cores = ButtonDefaults.textButtonColors(contentColor = CorOperador)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (aberto) {
            TextButton(onClick = onLimpar, colors = cores) {
                Text("Limpar histórico")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onAlternar, colors = cores) {
            Text(if (aberto) "Fechar" else "Histórico")
        }
    }
}

/** Lista dos cálculos feitos (LazyColumn só desenha o que aparece na tela). */
@Composable
fun PainelHistorico(
    historico: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        color = CorPainel,
        shape = RoundedCornerShape(16.dp)
    ) {
        if (historico.isEmpty()) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "Nenhum cálculo ainda", color = CorTextoSecundario)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                items(historico) { linha ->
                    Text(
                        text = linha,
                        color = CorTextoSecundario,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1115, heightDp = 300)
@Composable
fun PainelHistoricoPreview() {
    ProjetoTheme {
        PainelHistorico(historico = listOf("5 × 4 = 20", "2 + 3 = 5"))
    }
}
