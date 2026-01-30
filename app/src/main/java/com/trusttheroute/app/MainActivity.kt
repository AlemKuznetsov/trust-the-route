package com.trusttheroute.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.trusttheroute.app.ui.navigation.AppNavHost
import com.trusttheroute.app.ui.navigation.Screen
import com.trusttheroute.app.ui.theme.TrustTheRouteTheme
import com.trusttheroute.app.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }
}

@Composable
fun MainContent(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    authViewModel: com.trusttheroute.app.ui.viewmodel.AuthViewModel = hiltViewModel(),
    fontSizeViewModel: com.trusttheroute.app.ui.viewmodel.FontSizeViewModel = hiltViewModel()
) {
    val isDarkThemeEnabled by themeViewModel.isDarkThemeEnabled.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val fontSize by fontSizeViewModel.currentFontSize.collectAsState()
    
    // Проверяем статус авторизации при запуске
    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus()
    }
    
    TrustTheRouteTheme(darkTheme = isDarkThemeEnabled, fontSize = fontSize) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            
            // Определяем начальный экран в зависимости от статуса авторизации
            // Если статус еще не определен (null), показываем экран логина
            // Если статус определен, используем правильный экран сразу
            val startDestination = when (isLoggedIn) {
                true -> Screen.MainMenu.route
                false -> Screen.Login.route
                null -> Screen.Login.route // По умолчанию показываем логин, пока проверяется статус
            }
            
            // Навигация при изменении статуса авторизации (только если статус изменился с null на true/false)
            LaunchedEffect(isLoggedIn) {
                when (isLoggedIn) {
                    true -> {
                        // Если авторизован, переходим на главное меню
                        if (navController.currentDestination?.route != Screen.MainMenu.route) {
                            navController.navigate(Screen.MainMenu.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                    false -> {
                        // Если не авторизован, переходим на экран входа
                        if (navController.currentDestination?.route != Screen.Login.route) {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.MainMenu.route) { inclusive = true }
                            }
                        }
                    }
                    null -> {
                        // Во время проверки ничего не делаем
                    }
                }
            }
            
            AppNavHost(
                navController = navController,
                startDestination = startDestination
            )
        }
    }
}
