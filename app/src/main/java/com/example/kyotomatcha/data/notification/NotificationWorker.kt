package com.example.kyotomatcha.data.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.data.remote.RetrofitClient
import java.io.IOException

// Dijalankan WorkManager tiap 15 menit: menanyakan ke server "apa yang baru sejak terakhir", lalu menampilkan
// notifikasi. Hanya untuk pemilik dan kasir (karyawan tidak punya akses ke notifikasi ini).
class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val session = SessionManager(applicationContext)
        val token = session.getToken()
        if (!session.isLoggedIn() || token == null || session.getUserRole() == "Karyawan") return Result.success()

        // Garis awal disimpan per login: akun/token berbeda dimulai dari nol supaya riwayat lama tidak membanjiri.
        val prefs = applicationContext.getSharedPreferences("omacha_notif", Context.MODE_PRIVATE)
        val owner = token.hashCode().toString()
        val since = if (prefs.getString("owner", null) == owner) prefs.getString("last_seen", null) else null

        return try {
            val res = RetrofitClient.apiService.notifications(since = since, limit = 20)
            when {
                res.code() == 401 || res.code() == 403 -> Result.success()
                !res.isSuccessful -> Result.retry()
                else -> {
                    val body = res.body()
                    // Pertama kali (belum ada garis awal): cukup simpan garis awal, jangan tampilkan riwayat.
                    if (since != null && body != null) {
                        body.data.reversed().forEach { AppNotifier.show(applicationContext, it.id.hashCode(), it.title, it.body) }
                    }
                    body?.server_time?.let { prefs.edit().putString("owner", owner).putString("last_seen", it).apply() }
                    Result.success()
                }
            }
        } catch (e: IOException) {
            Result.retry()
        }
    }
}
