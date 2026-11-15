package com.example.kyotomatcha.data.repository

import android.util.Log
import com.example.kyotomatcha.data.remote.InventorySummaryDto
import com.example.kyotomatcha.data.remote.MovementDto
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.NewItemRequest
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.StockInRequest
import com.example.kyotomatcha.data.remote.StockItemDto
import com.example.kyotomatcha.data.remote.StockOutRequest
import com.example.kyotomatcha.data.remote.toApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class StockActionResult {
    // quantityBefore dihitung dari jawaban server (stok sesudah -/+ jumlah).
    data class Success(val reference: String, val item: StockItemDto, val quantityBefore: Double) : StockActionResult()
    data class Error(val message: String) : StockActionResult()
}

// Stok dipegang di server. Objek ini hanya menyimpan salinan terakhir untuk ditampilkan.
object InventoryRepository {
    private val api get() = RetrofitClient.apiService

    private val _items = MutableStateFlow<List<StockItemDto>>(emptyList())
    val items: StateFlow<List<StockItemDto>> = _items.asStateFlow()

    private val _summary = MutableStateFlow<InventorySummaryDto?>(null)
    val summary: StateFlow<InventorySummaryDto?> = _summary.asStateFlow()

    private val _movements = MutableStateFlow<List<MovementDto>>(emptyList())
    val movements: StateFlow<List<MovementDto>> = _movements.asStateFlow()

    // Pesan bila gagal memuat; null = berhasil / belum dimuat.
    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    suspend fun refresh() {
        try {
            val res = api.inventory()
            val body = res.body()
            if (res.isSuccessful && body != null) {
                _items.value = body.data.orEmpty()
                _summary.value = body.summary
                _loadError.value = null
            } else {
                _loadError.value = res.toApiError().message
            }
        } catch (e: Exception) {
            Log.e("InventoryRepository", "Memuat stok gagal", e)
            _loadError.value = NETWORK_ERROR_MESSAGE
        }
    }

    suspend fun refreshMovements() {
        try {
            val res = api.movements()
            if (res.isSuccessful) _movements.value = res.body()?.data.orEmpty()
        } catch (e: Exception) {
            Log.e("InventoryRepository", "Memuat riwayat stok gagal", e)
        }
    }

    suspend fun stockIn(itemId: Int, qty: Double, price: Int?, supplier: String?, note: String?): StockActionResult = try {
        val res = api.stockIn(StockInRequest(itemId, qty, price, supplier?.ifBlank { null }, note?.ifBlank { null }))
        val data = res.body()?.data
        if (res.isSuccessful && data != null) {
            refresh()
            OrderRepository.invalidateMenu()
            StockActionResult.Success(data.reference_no, data.stock, data.stock.quantity - qty)
        } else {
            StockActionResult.Error(res.toApiError().message)
        }
    } catch (e: Exception) {
        Log.e("InventoryRepository", "Stok masuk gagal", e)
        StockActionResult.Error(NETWORK_ERROR_MESSAGE)
    }

    suspend fun stockOut(itemId: Int, qty: Double, purpose: String, note: String?): StockActionResult = try {
        val res = api.stockOut(StockOutRequest(itemId, qty, purpose, note?.ifBlank { null }))
        val data = res.body()?.data
        if (res.isSuccessful && data != null) {
            refresh()
            OrderRepository.invalidateMenu()
            StockActionResult.Success(data.reference_no, data.stock, data.stock.quantity + qty)
        } else {
            StockActionResult.Error(res.toApiError().message)
        }
    } catch (e: Exception) {
        Log.e("InventoryRepository", "Stok keluar gagal", e)
        StockActionResult.Error(NETWORK_ERROR_MESSAGE)
    }

    /** Khusus pemilik. Mengembalikan null bila berhasil, atau pesan error. */
    suspend fun addItem(name: String, category: String, unit: String, minStock: Double?): String? = try {
        val res = api.addInventoryItem(NewItemRequest(name, category, unit, minStock))
        if (res.isSuccessful) {
            refresh()
            null
        } else {
            res.toApiError().message
        }
    } catch (e: Exception) {
        Log.e("InventoryRepository", "Tambah item gagal", e)
        NETWORK_ERROR_MESSAGE
    }
}
