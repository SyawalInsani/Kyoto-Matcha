package com.example.kyotomatcha.data.repository

import android.content.Context
import android.util.Log
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.data.remote.LoginRequest
import com.example.kyotomatcha.data.remote.MessageResponse
import com.example.kyotomatcha.data.remote.RegisterRequest
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.google.gson.Gson
import retrofit2.Response

// Hasil panggilan ke server: berhasil, atau gagal dengan pesan siap tampil.
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String, val fields: Set<String> = emptySet()) : AuthResult()
}

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
            errorFrom(res)
        }
    } catch (e: Exception) {
        Log.e("AuthRepository", "Login gagal", e)
        networkError()
    }

    suspend fun register(name: String, email: String, phone: String, pass: String): AuthResult = try {
        val res = api.register(RegisterRequest(name, email, phone, pass, pass))
        if (res.isSuccessful) AuthResult.Success else errorFrom(res)
    } catch (e: Exception) {
        Log.e("AuthRepository", "Register gagal", e)
        networkError()
    }

    fun getUserName(): String = sessionManager.getUserName()

    private fun networkError() =
        AuthResult.Error("Tidak bisa terhubung ke server. Periksa koneksi atau alamat server.")

    // Ambil pesan dari JSON error Laravel: {"message": "...", "errors": {"email": ["..."]}}
    private fun errorFrom(res: Response<*>): AuthResult.Error {
        val body = try {
            Gson().fromJson(res.errorBody()?.string(), MessageResponse::class.java)
        } catch (e: Exception) {
            null
        }
        val firstFieldError = body?.errors?.values?.firstOrNull()?.firstOrNull()
        val message = firstFieldError ?: body?.message ?: "Terjadi kesalahan (kode ${res.code()})."
        return AuthResult.Error(message, body?.errors?.keys ?: emptySet())
    }
}
