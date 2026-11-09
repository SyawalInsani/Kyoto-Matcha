package com.example.kyotomatcha.data.remote

object RetrofitClient {
    val apiService: ApiService by lazy {
        // Initialize Retrofit instance here
        object : ApiService {}
    }
}
