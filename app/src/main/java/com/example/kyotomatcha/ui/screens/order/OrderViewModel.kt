package com.example.kyotomatcha.ui.screens.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyotomatcha.data.local.SessionManager
import kotlinx.coroutines.launch
import com.example.kyotomatcha.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MenuItem(
    val id: String,
    val name: String,
    val price: Int,
    val stock: Int,
    val category: String, // Minuman, Dessert
    val imagePlaceholder: String = "🍵"
)

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1,
    var notes: String = ""
)

data class OrderModel(
    val id: String,
    val time: String,
    val date: String,
    val source: String, // Offline, Shopee, Gojek, Grab
    val itemsSummary: String,
    val total: Int,
    val status: String, // Menunggu bayar, Diproses, Selesai, Batal
    val cashier: String = "Muhammad Syawal Insani",
    val items: List<CartItem> = emptyList(),
    val serverId: Int = 0 // id pesanan di server (untuk bayar / ubah status)
)

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    val userName: String = sessionManager.getUserName().ifEmpty { "Salsa" }

    private val _selectedSource = MutableStateFlow(sessionManager.getSelectedSource())
    val selectedSource: StateFlow<String> = _selectedSource.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Minuman")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _orderListTab = MutableStateFlow("Semua")
    val orderListTab: StateFlow<String> = _orderListTab.asStateFlow()

    // Pilihan saat menyimpan pesanan offline: "Menunggu bayar" (lanjut ke QRIS) atau "Dibayar tunai".
    // Pesanan online tidak memakai pilihan ini: server langsung memberi status Diproses.
    private val _selectedOrderStatus = MutableStateFlow("Menunggu bayar")
    val selectedOrderStatus: StateFlow<String> = _selectedOrderStatus.asStateFlow()

    val menuItems = OrderRepository.menuItems
    val cart = OrderRepository.cart
    val orders = OrderRepository.orders

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    // Pesan error terakhir dari server saat menyimpan / mengubah pesanan; kosong = tidak ada.
    private val _orderError = MutableStateFlow("")
    val orderError: StateFlow<String> = _orderError.asStateFlow()

    init {
        viewModelScope.launch {
            OrderRepository.loadMenu()
            OrderRepository.loadOrders()
        }
    }

    fun refreshOrders() {
        viewModelScope.launch { OrderRepository.loadOrders() }
    }

    fun clearOrderError() {
        _orderError.value = ""
    }

    fun setSelectedSource(source: String) {
        sessionManager.setSelectedSource(source)
        _selectedSource.value = source
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setOrderListTab(tab: String) {
        _orderListTab.value = tab
    }

    fun addToCart(item: MenuItem) = OrderRepository.addToCart(item)

    fun updateQuantity(itemId: String, delta: Int) = OrderRepository.updateQuantity(itemId, delta)

    fun updateNotes(itemId: String, notes: String) = OrderRepository.updateNotes(itemId, notes)

    fun setSelectedOrderStatus(status: String) {
        _selectedOrderStatus.value = status
    }

    fun getTotalItems(): Int = cart.value.values.sumOf { it.quantity }

    fun getTotalPrice(): Int = cart.value.values.sumOf { it.menuItem.price * it.quantity }

    /**
     * Simpan keranjang sebagai pesanan di server. onSaved(goToQris): goToQris = true bila pesanan offline
     * menunggu pembayaran QRIS. Bila gagal, pesan ada di orderError dan keranjang tetap utuh.
     */
    fun createOrder(onSaved: (goToQris: Boolean) -> Unit) {
        if (_isSaving.value) return
        val source = sessionManager.getSelectedSource()
        val isOffline = source.equals("Offline", ignoreCase = true)
        val payCash = isOffline && _selectedOrderStatus.value == "Dibayar tunai"
        viewModelScope.launch {
            _isSaving.value = true
            _orderError.value = ""
            val error = OrderRepository.createOrder(source, payCash)
            _isSaving.value = false
            if (error == null) onSaved(isOffline && !payCash) else _orderError.value = error
        }
    }

    /**
     * Ubah status pesanan di server. newStatus: "Diproses" (konfirmasi bayar), "Selesai", "Batal".
     * onDone(null) bila berhasil, atau pesan error.
     */
    fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        reason: String? = null,
        payMethod: String = "qris",
        onDone: (String?) -> Unit = {}
    ) {
        viewModelScope.launch {
            val error = OrderRepository.updateOrderStatus(orderId, newStatus, reason, payMethod)
            _orderError.value = error.orEmpty()
            onDone(error)
        }
    }
}
