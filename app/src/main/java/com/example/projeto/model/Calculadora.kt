package com.example.projeto.model

import java.math.BigDecimal
import java.math.MathContext

/**
 * Regras da calculadora. Não depende do Android nem do Compose,
 * então pode ser testada com testes unitários simples.
 *
 * Cada função recebe o estado atual e devolve o novo estado.
 */
object Calculadora {

    private const val MAX_DIGITOS = 15
    private const val MAX_HISTORICO = 50

    // ------------------------------------------------------------------
    // Digitação
    // ------------------------------------------------------------------

    fun digitar(estado: EstadoCalculadora, digito: String): EstadoCalculadora {
        val e = prepararNovaEntrada(estado)
        return if (e.operacao == null) {
            e.copy(n1 = acrescentar(e.n1, digito))
        } else {
            e.copy(n2 = acrescentar(e.n2, digito))
        }
    }

    fun ponto(estado: EstadoCalculadora): EstadoCalculadora {
        val e = prepararNovaEntrada(estado)
        return if (e.operacao == null) {
            e.copy(n1 = adicionarPonto(e.n1))
        } else {
            e.copy(n2 = adicionarPonto(e.n2))
        }
    }

    /** Corrige número digitado errado: apaga o último caractere. */
    fun apagar(estado: EstadoCalculadora): EstadoCalculadora = when {
        estado.erro -> limpar(estado)
        estado.n2.isNotEmpty() -> estado.copy(n2 = removerUltimo(estado.n2))
        estado.operacao != null -> estado.copy(operacao = null)
        estado.n1.isNotEmpty() -> estado.copy(
            n1 = removerUltimo(estado.n1),
            resultadoNaTela = false,
            expressao = ""
        )
        else -> estado
    }

    /** Zera a conta, mas mantém o histórico. */
    fun limpar(estado: EstadoCalculadora): EstadoCalculadora =
        EstadoCalculadora(historico = estado.historico)

    fun limparHistorico(estado: EstadoCalculadora): EstadoCalculadora =
        estado.copy(historico = emptyList())

    // ------------------------------------------------------------------
    // Operações
    // ------------------------------------------------------------------

    fun escolherOperacao(estado: EstadoCalculadora, operacao: Operacao): EstadoCalculadora {
        if (estado.erro || estado.n1.paraNumero() == null) return estado

        // Encadeamento: em "2 + 3" apertar outro operador calcula antes de continuar
        val base = if (estado.operacao != null && estado.n2.paraNumero() != null) {
            calcular(estado)
        } else {
            estado
        }
        if (base.erro) return base
        return base.copy(operacao = operacao, resultadoNaTela = false)
    }

    fun igual(estado: EstadoCalculadora): EstadoCalculadora {
        val pronto = !estado.erro &&
            estado.operacao != null &&
            estado.n1.paraNumero() != null &&
            estado.n2.paraNumero() != null
        return if (pronto) calcular(estado) else estado
    }

    /** Desafio extra: porcentagem. */
    fun porcentagem(estado: EstadoCalculadora): EstadoCalculadora {
        if (estado.erro) return estado
        val a = estado.n1.paraNumero() ?: return estado
        val operacao = estado.operacao

        if (operacao != null) {
            val b = estado.n2.paraNumero() ?: return estado
            // 200 + 10%  ->  10% de 200 = 20      |      200 x 10%  ->  0,1
            val novo = if (operacao == Operacao.SOMA || operacao == Operacao.SUBTRACAO) {
                a.multiply(b).movePointLeft(2)
            } else {
                b.movePointLeft(2)
            }
            return estado.copy(n2 = formatar(novo))
        }

        val novo = formatar(a.movePointLeft(2))
        return estado.copy(
            n1 = novo,
            resultadoNaTela = true,
            expressao = "${formatar(a)}% =",
            historico = registrar(estado.historico, "${formatar(a)}% = $novo")
        )
    }

