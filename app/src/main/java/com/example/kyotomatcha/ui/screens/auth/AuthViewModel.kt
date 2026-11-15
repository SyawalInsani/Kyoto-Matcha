package com.example.kyotomatcha.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.repository.AuthRepository
import com.example.kyotomatcha.data.repository.AuthResult
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    // Status panggilan ke server
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Pesan error dari server; kosong = pakai teks bawaan banner.
    private val _serverError = MutableStateFlow("")
    val serverError: StateFlow<String> = _serverError.asStateFlow()

    // Login state
    private val _loginEmail = MutableStateFlow("")
    val loginEmail: StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _rememberMe = MutableStateFlow(true)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

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

    private val _lastRegisteredName = MutableStateFlow("Aulia Putri")
    val lastRegisteredName: StateFlow<String> = _lastRegisteredName.asStateFlow()

    private val _lastRegisteredEmail = MutableStateFlow("salsa@kyotomatcha.id")
    val lastRegisteredEmail: StateFlow<String> = _lastRegisteredEmail.asStateFlow()

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

    // Teks error per kolom (dari aturan di aplikasi atau pesan asli dari server).
    private val _regEmailErrorText = MutableStateFlow("")
    val regEmailErrorText: StateFlow<String> = _regEmailErrorText.asStateFlow()

    private val _regPhoneErrorText = MutableStateFlow("")
    val regPhoneErrorText: StateFlow<String> = _regPhoneErrorText.asStateFlow()

    private val _regPasswordErrorText = MutableStateFlow("")
    val regPasswordErrorText: StateFlow<String> = _regPasswordErrorText.asStateFlow()

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

        if (!hasErr && !checkOnly && !_isLoading.value) {
            viewModelScope.launch {
                _isLoading.value = true
                _serverError.value = ""
                when (val result = authRepository.login(email, password)) {
                    is AuthResult.Success -> onSuccess()
                    is AuthResult.Error -> {
                        _serverError.value = result.message
                        _showLoginErrorBanner.value = true
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun validateAndRegister(onSuccess: (email: String, name: String) -> Unit, checkOnly: Boolean = false) {
        val name = _fullName.value.trim()
        val email = _registerEmail.value.trim()
        // Spasi dan tanda "-" di nomor telepon dibuang (mis. "0812 8346 2705").
        val phone = _phoneNumber.value.replace(Regex("[\\s-]"), "")
        val password = _registerPassword.value.trim()
        val confirm = _confirmPassword.value.trim()

        var hasErr = false

        val nameErr = name.isBlank()
        _regNameError.value = nameErr
        if (nameErr) hasErr = true

        val emailErr = email.isBlank() || !email.contains("@")
        _regEmailError.value = emailErr
        _regEmailErrorText.value = if (emailErr) "Masukkan alamat email yang valid." else ""
        if (emailErr) hasErr = true

        // Sama dengan aturan server: angka saja, 8-15 digit, boleh diawali "+".
        val phoneErr = !Regex("^[0-9+][0-9]{7,14}$").matches(phone)
        _regPhoneError.value = phoneErr
        _regPhoneErrorText.value = when {
            !phoneErr -> ""
            phone.isBlank() -> "Nomor telepon wajib diisi."
            else -> "Nomor telepon hanya angka, 8-15 digit (boleh diawali +)."
        }
        if (phoneErr) hasErr = true

        // Sama dengan aturan server: minimal 8 karakter, berisi huruf dan angka.
        val passErr = password.length < 8 || password.none { it.isLetter() } || password.none { it.isDigit() }
        _regPasswordError.value = passErr
        _regPasswordErrorText.value = when {
            !passErr -> ""
            password.length < 8 -> "Kata sandi minimal 8 karakter."
            else -> "Kata sandi harus berisi huruf dan angka."
        }
        if (passErr) hasErr = true

        val confirmErr = (confirm != password || confirm.isBlank())
        _regConfirmError.value = confirmErr
        if (confirmErr) hasErr = true

        _showRegisterErrorBanner.value = hasErr

        if (!hasErr && !checkOnly && !_isLoading.value) {
            viewModelScope.launch {
                _isLoading.value = true
                _serverError.value = ""
                when (val result = authRepository.register(name, email, phone, password)) {
                    is AuthResult.Success -> {
                        _lastRegisteredName.value = name
                        _lastRegisteredEmail.value = email
                        onSuccess(email, name)
                    }
                    is AuthResult.Error -> {
                        _serverError.value = result.message
                        val f = result.fieldMessages
                        _regEmailError.value = "email" in f
                        _regEmailErrorText.value = f["email"].orEmpty()
                        _regPhoneError.value = "phone" in f
                        _regPhoneErrorText.value = f["phone"].orEmpty()
                        _regPasswordError.value = "password" in f
                        _regPasswordErrorText.value = f["password"].orEmpty()
                        _showRegisterErrorBanner.value = true
                    }
                }
                _isLoading.value = false
            }
        }
    }
}
