package com.example.kyotomatcha.ui.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Onboarding : Routes("onboarding")
    object Welcome : Routes("welcome")
    object Login : Routes("login")
    object Register : Routes("register")
    object RegStatus : Routes("reg_status/{email}/{name}") {
        fun createRoute(email: String, name: String): String {
            val encodedEmail = java.net.URLDecoder.decode(email, "UTF-8")
            val encodedName = java.net.URLDecoder.decode(name, "UTF-8")
            return "reg_status/$encodedEmail/$encodedName"
        }
    }
    object Dashboard : Routes("dashboard")
    object BusinessSummary : Routes("business_summary")
    object SalesReport : Routes("sales_report")
    object Notification : Routes("notification")
    object Absen : Routes("absen")
    object AttendanceHistory : Routes("attendance_history")
    object Order : Routes("order")
    object OrderSummary : Routes("order_summary")
    object QrisPayment : Routes("qris_payment")
    object SavedOrder : Routes("saved_order")
    object OrderList : Routes("order_list")
    object OrderDetail : Routes("order_detail/{orderId}") {
        fun createRoute(orderId: String): String = "order_detail/$orderId"
    }
    object PaymentConfirmation : Routes("payment_confirmation")
    object Profile : Routes("profile")
    object EditProfile : Routes("edit_profile")
    object ChangePassword : Routes("change_password")
    object NotificationSettings : Routes("notification_settings")
    object PrivacyPolicy : Routes("privacy_policy")
    object AboutApp : Routes("about_app")
    object Terms : Routes("terms")
    object About : Routes("about")
    object Inventory : Routes("inventory")
    object InventoryHistory : Routes("inventory_history")
    object StockIn : Routes("stock_in")
    object StockOut : Routes("stock_out")
    object Expense : Routes("expense")
    object AddExpense : Routes("add_expense")
}
