package com.example.pi3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class IncidentRegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_registration)

        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val ratingRisco = findViewById<RatingBar>(R.id.ratingRisco)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoria)

        val tvVoltar = findViewById<TextView>(R.id.tvVoltar)
        tvVoltar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.categorias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategoria.adapter = adapter
        }

        btnEnviar.setOnClickListener {
            val nomeProblema = findViewById<EditText>(R.id.etNomeProblema).text.toString()
            val risco = ratingRisco.rating
            val localizacao = findViewById<EditText>(R.id.etLocalizacao).text.toString()
            val categoria = spinnerCategoria.selectedItem?.toString() ?: ""
            val descricao = findViewById<EditText>(R.id.etDescricao).text.toString()

            if (spinnerCategoria.selectedItemPosition == 0) {
                Toast.makeText(this, "Selecione a categoria!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nomeProblema.isEmpty() || risco == 0f || localizacao.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val incidente = """
                Nome: $nomeProblema
                Risco: ${risco.toInt()}/5
                Local: $localizacao
                Categoria: $categoria
                Descrição: $descricao
            """.trimIndent()

            Toast.makeText(this, "Incidente Registrado!\n$incidente", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}