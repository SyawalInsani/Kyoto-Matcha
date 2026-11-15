package com.example.kyotomatcha.data.repository

import android.content.Context
import android.util.Log
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.data.remote.LoginRequest
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.RegisterRequest
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.toApiError
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

// Hasil panggilan ke server: berhasil, atau gagal dengan pesan siap tampil.
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String, val fieldMessages: Map<String, String> = emptyMap()) : AuthResult()
}

// Hasil pengecekan sesi saat aplikasi dibuka.
enum class SessionCheck { VALID, EXPIRED, OFFLINE }

class AuthRepository(context: Context) {
    private val sessionManager = SessionManager(context)
    private val api = RetrofitClient.apiService

    suspend fun login(email: String, pass: String): AuthResult = try {
        val res = api.login(LoginRequest(email, pass))
        val data = res.body()?.data
        if (res.isSuccessful && data != null) {
            sessionManager.saveSession(data.token, data.user.name, data.user.email, data.user.role, data.user.employee_code)
            AuthResult.Success
        } else {
            res.toApiError().let { AuthResult.Error(it.message, it.fieldMessages) }
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "Login gagal", e)
        AuthResult.Error(NETWORK_ERROR_MESSAGE)
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): AuthResult = try {
        val res = api.register(RegisterRequest(name, email, phone, pass, pass))
        if (res.isSuccessful) AuthResult.Success else res.toApiError().let { AuthResult.Error(it.message, it.fieldMessages) }
    } catch (e: Exception) {
        Log.e("AuthRepository", "Register gagal", e)
        AuthResult.Error(NETWORK_ERROR_MESSAGE)
    }

    // Dipanggil saat aplikasi dibuka: apakah token yang tersimpan masih berlaku di server?
    // Bila server tidak terjangkau, pengguna tetap dianggap login (tidak dipaksa keluar).
    suspend fun checkSession(): SessionCheck = try {
        val res = api.me()
        val user = res.body()?.data
        when {
            res.isSuccessful && user != null && user.status == "aktif" -> {
                sessionManager.saveSession(sessionManager.getToken().orEmpty(), user.name, user.email, user.role, user.employee_code)
                SessionCheck.VALID
            }
            res.code() == 401 || res.code() == 403 -> SessionCheck.EXPIRED
            else -> SessionCheck.OFFLINE
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "Cek sesi gagal", e)
        SessionCheck.OFFLINE
    }

    // Hapus sesi di HP lebih dulu (pengguna langsung keluar), lalu minta server mencabut token.
    suspend fun logout() {
        val token = sessionManager.getToken()
        sessionManager.setLoggedIn(false)
        ReportRepository.clear()
        if (token != null) {
            try {
                // NonCancellable: tetap jalan walau layar Profil sudah ditutup.
                withContext(NonCancellable) { api.logout("Bearer $token") }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Logout ke server gagal", e)
            }
        }
    }

    fun getUserName(): String = sessionManager.getUserName()
}
