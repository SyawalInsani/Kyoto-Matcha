package com.example.kyotomatcha.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.NotificationDto
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.toApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Daftar notifikasi dari server (pembayaran QRIS masuk, stok hampir habis/habis), terbaru di atas.
class NotificationViewModel : ViewModel() {
    // null = sedang memuat
    private val _items = MutableStateFlow<List<NotificationDto>?>(null)
    val items: StateFlow<List<NotificationDto>?> = _items.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _error.value = ""
            _items.value = null
            try {
                val res = RetrofitClient.apiService.notifications(limit = 50)
                val rows = res.body()?.data
                if (res.isSuccessful && rows != null) {
                    _items.value = rows
                } else {
                    _items.value = emptyList()
                    _error.value = res.toApiError().message
                }
            } catch (e: Exception) {
                _items.value = emptyList()
                _error.value = NETWORK_ERROR_MESSAGE
            }
        }
    }
}
