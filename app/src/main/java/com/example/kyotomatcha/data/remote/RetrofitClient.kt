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
    const val BASE_URL = "http://10.0.2.2:8000/api/"

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

    private val client = OkHttpClient.Builder()
        .addInterceptor(headers)
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