    /** Desafio extra: raiz quadrada do número que está sendo digitado. */
    fun raiz(estado: EstadoCalculadora): EstadoCalculadora {
        if (estado.erro) return estado
        val usandoN2 = estado.operacao != null
        val x = (if (usandoN2) estado.n2 else estado.n1).paraNumero() ?: return estado
        val conta = "√(${formatar(x)})"

        if (x.signum() < 0) return comErro(estado, conta)

        val r = formatar(BigDecimal.valueOf(Math.sqrt(x.toDouble())).round(MathContext(12)))
        val novo = estado.copy(
            expressao = "$conta =",
            historico = registrar(estado.historico, "$conta = $r")
        )
        return if (usandoN2) novo.copy(n2 = r) else novo.copy(n1 = r, resultadoNaTela = true)
    }

    // ------------------------------------------------------------------
    // Funções internas
    // ------------------------------------------------------------------

    private fun calcular(estado: EstadoCalculadora): EstadoCalculadora {
        val operacao = estado.operacao ?: return estado
        val a = estado.n1.paraNumero() ?: return estado
        val b = estado.n2.paraNumero() ?: return estado
        val conta = "${formatar(a)} ${operacao.simbolo} ${formatar(b)}"

        val resultado = when (operacao) {
            Operacao.SOMA -> a + b
            Operacao.SUBTRACAO -> a - b
            Operacao.MULTIPLICACAO -> a * b
            Operacao.DIVISAO -> {
                if (b.signum() == 0) return comErro(estado, conta) // divisão por zero
                a.divide(b, MathContext.DECIMAL64)
            }
        }

        val texto = formatar(resultado)
        return estado.copy(
            n1 = texto,
            n2 = "",
            operacao = null,
            resultadoNaTela = true,
            expressao = "$conta =",
            historico = registrar(estado.historico, "$conta = $texto")
        )
    }

    private fun comErro(estado: EstadoCalculadora, conta: String): EstadoCalculadora =
        estado.copy(
            n1 = "",
            n2 = "",
            operacao = null,
            erro = true,
            resultadoNaTela = false,
            expressao = "$conta =",
            historico = registrar(estado.historico, "$conta = Erro")
        )

    /** Depois de um erro ou de um resultado, o próximo número começa do zero. */
    private fun prepararNovaEntrada(estado: EstadoCalculadora): EstadoCalculadora {
        val e = if (estado.erro) limpar(estado) else estado
        return if (e.resultadoNaTela && e.operacao == null) {
            e.copy(n1 = "", expressao = "", resultadoNaTela = false)
        } else {
            e
        }
    }

    private fun acrescentar(atual: String, digito: String): String {
        if (atual.count { it.isDigit() } >= MAX_DIGITOS) return atual  // limite de 15 dígitos
        return if (atual == "0") digito else atual + digito            // evita "007"
    }

    private fun adicionarPonto(atual: String): String = when {
        "." in atual -> atual                                          // só um ponto por número
        atual.isEmpty() || atual == "-" -> atual + "0."                // ".5" vira "0.5"
        else -> "$atual."
    }

    private fun removerUltimo(texto: String): String {
        val menor = texto.dropLast(1)
        return if (menor == "-") "" else menor
    }

    private fun registrar(historico: List<String>, linha: String): List<String> =
        (listOf(linha) + historico).take(MAX_HISTORICO)

    /** Converte o texto em número; devolve null se estiver vazio ou incompleto ("-"). */
    private fun String.paraNumero(): BigDecimal? = toBigDecimalOrNull()

    /** BigDecimal evita erros como 0.1 + 0.2 = 0.30000000000000004. */
    private fun formatar(valor: BigDecimal): String {
        val v = valor.round(MathContext.DECIMAL64)
        return if (v.signum() == 0) "0" else v.stripTrailingZeros().toPlainString()
    }
}
