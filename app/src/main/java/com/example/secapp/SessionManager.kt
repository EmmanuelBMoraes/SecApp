package com.example.secapp

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val context: Context = context

    fun saveSession(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString("user_email", null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.contains("user_email")
    }

    fun logout() {
        val email = prefs.getString("user_email", null)

        if (!email.isNullOrEmpty()) {
            val userPrefs = context.getSharedPreferences("user_prefs_${email}", Context.MODE_PRIVATE)
            val userEditor = userPrefs.edit()
            userEditor.clear()
            userEditor.apply()
        }

        val editor = prefs.edit()
        editor.remove("user_email")
        editor.apply()
    }
}

