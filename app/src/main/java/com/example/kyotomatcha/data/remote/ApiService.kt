package com.example.kyotomatcha.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val password_confirmation: String
)

// Nama field mengikuti JSON dari backend Laravel.
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val employee_code: String?
)

data class LoginData(val token: String, val user: UserDto)

data class LoginResponse(val message: String?, val data: LoginData?)

// Register: jawaban sukses tidak berisi token (akun menunggu persetujuan pemilik).
data class MessageResponse(val message: String?, val errors: Map<String, List<String>>?)

interface ApiService {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<MessageResponse>
}
