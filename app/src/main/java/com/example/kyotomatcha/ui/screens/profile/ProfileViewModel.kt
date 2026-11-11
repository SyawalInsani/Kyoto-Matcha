package com.example.kyotomatcha.ui.screens.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kyotomatcha.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _name = MutableStateFlow(sessionManager.getUserName().ifEmpty { "Salsa" })
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow(sessionManager.getUserEmail().ifEmpty { "salsa@kyotomatcha.id" })
    val email: StateFlow<String> = _email.asStateFlow()

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
        sessionManager.saveUser(newName, newEmail, sessionManager.getSavedPassword())
    }

    fun toggleReminderAbsen(value: Boolean) { _reminderAbsen.value = value }
    fun toggleStockWarning(value: Boolean) { _stockWarning.value = value }
    fun toggleStockMovement(value: Boolean) { _stockMovement.value = value }
    fun toggleSalesSummary(value: Boolean) { _salesSummary.value = value }

    fun logout() {
        sessionManager.setLoggedIn(false)
    }
}
