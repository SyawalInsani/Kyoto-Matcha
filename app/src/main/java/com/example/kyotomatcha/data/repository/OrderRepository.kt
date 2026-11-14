package com.example.kyotomatcha.data.repository

import com.example.kyotomatcha.ui.screens.order.CartItem
import com.example.kyotomatcha.ui.screens.order.MenuItem
import com.example.kyotomatcha.ui.screens.order.OrderModel
import com.example.kyotomatcha.util.DateUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object OrderRepository {
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

    private val _cart = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cart: StateFlow<Map<String, CartItem>> = _cart.asStateFlow()

    private val _orders = MutableStateFlow(
        listOf(
            OrderModel("KM-261103-038", "09:40", "3 Nov 2026", "Offline", "2 Matcha Latte + 1 Strawberry Matcha", 80000, "Menunggu bayar", "Salsa", emptyList()),
            OrderModel("KM-261103-037", "09:35", "3 Nov 2026", "Gojek", "Matcha Latte + Strawberry Matcha", 55000, "Diproses", "Salsa", emptyList()),
            OrderModel("KM-261103-036", "09:28", "3 Nov 2026", "Grab", "2 Strawberry Matcha", 60000, "Selesai", "Salsa", emptyList()),
            OrderModel("KM-261103-035", "09:20", "3 Nov 2026", "Shopee", "1 Strawberry Matcha", 30000, "Batal", "Salsa", emptyList())
        )
    )
    val orders: StateFlow<List<OrderModel>> = _orders.asStateFlow()

    fun addToCart(item: MenuItem) {
        val currentItems = _menuItems.value.map { menuItem ->
            if (menuItem.id == item.id && menuItem.stock > 0) {
                menuItem.copy(stock = menuItem.stock - 1)
            } else {
                menuItem
            }
        }
        _menuItems.value = currentItems

        val currentCart = _cart.value.toMutableMap()
        val updatedMenuItem = currentItems.find { it.id == item.id } ?: item
        if (currentCart.containsKey(item.id)) {
            val existing = currentCart[item.id]!!
            existing.quantity++
        } else {
            currentCart[item.id] = CartItem(updatedMenuItem, 1, "")
        }
        _cart.value = currentCart
    }

    fun updateQuantity(itemId: String, delta: Int) {
        val currentCart = _cart.value.toMutableMap()
        val item = currentCart[itemId]
        if (item != null) {
            item.quantity += delta

            val currentItems = _menuItems.value.map { menuItem ->
                if (menuItem.id == itemId) {
                    val newStock = menuItem.stock - delta
                    menuItem.copy(stock = maxOf(0, newStock))
                } else {
                    menuItem
                }
            }
            _menuItems.value = currentItems

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

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun createOrder(source: String, userName: String, status: String = "Diproses") {
        val cartItems = _cart.value.values.toList()
        val total = cartItems.sumOf { it.menuItem.price * it.quantity }
        val itemsSummary = cartItems.joinToString(" + ") { "${it.quantity} ${it.menuItem.name}" }
        val newOrder = OrderModel(
            id = "KM-${System.currentTimeMillis().toString().takeLast(6)}",
            time = DateUtil.getCurrentTimeFormatted(),
            date = DateUtil.getCurrentDateFormatted(),
            source = source,
            itemsSummary = itemsSummary.ifEmpty { "Pesanan Baru" },
            total = total,
            status = status,
            cashier = userName,
            items = cartItems
        )
        val currentOrders = _orders.value.toMutableList()
        currentOrders.add(0, newOrder)
        _orders.value = currentOrders
        clearCart()
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        val currentOrders = _orders.value.map { order ->
            if (order.id.equals(orderId, ignoreCase = true)) {
                order.copy(status = newStatus)
            } else {
                order
            }
        }
        _orders.value = currentOrders
    }
}
