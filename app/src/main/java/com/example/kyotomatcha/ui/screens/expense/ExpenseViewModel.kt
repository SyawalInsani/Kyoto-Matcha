package com.example.kyotomatcha.ui.screens.expense

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.remote.ExpenseBody
import com.example.kyotomatcha.data.remote.ExpenseDto
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.toApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseViewModel : ViewModel() {
    private val api get() = RetrofitClient.apiService

    private val _items = MutableStateFlow<List<ExpenseDto>>(emptyList())
    val items: StateFlow<List<ExpenseDto>> = _items.asStateFlow()

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total.asStateFlow()

    // false sampai daftar pertama kali selesai dimuat (agar tidak tampil "belum ada" saat masih memuat).
    private val _loaded = MutableStateFlow(false)
    val loaded: StateFlow<Boolean> = _loaded.asStateFlow()

    private val _loadError = MutableStateFlow("")
    val loadError: StateFlow<String> = _loadError.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _formError = MutableStateFlow("")
    val formError: StateFlow<String> = _formError.asStateFlow()

    /** Pengeluaran hari ini dari server. */
    fun refresh() {
        viewModelScope.launch {
            try {
                val res = api.expenses()
                val body = res.body()
                if (res.isSuccessful && body != null) {
                    _items.value = body.data.orEmpty()
                    _total.value = body.total ?: 0
                    _loadError.value = ""
                } else {
                    _loadError.value = res.toApiError().message
                }
            } catch (e: Exception) {
                Log.e("ExpenseViewModel", "Memuat pengeluaran gagal", e)
                _loadError.value = NETWORK_ERROR_MESSAGE
            }
            _loaded.value = true
        }
    }

    fun clearError() {
        _formError.value = ""
    }

    /** categoryLabel: "Uang makan" / "Bahan" / "Lainnya". onSaved dipanggil bila server menerima. */
    fun submit(categoryLabel: String, description: String, amountText: String, yesterday: Boolean, onSaved: () -> Unit) {
        val amount = amountText.toIntOrNull()
        when {
            description.isBlank() -> _formError.value = "Keterangan wajib diisi."
            amount == null || amount <= 0 -> _formError.value = "Nominal harus berupa angka lebih dari 0."
            _isSaving.value -> Unit
            else -> viewModelScope.launch {
                _isSaving.value = true
                _formError.value = ""
                try {
                    val category = when (categoryLabel) {
                        "Uang makan" -> "uang_makan"
                        "Bahan" -> "bahan"
                        else -> "lainnya"
                    }
                    val res = api.addExpense(ExpenseBody(category, description.trim(), amount, if (yesterday) dayString(-1) else null))
                    if (res.isSuccessful) {
                        onSaved()
                    } else {
                        _formError.value = res.toApiError().message
                    }
                } catch (e: Exception) {
                    Log.e("ExpenseViewModel", "Simpan pengeluaran gagal", e)
                    _formError.value = NETWORK_ERROR_MESSAGE
                }
                _isSaving.value = false
            }
        }
    }

    private fun dayString(offsetDays: Int): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offsetDays) }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }
}
