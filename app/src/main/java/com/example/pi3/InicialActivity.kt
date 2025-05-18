package com.example.pi3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class InicialActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var registroListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_inicial)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        registroListView = findViewById(R.id.registroListView)
        val addRegistroButton = findViewById<Button>(R.id.addRegistroButton)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        swipeRefresh.setOnRefreshListener {
            carregarRegistros()
        }

        addRegistroButton.setOnClickListener {
            val intent = Intent(this, IncidentRegistrationActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        carregarRegistros()
    }

    private fun carregarRegistros() {
        swipeRefresh.isRefreshing = true

        db.collection("registro_riscos")
            .get()
            .addOnSuccessListener { result ->
                val registros = mutableListOf<Registro>()
                for (document in result) {
                    val nomeProblema = document.getString("nomeProblema") ?: "Sem nome"
                    val descricao = document.getString("descricao") ?: "Sem descrição"
                    val fotoUrl = document.getString("fotoUrl")
                    val localizacao = document.getString("localizacao")
                    val categoria = document.getString("categoria")
                    val status = document.getString("status")
                    val classificacao = document.getDouble("risco")?.toFloat() ?: 0f

                    registros.add(Registro(nomeProblema, descricao, fotoUrl, localizacao, categoria, status, classificacao))
                }

                val adapter = RegistroAdapter(this, registros)
                registroListView.adapter = adapter

                registroListView.setOnItemClickListener { _, _, position, _ ->
                    val registro = registros[position]
                    val intent = Intent(this, DetalheRegistroActivity::class.java).apply {
                        putExtra("nomeProblema", registro.nomeProblema)
                        putExtra("descricao", registro.descricao)
                        putExtra("fotoUrl", registro.fotoUrl)
                        putExtra("localizacao", registro.localizacao)
                        putExtra("categoria", registro.categoria)
                        putExtra("status", registro.status)
                        putExtra("classificacao", registro.classificacao)
                    }
                    startActivity(intent)
                }

                swipeRefresh.isRefreshing = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar os registros: $e", Toast.LENGTH_SHORT).show()
                swipeRefresh.isRefreshing = false
            }
    }
}
