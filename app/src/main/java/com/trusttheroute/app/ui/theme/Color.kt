package com.trusttheroute.app.ui.theme

import androidx.compose.ui.graphics.Color

// Основные цвета приложения (холодные тона)
val BluePrimary = Color(0xFF3B82F6)  // #3b82f6 (blue-500)
val CyanAccent = Color(0xFF06B6D4)   // #06b6d4
val IndigoAccent = Color(0xFF6366F1) // #6366f1

// Акцентные цвета для темной темы
val Blue400 = Color(0xFF60A5FA)  // #60a5fa - основной акцент темной темы
val Blue500 = Color(0xFF3B82F6)  // #3b82f6 - границы, hover
val Blue600 = Color(0xFF2563EB)  // #2563eb - кнопки
val Blue700 = Color(0xFF1D4ED8)  // #1d4ed8 - hover кнопок
val Blue900 = Color(0xFF1E3A8A)  // #1e3a8a - темные акценты
val Blue950 = Color(0xFF172554)  // #172554 - самый темный синий

// Светлая тема
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF8FAFC)
val LightSurfaceVariant = Color(0xFFF1F5F9)  // slate-100 (для фона изображений)
val LightOnSurface = Color(0xFF1E293B)  // slate-700
val LightOnSurfaceVariant = Color(0xFF64748B)  // slate-600

// Темная тема - Slate цвета
val DarkBackground = Color(0xFF0F172A)  // slate-900 - самый темный фон
val DarkSurface = Color(0xFF1E293B)    // slate-800 - карточки, header
val DarkSurfaceVariant = Color(0xFF334155)  // slate-700 - элементы, границы
val DarkSurfaceHover = Color(0xFF475569)  // slate-600 - hover состояния
val DarkOnSurface = Color(0xFFF1F5F9)   // slate-100 - заголовки
val DarkOnSurfaceVariant = Color(0xFFE2E8F0)  // slate-200 - основной текст
val DarkOnSurfaceSecondary = Color(0xFFCBD5E1)  // slate-300 - вторичный текст
val DarkOnSurfacePlaceholder = Color(0xFF94A3B8)  // slate-400 - плейсхолдеры
val DarkOnSurfaceDescription = Color(0xFF64748B)  // slate-500 - описания

// Границы для светлой темы
val BorderLight = Color(0xFFE0E7FF)  // blue-100
val BorderMedium = Color(0xFFC7D2FE)  // blue-200

// Границы для темной темы
val DarkBorder = Color(0xFF334155)  // slate-700 - основная граница
val DarkBorderHover = Color(0xFF475569)  // slate-600 - hover граница
val DarkBorderAccent = Color(0xFF3B82F6)  // blue-500 - акцентная граница
val DarkBorderActive = Color(0xFF60A5FA)  // blue-400 - активная граница

// Прозрачности для темной темы
val DarkOverlay = Color(0x00000000).copy(alpha = 0.6f)  // bg-black/60
val DarkOverlayStrong = Color(0x00000000).copy(alpha = 0.7f)  // bg-black/70

// Общие цвета
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
