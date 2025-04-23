package com.example.pi3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class CadastroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val etNomeCadastro = findViewById<EditText>(R.id.etNomeCadastro)
        val etEmailCadastro = findViewById<EditText>(R.id.etEmailCadastro)
        val etSenhaCadastro = findViewById<EditText>(R.id.etSenhaCadastro)
        val etConfirmarSenhaCadastro = findViewById<EditText>(R.id.etConfirmarSenhaCadastro)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val btnVoltarLogin = findViewById<Button>(R.id.btnVoltarLogin)

        val db = FirebaseFirestore.getInstance()
        val COLLECTION_USUARIOS = "usuarios_cadastro" // Nome da sua coleção no Firestore

        btnCadastrar.setOnClickListener {
            Log.d("CadastroClick", "Botão Cadastrar clicado!")

            val nome = etNomeCadastro.text.toString()
            val email = etEmailCadastro.text.toString()
            val senha = etSenhaCadastro.text.toString()
            val confirmarSenha = etConfirmarSenhaCadastro.text.toString()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("CadastroFirebase", "Iniciando envio para o Firebase...")

            val userData = hashMapOf(
                "nome" to nome,
                "email" to email
                // NÃO envie a senha diretamente para o Firestore por segurança!
            )

            db.collection(COLLECTION_USUARIOS)
                .add(userData)
                .addOnSuccessListener { documentReference ->
                    Log.d("CadastroFirebase", "Sucesso ao enviar. ID: ${documentReference.id}")
                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("CadastroFirebase", "Falha ao enviar: ${e.message}", e)
                    Toast.makeText(this, "Erro ao cadastrar.", Toast.LENGTH_SHORT).show()
                }

            Log.d("CadastroFirebase", "Fim do bloco de envio.")
        }

        btnVoltarLogin.setOnClickListener {
            finish()
        }
    }
}