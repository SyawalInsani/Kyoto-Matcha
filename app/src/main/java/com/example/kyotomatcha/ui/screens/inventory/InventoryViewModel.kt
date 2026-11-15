package com.example.kyotomatcha.ui.screens.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.data.repository.InventoryRepository
import com.example.kyotomatcha.data.repository.StockActionResult
import com.example.kyotomatcha.util.QuantityUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val session = SessionManager(application)

    // Hanya pemilik yang boleh menambah item stok (aturan yang sama dijaga server).
    val isOwner: Boolean = session.getUserRole().equals("Pemilik", ignoreCase = true)

    val items = InventoryRepository.items
    val summary = InventoryRepository.summary
    val movements = InventoryRepository.movements
    val loadError = InventoryRepository.loadError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Terpisah dari isLoading agar pemuatan stok dan riwayat yang bersamaan tidak saling menimpa.
    private val _movementsLoading = MutableStateFlow(true)
    val movementsLoading: StateFlow<Boolean> = _movementsLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // Pesan kesalahan form (validasi di HP atau penolakan server); kosong = tidak ada.
    private val _formError = MutableStateFlow("")
    val formError: StateFlow<String> = _formError.asStateFlow()

    // Hasil simpan terakhir; layar menampilkan ringkasan bila tidak null.
    private val _result = MutableStateFlow<StockActionResult.Success?>(null)
    val result: StateFlow<StockActionResult.Success?> = _result.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            InventoryRepository.refresh()
            _isLoading.value = false
        }
    }

    fun loadMovements() {
        viewModelScope.launch {
            _movementsLoading.value = true
            InventoryRepository.refreshMovements()
            _movementsLoading.value = false
        }
    }

    fun clearForm() {
        _formError.value = ""
        _result.value = null
    }

    fun submitStockIn(itemId: Int?, qtyText: String, priceText: String, supplier: String, note: String) {
        val qty = QuantityUtil.parse(qtyText)
        val price = priceText.filter { it.isDigit() }.toIntOrNull()
        when {
            itemId == null -> _formError.value = "Pilih item terlebih dulu."
            qty == null || qty <= 0 -> _formError.value = "Jumlah harus berupa angka lebih dari 0."
            else -> save { InventoryRepository.stockIn(itemId, qty, price, supplier, note) }
        }
    }

    fun submitStockOut(itemId: Int?, qtyText: String, purpose: String, note: String) {
        val qty = QuantityUtil.parse(qtyText)
        when {
            itemId == null -> _formError.value = "Pilih item terlebih dulu."
            qty == null || qty <= 0 -> _formError.value = "Jumlah harus berupa angka lebih dari 0."
            else -> save { InventoryRepository.stockOut(itemId, qty, purpose, note) }
        }
    }

    /** Menambah item baru (pemilik). onDone(null) bila berhasil, atau pesan error. */
    fun addItem(name: String, category: String, unit: String, minText: String, onDone: (String?) -> Unit) {
        val min = if (minText.isBlank()) null else QuantityUtil.parse(minText)
        when {
            name.isBlank() -> onDone("Nama item wajib diisi.")
            minText.isNotBlank() && (min == null || min < 0) -> onDone("Batas stok harus berupa angka.")
            else -> viewModelScope.launch {
                _isSaving.value = true
                onDone(InventoryRepository.addItem(name.trim(), category, unit, min))
                _isSaving.value = false
            }
        }
    }

    private fun save(call: suspend () -> StockActionResult) {
        if (_isSaving.value) return
        viewModelScope.launch {
            _isSaving.value = true
            _formError.value = ""
            when (val r = call()) {
                is StockActionResult.Success -> _result.value = r
                is StockActionResult.Error -> _formError.value = r.message
            }
            _isSaving.value = false
        }
    }
}
