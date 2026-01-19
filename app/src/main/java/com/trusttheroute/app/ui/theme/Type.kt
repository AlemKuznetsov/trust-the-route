package com.trusttheroute.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Базовые размеры шрифтов (средний размер)
 */
private val baseTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Создает Typography с учетом выбранного размера шрифта
 * @param fontSize "small", "medium", или "large"
 */
fun getTypography(fontSize: String): Typography {
    val multiplier = when (fontSize) {
        "small" -> 0.85f
        "large" -> 1.2f
        else -> 1.0f // medium
    }
    
    return Typography(
        displayLarge = baseTypography.displayLarge.copy(
            fontSize = baseTypography.displayLarge.fontSize * multiplier,
            lineHeight = baseTypography.displayLarge.lineHeight * multiplier
        ),
        displayMedium = baseTypography.displayMedium.copy(
            fontSize = baseTypography.displayMedium.fontSize * multiplier,
            lineHeight = baseTypography.displayMedium.lineHeight * multiplier
        ),
        displaySmall = baseTypography.displaySmall.copy(
            fontSize = baseTypography.displaySmall.fontSize * multiplier,
            lineHeight = baseTypography.displaySmall.lineHeight * multiplier
        ),
        headlineLarge = baseTypography.headlineLarge.copy(
            fontSize = baseTypography.headlineLarge.fontSize * multiplier,
            lineHeight = baseTypography.headlineLarge.lineHeight * multiplier
        ),
        headlineMedium = baseTypography.headlineMedium.copy(
            fontSize = baseTypography.headlineMedium.fontSize * multiplier,
            lineHeight = baseTypography.headlineMedium.lineHeight * multiplier
        ),
        headlineSmall = baseTypography.headlineSmall.copy(
            fontSize = baseTypography.headlineSmall.fontSize * multiplier,
            lineHeight = baseTypography.headlineSmall.lineHeight * multiplier
        ),
        titleLarge = baseTypography.titleLarge.copy(
            fontSize = baseTypography.titleLarge.fontSize * multiplier,
            lineHeight = baseTypography.titleLarge.lineHeight * multiplier
        ),
        titleMedium = baseTypography.titleMedium.copy(
            fontSize = baseTypography.titleMedium.fontSize * multiplier,
            lineHeight = baseTypography.titleMedium.lineHeight * multiplier
        ),
        titleSmall = baseTypography.titleSmall.copy(
            fontSize = baseTypography.titleSmall.fontSize * multiplier,
            lineHeight = baseTypography.titleSmall.lineHeight * multiplier
        ),
        bodyLarge = baseTypography.bodyLarge.copy(
            fontSize = baseTypography.bodyLarge.fontSize * multiplier,
            lineHeight = baseTypography.bodyLarge.lineHeight * multiplier
        ),
        bodyMedium = baseTypography.bodyMedium.copy(
            fontSize = baseTypography.bodyMedium.fontSize * multiplier,
            lineHeight = baseTypography.bodyMedium.lineHeight * multiplier
        ),
        bodySmall = baseTypography.bodySmall.copy(
            fontSize = baseTypography.bodySmall.fontSize * multiplier,
            lineHeight = baseTypography.bodySmall.lineHeight * multiplier
        ),
        labelLarge = baseTypography.labelLarge.copy(
            fontSize = baseTypography.labelLarge.fontSize * multiplier,
            lineHeight = baseTypography.labelLarge.lineHeight * multiplier
        ),
        labelMedium = baseTypography.labelMedium.copy(
            fontSize = baseTypography.labelMedium.fontSize * multiplier,
            lineHeight = baseTypography.labelMedium.lineHeight * multiplier
        ),
        labelSmall = baseTypography.labelSmall.copy(
            fontSize = baseTypography.labelSmall.fontSize * multiplier,
            lineHeight = baseTypography.labelSmall.lineHeight * multiplier
        )
    )
}

/**
 * Дефолтная Typography для обратной совместимости
 */
val Typography = baseTypography
