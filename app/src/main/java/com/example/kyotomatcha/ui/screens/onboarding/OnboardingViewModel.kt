package com.example.kyotomatcha.ui.screens.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.data.model.OnboardingPage
import com.example.kyotomatcha.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val repository = OnboardingRepository()

    private val _pages = MutableStateFlow<List<OnboardingPage>>(repository.getPages())
    val pages: StateFlow<List<OnboardingPage>> = _pages.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun completeOnboarding() {
        sessionManager.setOnboardingShown(true)
    }

    fun nextPage(onFinish: () -> Unit) {
        if (_currentIndex.value < _pages.value.size - 1) {
            _currentIndex.value++
        } else {
            completeOnboarding()
            onFinish()
        }
    }
}
