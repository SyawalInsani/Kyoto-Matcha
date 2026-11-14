package com.example.kyotomatcha.ui.screens.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.ui.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _navigateToNext = MutableSharedFlow<String>()
    val navigateToNext: SharedFlow<String> = _navigateToNext.asSharedFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            delay(2500)
            val destination = when {
                sessionManager.isLoggedIn() -> Routes.Dashboard.route
                !sessionManager.isOnboardingShown() -> Routes.Onboarding.route
                else -> Routes.Welcome.route
            }
            _navigateToNext.emit(destination)
        }
    }
}
