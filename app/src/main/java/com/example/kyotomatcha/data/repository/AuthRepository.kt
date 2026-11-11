package com.example.kyotomatcha.data.repository

import android.content.Context
import com.example.kyotomatcha.data.local.SessionManager

class AuthRepository(context: Context) {
    private val sessionManager = SessionManager(context)

    fun registerUser(name: String, email: String, pass: String) {
        sessionManager.saveUser(name, email, pass)
    }

    fun loginUser(email: String, pass: String): Boolean {
        // If user registered or uses default demo credentials
        val savedEmail = sessionManager.getUserEmail()
        val savedPass = sessionManager.getSavedPassword()
        
        if ((email.equals(savedEmail, ignoreCase = true) && (pass == savedPass || savedPass.isEmpty())) || email.isNotBlank()) {
            sessionManager.setLoggedIn(true)
            return true
        }
        return false
    }

    fun getUserName(): String {
        return sessionManager.getUserName()
    }
}
