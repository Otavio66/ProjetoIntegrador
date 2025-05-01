package com.example.pi3

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.util.*
import android.content.pm.PackageManager
import android.app.AlertDialog

class IncidentRegistrationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imageUri: Uri
    private var cameraPhotoFile: File? = null
    private val db = FirebaseFirestore.getInstance()

    private val cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && cameraPhotoFile != null) {
            imageUri = FileProvider.getUriForFile(this, "com.example.pi3.fileprovider", cameraPhotoFile!!)
            Log.d("IncidentRegistration", "Foto tirada com sucesso: $imageUri")
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
        try {
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
                builder.setItems(options) { _, which ->
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

            checkLocationPermissionAndGetLocation(etLocalizacao)

            btnEnviar.setOnClickListener {
                val nomeProblema = findViewById<EditText>(R.id.etNomeProblema).text.toString()
                val risco = ratingRisco.rating
                val localizacao = etLocalizacao.text.toString()
                val categoria = spinnerCategoria.selectedItem?.toString() ?: ""
                val descricao = findViewById<EditText>(R.id.etDescricao).text.toString()

                if (nomeProblema.isEmpty() || risco == 0f || localizacao.isEmpty() || categoria == "Selecione" || descricao.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos corretamente!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val riscoData: HashMap<String, Any> = hashMapOf(
                    "nomeProblema" to nomeProblema,
                    "risco" to risco.toInt(),
                    "localizacao" to localizacao,
                    "categoria" to categoria,
                    "descricao" to descricao,
                    "status" to "Ativo" // Adicionando o campo 'status' com valor 'ativo'
                )

                if (::imageUri.isInitialized) {
                    MediaManager.get().upload(imageUri)
                        .unsigned("upload_riscos")
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String) {
                                Toast.makeText(this@IncidentRegistrationActivity, "Iniciando upload...", Toast.LENGTH_SHORT).show()
                            }

                            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                                val url = resultData["secure_url"] as String
                                riscoData["fotoUrl"] = url
                                salvarNoFirestore(riscoData)
                            }

                            override fun onError(requestId: String, error: ErrorInfo) {
                                Toast.makeText(this@IncidentRegistrationActivity, "Erro no upload: ${error.description}", Toast.LENGTH_LONG).show()
                            }

                            override fun onReschedule(requestId: String, error: ErrorInfo) {}
                        }).dispatch()
                } else {
                    salvarNoFirestore(riscoData)
                }
            }
        } catch (e: Exception) {
            Log.e("IncidentRegistration", "Erro crítico ao abrir a tela: ${e.message}", e)
            showErrorDialog("Erro crítico ao abrir a tela", e.message ?: "Erro desconhecido")
        }
    }

    private fun salvarNoFirestore(riscoData: HashMap<String, Any>) {
        db.collection("registro_riscos")
            .add(riscoData)
            .addOnSuccessListener {
                Toast.makeText(this, "Risco registrado com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao registrar risco: $e")
                Toast.makeText(this, "Erro ao registrar risco: $e", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkLocationPermissionAndGetLocation(etLocalizacao: EditText) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation(etLocalizacao)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setTitle("Permissão Necessária")
                    .setMessage("Este app precisa da permissão de localização para registrar o local do incidente.")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val etLocalizacao = findViewById<EditText>(R.id.etLocalizacao)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation(etLocalizacao)
            } else {
                Toast.makeText(this, "Permissão de localização negada. Insira manualmente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocation(etLocalizacao: EditText) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        etLocalizacao.setText("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                    } else {
                        etLocalizacao.setText("Localização não disponível. Insira manualmente.")
                    }
                }
                .addOnFailureListener {
                    etLocalizacao.setText("Erro ao obter localização. Insira manualmente.")
                    Log.e("Location", "Erro ao obter localização: $it")
                }
        } catch (e: SecurityException) {
            Log.e("Location", "Permissão não concedida: $e")
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile = File.createTempFile("incident_image", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES))
            cameraPhotoFile = photoFile
            val photoUri = FileProvider.getUriForFile(this, "com.example.pi3.fileprovider", photoFile)
            imageUri = photoUri

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            cameraResult.launch(intent)
        } else {
            Toast.makeText(this, "Câmera não disponível", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage("Motivo: $message")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // Função para atualizar documentos existentes e adicionar o campo 'status'
    private fun adicionarStatusAosDocumentosExistentes() {
        db.collection("registro_riscos")
            .get() // Obtém todos os documentos da coleção
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Verifica se o campo 'status' não existe no documento
                    if (!document.contains("status")) {
                        val documentId = document.id
                        // Atualiza o documento com o campo 'status' e valor 'ativo'
                        db.collection("registro_riscos").document(documentId)
                            .update("status", "Ativo")
                            .addOnSuccessListener {
                                Log.d("Firestore", "Campo 'status' adicionado ao documento $documentId")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Erro ao adicionar o campo 'status': $e")
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao obter documentos: $e")
            }
    }
}
