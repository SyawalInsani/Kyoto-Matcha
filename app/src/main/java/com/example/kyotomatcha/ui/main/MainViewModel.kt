package com.example.kyotomatcha.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _restartOnboarding = MutableLiveData<Boolean>()
    val restartOnboarding: LiveData<Boolean> get() = _restartOnboarding

    fun onResetOnboardingClicked() {
        _restartOnboarding.value = true
    }
}
