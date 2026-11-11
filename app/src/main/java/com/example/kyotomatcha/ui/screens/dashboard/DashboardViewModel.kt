package com.example.kyotomatcha.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kyotomatcha.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _userName = MutableStateFlow(sessionManager.getUserName())
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _branches = listOf("Ringkasan usaha")
    val branches: List<String> = _branches

    private val _selectedBranch = MutableStateFlow("Kyoto Matcha")
    val selectedBranch: StateFlow<String> = _selectedBranch.asStateFlow()

    private val _isDropdownExpanded = MutableStateFlow(false)
    val isDropdownExpanded: StateFlow<Boolean> = _isDropdownExpanded.asStateFlow()

    fun setDropdownExpanded(expanded: Boolean) {
        _isDropdownExpanded.value = expanded
    }

    fun selectBranch(branch: String) {
        _isDropdownExpanded.value = false
    }
}
