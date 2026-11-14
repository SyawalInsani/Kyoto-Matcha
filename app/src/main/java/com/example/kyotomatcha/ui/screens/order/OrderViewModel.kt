package com.example.kyotomatcha.ui.screens.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kyotomatcha.data.local.SessionManager
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
    val items: List<CartItem> = emptyList()
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

    private val _selectedOrderStatus = MutableStateFlow("Diproses")
    val selectedOrderStatus: StateFlow<String> = _selectedOrderStatus.asStateFlow()

    val menuItems = OrderRepository.menuItems
    val cart = OrderRepository.cart
    val orders = OrderRepository.orders

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

    fun createOrder(status: String = _selectedOrderStatus.value) {
        OrderRepository.createOrder(sessionManager.getSelectedSource(), userName, status)
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        OrderRepository.updateOrderStatus(orderId, newStatus)
    }
}
