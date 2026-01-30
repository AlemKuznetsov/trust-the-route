package com.trusttheroute.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.trusttheroute.app.ui.screens.about.AboutScreen
import com.trusttheroute.app.ui.screens.auth.LoginScreen
import com.trusttheroute.app.ui.screens.auth.RegisterScreen
import com.trusttheroute.app.ui.screens.auth.ResetPasswordScreen
import com.trusttheroute.app.ui.screens.main.MainMenuScreen
import com.trusttheroute.app.ui.screens.map.MapScreen
import com.trusttheroute.app.ui.screens.routes.RouteDetailsScreen
import com.trusttheroute.app.ui.screens.routes.RoutesScreen
import com.trusttheroute.app.ui.screens.settings.SettingsScreen
import com.trusttheroute.app.ui.screens.settings.LanguageScreen
import com.trusttheroute.app.ui.screens.settings.ThemeScreen
import com.trusttheroute.app.ui.screens.settings.NotificationsScreen
import com.trusttheroute.app.ui.screens.settings.FontSizeScreen
import com.trusttheroute.app.ui.screens.settings.AppInfoScreen
import com.trusttheroute.app.ui.screens.settings.AccountSettingsScreen
import com.trusttheroute.app.ui.screens.settings.UpdateProfileScreen
import com.trusttheroute.app.ui.screens.settings.ChangePasswordScreen
import com.trusttheroute.app.ui.screens.settings.DeleteAccountScreen

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object Routes : Screen("routes")
    object RouteDetails : Screen("route_details/{routeId}") {
        fun createRoute(routeId: String) = "route_details/$routeId"
    }
    object Map : Screen("map/{routeId}") {
        fun createRoute(routeId: String) = "map/$routeId"
    }
    object Settings : Screen("settings")
    object Language : Screen("language")
    object Theme : Screen("theme")
    object Notifications : Screen("notifications")
    object FontSize : Screen("font_size")
    object AppInfo : Screen("app_info")
    object About : Screen("about")
    object Login : Screen("login")
    object Register : Screen("register")
    object ResetPassword : Screen("reset_password")
    object AccountSettings : Screen("account_settings")
    object UpdateProfile : Screen("update_profile")
    object ChangePassword : Screen("change_password")
    object DeleteAccount : Screen("delete_account")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.MainMenu.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onRoutesClick = { navController.navigate(Screen.Routes.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onAboutClick = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.Routes.route) {
            RoutesScreen(
                onBackClick = { navController.popBackStack() },
                onRouteSelected = { routeId ->
                    navController.navigate(Screen.RouteDetails.createRoute(routeId))
                }
            )
        }

        composable(
            route = Screen.RouteDetails.route,
            arguments = listOf(
                navArgument("routeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            RouteDetailsScreen(
                routeId = routeId,
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.Map.createRoute(routeId))
                }
            )
        }

        composable(
            route = Screen.Map.route,
            arguments = listOf(
                navArgument("routeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            MapScreen(
                routeId = routeId,
                onBackClick = { navController.popBackStack() },
                onNavigateToRoutes = { navController.navigate(Screen.Routes.route) },
                onNavigateToMainMenu = { navController.navigate(Screen.MainMenu.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLanguageClick = { navController.navigate(Screen.Language.route) },
                onThemeClick = { navController.navigate(Screen.Theme.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                onFontSizeClick = { navController.navigate(Screen.FontSize.route) },
                onPrivacyClick = { /* TODO: Navigate to privacy settings */ },
                onAccountClick = { navController.navigate(Screen.AccountSettings.route) },
                onAppInfoClick = { navController.navigate(Screen.AppInfo.route) },
                onAboutClick = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.Language.route) {
            LanguageScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Theme.route) {
            ThemeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.FontSize.route) {
            FontSizeScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AppInfo.route) {
            AppInfoScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.MainMenu.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onResetPasswordClick = { navController.navigate(Screen.ResetPassword.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.MainMenu.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AccountSettings.route) {
            AccountSettingsScreen(
                onBackClick = { navController.popBackStack() },
                onUpdateProfileClick = { navController.navigate(Screen.UpdateProfile.route) },
                onChangePasswordClick = { navController.navigate(Screen.ChangePassword.route) },
                onLogoutClick = { navController.navigate(Screen.Login.route) { popUpTo(0) } },
                onDeleteAccountClick = { navController.navigate(Screen.DeleteAccount.route) }
            )
        }

        composable(Screen.UpdateProfile.route) {
            UpdateProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.DeleteAccount.route) {
            DeleteAccountScreen(
                onBackClick = { navController.popBackStack() },
                onAccountDeleted = { navController.navigate(Screen.Login.route) { popUpTo(0) } }
            )
        }
    }
}
