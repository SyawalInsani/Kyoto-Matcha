package com.example.kyotomatcha.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kyotomatcha.ui.screens.about.AboutScreen
import com.example.kyotomatcha.ui.screens.auth.LoginScreen
import com.example.kyotomatcha.ui.screens.auth.RegisterScreen
import com.example.kyotomatcha.ui.screens.auth.RegistrationStatusScreen
import com.example.kyotomatcha.ui.screens.onboarding.OnboardingScreen
import com.example.kyotomatcha.ui.screens.splash.SplashScreen
import com.example.kyotomatcha.ui.screens.welcome.WelcomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Splash.route) {
        composable(Routes.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Routes.Onboarding.route) {
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
                    // Navigate to main/dashboard or appropriate screen
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
        composable(Routes.About.route) {
            AboutScreen()
        }
    }
}
