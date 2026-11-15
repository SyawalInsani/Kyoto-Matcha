package com.example.kyotomatcha.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.remote.SalesReportDto
import com.example.kyotomatcha.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesReportViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow("Hari ini")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val _report = MutableStateFlow<SalesReportDto?>(null)
    val report: StateFlow<SalesReportDto?> = _report.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Tab yang dipilih terakhir; jawaban yang datang terlambat untuk tab lain dibuang.
    private var requested = ""

    init {
        load()
    }

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
        load()
    }

    fun load() {
        val period = when (_selectedTab.value) {
            "7 hari" -> "7days"
            "Bulan" -> "month"
            else -> "today"
        }
        requested = period
        viewModelScope.launch {
            _isLoading.value = true
            val (data, message) = ReportRepository.salesReport(period)
            if (requested != period) return@launch
            if (data != null) {
                _report.value = data
                _error.value = ""
            } else {
                _error.value = message.orEmpty()
            }
            _isLoading.value = false
        }
    }
}
