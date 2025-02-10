package com.example.secapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.secapp.databinding.ActivityProfileBinding
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var sessionManager: SessionManager
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 101
    private val PERMISSION_REQUEST_CODE = 102

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileImageView = findViewById(R.id.profileImageView)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        sessionManager = SessionManager(this)

        binding.btnChooseImage.setOnClickListener {
            showImageSourceDialog()
        }

        val email = intent.getStringExtra("email")
        if (!email.isNullOrEmpty()) {
            editor.putString("username", email)
            editor.apply()
        }

        binding.usernameText.text = String.format("Olá, %s", email)

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            binding.profileImageView.setImageDrawable(null)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }
    private fun showImageSourceDialog() {
        val options = arrayOf("Tirar foto", "Escolher da galeria")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha uma opção")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> checkCameraPermission()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Permissão negada para usar a câmera", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    profileImageView.setImageBitmap(imageBitmap)
                    if (imageBitmap != null) {
                        saveProfileImage(imageBitmap)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val imageUri: Uri = data?.data ?: return
                    profileImageView.setImageURI(imageUri)
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    if (bitmap != null) {
                        saveProfileImage(bitmap)
                    }
                }

            }
        }
    }
    private fun saveProfileImage(bitmap: Bitmap) {
        val email = sessionManager.getUserEmail()
        val file = File(filesDir, "profile_image_${email}.jpg")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        fileOutputStream.close()

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("profile_image_path_${email}", file.absolutePath)
        editor.apply()
    }


    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "Usuário desconhecido")
        binding.usernameText.text = String.format("Olá, %s", username)

        val email = sessionManager.getUserEmail()
        val profileImagePath = sharedPreferences.getString("profile_image_path_${email}", null)

        if (profileImagePath != null) {
            val file = File(profileImagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(profileImagePath)
                profileImageView.setImageBitmap(bitmap)
            }
        }
    }

}