package com.example.secapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.secapp.ui.theme.SecAppTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val biometricManager = BiometricManager.from(this)

        when(biometricManager.canAuthenticate(BIOMETRIC_STRONG)){
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                showToast("No hardware boometrico")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showToast("Hardware não disponivel")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showToast("Não há biometria registrada")
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showToast("Autenticação realizada com sucesso!")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast("Erro de autenticação: $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("Autenticação falhou!")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticação biométrica")
            .setSubtitle("Use sua impressão digital para continuar")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


    private fun showToast(mensagem: String){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SecAppTheme {
        Greeting("Android")
    }
}