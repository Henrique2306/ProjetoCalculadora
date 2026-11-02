package com.example.projeto.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projeto.ui.theme.CorFuncao
import com.example.projeto.ui.theme.CorIgual
import com.example.projeto.ui.theme.CorNumero
import com.example.projeto.ui.theme.CorOperador
import com.example.projeto.ui.theme.CorTexto
import com.example.projeto.ui.theme.ProjetoTheme

/** Tipo da tecla: define a cor do botão. */
enum class TipoTecla { NUMERO, FUNCAO, OPERADOR, IGUAL }

/** Todas as teclas da calculadora: o texto do botão, o tipo e a descrição (acessibilidade). */
enum class Tecla(
    val rotulo: String,
    val tipo: TipoTecla,
    val descricao: String = rotulo
) {
    ZERO("0", TipoTecla.NUMERO),
    UM("1", TipoTecla.NUMERO),
    DOIS("2", TipoTecla.NUMERO),
    TRES("3", TipoTecla.NUMERO),
    QUATRO("4", TipoTecla.NUMERO),
    CINCO("5", TipoTecla.NUMERO),
    SEIS("6", TipoTecla.NUMERO),
    SETE("7", TipoTecla.NUMERO),
    OITO("8", TipoTecla.NUMERO),
    NOVE("9", TipoTecla.NUMERO),
    PONTO(".", TipoTecla.NUMERO, "Ponto decimal"),

    SOMA("+", TipoTecla.OPERADOR, "Somar"),
    SUBTRACAO("−", TipoTecla.OPERADOR, "Subtrair"),
    MULTIPLICACAO("×", TipoTecla.OPERADOR, "Multiplicar"),
    DIVISAO("÷", TipoTecla.OPERADOR, "Dividir"),
    IGUAL("=", TipoTecla.IGUAL, "Igual"),

    LIMPAR("C", TipoTecla.FUNCAO, "Limpar"),
    APAGAR("⌫", TipoTecla.FUNCAO, "Apagar último dígito"),
    PORCENTAGEM("%", TipoTecla.FUNCAO, "Porcentagem"),
    RAIZ("√", TipoTecla.FUNCAO, "Raiz quadrada")
}

// O teclado é desenhado a partir desta lista: 5 linhas x 4 colunas
private val LinhasDoTeclado = listOf(
    listOf(Tecla.LIMPAR, Tecla.APAGAR, Tecla.PORCENTAGEM, Tecla.DIVISAO),
    listOf(Tecla.SETE, Tecla.OITO, Tecla.NOVE, Tecla.MULTIPLICACAO),
    listOf(Tecla.QUATRO, Tecla.CINCO, Tecla.SEIS, Tecla.SUBTRACAO),
    listOf(Tecla.UM, Tecla.DOIS, Tecla.TRES, Tecla.SOMA),
    listOf(Tecla.RAIZ, Tecla.ZERO, Tecla.PONTO, Tecla.IGUAL)
)

/** Teclado completo. Avisa a tela (onTecla) quando uma tecla é tocada. */
@Composable
fun Teclado(
    onTecla: (Tecla) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        for (linha in LinhasDoTeclado) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (tecla in linha) {
                    BotaoCalculadora(
                        tecla = tecla,
                        onClick = { onTecla(tecla) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

/** Um botão do teclado: Button + Text, com a cor escolhida pelo tipo da tecla. */
@Composable
fun BotaoCalculadora(
    tecla: Tecla,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val corDeFundo = when (tecla.tipo) {
        TipoTecla.NUMERO -> CorNumero
        TipoTecla.FUNCAO -> CorFuncao
        TipoTecla.OPERADOR -> CorOperador
        TipoTecla.IGUAL -> CorIgual
    }

    Button(
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = tecla.descricao },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = corDeFundo,
            contentColor = CorTexto
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = tecla.rotulo, fontSize = 26.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F1115, heightDp = 400)
@Composable
fun TecladoPreview() {
    ProjetoTheme {
        Teclado(onTecla = {})
    }
}
