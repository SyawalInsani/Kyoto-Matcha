package com.example.kyotomatcha.data.repository

import android.util.Log
import com.example.kyotomatcha.data.remote.CreateOrderRequest
import com.example.kyotomatcha.data.remote.NETWORK_ERROR_MESSAGE
import com.example.kyotomatcha.data.remote.OrderDto
import com.example.kyotomatcha.data.remote.OrderItemRequest
import com.example.kyotomatcha.data.remote.OrderStatusRequest
import com.example.kyotomatcha.data.remote.PayRequest
import com.example.kyotomatcha.data.remote.ProofDto
import com.example.kyotomatcha.data.remote.RetrofitClient
import com.example.kyotomatcha.data.remote.toApiError
import com.example.kyotomatcha.ui.screens.order.CartItem
import com.example.kyotomatcha.ui.screens.order.MenuItem
import com.example.kyotomatcha.ui.screens.order.OrderModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object OrderRepository {
    // Menu diambil dari server (GET /api/products). Daftar ini kosong sampai berhasil dimuat.
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    @Volatile
    private var menuLoaded = false

    // Menu yang stoknya tidak dilacak server (stock = null) dianggap selalu tersedia.
    private const val UNTRACKED_STOCK = 999

    /** Paksa muat ulang menu (stok berubah) pada pembukaan layar Pesanan berikutnya. */
    fun invalidateMenu() {
        menuLoaded = false
    }

    /** Muat menu dari server sekali per sesi aplikasi. Gagal -> dicoba lagi saat layar dibuka berikutnya. */
    suspend fun loadMenu() {
        if (menuLoaded) return
        try {
            val res = RetrofitClient.apiService.products()
            val products = res.body()?.data
            if (res.isSuccessful && products != null) {
                _menuItems.value = products.map {
                    val isDessert = it.category == "dessert"
                    MenuItem(
                        id = it.id.toString(),
                        name = it.name,
                        price = it.price,
                        stock = it.stock ?: UNTRACKED_STOCK,
                        category = if (isDessert) "Dessert" else "Minuman",
                        imagePlaceholder = if (isDessert) "🍰" else "🍵"
                    )
                }
                menuLoaded = true
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Memuat menu gagal", e)
        }
    }

    private val _cart = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cart: StateFlow<Map<String, CartItem>> = _cart.asStateFlow()

    // Pesanan hari ini dari server (terbaru di atas). Kosong sampai dimuat.
    private val _orders = MutableStateFlow<List<OrderModel>>(emptyList())
    val orders: StateFlow<List<OrderModel>> = _orders.asStateFlow()

    // Semua minuman memakai gelas yang sama, jadi stok yang tampil berkurang bersama. Dessert punya stok sendiri.
    private fun sharesStock(a: MenuItem, b: MenuItem) =
        if (a.category.equals("Minuman", ignoreCase = true)) b.category.equals("Minuman", ignoreCase = true) else a.id == b.id

    fun addToCart(item: MenuItem) {
        val currentItems = _menuItems.value.map { menuItem ->
            if (sharesStock(item, menuItem) && menuItem.stock > 0) {
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

            val changed = _menuItems.value.find { it.id == itemId }
            val currentItems = _menuItems.value.map { menuItem ->
                if (changed != null && sharesStock(changed, menuItem)) {
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

    /** Muat pesanan hari ini dari server. */
    suspend fun loadOrders() {
        try {
            val res = RetrofitClient.apiService.orders()
            val rows = res.body()?.data
            if (res.isSuccessful && rows != null) _orders.value = rows.map { toModel(it) }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Memuat pesanan gagal", e)
        }
    }

    /**
     * Catat isi keranjang sebagai pesanan di server. Harga dan total dihitung server.
     * payCash = true: pesanan offline langsung dibayar tunai (status Diproses).
     * Mengembalikan null bila berhasil, atau pesan error.
     */
    suspend fun createOrder(source: String, payCash: Boolean): String? {
        val lines = _cart.value.values.toList()
        if (lines.isEmpty()) return "Keranjang masih kosong."
        val isOnline = !source.equals("Offline", ignoreCase = true)
        val request = CreateOrderRequest(
            source = if (isOnline) "online" else "offline",
            platform = if (isOnline) source.lowercase() else null,
            items = lines.map { OrderItemRequest(it.menuItem.id.toInt(), it.quantity, it.notes.ifBlank { null }) }
        )
        return try {
            val api = RetrofitClient.apiService
            val res = api.createOrder(request)
            var order = res.body()?.data
            if (!res.isSuccessful || order == null) return res.toApiError().message

            var payError: String? = null
            if (payCash && !isOnline) {
                val payRes = api.payOrder(order.id, PayRequest("tunai"))
                if (payRes.isSuccessful) order = payRes.body()?.data ?: order else payError = payRes.toApiError().message
            }
            _orders.value = listOf(toModel(order)) + _orders.value.filter { it.id != order.order_number }
            clearCart()
            refreshMenuAfterStockChange()
            payError?.let { "Pesanan tercatat, tetapi pembayaran gagal dikonfirmasi: $it" }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Catat pesanan gagal", e)
            NETWORK_ERROR_MESSAGE
        }
    }

    /**
     * Ubah status pesanan di server. newStatus memakai istilah layar:
     * "Diproses" = konfirmasi pembayaran (payMethod: qris/tunai), "Selesai", "Batal" (dengan alasan).
     * Mengembalikan null bila berhasil, atau pesan error.
     */
    suspend fun updateOrderStatus(orderNumber: String, newStatus: String, reason: String?, payMethod: String): String? {
        val order = _orders.value.firstOrNull { it.id.equals(orderNumber, ignoreCase = true) }
            ?: return "Pesanan tidak ditemukan. Muat ulang daftar pesanan."
        return try {
            val api = RetrofitClient.apiService
            val res = when (newStatus) {
                "Diproses" -> api.payOrder(order.serverId, PayRequest(payMethod))
                "Selesai" -> api.updateOrderStatus(order.serverId, OrderStatusRequest("selesai", null))
                else -> api.updateOrderStatus(order.serverId, OrderStatusRequest("batal", reason?.ifBlank { null } ?: "Dibatalkan"))
            }
            val updated = res.body()?.data
            if (res.isSuccessful && updated != null) {
                _orders.value = _orders.value.map { if (it.serverId == updated.id) toModel(updated) else it }
                if (newStatus == "Batal") refreshMenuAfterStockChange()
                null
            } else {
                loadOrders() // tampilkan status yang sebenarnya di server
                res.toApiError().message
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Ubah status pesanan gagal", e)
            NETWORK_ERROR_MESSAGE
        }
    }

    /** Kirim foto bukti bayar QRIS; server membaca dan mencocokkannya dengan pesanan. Pesanan belum berubah status. */
    suspend fun uploadProof(orderNumber: String, jpeg: ByteArray): Pair<ProofDto?, String?> {
        val order = _orders.value.firstOrNull { it.id.equals(orderNumber, ignoreCase = true) }
            ?: return null to "Pesanan tidak ditemukan. Muat ulang daftar pesanan."
        return try {
            val part = MultipartBody.Part.createFormData("foto", "bukti.jpg", RequestBody.create(MediaType.parse("image/jpeg"), jpeg))
            val res = RetrofitClient.apiService.uploadProof(order.serverId, part)
            val data = res.body()?.data
            if (res.isSuccessful && data != null) data to null else null to res.toApiError().message
        } catch (e: Exception) {
            Log.e("OrderRepository", "Unggah bukti bayar gagal", e)
            null to NETWORK_ERROR_MESSAGE
        }
    }

    /** Lanjutkan setelah bukti sesuai: pesanan jadi Diproses dan masuk penjualan hari itu. Null = berhasil. */
    suspend fun confirmProof(orderNumber: String): String? {
        val order = _orders.value.firstOrNull { it.id.equals(orderNumber, ignoreCase = true) }
            ?: return "Pesanan tidak ditemukan. Muat ulang daftar pesanan."
        return try {
            val res = RetrofitClient.apiService.confirmProof(order.serverId)
            loadOrders()
            if (res.isSuccessful) null else res.toApiError().message
        } catch (e: Exception) {
            Log.e("OrderRepository", "Konfirmasi bukti bayar gagal", e)
            NETWORK_ERROR_MESSAGE
        }
    }

    /** Riwayat pembayaran QRIS offline hari ini. Pair: (daftar, pesan error). */
    suspend fun qrisPayments(): Pair<List<ProofDto>, String?> = try {
        val res = RetrofitClient.apiService.qrisPayments()
        val rows = res.body()?.data
        if (res.isSuccessful && rows != null) rows to null else emptyList<ProofDto>() to res.toApiError().message
    } catch (e: Exception) {
        Log.e("OrderRepository", "Memuat riwayat QRIS gagal", e)
        emptyList<ProofDto>() to NETWORK_ERROR_MESSAGE
    }

    /** Hapus foto bukti agar hemat ruang di server. Hasil baca tetap ada. Mengembalikan pesan error, atau null bila berhasil. */
    suspend fun deleteProofImage(paymentId: Int): String? = try {
        val res = RetrofitClient.apiService.deleteProofImage(paymentId)
        if (res.isSuccessful) null else res.toApiError().message
    } catch (e: Exception) {
        Log.e("OrderRepository", "Menghapus foto bukti gagal", e)
        NETWORK_ERROR_MESSAGE
    }

    // Stok berubah (pesanan dicatat/dibatalkan): muat ulang menu agar angka stok sesuai server.
    private suspend fun refreshMenuAfterStockChange() {
        menuLoaded = false
        loadMenu()
    }

    private fun toModel(o: OrderDto): OrderModel {
        val menu = _menuItems.value.associateBy { it.id }
        val items = o.items.map { line ->
            val known = menu[line.product_id.toString()]
            CartItem(
                MenuItem(line.product_id.toString(), line.name, line.unit_price, known?.stock ?: 0, known?.category ?: "Minuman", known?.imagePlaceholder ?: "🍵"),
                line.qty,
                line.note.orEmpty()
            )
        }
        return OrderModel(
            id = o.order_number,
            time = o.created_at.substring(11, 16),
            date = shortDate(o.created_at),
            source = if (o.source == "online") (o.platform ?: "Online").replaceFirstChar { it.uppercase() } else "Offline",
            itemsSummary = items.joinToString(" + ") { "${it.quantity} ${it.menuItem.name}" },
            total = o.total,
            status = when (o.status) {
                "menunggu_bayar" -> "Menunggu bayar"
                "diproses" -> "Diproses"
                "selesai" -> "Selesai"
                else -> "Batal"
            },
            cashier = o.cashier,
            items = items,
            serverId = o.id
        )
    }

    // "2026-11-16T09:40:00+08:00" -> "16 Nov 2026" (tanggal setempat kedai, sesuai yang dikirim server).
    private fun shortDate(iso: String): String {
        val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
        val parts = iso.take(10).split("-")
        val month = parts.getOrNull(1)?.toIntOrNull()?.let { months.getOrNull(it - 1) } ?: return iso.take(10)
        return "${parts[2].toInt()} $month ${parts[0]}"
    }
}
