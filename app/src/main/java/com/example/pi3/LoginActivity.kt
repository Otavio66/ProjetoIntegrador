package com.example.pi3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Vincula ao XML da tela

        // Botão para ir para Cadastro
        val tvCadastrar = findViewById<TextView>(R.id.tvCadastrar)
        tvCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        // Botão de Login
        val btnLogin = findViewById<Button>(R.id.btnEntrar) // Corrigi o ID para corresponder ao XML
        btnLogin.setOnClickListener {
            val identificacao = findViewById<EditText>(R.id.etIdentificacao).text.toString()
            val senha = findViewById<EditText>(R.id.etSenha).text.toString()

            if (identificacao.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()
                // Aqui você adicionaria a lógica de autenticação real
            }
        }
    }
}