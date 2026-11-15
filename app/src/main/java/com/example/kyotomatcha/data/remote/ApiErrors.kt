package com.example.kyotomatcha.data.remote

import com.google.gson.Gson
import retrofit2.Response

// Pesan error siap tampil, plus pesan per kolom (kunci = nama kolom, mis. "email") untuk ditandai di layar.
data class ApiError(val message: String, val fieldMessages: Map<String, String> = emptyMap())

// Ambil pesan dari JSON error Laravel: {"message": "...", "errors": {"email": ["..."]}}
fun Response<*>.toApiError(): ApiError {
    val body = try {
        Gson().fromJson(errorBody()?.string(), MessageResponse::class.java)
    } catch (e: Exception) {
        null
    }
    val fieldMessages = body?.errors.orEmpty().mapNotNull { (field, msgs) ->
        msgs.firstOrNull()?.let { field to it }
    }.toMap()
    val message = fieldMessages.values.firstOrNull() ?: body?.message ?: "Terjadi kesalahan (kode ${code()})."
    return ApiError(message, fieldMessages)
}

const val NETWORK_ERROR_MESSAGE = "Tidak bisa terhubung ke server. Periksa koneksi atau alamat server."
