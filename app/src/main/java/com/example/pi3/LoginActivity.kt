package com.example.pi3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth  // FirebaseAuth para autenticação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Vincula ao XML da tela

        // Inicializando o Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Testando a integração com o Firestore
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

        // Botao Cadastro
        val tvCadastrar = findViewById<TextView>(R.id.tvCadastrar)
        tvCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        // Botao login
        val btnLogin = findViewById<Button>(R.id.btnEntrar)
        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.etIdentificacao).text.toString()
            val senha = findViewById<EditText>(R.id.etSenha).text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                // Tenta fazer login
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login realizado
                            val user = auth.currentUser
                            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                            // Redireciona para a tela inicial se o logi estiver funcionado
                            val intent = Intent(this, InicialActivity::class.java)  // Direciona para InicialActivity
                            startActivity(intent)
                            finish()  // Fecha a tela de login
                        } else {
                            // se o login falhar
                            Log.e("FirebaseLogin", "Erro no login: ${task.exception?.message}")
                            Toast.makeText(this, "Falha no login. Verifique seu email e senha.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Debug Button
        val tvDebug = findViewById<TextView>(R.id.tvDebugRegistrar)
        tvDebug.setOnClickListener {
            val intent = Intent(this, InicialActivity::class.java) // Redireciona para a InicialActivity
            startActivity(intent)
        }
    }
}
