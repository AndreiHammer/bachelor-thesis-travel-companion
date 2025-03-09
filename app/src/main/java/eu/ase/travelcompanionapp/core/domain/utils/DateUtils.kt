package eu.ase.travelcompanionapp.core.domain.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())
    private val apiFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun convertMillisToLocalDate(millis: Long): ZonedDateTime {
        val instant = Instant.ofEpochMilli(millis)
        return instant.atZone(ZoneId.systemDefault())
    }

    fun dateToString(date: ZonedDateTime): String {
        return displayFormatter.format(date)
    }

    fun displayDateToApiFormat(displayDate: String): String {
        val localDate = LocalDate.parse(displayDate, displayFormatter)
        return localDate.format(apiFormatter)
    }

    fun getCurrentAndNextDayDates(): Pair<String, String> {
        val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
        val tomorrow = today.plusDays(1)
        
        return Pair(
            dateToString(today),
            dateToString(tomorrow)
        )
    }

    fun parseDisplayDate(displayDate: String): ZonedDateTime? {
        return try {
            val localDate = LocalDate.parse(displayDate, displayFormatter)
            localDate.atStartOfDay(ZoneId.systemDefault())
        } catch (e: Exception) {
            null
        }
    }
}