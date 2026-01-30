package com.trusttheroute.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.ui.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    onBackClick: () -> Unit,
    onUpdateProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isDarkTheme = isDarkTheme()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }
    
    val gradientBrush = if (isDarkTheme) {
        Brush.linearGradient(
            colors = listOf(DarkBackground, DarkSurface, DarkBackground),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFFEFF6FF), Color(0xFFFFFFFF), Color(0xFFECFEFF)),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Настройка учетной записи",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isDarkTheme) Blue400 else BluePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "Назад",
                            tint = if (isDarkTheme) Blue400 else BluePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) DarkSurface else White,
                    titleContentColor = if (isDarkTheme) Blue400 else BluePrimary,
                    navigationIconContentColor = if (isDarkTheme) Blue400 else BluePrimary
                ),
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = if (isDarkTheme) DarkBorder else BorderLight,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Данные профиля
                Text(
                    text = "Данные профиля",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Blue400 else BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Имя / отображаемое имя
                AccountInfoCard(
                    title = "Имя",
                    value = currentUser?.name ?: "Не указано",
                    icon = Icons.Default.Person,
                    onClick = onUpdateProfileClick,
                    isDarkTheme = isDarkTheme
                )

                // Email (только просмотр)
                AccountInfoCard(
                    title = "Адрес электронной почты",
                    value = currentUser?.email ?: "Не указано",
                    icon = Icons.Default.Email,
                    onClick = null, // Только просмотр
                    isDarkTheme = isDarkTheme
                )

                // Смена пароля (только для пользователей с email/password авторизацией)
                if (currentUser?.authMethod != "yandex") {
                    AccountActionCard(
                        title = "Смена пароля",
                        icon = Icons.Default.Lock,
                        onClick = onChangePasswordClick,
                        isDarkTheme = isDarkTheme
                    )
                } else {
                    // Для пользователей YandexID показываем информационное сообщение
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) DarkSurface else White
                        ),
                        border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
                        elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isDarkTheme) Blue400 else BluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Смена пароля",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isDarkTheme) DarkOnSurface else LightOnSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Вы авторизованы через Yandex ID. Для смены пароля используйте настройки вашего аккаунта Yandex.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDarkTheme) DarkOnSurfaceDescription else LightOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Выход из аккаунта
                AccountActionCard(
                    title = "Выход из аккаунта",
                    icon = Icons.Default.ExitToApp,
                    onClick = {
                        authViewModel.logout()
                        onLogoutClick()
                    },
                    isDarkTheme = isDarkTheme,
                    isDestructive = false
                )

                // Удаление учетной записи
                AccountActionCard(
                    title = "Удаление учетной записи",
                    icon = Icons.Default.Delete,
                    onClick = onDeleteAccountClick,
                    isDarkTheme = isDarkTheme,
                    isDestructive = true
                )

                // Дополнительно
                Text(
                    text = "Дополнительно",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Blue400 else BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                // Статус подписки (в будущем)
                AccountInfoCard(
                    title = "Статус подписки",
                    value = "Бесплатная версия",
                    icon = Icons.Default.Star,
                    onClick = null,
                    isDarkTheme = isDarkTheme
                )

                // Дата регистрации (read-only)
                AccountInfoCard(
                    title = "Дата регистрации",
                    value = "Не указано", // TODO: Добавить поле createdAt в User модель
                    icon = Icons.Default.CalendarToday,
                    onClick = null,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
fun AccountInfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)?,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface else White
        ),
        border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
        elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDarkTheme) Blue400 else BluePrimary,
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme) DarkOnSurfaceDescription else LightOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDarkTheme) DarkOnSurface else LightOnSurface
                )
            }
            if (onClick != null) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = if (isDarkTheme) Blue400 else BluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AccountActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    isDestructive: Boolean = false
) {
    val iconColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        if (isDarkTheme) Blue400 else BluePrimary
    }

    val textColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        if (isDarkTheme) DarkOnSurface else LightOnSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) DarkSurface else White
        ),
        border = if (isDarkTheme) androidx.compose.foundation.BorderStroke(1.dp, DarkBorder) else null,
        elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isDarkTheme) DarkOnSurfaceDescription else LightOnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
