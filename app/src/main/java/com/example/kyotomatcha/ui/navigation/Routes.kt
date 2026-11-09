package com.example.kyotomatcha.ui.navigation

sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Onboarding : Routes("onboarding")
    object Welcome : Routes("welcome")
    object Login : Routes("login")
    object Register : Routes("register")
    object RegStatus : Routes("reg_status/{email}/{name}") {
        fun createRoute(email: String, name: String): String {
            val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
            val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
            return "reg_status/$encodedEmail/$encodedName"
        }
    }
    object About : Routes("about")
}
