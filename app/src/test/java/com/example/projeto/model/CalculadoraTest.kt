package com.example.projeto.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculadoraTest {

    /** Digita uma sequência como "12.5" (dígitos e ponto), tecla por tecla. */
    private fun digitar(inicio: EstadoCalculadora, texto: String): EstadoCalculadora =
        texto.fold(inicio) { estado, c ->
            if (c == '.') Calculadora.ponto(estado) else Calculadora.digitar(estado, c.toString())
        }

    /** Faz uma conta completa: a, operação, b e "=". */
    private fun conta(a: String, operacao: Operacao, b: String): EstadoCalculadora {
        var estado = digitar(EstadoCalculadora(), a)
        estado = Calculadora.escolherOperacao(estado, operacao)
        estado = digitar(estado, b)
        return Calculadora.igual(estado)
    }

    // ----- Operações básicas -----

    @Test
    fun estadoInicialMostraZero() {
        assertEquals("0", EstadoCalculadora().textoVisor)
    }

    @Test
    fun soma() {
        assertEquals("5", conta("2", Operacao.SOMA, "3").textoVisor)
    }

    @Test
    fun subtracaoComResultadoNegativo() {
        assertEquals("-2", conta("3", Operacao.SUBTRACAO, "5").textoVisor)
    }

    @Test
    fun multiplicacaoComDecimal() {
        assertEquals("10", conta("4", Operacao.MULTIPLICACAO, "2.5").textoVisor)
    }

    @Test
    fun divisaoComResultadoDecimal() {
        assertEquals("2.5", conta("10", Operacao.DIVISAO, "4").textoVisor)
    }

    @Test
    fun decimaisSemErroDePrecisao() {
        assertEquals("0.3", conta("0.1", Operacao.SOMA, "0.2").textoVisor)
    }

    // ----- Erros -----

    @Test
    fun divisaoPorZeroMostraErroERecupera() {
        var estado = conta("8", Operacao.DIVISAO, "0")
        assertTrue(estado.erro)
        assertEquals("Erro", estado.textoVisor)

        estado = Calculadora.digitar(estado, "7") // digitar depois do erro recomeça do zero
        assertFalse(estado.erro)
        assertEquals("7", estado.textoVisor)
    }

    @Test
    fun operadorSemNumeroEIgnorado() {
        val estado = Calculadora.escolherOperacao(EstadoCalculadora(), Operacao.SOMA)
        assertEquals("0", estado.textoVisor)
    }

    @Test
    fun igualSemSegundoNumeroNaoFazNada() {
        var estado = digitar(EstadoCalculadora(), "5")
        estado = Calculadora.escolherOperacao(estado, Operacao.SOMA)
        estado = Calculadora.igual(estado)
        assertEquals("5 +", estado.textoVisor)
    }

    // ----- Digitação e correção -----

    @Test
    fun zerosAEsquerdaSaoIgnorados() {
        assertEquals("7", digitar(EstadoCalculadora(), "007").textoVisor)
    }

    @Test
    fun pontoDuplicadoEIgnorado() {
        assertEquals("1.23", digitar(EstadoCalculadora(), "1.2.3").textoVisor)
    }

    @Test
    fun pontoNoInicioViraZeroPonto() {
        assertEquals("0.5", digitar(EstadoCalculadora(), ".5").textoVisor)
    }

    @Test
    fun limiteDeQuinzeDigitos() {
        val estado = digitar(EstadoCalculadora(), "12345678901234567890")
        assertEquals("123456789012345", estado.textoVisor)
    }

    @Test
    fun apagarRemoveUltimoDigito() {
        val estado = Calculadora.apagar(digitar(EstadoCalculadora(), "123"))
        assertEquals("12", estado.textoVisor)
    }

    @Test
    fun apagarRemoveOperadorQuandoSegundoNumeroVazio() {
        var estado = digitar(EstadoCalculadora(), "12")
        estado = Calculadora.escolherOperacao(estado, Operacao.SOMA)
        estado = Calculadora.apagar(estado)
        assertEquals("12", estado.textoVisor)
    }

    @Test
    fun limparZeraAContaMasMantemOHistorico() {
        val estado = Calculadora.limpar(conta("2", Operacao.SOMA, "3"))
        assertEquals("0", estado.textoVisor)
        assertEquals("", estado.expressao)
        assertEquals(1, estado.historico.size)
    }

    @Test
    fun digitarDepoisDoResultadoIniciaNovoNumero() {
        val estado = Calculadora.digitar(conta("2", Operacao.SOMA, "3"), "9")
        assertEquals("9", estado.textoVisor)
    }

    @Test
    fun resultadoPodeContinuarEmNovaOperacao() {
        var estado = conta("2", Operacao.SOMA, "3")
        estado = Calculadora.escolherOperacao(estado, Operacao.MULTIPLICACAO)
        estado = digitar(estado, "4")
        estado = Calculadora.igual(estado)
        assertEquals("20", estado.textoVisor)
    }

    @Test
    fun encadeamentoDeOperacoes() {
        var estado = digitar(EstadoCalculadora(), "2")
        estado = Calculadora.escolherOperacao(estado, Operacao.SOMA)
        estado = digitar(estado, "3")
        estado = Calculadora.escolherOperacao(estado, Operacao.SOMA)
        assertEquals("5 +", estado.textoVisor)
    }

    // ----- Desafio extra -----

    @Test
    fun porcentagemSimples() {
        val estado = Calculadora.porcentagem(digitar(EstadoCalculadora(), "50"))
        assertEquals("0.5", estado.textoVisor)
    }

    @Test
    fun porcentagemEmSoma() {
        var estado = digitar(EstadoCalculadora(), "200")
        estado = Calculadora.escolherOperacao(estado, Operacao.SOMA)
        estado = digitar(estado, "10")
        estado = Calculadora.porcentagem(estado)
        estado = Calculadora.igual(estado)
        assertEquals("220", estado.textoVisor)
    }

    @Test
    fun raizQuadrada() {
        val estado = Calculadora.raiz(digitar(EstadoCalculadora(), "9"))
        assertEquals("3", estado.textoVisor)
    }

    @Test
    fun raizNaoExata() {
        val estado = Calculadora.raiz(digitar(EstadoCalculadora(), "2"))
        assertEquals("1.41421356237", estado.textoVisor)
    }

    @Test
    fun raizDeNegativoGeraErro() {
        val estado = Calculadora.raiz(conta("3", Operacao.SUBTRACAO, "5"))
        assertTrue(estado.erro)
    }

    @Test
    fun historicoGuardaDoMaisRecenteParaOMaisAntigo() {
        var estado = conta("2", Operacao.SOMA, "3")
        estado = Calculadora.escolherOperacao(estado, Operacao.MULTIPLICACAO)
        estado = digitar(estado, "4")
        estado = Calculadora.igual(estado)
        assertEquals(listOf("5 × 4 = 20", "2 + 3 = 5"), estado.historico)
    }

    @Test
    fun limparHistorico() {
        val estado = Calculadora.limparHistorico(conta("2", Operacao.SOMA, "3"))
        assertTrue(estado.historico.isEmpty())
    }
}
