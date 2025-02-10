package com.example.secapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DbHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DbHelper(this)
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            } else if (dbHelper.loginUser(email, password)) {

                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                sessionManager.saveSession(email)
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
                finish()
            } else {

                Toast.makeText(this, "Login ou senha incorretos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerButton.setOnClickListener {

            val intent = Intent(this, RegisterLogin::class.java)
            startActivity(intent)
        }
    }
}
