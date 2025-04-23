package com.example.pi3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CadastroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        btnCadastrar.setOnClickListener {
            val nome = findViewById<EditText>(R.id.etNomeCadastro).text.toString()
            val email = findViewById<EditText>(R.id.etEmailCadastro).text.toString()
            val senha = findViewById<EditText>(R.id.etSenhaCadastro).text.toString()
            val confirmarSenha = findViewById<EditText>(R.id.etConfirmarSenhaCadastro).text.toString()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
            finish() // Volta para a tela de login
        }

        // Botão de Voltar para o Login
        val btnVoltarLogin = findViewById<Button>(R.id.btnVoltarLogin)
        btnVoltarLogin.setOnClickListener {
            finish() // Simplesmente finaliza a Activity de Cadastro e volta para a anterior (LoginActivity)
        }
    }
}