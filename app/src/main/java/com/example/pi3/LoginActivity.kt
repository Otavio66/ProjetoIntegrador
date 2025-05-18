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

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            startActivity(Intent(this, InicialActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_login)

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

        val tvCadastrar = findViewById<TextView>(R.id.tvCadastrar)
        tvCadastrar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
            finish() // opcional — remove a LoginActivity da pilha
        }

        val btnLogin = findViewById<Button>(R.id.btnEntrar)
        btnLogin.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val senha = findViewById<EditText>(R.id.etSenha).text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, InicialActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("FirebaseLogin", "Erro no login: ${task.exception?.message}")
                            Toast.makeText(this, "Falha no login. Verifique seu email e senha.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        val tvEsqueciSenha = findViewById<TextView>(R.id.tvEsqueciSenha)
        tvEsqueciSenha.setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu e-mail para redefinir a senha.", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email de redefinição enviado!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao enviar email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
