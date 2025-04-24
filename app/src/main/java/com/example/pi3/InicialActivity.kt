package com.example.pi3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class InicialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicial) //


        val backButton = findViewById<ImageView>(R.id.backButton)
        val addRegistroButton = findViewById<Button>(R.id.addRegistroButton)
        val registroListView = findViewById<ListView>(R.id.registroListView)


        backButton.setOnClickListener {
            finish()
        }

        addRegistroButton.setOnClickListener {
            val intent = Intent(this, IncidentRegistrationActivity::class.java)
            startActivity(intent)
        }


    }
}
