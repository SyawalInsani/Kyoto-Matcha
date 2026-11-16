package com.example.kyotomatcha.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val password_confirmation: String
)

// Nama field mengikuti JSON dari backend Laravel.
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val employee_code: String?
)

data class LoginData(val token: String, val user: UserDto)

data class LoginResponse(val message: String?, val data: LoginData?)

data class MeResponse(val data: UserDto?)

// Register: jawaban sukses tidak berisi token (akun menunggu persetujuan pemilik).
// Juga dipakai membaca jawaban error Laravel: {"message": "...", "errors": {...}}.
data class MessageResponse(val message: String?, val errors: Map<String, List<String>>?)

data class ProductDto(
    val id: Int,
    val name: String,
    val category: String,
    val type: String,
    val price: Int,
    val uses_cup: Boolean,
    val stock: Int?
)

data class ProductsResponse(val data: List<ProductDto>?)

data class CheckOutRequest(val shift: String, val latitude: Double, val longitude: Double)

data class CheckInRequest(val shift: String, val latitude: Double, val longitude: Double)

data class AttendanceDto(
    val id: Int,
    val date: String,
    val shift: String,
    val status: String,
    val late_minutes: Int,
    val distance_m: Int?,
    val check_in_at: String?,
    val check_out_at: String?
)

data class AttendanceResponse(val message: String?, val data: AttendanceDto?)

data class TodayResponse(val data: List<AttendanceDto>?)

// ---- Stok ----
data class StockItemDto(
    val id: Int,
    val name: String,
    val category: String, // bahan, kemasan, dessert
    val unit: String,     // gram, ml, pcs, liter
    val quantity: Double,
    val min_stock: Double,
    val status: String    // tersedia, menipis, habis
)

data class InventorySummaryDto(val total: Int, val tersedia: Int, val menipis: Int, val habis: Int)

data class InventoryResponse(val summary: InventorySummaryDto?, val data: List<StockItemDto>?)

data class StockInRequest(
    val inventory_item_id: Int,
    val qty: Double,
    val purchase_price: Int?,
    val supplier: String?,
    val note: String?
)

data class StockOutRequest(val inventory_item_id: Int, val qty: Double, val purpose: String, val note: String?)

data class StockResultData(val reference_no: String, val stock: StockItemDto)

data class StockResultResponse(val message: String?, val data: StockResultData?)

data class NewItemRequest(val name: String, val category: String, val unit: String, val min_stock: Double?)

data class NewItemResponse(val message: String?, val data: StockItemDto?)

data class MovementDto(
    val id: Int,
    val reference_no: String,
    val type: String, // masuk, keluar
    val purpose: String?,
    val item_name: String,
    val unit: String,
    val qty: Double,
    val qty_before: Double,
    val qty_after: Double,
    val supplier: String?,
    val purchase_price: Int?,
    val note: String?,
    val user_name: String,
    val created_at: String
)

data class MovementsResponse(val data: List<MovementDto>?)

// ---- Pesanan ----
data class OrderItemRequest(val product_id: Int, val qty: Int, val note: String?)

data class CreateOrderRequest(val source: String, val platform: String?, val items: List<OrderItemRequest>)

data class OrderItemDto(
    val product_id: Int,
    val name: String,
    val qty: Int,
    val unit_price: Int,
    val subtotal: Int,
    val note: String?
)

data class PaymentDto(val method: String, val amount: Int, val status: String)

data class OrderDto(
    val id: Int,
    val order_number: String,
    val source: String,   // offline, online
    val platform: String?, // shopee, gojek, grab
    val status: String,   // menunggu_bayar, diproses, selesai, batal
    val total: Int,
    val cashier: String,
    val created_at: String,
    val items: List<OrderItemDto>,
    val payment: PaymentDto?
)

data class OrderResponse(val message: String?, val data: OrderDto?)

data class OrdersResponse(val data: List<OrderDto>?)

data class PayRequest(val method: String)

data class OrderStatusRequest(val status: String, val cancel_reason: String?)

// ---- Izin tidak hadir dan persetujuan ----
data class StaffDto(val id: Int, val name: String, val role: String)

data class StaffResponse(val data: List<StaffDto>?)

data class LeaveBody(
    val date: String, // yyyy-MM-dd
    val shift: String,
    val reason: String,
    val covered_by: Int?,
    val replacement_name: String?
)

data class LeaveDto(
    val id: Int,
    val user_id: Int,
    val user_name: String?,
    val date: String,
    val shift: String,
    val reason: String,
    val covered_by: Int?,
    val covered_by_name: String?,
    val replacement_name: String?,
    val status: String, // menunggu, disetujui, ditolak
    val reject_reason: String?
)

data class LeaveResponse(val message: String?, val data: LeaveDto?)

data class LeavesResponse(val data: List<LeaveDto>?)

data class RejectBody(val reason: String?)

data class PendingUsersResponse(val data: List<UserDto>?)

data class ApproveUserBody(val role: String)

// ---- Beranda dan laporan ----
data class SalesSummaryDto(val total: Int, val orders: Int, val cups: Int, val waiting_payment: Int, val cancelled: Int)

data class AttendanceSummaryDto(
    val expected: Int,
    val present: Int,
    val on_time: Int,
    val late: Int,
    val on_leave: Int,
    val absent: Int,
    val percentage: Int
)

