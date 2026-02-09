package com.trusttheroute.app.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    // Месяцы на русском языке
    private val russianMonths = arrayOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    /**
     * Форматирует дату в формате ISO 8601 в словесно-цифровой формат на русском языке
     * Пример: "2016-06-05T10:30:00Z" -> "5 июня 2016 г."
     * 
     * @param isoDateString Дата в формате ISO 8601 (например, "2016-06-05T10:30:00Z")
     * @return Отформатированная дата в формате "5 июня 2016 г." или null если не удалось распарсить
     */
    fun formatRegistrationDate(isoDateString: String?): String {
        if (isoDateString.isNullOrBlank()) {
            return "Не указано"
        }

        return try {
            // Пробуем разные форматы ISO 8601
            val date = parseIsoDate(isoDateString)
            if (date != null) {
                formatToRussianDate(date)
            } else {
                "Не указано"
            }
        } catch (e: Exception) {
            android.util.Log.e("DateFormatter", "Ошибка форматирования даты: $isoDateString", e)
            "Не указано"
        }
    }

    /**
     * Парсит ISO 8601 дату в разные форматы
     */
    private fun parseIsoDate(isoDateString: String): Date? {
        // Различные форматы ISO 8601
        val formats = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                return sdf.parse(isoDateString)
            } catch (e: Exception) {
                // Пробуем следующий формат
                continue
            }
        }
        return null
    }

    /**
     * Форматирует Date в русский формат "5 июня 2016 г."
     */
    private fun formatToRussianDate(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) // 0-11
        val year = calendar.get(Calendar.YEAR)

        val monthName = russianMonths[month]
        return "$day $monthName $year г."
    }
}
