package com.example.pi3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.content.Intent

class CadastroActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth  // FirebaseAuth para autenticaçao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        auth = FirebaseAuth.getInstance()  // inicializa o Firebase authentication

        val etNomeCadastro = findViewById<EditText>(R.id.etNomeCadastro)
        val etEmailCadastro = findViewById<EditText>(R.id.etEmailCadastro)
        val etSenhaCadastro = findViewById<EditText>(R.id.etSenhaCadastro)
        val etConfirmarSenhaCadastro = findViewById<EditText>(R.id.etConfirmarSenhaCadastro)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val tvVoltarLogin = findViewById<TextView>(R.id.tvVoltarLogin)

        tvVoltarLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val db = FirebaseFirestore.getInstance()
        val COLLECTION_USUARIOS = "usuarios_cadastro" // nome da coleçap no Firestore

        // Lógica do botão de cadastro
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

            // verifica se a senha tem 6 caractere
            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // verifica se o email já esta sendo usado
            auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods != null && signInMethods.isNotEmpty()) {
                            // Se o email esta sendo usado
                            Toast.makeText(this, "Este email já está em uso. Tente outro!", Toast.LENGTH_SHORT).show()
                        } else {

                            Log.d("CadastroFirebase", "Iniciando cadastro no Firebase Authentication...")

                            // Cria user na firebase
                            auth.createUserWithEmailAndPassword(email, senha)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        Log.d("CadastroFirebase", "Usuário criado com sucesso!")
                                        val user = auth.currentUser

                                        // armazena nome e email
                                        val userData = hashMapOf(
                                            "nome" to nome,
                                            "email" to email
                                        )

                                        db.collection(COLLECTION_USUARIOS)
                                            .document(user?.uid ?: "")
                                            .set(userData)
                                            .addOnSuccessListener {
                                                Log.d("CadastroFirebase", "Dados do usuário armazenados no Firestore")
                                                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

                                                val intent = Intent(this, LoginActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("CadastroFirebase", "Erro ao salvar dados no Firestore: ${e.message}", e)
                                                Toast.makeText(this, "Erro ao cadastrar no Firestore", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Log.e("CadastroFirebase", "Erro ao criar usuário: ${task.exception?.message}")
                                        Toast.makeText(this, "Erro ao cadastrar usuário. Tente novamente.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Log.e("CadastroFirebase", "Erro ao verificar email: ${task.exception?.message}")
                        Toast.makeText(this, "Erro ao verificar email. Tente novamente.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
