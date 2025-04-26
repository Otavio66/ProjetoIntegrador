package com.example.pi3

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID
import java.io.File
import android.util.Log
import android.app.AlertDialog
import android.provider.MediaStore

class IncidentRegistrationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imageUri: Uri
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Registrar o contrato para a câmera e galeria
    private val cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val photoUri = result.data?.data
            if (photoUri != null) {
                imageUri = photoUri
                Log.d("IncidentRegistration", "Foto tirada com sucesso: $imageUri")
            }
        }
    }

    private val galleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            Log.d("IncidentRegistration", "Imagem da galeria escolhida: $imageUri")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incident_registration)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val etLocalizacao = findViewById<EditText>(R.id.etLocalizacao)
        val ratingRisco = findViewById<RatingBar>(R.id.ratingRisco)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerCategoria)
        val btnEscolherImagem = findViewById<Button>(R.id.btnEscolherImagem)

        val debugLogin = findViewById<TextView>(R.id.debugLogin)
        val debugMain = findViewById<TextView>(R.id.debugMain)

        ArrayAdapter.createFromResource(
            this,
            R.array.categorias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategoria.adapter = adapter
        }

        btnEscolherImagem.setOnClickListener {
            val options = arrayOf("Tirar Foto", "Escolher da Galeria")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Escolher Imagem")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> galleryResult.launch("image/*")
                }
            }
            builder.show()
        }

        debugLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        debugMain.setOnClickListener {
            startActivity(Intent(this, InicialActivity::class.java))
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getLocation(etLocalizacao)
        }

        btnEnviar.setOnClickListener {
            val nomeProblema = findViewById<EditText>(R.id.etNomeProblema).text.toString()
            val risco = ratingRisco.rating
            val localizacao = etLocalizacao.text.toString()
            val categoria = spinnerCategoria.selectedItem?.toString() ?: ""
            val descricao = findViewById<EditText>(R.id.etDescricao).text.toString()

            if (nomeProblema.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o nome do problema.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (risco == 0f) {
                Toast.makeText(this, "Por favor, avalie o risco do problema.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (localizacao.isEmpty()) {
                Toast.makeText(this, "Por favor, insira a localização do risco.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (categoria == "" || categoria == "Selecione") {
                Toast.makeText(this, "Por favor, selecione uma categoria.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (descricao.isEmpty()) {
                Toast.makeText(this, "Por favor, insira uma descrição do risco.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val riscoData = hashMapOf(
                "nomeProblema" to nomeProblema,
                "risco" to risco.toInt(),
                "localizacao" to localizacao,
                "categoria" to categoria,
                "descricao" to descricao
            )

            if (::imageUri.isInitialized) {
                val fileExtension = imageUri.lastPathSegment?.substringAfterLast('.') ?: "jpg"
                val storageRef: StorageReference = storage.reference.child("registro_riscos/${UUID.randomUUID()}.$fileExtension")

                val uploadTask = storageRef.putFile(imageUri)

                uploadTask.addOnSuccessListener {
                    Log.d("FirebaseUpload", "Upload da imagem realizado com sucesso!")
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        riscoData["fotoUrl"] = uri.toString()

                        db.collection("registro_riscos")
                            .add(riscoData)
                            .addOnSuccessListener { _ ->
                                Toast.makeText(this, "Risco registrado com sucesso!", Toast.LENGTH_LONG).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirebaseUpload", "Erro ao registrar risco: $e")
                                Toast.makeText(this, "Erro ao registrar risco: $e", Toast.LENGTH_LONG).show()
                            }
                    }
                }.addOnFailureListener { e ->
                    Log.e("FirebaseUpload", "Erro ao enviar a imagem: $e")
                    Toast.makeText(this, "Erro ao enviar a imagem: $e", Toast.LENGTH_LONG).show()
                }
            } else {
                db.collection("registro_riscos")
                    .add(riscoData)
                    .addOnSuccessListener { _ ->
                        Toast.makeText(this, "Risco registrado com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao registrar risco: $e", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }

    private fun getLocation(etLocalizacao: EditText) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(this, OnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        etLocalizacao.setText("Latitude: $latitude, Longitude: $longitude")
                    } else {
                        etLocalizacao.setText("Localização não disponível. Por favor, insira um local aproximado.")
                    }
                })
        } catch (e: SecurityException) {
            Log.e("Location", "Permissão de localização negada. Erro: $e")
            Toast.makeText(this, "Permissão de localização negada. Por favor, insira um local manualmente.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation(findViewById(R.id.etLocalizacao))
            } else {
                Log.d("Permissions", "Permissão de localização negada")
                Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val fileName = "incident_image.jpg"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val photoFile = File.createTempFile(fileName, ".jpg", storageDir)
            imageUri = Uri.fromFile(photoFile)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraResult.launch(intent)  // Usando o novo método de Activity Result API
        } else {
            Toast.makeText(this, "Câmera não disponível", Toast.LENGTH_SHORT).show()
        }
    }
}
