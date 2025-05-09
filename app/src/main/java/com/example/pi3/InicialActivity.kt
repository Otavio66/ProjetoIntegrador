package com.example.pi3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class InicialActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicial)

        val addRegistroButton = findViewById<Button>(R.id.addRegistroButton)
        val registroListView = findViewById<ListView>(R.id.registroListView)
        val debugButton = findViewById<Button>(R.id.debugButton)

        debugButton.setOnClickListener {
            Toast.makeText(this, "Atualizando registros...", Toast.LENGTH_SHORT).show()
            carregarRegistros(registroListView)
        }

        addRegistroButton.setOnClickListener {
            val intent = Intent(this, IncidentRegistrationActivity::class.java)
            startActivity(intent)
        }

        carregarRegistros(registroListView)
    }

    private fun carregarRegistros(registroListView: ListView) {
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
                    val intent = Intent(this, DetalheRegistroActivity::class.java)

                    intent.putExtra("nomeProblema", registro.nomeProblema)
                    intent.putExtra("descricao", registro.descricao)
                    intent.putExtra("fotoUrl", registro.fotoUrl)
                    intent.putExtra("localizacao", registro.localizacao)
                    intent.putExtra("categoria", registro.categoria)
                    intent.putExtra("status", registro.status)
                    intent.putExtra("classificacao", registro.classificacao)

                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar os registros: $e", Toast.LENGTH_SHORT).show()
            }
    }
}
