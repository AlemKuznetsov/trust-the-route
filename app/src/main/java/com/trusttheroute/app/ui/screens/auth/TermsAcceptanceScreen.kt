package com.trusttheroute.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.trusttheroute.app.ui.theme.*

@Composable
fun TermsAcceptanceScreen(
    onAccept: () -> Unit,
    onViewTerms: () -> Unit
) {
    val isDarkTheme = isDarkTheme()
    var termsAccepted by remember { mutableStateOf(false) }
    var privacyAccepted by remember { mutableStateOf(false) }
    
    val gradientColors = if (isDarkTheme) {
        listOf(DarkBackground, DarkSurface, DarkBackground)
    } else {
        listOf(CyanAccent.copy(alpha = 0.1f), LightBackground)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Добро пожаловать!",
            style = MaterialTheme.typography.headlineMedium,
            color = if (isDarkTheme) Blue400 else BluePrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Для продолжения необходимо принять пользовательское соглашение",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) DarkSurface else LightSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Пользовательское соглашение",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Blue400 else BluePrimary
                )
                
                Text(
                    text = "Используя приложение Trust The Route, вы соглашаетесь с условиями настоящего Пользовательского соглашения. Если вы не согласны с условиями, пожалуйста, не используйте приложение.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                )
                
                TextButton(onClick = onViewTerms) {
                    Text("Прочитать полное соглашение")
                }
                
                Divider()
                
                Text(
                    text = "Согласие на обработку персональных данных",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Blue400 else BluePrimary
                )
                
                Text(
                    text = "Я даю согласие на обработку моих персональных данных в соответствии с Политикой конфиденциальности.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkOnSurfaceVariant else LightOnSurfaceVariant
                )
            }
        }
        
        // Чекбоксы согласия
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )
                Text(
                    text = "Я принимаю пользовательское соглашение",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = privacyAccepted,
                    onCheckedChange = { privacyAccepted = it }
                )
                Text(
                    text = "Я согласен на обработку персональных данных",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) DarkOnSurface else LightOnSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Кнопка принятия
        Button(
            onClick = {
                if (termsAccepted && privacyAccepted) {
                    onAccept()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = termsAccepted && privacyAccepted,
            colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary
            )
        ) {
            Text("Принять и продолжить", fontWeight = FontWeight.Bold)
        }
    }
}
