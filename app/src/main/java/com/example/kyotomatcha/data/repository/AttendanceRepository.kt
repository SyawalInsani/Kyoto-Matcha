package com.example.kyotomatcha.data.repository

import android.util.Log
import com.example.kyotomatcha.data.remote.AttendanceDto
import com.example.kyotomatcha.data.remote.CheckInRequest
import com.example.kyotomatcha.data.remote.CheckOutRequest
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.toApiError

sealed class AttendanceResult {
    data class Success(val message: String, val record: AttendanceDto?) : AttendanceResult()
    data class Error(val message: String) : AttendanceResult()
}

class AttendanceRepository {
    private val api = RetrofitClient.apiService

    /** Catatan absen saya hari ini, atau null jika gagal diambil. */
    suspend fun today(): List<AttendanceDto>? = try {
        val res = api.attendanceToday()
        if (res.isSuccessful) res.body()?.data.orEmpty() else null
    } catch (e: Exception) {
        Log.e("AttendanceRepository", "Ambil absen hari ini gagal", e)
        null
    }

    suspend fun checkIn(shift: String, lat: Double, lng: Double): AttendanceResult = try {
        val res = api.checkIn(CheckInRequest(shift, lat, lng))
        if (res.isSuccessful) {
            AttendanceResult.Success(res.body()?.message ?: "Absen masuk berhasil.", res.body()?.data)
        } else {
            AttendanceResult.Error(res.toApiError().message)
        }
    } catch (e: Exception) {
        Log.e("AttendanceRepository", "Absen masuk gagal", e)
        AttendanceResult.Error(NETWORK_ERROR_MESSAGE)
    }

    suspend fun checkOut(shift: String, lat: Double, lng: Double): AttendanceResult = try {
        val res = api.checkOut(CheckOutRequest(shift, lat, lng))
        if (res.isSuccessful) {
            AttendanceResult.Success(res.body()?.message ?: "Absen pulang berhasil.", res.body()?.data)
        } else {
            AttendanceResult.Error(res.toApiError().message)
        }
    } catch (e: Exception) {
        Log.e("AttendanceRepository", "Absen pulang gagal", e)
        AttendanceResult.Error(NETWORK_ERROR_MESSAGE)
    }
}
