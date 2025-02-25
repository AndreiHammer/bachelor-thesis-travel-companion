package eu.ase.travelcompanionapp.core.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateConverter {
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
}