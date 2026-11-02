package com.example.projeto.model

/** As quatro operações da calculadora e o símbolo mostrado no visor. */
enum class Operacao(val simbolo: String) {
    SOMA("+"),
    SUBTRACAO("−"),
    MULTIPLICACAO("×"),
    DIVISAO("÷")
}

/**
 * Tudo o que a calculadora "sabe" em um instante.
 *
 * É imutável: cada tecla gera um NOVO estado (com copy), e o Compose
 * redesenha a tela quando o estado muda. Assim a tela só descreve
 * "como mostrar este estado".
 */
data class EstadoCalculadora(
    val n1: String = "",                  // primeiro número (texto digitado)
    val n2: String = "",                  // segundo número (texto digitado)
    val operacao: Operacao? = null,       // null = nenhuma operação escolhida
    val erro: Boolean = false,            // true após divisão por zero, raiz de negativo...
    val resultadoNaTela: Boolean = false, // true logo depois de "=": o próximo dígito recomeça
    val expressao: String = "",           // linha pequena acima do visor: "2 + 3 ="
    val historico: List<String> = emptyList() // mais recente primeiro
) {
    /** Texto grande do visor. */
    val textoVisor: String
        get() = when {
            erro -> "Erro"
            operacao == null -> n1.ifEmpty { "0" }
            else -> "$n1 ${operacao.simbolo} $n2".trimEnd()
        }
}
