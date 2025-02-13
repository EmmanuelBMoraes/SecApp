package com.example.secapp
import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
class BiometricHelper(private val context: Context, private val callback: BiometricCallback) {

    interface BiometricCallback {
        fun onSuccess()
        fun onError(errorMessage: String)
        fun onFailure()
    }
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    fun showBiometricPrompt(activity: androidx.fragment.app.FragmentActivity) {//recebe um FragmentActivity
        //como parâmetro pq a BiometricPrompt precisa saber onde a autenticação será exibida
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {//define as ações na autenticação.
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback.onSuccess()
                }
                //serão executadas dependendo do resultado
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback.onError(errString.toString())
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback.onFailure()
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()//define os detalhes do pop-up.
            .setTitle("Autenticação biométrica")
            .setSubtitle("Use sua impressão digital para continuar")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
        biometricPrompt.authenticate(promptInfo)//exibe a tela de autenticação para o usuário.
    }
}