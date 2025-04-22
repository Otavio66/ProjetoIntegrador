package com.example.pi3

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class IncidentRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_registration)

        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val spinnerRisco = findViewById<Spinner>(R.id.spinnerRisco)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoria)

        btnEnviar.setOnClickListener {
            val nomeProblema = findViewById<EditText>(R.id.etNomeProblema).text.toString()
            val risco = spinnerRisco.selectedItem?.toString() ?: ""
            val localizacao = findViewById<EditText>(R.id.etLocalizacao).text.toString()
            val categoria = spinnerCategoria.selectedItem?.toString() ?: ""
            val descricao = findViewById<EditText>(R.id.etDescricao).text.toString()

            if (spinnerRisco.selectedItemPosition == 0) {
                Toast.makeText(this, "Selecione o nível de risco!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (spinnerCategoria.selectedItemPosition == 0) {
                Toast.makeText(this, "Selecione a categoria!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nomeProblema.isEmpty() || risco.isEmpty() || localizacao.isEmpty() || categoria.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulação de envio dos dados
            val incidente = """
                Nome: $nomeProblema
                Risco: $risco
                Local: $localizacao
                Categoria: $categoria
                Descrição: $descricao
            """.trimIndent()

            Toast.makeText(this, "Incidente Registrado!\n$incidente", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}