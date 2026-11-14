package com.example.kyotomatcha.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kyotomatcha.ui.screens.about.AboutScreen
import com.example.kyotomatcha.ui.screens.attendance.AbsensiScreen
import com.example.kyotomatcha.ui.screens.attendance.AttendanceHistoryScreen
import com.example.kyotomatcha.ui.screens.auth.LoginScreen
import com.example.kyotomatcha.ui.screens.auth.RegisterScreen
import com.example.kyotomatcha.ui.screens.auth.RegistrationStatusScreen
import com.example.kyotomatcha.ui.screens.dashboard.BusinessSummaryScreen
import com.example.kyotomatcha.ui.screens.dashboard.DashboardScreen
import com.example.kyotomatcha.ui.screens.dashboard.NotificationScreen
import com.example.kyotomatcha.ui.screens.dashboard.SalesReportScreen
import com.example.kyotomatcha.ui.screens.expense.AddExpenseScreen
import com.example.kyotomatcha.ui.screens.expense.ExpenseScreen
import com.example.kyotomatcha.ui.screens.inventory.InventoryHistoryScreen
import com.example.kyotomatcha.ui.screens.inventory.InventoryScreen
import com.example.kyotomatcha.ui.screens.inventory.StockInScreen
import com.example.kyotomatcha.ui.screens.inventory.StockOutScreen
import com.example.kyotomatcha.ui.screens.onboarding.OnboardingScreen
import com.example.kyotomatcha.ui.screens.order.OrderDetailScreen
import com.example.kyotomatcha.ui.screens.order.OrderListScreen
import com.example.kyotomatcha.ui.screens.order.OrderScreen
import com.example.kyotomatcha.ui.screens.order.OrderSummaryScreen
import com.example.kyotomatcha.ui.screens.order.PaymentConfirmationScreen
import com.example.kyotomatcha.ui.screens.order.QrisPaymentScreen
import com.example.kyotomatcha.ui.screens.order.SavedOrderScreen
import com.example.kyotomatcha.ui.screens.profile.AboutAppScreen
import com.example.kyotomatcha.ui.screens.profile.ChangePasswordScreen
import com.example.kyotomatcha.ui.screens.profile.EditProfileScreen
import com.example.kyotomatcha.ui.screens.profile.NotificationSettingsScreen
import com.example.kyotomatcha.ui.screens.profile.PrivacyPolicyScreen
import com.example.kyotomatcha.ui.screens.profile.ProfileScreen
import com.example.kyotomatcha.ui.screens.profile.TermsScreen
import com.example.kyotomatcha.ui.screens.splash.SplashScreen
import com.example.kyotomatcha.ui.screens.welcome.WelcomeScreen

fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Routes.Welcome.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Welcome.route) {
            WelcomeScreen(
                onCreateAccount = {
                    navController.navigate(Routes.Register.route)
                },
                onLogin = {
                    navController.navigate(Routes.Login.route)
                }
            )
        }
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }
        composable(Routes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { email, name ->
                    navController.navigate(Routes.RegStatus.createRoute(email, name)) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.RegStatus.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val encodedName = backStackEntry.arguments?.getString("name") ?: ""
            val email = java.net.URLDecoder.decode(encodedEmail, "UTF-8")
            val name = java.net.URLDecoder.decode(encodedName, "UTF-8")
            RegistrationStatusScreen(
                email = email,
                name = name,
                onBackToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.RegStatus.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Dashboard.route) {
            DashboardScreen(
                onBranchClick = {
                    navController.navigate(Routes.BusinessSummary.route)
                },
                onReportClick = {
                    navController.navigate(Routes.SalesReport.route)
                },
                onNotificationClick = {
                    navController.navigate(Routes.Notification.route)
                },
                onAbsenClick = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onOrderClick = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onProfileClick = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                },
                onExpenseClick = {
                    navController.navigate(Routes.Expense.route)
                }
            )
        }
        composable(Routes.BusinessSummary.route) {
            BusinessSummaryScreen(
                onNavigateBack = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNotificationClick = {
                    navController.navigate(Routes.Notification.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.SalesReport.route) {
            SalesReportScreen(
                onNavigateBack = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.Notification.route) {
            NotificationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.Absen.route) {
            AbsensiScreen(
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.AttendanceHistory.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.AttendanceHistory.route) {
            AttendanceHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.Order.route) {
            OrderScreen(
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToSummary = {
                    navController.navigate(Routes.OrderSummary.route)
                },
                onNavigateToList = {
                    navController.navigate(Routes.OrderList.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.OrderSummary.route) {
            OrderSummaryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToQris = {
                    navController.navigate(Routes.QrisPayment.route)
                },
                onNavigateToSavedOrder = {
                    navController.navigate(Routes.OrderList.route) {
                        popUpTo(Routes.Order.route) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.QrisPayment.route) {
            QrisPaymentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToConfirmation = {
                    navController.navigate(Routes.PaymentConfirmation.route)
                },
                onNavigateToSavedOrder = {
                    navController.navigate(Routes.OrderList.route) {
                        popUpTo(Routes.Order.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.SavedOrder.route) {
            SavedOrderScreen(
                onNavigateNewOrder = {
                    navController.navigate(Routes.Order.route) {
                        popUpTo(Routes.Order.route) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToList = {
                    navController.navigate(Routes.OrderList.route) {
                        popUpTo(Routes.Dashboard.route) { inclusive = false }
                    }
                }
            )
        }
        composable(Routes.OrderList.route) {
            OrderListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { orderId ->
                    navController.navigate(Routes.OrderDetail.createRoute(orderId))
                },
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(
            route = Routes.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: "KM-261103-037"
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.PaymentConfirmation.route) {
            PaymentConfirmationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onConfirmSuccess = {
                    navController.navigate(Routes.OrderList.route) {
                        popUpTo(Routes.Order.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Profile.route) {
            ProfileScreen(
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNavigateToEditProfile = {
                    navController.navigate(Routes.EditProfile.route)
                },
                onNavigateToChangePassword = {
                    navController.navigate(Routes.ChangePassword.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Routes.NotificationSettings.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Routes.AboutApp.route)
                },
                onNavigateToPrivacy = {
                    navController.navigate(Routes.PrivacyPolicy.route)
                },
                onNavigateToTerms = {
                    navController.navigate(Routes.Terms.route)
                },
                onLogout = {
                    navController.navigate(Routes.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.NotificationSettings.route) {
            NotificationSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.AboutApp.route) {
            AboutAppScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPrivacy = {
                    navController.navigate(Routes.PrivacyPolicy.route)
                },
                onNavigateToTerms = {
                    navController.navigate(Routes.Terms.route)
                }
            )
        }
        composable(Routes.Terms.route) {
            TermsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.About.route) {
            AboutScreen()
        }
        composable(Routes.Inventory.route) {
            InventoryScreen(
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onNavigateToStockIn = {
                    navController.navigate(Routes.StockIn.route)
                },
                onNavigateToStockOut = {
                    navController.navigate(Routes.StockOut.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.InventoryHistory.route)
                }
            )
        }
        composable(Routes.InventoryHistory.route) {
            InventoryHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.StockIn.route) {
            StockInScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.InventoryHistory.route)
                }
            )
        }
        composable(Routes.StockOut.route) {
            StockOutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.InventoryHistory.route)
                }
            )
        }
        composable(Routes.Expense.route) {
            ExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddExpense = {
                    navController.navigate(Routes.AddExpense.route)
                },
                onNavigateHome = {
                    navController.navigateToTab(Routes.Dashboard.route)
                },
                onNavigateToAbsen = {
                    navController.navigateToTab(Routes.Absen.route)
                },
                onNavigateToOrder = {
                    navController.navigateToTab(Routes.Order.route)
                },
                onNavigateToProfile = {
                    navController.navigateToTab(Routes.Profile.route)
                },
                onInventoryClick = {
                    navController.navigateToTab(Routes.Inventory.route)
                }
            )
        }
        composable(Routes.AddExpense.route) {
            AddExpenseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
