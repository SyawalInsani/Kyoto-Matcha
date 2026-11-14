package com.example.kyotomatcha.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("omacha_prefs", Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String, pass: String, role: String = "Kasir") {
        prefs.edit().apply {
            putString("KEY_NAME", name)
            putString("KEY_EMAIL", email)
            putString("KEY_PASSWORD", pass)
            putString("KEY_ROLE", role)
            putBoolean("KEY_IS_LOGGED_IN", true)
            apply()
        }
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("KEY_IS_LOGGED_IN", loggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("KEY_IS_LOGGED_IN", false)
    }

    fun getUserName(): String {
        return prefs.getString("KEY_NAME", "Salsa") ?: "Salsa"
    }

    fun getUserEmail(): String {
        return prefs.getString("KEY_EMAIL", "salsa@kyotomatcha.id") ?: "salsa@kyotomatcha.id"
    }

    fun getSavedPassword(): String {
        return prefs.getString("KEY_PASSWORD", "") ?: ""
    }

    fun getUserRole(): String {
        return prefs.getString("KEY_ROLE", "Kasir") ?: "Kasir"
    }

    fun setUserRole(role: String) {
        prefs.edit().putString("KEY_ROLE", role).apply()
    }

    fun isOnboardingShown(): Boolean {
        return prefs.getBoolean("KEY_ONBOARDING_SHOWN", false)
    }

    fun setOnboardingShown(shown: Boolean) {
        prefs.edit().putBoolean("KEY_ONBOARDING_SHOWN", shown).apply()
    }

    fun getSelectedSource(): String {
        return prefs.getString("KEY_SELECTED_SOURCE", "Offline") ?: "Offline"
    }

    fun setSelectedSource(source: String) {
        prefs.edit().putString("KEY_SELECTED_SOURCE", source).apply()
    }
}
