package com.example.kyotomatcha

import android.app.Application
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.data.remote.RetrofitClient

class OmachaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Setiap permintaan ke server otomatis membawa token login yang tersimpan di HP.
        val session = SessionManager(this)
        RetrofitClient.tokenProvider = { session.getToken() }
    }
}
