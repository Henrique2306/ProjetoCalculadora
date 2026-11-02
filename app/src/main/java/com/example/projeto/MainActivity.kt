package com.example.projeto

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.projeto.ui.CalculadoraScreen
import com.example.projeto.ui.theme.ProjetoTheme

/** Ponto de entrada do app: só define o tema e chama a tela. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tela inteira, com ícones claros nas barras do sistema (o fundo é escuro)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            ProjetoTheme {
                CalculadoraScreen()
            }
        }
    }
}
