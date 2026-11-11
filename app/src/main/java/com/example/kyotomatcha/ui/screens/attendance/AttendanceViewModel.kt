package com.example.kyotomatcha.ui.screens.attendance

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AttendanceViewModel : ViewModel() {
    private val _attendanceStatus = MutableStateFlow("Belum masuk") // Belum masuk, Terlambat, Selesai
    val attendanceStatus: StateFlow<String> = _attendanceStatus.asStateFlow()

    private val _hasCheckedIn = MutableStateFlow(false)
    val hasCheckedIn: StateFlow<Boolean> = _hasCheckedIn.asStateFlow()

    private val _hasCheckedOut = MutableStateFlow(false)
    val hasCheckedOut: StateFlow<Boolean> = _hasCheckedOut.asStateFlow()

    private val _selectedTab = MutableStateFlow("Semua")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    fun checkIn() {
        _hasCheckedIn.value = true
        _attendanceStatus.value = "Terlambat" // or Tepat waktu
    }

    fun checkOut() {
        _hasCheckedOut.value = true
        _attendanceStatus.value = "Selesai"
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }
}
