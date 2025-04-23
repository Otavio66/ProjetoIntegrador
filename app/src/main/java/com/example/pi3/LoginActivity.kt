package com.example.pi3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Vincula ao XML da tela

        // Inicialize o Firestore para o teste (forma mais explícita)
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val testeData = hashMapOf(
            "teste_login_activity" to "Integração Firebase OK!",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("testes_login")
            .add(testeData)
            .addOnSuccessListener { documentReference ->
                Log.d("FirebaseTeste", "Teste da LoginActivity: Documento adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseTeste", "Teste da LoginActivity: Erro ao adicionar documento: ${e.message}", e)
            }

        // Botão para ir para Cadastro
        val tvCadastrar = findViewById<TextView>(R.id.tvCadastrar)
        tvCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        // Botão de Login
        val btnLogin = findViewById<Button>(R.id.btnEntrar)
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