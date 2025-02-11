package com.example.secapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.secapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicia a LoginActivity automaticamente quando o app é aberto
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finaliza a MainActivity para não voltar a ela ao pressionar o botão voltar

        val helloTextView: TextView = findViewById(R.id.helloTextView)
        helloTextView.text = "Hello Android!" // Atualizando o texto diretamente

    }


}
