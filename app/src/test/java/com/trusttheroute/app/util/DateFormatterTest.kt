package com.trusttheroute.app.util

import org.junit.Assert.*
import org.junit.Test

class DateFormatterTest {

    @Test
    fun `test formatRegistrationDate with valid ISO 8601 date`() {
        val isoDate = "2016-06-05T10:30:00Z"
        val formatted = DateFormatter.formatRegistrationDate(isoDate)
        
        assertTrue("Should format date correctly", formatted.contains("июня"))
        assertTrue("Should contain year", formatted.contains("2016"))
        assertTrue("Should contain day", formatted.contains("5"))
    }

    @Test
    fun `test formatRegistrationDate with ISO 8601 date with milliseconds`() {
        val isoDate = "2024-01-15T14:30:00.000Z"
        val formatted = DateFormatter.formatRegistrationDate(isoDate)
        
        assertTrue("Should format date correctly", formatted.contains("января"))
        assertTrue("Should contain year", formatted.contains("2024"))
    }

    @Test
    fun `test formatRegistrationDate with date without time`() {
        val isoDate = "2024-12-25"
        val formatted = DateFormatter.formatRegistrationDate(isoDate)
        
        assertTrue("Should format date correctly", formatted.contains("декабря"))
        assertTrue("Should contain year", formatted.contains("2024"))
    }

    @Test
    fun `test formatRegistrationDate with null returns default`() {
        val formatted = DateFormatter.formatRegistrationDate(null)
        assertEquals("Should return default for null", "Не указано", formatted)
    }

    @Test
    fun `test formatRegistrationDate with empty string returns default`() {
        val formatted = DateFormatter.formatRegistrationDate("")
        assertEquals("Should return default for empty string", "Не указано", formatted)
    }

    @Test
    fun `test formatRegistrationDate with blank string returns default`() {
        val formatted = DateFormatter.formatRegistrationDate("   ")
        assertEquals("Should return default for blank string", "Не указано", formatted)
    }

    @Test
    fun `test formatRegistrationDate with invalid date returns default`() {
        val formatted = DateFormatter.formatRegistrationDate("invalid-date")
        assertEquals("Should return default for invalid date", "Не указано", formatted)
    }

    @Test
    fun `test formatRegistrationDate formats all months correctly`() {
        val testCases = mapOf(
            "2024-01-15" to "января",
            "2024-02-15" to "февраля",
            "2024-03-15" to "марта",
            "2024-04-15" to "апреля",
            "2024-05-15" to "мая",
            "2024-06-15" to "июня",
            "2024-07-15" to "июля",
            "2024-08-15" to "августа",
            "2024-09-15" to "сентября",
            "2024-10-15" to "октября",
            "2024-11-15" to "ноября",
            "2024-12-15" to "декабря"
        )

        testCases.forEach { (date, expectedMonth) ->
            val formatted = DateFormatter.formatRegistrationDate(date)
            assertTrue("Date $date should contain $expectedMonth", formatted.contains(expectedMonth))
        }
    }
}
