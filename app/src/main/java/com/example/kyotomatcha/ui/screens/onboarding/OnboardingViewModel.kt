package com.example.kyotomatcha.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import com.example.kyotomatcha.data.model.OnboardingPage
import com.example.kyotomatcha.data.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel(
    private val repository: OnboardingRepository = OnboardingRepository()
) : ViewModel() {

    private val _pages = MutableStateFlow<List<OnboardingPage>>(repository.getPages())
    val pages: StateFlow<List<OnboardingPage>> = _pages.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    fun setCurrentIndex(index: Int) {
        _currentIndex.value = index
    }

    fun nextPage(onFinish: () -> Unit) {
        if (_currentIndex.value < _pages.value.size - 1) {
            _currentIndex.value++
        } else {
            onFinish()
        }
    }
}