data class StockAlertItemDto(val name: String, val status: String, val quantity: Double, val unit: String)

data class StockAlertsDto(val low: Int, val empty: Int, val needs_restock: Int, val items: List<StockAlertItemDto>)

// sales = null untuk karyawan (angka penjualan hanya untuk pemilik dan kasir).
data class DashboardDto(
    val date: String,
    val sales: SalesSummaryDto?,
    val chart: List<ChartDayDto>?,
    val attendance: AttendanceSummaryDto,
    val stock: StockAlertsDto
)

data class DashboardResponse(val data: DashboardDto?)

data class ChartDayDto(val date: String, val total: Int)

data class SourceSalesDto(val key: String, val label: String, val total: Int, val orders: Int)

data class ExpenseTotalDto(val category: String, val label: String, val total: Int)

data class BestSellerDto(val name: String, val qty: Int)

data class BestSellersDto(val top: List<BestSellerDto>, val others: Int)

data class SalesReportDto(
    val period: String,
    val from: String,
    val to: String,
    val sales: SalesSummaryDto,
    val chart: List<ChartDayDto>,
    val by_source: List<SourceSalesDto>,
    val expenses: List<ExpenseTotalDto>,
    val total_expenses: Int,
    val net: Int,
    val best_sellers: BestSellersDto
)

data class SalesReportResponse(val data: SalesReportDto?)

// ---- Pengeluaran ----
data class ExpenseDto(
    val id: Int,
    val category: String, // uang_makan, bahan, lainnya
    val category_label: String,
    val description: String,
    val amount: Int,
    val expense_date: String,
    val recorded_by: String,
    val created_at: String
)

data class ExpensesResponse(val date: String?, val total: Int?, val count: Int?, val data: List<ExpenseDto>?)

data class ExpenseBody(val category: String, val description: String, val amount: Int, val expense_date: String?)

data class ExpenseResponse(val message: String?, val data: ExpenseDto?)

interface ApiService {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<MessageResponse>

    @GET("me")
    suspend fun me(): Response<MeResponse>

    // Token dikirim manual karena sesi di HP sudah dihapus lebih dulu saat Keluar.
    @POST("logout")
    suspend fun logout(@Header("Authorization") bearer: String): Response<MessageResponse>

    @GET("products")
    suspend fun products(): Response<ProductsResponse>

    @GET("attendance/today")
    suspend fun attendanceToday(): Response<TodayResponse>

    @POST("attendance/check-in")
    suspend fun checkIn(@Body body: CheckInRequest): Response<AttendanceResponse>

    @POST("attendance/check-out")
    suspend fun checkOut(@Body body: CheckOutRequest): Response<AttendanceResponse>

    @GET("inventory")
    suspend fun inventory(): Response<InventoryResponse>

    @GET("inventory/movements")
    suspend fun movements(): Response<MovementsResponse>

    @POST("inventory/stock-in")
    suspend fun stockIn(@Body body: StockInRequest): Response<StockResultResponse>

    @POST("inventory/stock-out")
    suspend fun stockOut(@Body body: StockOutRequest): Response<StockResultResponse>

    @POST("inventory/items")
    suspend fun addInventoryItem(@Body body: NewItemRequest): Response<NewItemResponse>

    @GET("orders")
    suspend fun orders(): Response<OrdersResponse>

    @POST("orders")
    suspend fun createOrder(@Body body: CreateOrderRequest): Response<OrderResponse>

    @POST("orders/{id}/pay")
    suspend fun payOrder(@Path("id") id: Int, @Body body: PayRequest): Response<OrderResponse>

    @POST("orders/{id}/status")
    suspend fun updateOrderStatus(@Path("id") id: Int, @Body body: OrderStatusRequest): Response<OrderResponse>

    @GET("staff")
    suspend fun staff(): Response<StaffResponse>

    @POST("leaves")
    suspend fun submitLeave(@Body body: LeaveBody): Response<LeaveResponse>

    @GET("leaves/mine")
    suspend fun myLeaves(): Response<LeavesResponse>

    @GET("leaves")
    suspend fun pendingLeaves(): Response<LeavesResponse>

    @POST("leaves/{id}/approve")
    suspend fun approveLeave(@Path("id") id: Int): Response<LeaveResponse>

    @POST("leaves/{id}/reject")
    suspend fun rejectLeave(@Path("id") id: Int, @Body body: RejectBody): Response<LeaveResponse>

    @GET("users/pending")
    suspend fun pendingUsers(): Response<PendingUsersResponse>

    @POST("users/{id}/approve")
    suspend fun approveUser(@Path("id") id: Int, @Body body: ApproveUserBody): Response<MessageResponse>

    @POST("users/{id}/reject")
    suspend fun rejectUser(@Path("id") id: Int): Response<MessageResponse>

    @GET("dashboard")
    suspend fun dashboard(): Response<DashboardResponse>

    @GET("reports/sales")
    suspend fun salesReport(@Query("period") period: String): Response<SalesReportResponse>

    @GET("expenses")
    suspend fun expenses(): Response<ExpensesResponse>

    @POST("expenses")
    suspend fun addExpense(@Body body: ExpenseBody): Response<ExpenseResponse>
}
