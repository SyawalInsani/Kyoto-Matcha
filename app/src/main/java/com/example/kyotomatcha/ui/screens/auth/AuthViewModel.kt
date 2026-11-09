package com.example.kyotomatcha.ui.screens.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {

    // Login state
    private val _loginEmail = MutableStateFlow("")
    val loginEmail: StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _rememberMe = MutableStateFlow(true)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    // Login validation errors
    private val _showLoginErrorBanner = MutableStateFlow(false)
    val showLoginErrorBanner: StateFlow<Boolean> = _showLoginErrorBanner.asStateFlow()

    private val _emailError = MutableStateFlow(false)
    val emailError: StateFlow<Boolean> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError.asStateFlow()

    private val _emailErrorText = MutableStateFlow("")
    val emailErrorText: StateFlow<String> = _emailErrorText.asStateFlow()

    private val _passwordErrorText = MutableStateFlow("")
    val passwordErrorText: StateFlow<String> = _passwordErrorText.asStateFlow()

    // Register state
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _registerEmail = MutableStateFlow("")
    val registerEmail: StateFlow<String> = _registerEmail.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _agreeTerms = MutableStateFlow(true)
    val agreeTerms: StateFlow<Boolean> = _agreeTerms.asStateFlow()

    // Register validation errors
    private val _showRegisterErrorBanner = MutableStateFlow(false)
    val showRegisterErrorBanner: StateFlow<Boolean> = _showRegisterErrorBanner.asStateFlow()

    private val _regNameError = MutableStateFlow(false)
    val regNameError: StateFlow<Boolean> = _regNameError.asStateFlow()

    private val _regEmailError = MutableStateFlow(false)
    val regEmailError: StateFlow<Boolean> = _regEmailError.asStateFlow()

    private val _regPhoneError = MutableStateFlow(false)
    val regPhoneError: StateFlow<Boolean> = _regPhoneError.asStateFlow()

    private val _regPasswordError = MutableStateFlow(false)
    val regPasswordError: StateFlow<Boolean> = _regPasswordError.asStateFlow()

    private val _regConfirmError = MutableStateFlow(false)
    val regConfirmError: StateFlow<Boolean> = _regConfirmError.asStateFlow()

    fun setLoginEmail(email: String) {
        _loginEmail.value = email
        if (_emailError.value) validateAndLogin(onSuccess = {}, checkOnly = true)
    }

    fun setLoginPassword(password: String) {
        _loginPassword.value = password
        if (_passwordError.value) validateAndLogin(onSuccess = {}, checkOnly = true)
    }

    fun setRememberMe(remember: Boolean) { _rememberMe.value = remember }

    fun setFullName(name: String) {
        _fullName.value = name
        if (_regNameError.value) validateAndRegister(onSuccess = { _, _ -> }, checkOnly = true)
    }
    fun setRegisterEmail(email: String) {
        _registerEmail.value = email
        if (_regEmailError.value) validateAndRegister(onSuccess = { _, _ -> }, checkOnly = true)
    }
    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
        if (_regPhoneError.value) validateAndRegister(onSuccess = { _, _ -> }, checkOnly = true)
    }
    fun setRegisterPassword(password: String) {
        _registerPassword.value = password
        if (_regPasswordError.value) validateAndRegister(onSuccess = { _, _ -> }, checkOnly = true)
    }
    fun setConfirmPassword(confirm: String) {
        _confirmPassword.value = confirm
        if (_regConfirmError.value) validateAndRegister(onSuccess = { _, _ -> }, checkOnly = true)
    }
    fun setAgreeTerms(agree: Boolean) { _agreeTerms.value = agree }

    fun validateAndLogin(onSuccess: () -> Unit, checkOnly: Boolean = false) {
        val email = _loginEmail.value.trim()
        val password = _loginPassword.value.trim()

        var hasErr = false

        if (email.isBlank()) {
            _emailError.value = true
            _emailErrorText.value = "Alamat email wajib diisi."
            hasErr = true
        } else if (!email.contains("@") || email.endsWith("@") || !email.contains(".")) {
            _emailError.value = true
            _emailErrorText.value = "Masukkan alamat email yang valid."
            hasErr = true
        } else {
            _emailError.value = false
            _emailErrorText.value = ""
        }

        if (password.isBlank()) {
            _passwordError.value = true
            _passwordErrorText.value = "Kata sandi wajib diisi."
            hasErr = true
        } else {
            _passwordError.value = false
            _passwordErrorText.value = ""
        }

        _showLoginErrorBanner.value = hasErr

        if (!hasErr && !checkOnly) {
            onSuccess()
        }
    }

    fun validateAndRegister(onSuccess: (email: String, name: String) -> Unit, checkOnly: Boolean = false) {
        val name = _fullName.value.trim()
        val email = _registerEmail.value.trim()
        val phone = _phoneNumber.value.trim()
        val password = _registerPassword.value.trim()
        val confirm = _confirmPassword.value.trim()

        var hasErr = false

        val nameErr = name.isBlank()
        _regNameError.value = nameErr
        if (nameErr) hasErr = true

        val emailErr = email.isBlank() || !email.contains("@")
        _regEmailError.value = emailErr
        if (emailErr) hasErr = true

        val phoneErr = phone.isBlank()
        _regPhoneError.value = phoneErr
        if (phoneErr) hasErr = true

        val passErr = password.length < 8
        _regPasswordError.value = passErr
        if (passErr) hasErr = true

        val confirmErr = (confirm != password || confirm.isBlank())
        _regConfirmError.value = confirmErr
        if (confirmErr) hasErr = true

        _showRegisterErrorBanner.value = hasErr

        if (!hasErr && !checkOnly) {
            onSuccess(email, name)
        }
    }
}
