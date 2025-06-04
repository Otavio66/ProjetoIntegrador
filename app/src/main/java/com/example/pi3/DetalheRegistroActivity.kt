package com.example.pi3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetalheRegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_registro)

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            startActivity(Intent(this, InicialActivity::class.java))
            finish()
        }

        val nomeProblemaTextView = findViewById<TextView>(R.id.incident_title)
        val fotoImageView = findViewById<ImageView>(R.id.incident_image)
        val localizacaoTextView = findViewById<TextView>(R.id.incident_location_info)
        val categoriaTextView = findViewById<TextView>(R.id.incident_category_info)
        val descricaoTextView = findViewById<TextView>(R.id.incident_description_info)
        val classificacaoRatingBar = findViewById<RatingBar>(R.id.ratingRisco)

        // Pegando os dados do Intent
        val nomeProblema = intent.getStringExtra("nomeProblema")
        val descricao = intent.getStringExtra("descricao")
        val fotoUrl = intent.getStringExtra("fotoUrl")
        val localizacao = intent.getStringExtra("localizacao")
        val categoria = intent.getStringExtra("categoria")
        val classificacao = intent.getFloatExtra("classificacao", 0f)

        nomeProblemaTextView.text = nomeProblema
        localizacaoTextView.text = localizacao
        categoriaTextView.text = categoria
        descricaoTextView.text = descricao
        classificacaoRatingBar.rating = classificacao


        if (!fotoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(fotoUrl)  // URL da imagem do Cloudinary
                .into(fotoImageView)  // Coloca a imagem no ImageView
        }
    }
}
