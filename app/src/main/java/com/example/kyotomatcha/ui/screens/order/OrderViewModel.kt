package com.example.kyotomatcha.ui.screens.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kyotomatcha.data.local.SessionManager
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
    val cashier: String = "Muhammad Syawal Insani"
)

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    val userName: String = sessionManager.getUserName().ifEmpty { "Muhammad Syawal Insani" }

    private val _selectedSource = MutableStateFlow("Offline")
    val selectedSource: StateFlow<String> = _selectedSource.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Minuman")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _orderListTab = MutableStateFlow("Semua")
    val orderListTab: StateFlow<String> = _orderListTab.asStateFlow()

    private val _menuItems = MutableStateFlow(
        listOf(
            MenuItem("1", "Matcha Latte", 25000, 24, "Minuman", "🍵"),
            MenuItem("2", "Oat Matcha Latte", 30000, 24, "Minuman", "🥛"),
            MenuItem("3", "Strawberry Matcha", 30000, 18, "Minuman", "🍓"),
            MenuItem("4", "Coconut Matcha", 30000, 24, "Minuman", "🥥"),
            MenuItem("5", "Blueberry Matcha", 30000, 20, "Minuman", "🫐"),
            MenuItem("6", "Brownies Burnt Cheesecake", 25000, 6, "Dessert", "🍰"),
            MenuItem("7", "Matcha Burnt Cheesecake", 25000, 6, "Dessert", "🧀"),
            MenuItem("8", "Chocolate Burnt Cheesecake", 25000, 6, "Dessert", "🍫"),
            MenuItem("9", "Oreo Burnt Cheesecake", 25000, 6, "Dessert", "🍪")
        )
    )
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _cart = MutableStateFlow<Map<String, CartItem>>(
        mapOf(
            "1" to CartItem(MenuItem("1", "Matcha Latte", 25000, 24, "Minuman", "🍵"), 2, "Kurangi gula"),
            "3" to CartItem(MenuItem("3", "Strawberry Matcha", 30000, 18, "Minuman", "🍓"), 1, "Es sedikit")
        )
    )
    val cart: StateFlow<Map<String, CartItem>> = _cart.asStateFlow()

    private val _orders = MutableStateFlow(
        listOf(
            OrderModel("KM-261103-038", "09:40", "3 Nov 2026", "Offline", "2 Matcha Latte + 1 Strawberry Matcha", 80000, "Menunggu bayar", userName),
            OrderModel("KM-261103-037", "09:35", "3 Nov 2026", "Gojek", "Matcha Latte + Strawberry Matcha", 55000, "Diproses", userName),
            OrderModel("KM-261103-036", "09:28", "3 Nov 2026", "Grab", "2 Strawberry Matcha", 60000, "Selesai", userName),
            OrderModel("KM-261103-035", "09:20", "3 Nov 2026", "Shopee", "1 Strawberry Matcha", 30000, "Batal", userName)
        )
    )
    val orders: StateFlow<List<OrderModel>> = _orders.asStateFlow()

    fun setSelectedSource(source: String) {
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

    fun addToCart(item: MenuItem) {
        val currentCart = _cart.value.toMutableMap()
        if (currentCart.containsKey(item.id)) {
            val existing = currentCart[item.id]!!
            existing.quantity++
        } else {
            currentCart[item.id] = CartItem(item, 1, "")
        }
        _cart.value = currentCart
    }

    fun updateQuantity(itemId: String, delta: Int) {
        val currentCart = _cart.value.toMutableMap()
        val item = currentCart[itemId]
        if (item != null) {
            item.quantity += delta
            if (item.quantity <= 0) {
                currentCart.remove(itemId)
            } else {
                currentCart[itemId] = item
            }
            _cart.value = currentCart
        }
    }

    fun updateNotes(itemId: String, notes: String) {
        val currentCart = _cart.value.toMutableMap()
        val item = currentCart[itemId]
        if (item != null) {
            item.notes = notes
            _cart.value = currentCart
        }
    }

    fun getTotalItems(): Int {
        return _cart.value.values.sumOf { it.quantity }
    }

    fun getTotalPrice(): Int {
        return _cart.value.values.sumOf { it.menuItem.price * it.quantity }
    }
}
