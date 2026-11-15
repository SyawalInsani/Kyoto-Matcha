package com.example.kyotomatcha.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

data class MeResponse(val data: UserDto?)

// Register: jawaban sukses tidak berisi token (akun menunggu persetujuan pemilik).
// Juga dipakai membaca jawaban error Laravel: {"message": "...", "errors": {...}}.
data class MessageResponse(val message: String?, val errors: Map<String, List<String>>?)

data class ProductDto(
    val id: Int,
    val name: String,
    val category: String,
    val type: String,
    val price: Int,
    val uses_cup: Boolean
)

data class ProductsResponse(val data: List<ProductDto>?)

data class CheckOutRequest(val shift: String, val latitude: Double, val longitude: Double)

data class CheckInRequest(val shift: String, val latitude: Double, val longitude: Double)

data class AttendanceDto(
    val id: Int,
    val date: String,
    val shift: String,
    val status: String,
    val late_minutes: Int,
    val distance_m: Int?,
    val check_in_at: String?,
    val check_out_at: String?
)

data class AttendanceResponse(val message: String?, val data: AttendanceDto?)

data class TodayResponse(val data: List<AttendanceDto>?)

interface ApiService {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<MessageResponse>

    @GET("me")
    suspend fun me(): Response<MeResponse>

    // Token dikirim manual karena sesi di HP sudah dihapus lebih dulu saat Keluar.
    @POST("logout")
    suspend fun logout(@Header("Authorization") bearer: String): Response<MessageResponse>

    @GET("products")
    suspend fun products(): Response<ProductsResponse>

    @GET("attendance/today")
    suspend fun attendanceToday(): Response<TodayResponse>

    @POST("attendance/check-in")
    suspend fun checkIn(@Body body: CheckInRequest): Response<AttendanceResponse>

    @POST("attendance/check-out")
    suspend fun checkOut(@Body body: CheckOutRequest): Response<AttendanceResponse>
}
