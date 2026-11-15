package com.example.kyotomatcha.data.repository

import android.util.Log
import com.example.kyotomatcha.data.remote.DashboardDto
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.SalesReportDto
import com.example.kyotomatcha.data.remote.toApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Angka Beranda, Ringkasan Usaha, dan Laporan. Semua dihitung server dari pesanan, absensi, dan stok asli.
object ReportRepository {
    private val api get() = RetrofitClient.apiService

    private val _dashboard = MutableStateFlow<DashboardDto?>(null)
    val dashboard: StateFlow<DashboardDto?> = _dashboard.asStateFlow()

    /** Muat ulang angka hari ini. Mengembalikan null bila berhasil, atau pesan error. */
    suspend fun refreshDashboard(): String? = try {
        val res = api.dashboard()
        val data = res.body()?.data
        if (res.isSuccessful && data != null) {
            _dashboard.value = data
            null
        } else {
            res.toApiError().message
        }
    } catch (e: Exception) {
        Log.e("ReportRepository", "Memuat beranda gagal", e)
        NETWORK_ERROR_MESSAGE
    }

    /** period: today, 7days, month. Mengembalikan laporan atau pesan error. */
    suspend fun salesReport(period: String): Pair<SalesReportDto?, String?> = try {
        val res = api.salesReport(period)
        val data = res.body()?.data
        if (res.isSuccessful && data != null) data to null else null to res.toApiError().message
    } catch (e: Exception) {
        Log.e("ReportRepository", "Memuat laporan gagal", e)
        null to NETWORK_ERROR_MESSAGE
    }

    /** Dipanggil saat keluar akun agar angka akun sebelumnya tidak terlihat oleh akun berikutnya. */
    fun clear() {
        _dashboard.value = null
    }
}
