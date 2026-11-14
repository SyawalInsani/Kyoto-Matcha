package com.example.kyotomatcha.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 = alamat komputer Anda jika aplikasi berjalan di emulator.
    // HP asli: ganti dengan IP komputer di jaringan Wi-Fi yang sama, mis. http://192.168.1.10:8000/api/
    // Server: php artisan serve --host=0.0.0.0 (port 8000).
    const val BASE_URL = "http://10.0.2.2:8000/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
