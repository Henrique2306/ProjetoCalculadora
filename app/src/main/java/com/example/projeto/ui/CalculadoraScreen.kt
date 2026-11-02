package com.example.projeto.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projeto.model.Calculadora
import com.example.projeto.model.EstadoCalculadora
import com.example.projeto.model.Operacao
import com.example.projeto.ui.components.BarraHistorico
import com.example.projeto.ui.components.PainelHistorico
import com.example.projeto.ui.components.Tecla
import com.example.projeto.ui.components.Teclado
import com.example.projeto.ui.components.Visor
import com.example.projeto.ui.theme.ProjetoTheme

/**
 * Tela da calculadora. É aqui que moram os dois estados da tela:
 *  - estado: a conta (n1, n2, operação, histórico...)
 *  - historicoAberto: se o painel do histórico está visível
 *
 * Os componentes (Visor, Teclado, Histórico) só recebem dados e avisam
 * quando algo acontece (onTecla, onAlternar...). Isso se chama "elevar o estado".
 */
@Composable
fun CalculadoraScreen() {
    // rememberSaveable: o estado sobrevive à rotação da tela
    var estado by rememberSaveable(stateSaver = EstadoCalculadoraSaver) {
        mutableStateOf(EstadoCalculadora())
    }
    var historicoAberto by rememberSaveable { mutableStateOf(false) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(12.dp)
        ) {
            BarraHistorico(
                aberto = historicoAberto,
                onAlternar = { historicoAberto = !historicoAberto },
                onLimpar = { estado = Calculadora.limparHistorico(estado) }
            )

            if (historicoAberto) {
                PainelHistorico(
                    historico = estado.historico,
                    modifier = Modifier.weight(1f)
                )
            }

            Visor(
                expressao = estado.expressao,
                texto = estado.textoVisor,
                erro = estado.erro,
                modifier = Modifier.weight(1f)
            )

            Teclado(
                onTecla = { tecla -> estado = processarTecla(estado, tecla) },
                modifier = Modifier.weight(3f)
            )
        }
    }
}

/** Liga cada tecla à função certa da Calculadora. */
private fun processarTecla(estado: EstadoCalculadora, tecla: Tecla): EstadoCalculadora =
    when (tecla) {
        Tecla.LIMPAR -> Calculadora.limpar(estado)
        Tecla.APAGAR -> Calculadora.apagar(estado)
        Tecla.PORCENTAGEM -> Calculadora.porcentagem(estado)
        Tecla.RAIZ -> Calculadora.raiz(estado)
        Tecla.PONTO -> Calculadora.ponto(estado)
        Tecla.IGUAL -> Calculadora.igual(estado)
        Tecla.SOMA -> Calculadora.escolherOperacao(estado, Operacao.SOMA)
        Tecla.SUBTRACAO -> Calculadora.escolherOperacao(estado, Operacao.SUBTRACAO)
        Tecla.MULTIPLICACAO -> Calculadora.escolherOperacao(estado, Operacao.MULTIPLICACAO)
        Tecla.DIVISAO -> Calculadora.escolherOperacao(estado, Operacao.DIVISAO)
        else -> Calculadora.digitar(estado, tecla.rotulo)   // teclas 0 a 9
    }

/** Ensina o rememberSaveable a guardar o estado quando a tela gira. */
@Suppress("UNCHECKED_CAST")
private val EstadoCalculadoraSaver = listSaver<EstadoCalculadora, Any>(
    save = {
        listOf(
            it.n1,
            it.n2,
            it.operacao?.name ?: "",
            it.erro,
            it.resultadoNaTela,
            it.expressao,
            ArrayList(it.historico)
        )
    },
    restore = {
        val nomeOperacao = it[2] as String
        EstadoCalculadora(
            n1 = it[0] as String,
            n2 = it[1] as String,
            operacao = if (nomeOperacao.isEmpty()) null else Operacao.valueOf(nomeOperacao),
            erro = it[3] as Boolean,
            resultadoNaTela = it[4] as Boolean,
            expressao = it[5] as String,
            historico = it[6] as List<String>
        )
    }
)

@Preview(showBackground = true, backgroundColor = 0xFF0F1115)
@Composable
fun CalculadoraScreenPreview() {
    ProjetoTheme {
        CalculadoraScreen()
    }
}
