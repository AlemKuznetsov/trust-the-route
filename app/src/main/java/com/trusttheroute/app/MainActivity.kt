package com.trusttheroute.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.trusttheroute.app.ui.navigation.AppNavHost
import com.trusttheroute.app.ui.navigation.Screen
import com.trusttheroute.app.ui.theme.TrustTheRouteTheme
import com.trusttheroute.app.ui.viewmodel.FontSizeViewModel
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
    fontSizeViewModel: FontSizeViewModel = hiltViewModel()
) {
    val isDarkThemeEnabled by themeViewModel.isDarkThemeEnabled.collectAsState()
    val fontSize by fontSizeViewModel.currentFontSize.collectAsState()
    
    TrustTheRouteTheme(
        darkTheme = isDarkThemeEnabled,
        fontSize = fontSize
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavHost(
                navController = navController,
                startDestination = Screen.MainMenu.route
            )
        }
    }
}
