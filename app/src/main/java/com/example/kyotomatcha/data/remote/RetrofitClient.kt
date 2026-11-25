package com.example.kyotomatcha.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 10.0.2.2 = alamat komputer Anda jika aplikasi berjalan di emulator.
    // HP asli: ganti dengan IP komputer di jaringan Wi-Fi yang sama, mis. http://192.168.1.10:8000/api/
    // Server: php artisan serve --host=0.0.0.0 (port 8000).
    const val BASE_URL = "https://kyotomatcha.sovereigns.my.id/api/"

    // Diisi oleh OmachaApp. Mengembalikan token login, atau null bila belum login.
    @Volatile
    var tokenProvider: () -> String? = { null }

    private val headers = Interceptor { chain ->
        val builder = chain.request().newBuilder().header("Accept", "application/json")
        // Header Authorization yang sudah dipasang manual (mis. saat logout) tidak ditimpa.
        if (chain.request().header("Authorization") == null) {
            tokenProvider()?.let { builder.header("Authorization", "Bearer $it") }
        }
        chain.proceed(builder.build())
    }

    // Server membaca foto bukti bayar (OCR) dan bisa memakan puluhan detik: hanya panggilan unggah yang diberi batas lebih lama.
    private val proofTimeout = Interceptor { chain ->
        val request = chain.request()
        if (request.url().encodedPath().endsWith("/proof")) {
            chain.withReadTimeout(90, TimeUnit.SECONDS).proceed(request)
        } else {
            chain.proceed(request)
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(headers)
        .addInterceptor(proofTimeout)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
