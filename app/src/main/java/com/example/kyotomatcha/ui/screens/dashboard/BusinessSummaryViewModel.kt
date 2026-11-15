package com.example.kyotomatcha.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.repository.ReportRepository
import kotlinx.coroutines.launch

class BusinessSummaryViewModel : ViewModel() {
    // Angka yang sama dengan Beranda (satu sumber: server).
    val dashboard = ReportRepository.dashboard

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { ReportRepository.refreshDashboard() }
    }
}
