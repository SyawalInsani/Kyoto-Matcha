package com.example.kyotomatcha.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("omacha_prefs", Context.MODE_PRIVATE)

    // Dipanggil setelah login ke server berhasil. Kata sandi sengaja tidak disimpan.
    // Peran dari server ("kasir") disimpan dengan huruf awal besar ("Kasir") karena dipakai untuk tampilan.
    fun saveSession(token: String, name: String, email: String, role: String, employeeCode: String?) {
        prefs.edit().apply {
            putString("KEY_TOKEN", token)
            putString("KEY_NAME", name)
            putString("KEY_EMAIL", email)
            putString("KEY_ROLE", role.replaceFirstChar { it.uppercase() })
            putString("KEY_EMPLOYEE_CODE", employeeCode)
            putBoolean("KEY_IS_LOGGED_IN", true)
            apply()
        }
    }

    // Ubah profil: baru tersimpan di HP, belum dikirim ke server.
    fun updateProfile(name: String, email: String) {
        prefs.edit().putString("KEY_NAME", name).putString("KEY_EMAIL", email).apply()
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().apply {
            putBoolean("KEY_IS_LOGGED_IN", loggedIn)
            if (!loggedIn) remove("KEY_TOKEN")
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("KEY_IS_LOGGED_IN", false)
    }

    fun getToken(): String? = prefs.getString("KEY_TOKEN", null)

    // Kode karyawan dari server (mis. KM-003). Pemilik tidak punya kode, jadi tampil "-".
    fun getEmployeeCode(): String = prefs.getString("KEY_EMPLOYEE_CODE", null)?.ifBlank { null } ?: "-"

    fun getUserName(): String {
        return prefs.getString("KEY_NAME", "Salsa") ?: "Salsa"
    }

    fun getUserEmail(): String {
        return prefs.getString("KEY_EMAIL", "salsa@kyotomatcha.id") ?: "salsa@kyotomatcha.id"
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

    fun getProfileImageUri(): String? {
        return prefs.getString("KEY_PROFILE_IMAGE_URI", null)
    }

    fun setProfileImageUri(uri: String?) {
        if (uri == null) {
            prefs.edit().remove("KEY_PROFILE_IMAGE_URI").apply()
        } else {
            prefs.edit().putString("KEY_PROFILE_IMAGE_URI", uri).apply()
        }
    }
}
