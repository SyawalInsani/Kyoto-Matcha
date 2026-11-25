package com.example.kyotomatcha.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.data.remote.FeedbackRequest
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.toApiError
import com.example.kyotomatcha.data.repository.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val authRepository = AuthRepository(application)

    private val _name = MutableStateFlow(sessionManager.getUserName().ifEmpty { "Salsa" })
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow(sessionManager.getUserEmail().ifEmpty { "salsa@kyotomatcha.id" })
    val email: StateFlow<String> = _email.asStateFlow()

    private val _role = MutableStateFlow(sessionManager.getUserRole().ifEmpty { "Kasir" })
    val role: StateFlow<String> = _role.asStateFlow()

    private val _phone = MutableStateFlow("0812 8346 2705")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _reminderAbsen = MutableStateFlow(true)
    val reminderAbsen: StateFlow<Boolean> = _reminderAbsen.asStateFlow()

    private val _stockWarning = MutableStateFlow(true)
    val stockWarning: StateFlow<Boolean> = _stockWarning.asStateFlow()

    private val _stockMovement = MutableStateFlow(true)
    val stockMovement: StateFlow<Boolean> = _stockMovement.asStateFlow()

    private val _salesSummary = MutableStateFlow(false)
    val salesSummary: StateFlow<Boolean> = _salesSummary.asStateFlow()

    fun updateProfile(newName: String, newEmail: String, newPhone: String) {
        _name.value = newName
        _email.value = newEmail
        _phone.value = newPhone
        sessionManager.updateProfile(newName, newEmail)
    }

    fun toggleReminderAbsen(value: Boolean) { _reminderAbsen.value = value }
    fun toggleStockWarning(value: Boolean) { _stockWarning.value = value }
    fun toggleStockMovement(value: Boolean) { _stockMovement.value = value }
    fun toggleSalesSummary(value: Boolean) { _salesSummary.value = value }

    /** Kirim laporan masalah ke server. onDone(berhasil, pesan untuk ditampilkan). */
    fun sendReport(text: String, onDone: (Boolean, String) -> Unit) {
        val msg = text.trim()
        if (msg.length < 5) {
            onDone(false, "Tulis minimal 5 huruf.")
            return
        }
        viewModelScope.launch {
            try {
                val res = RetrofitClient.apiService.sendFeedback(FeedbackRequest(msg, "Profil", "1.0.0"))
                if (res.isSuccessful) onDone(true, res.body()?.message ?: "Terima kasih, laporan terkirim.")
                else onDone(false, res.toApiError().message)
            } catch (e: Exception) {
                onDone(false, NETWORK_ERROR_MESSAGE)
            }
        }
    }

    // Sesi di HP dihapus seketika; pencabutan token di server berjalan di latar belakang.
    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
