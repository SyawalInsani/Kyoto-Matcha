package com.example.kyotomatcha.data.repository

import android.util.Log
import com.example.kyotomatcha.data.remote.ApproveUserBody
import com.example.kyotomatcha.data.remote.LeaveBody
import com.example.kyotomatcha.data.remote.LeaveDto
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.RejectBody
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.StaffDto
import com.example.kyotomatcha.data.remote.UserDto
import com.example.kyotomatcha.data.remote.toApiError
import retrofit2.Response

// Semua fungsi mengembalikan null / daftar kosong-nullable bila jaringan gagal; fungsi tindakan
// mengembalikan null bila berhasil atau pesan error siap tampil.
object LeaveRepository {
    private val api get() = RetrofitClient.apiService

    suspend fun staff(): List<StaffDto> = try {
        api.staff().body()?.data.orEmpty()
    } catch (e: Exception) {
        Log.e("LeaveRepository", "Memuat rekan gagal", e)
        emptyList()
    }

    suspend fun myLeaves(): List<LeaveDto>? = load { api.myLeaves() }?.data

    suspend fun pendingLeaves(): List<LeaveDto>? = load { api.pendingLeaves() }?.data

    suspend fun pendingUsers(): List<UserDto>? = load { api.pendingUsers() }?.data

    suspend fun submit(body: LeaveBody): String? = act { api.submitLeave(body) }

    suspend fun decideLeave(id: Int, approve: Boolean, reason: String?): String? =
        if (approve) act { api.approveLeave(id) } else act { api.rejectLeave(id, RejectBody(reason?.ifBlank { null })) }

    suspend fun decideUser(id: Int, approve: Boolean, role: String): String? =
        if (approve) act { api.approveUser(id, ApproveUserBody(role)) } else act { api.rejectUser(id) }

    private suspend fun <T> load(call: suspend () -> Response<T>): T? = try {
        val res = call()
        if (res.isSuccessful) res.body() else null
    } catch (e: Exception) {
        Log.e("LeaveRepository", "Memuat data gagal", e)
        null
    }

    private suspend fun act(call: suspend () -> Response<*>): String? = try {
        val res = call()
        if (res.isSuccessful) null else res.toApiError().message
    } catch (e: Exception) {
        Log.e("LeaveRepository", "Tindakan gagal", e)
        NETWORK_ERROR_MESSAGE
    }
}
