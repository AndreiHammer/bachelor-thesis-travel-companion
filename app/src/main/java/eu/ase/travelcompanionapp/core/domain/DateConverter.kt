package eu.ase.travelcompanionapp.core.domain

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateConverter {
    fun convertMillisToLocalDate(millis: Long): ZonedDateTime {
        // Interpret the milliseconds as the start of the day in UTC, then convert to Los Angeles time
        val utcDateAtStartOfDay = Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        // Convert to the same instant in Local time zone
        val localDate = utcDateAtStartOfDay.atStartOfDay(ZoneId.systemDefault())

        return localDate

    }

    fun dateToString(date: ZonedDateTime): String {
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())
        return dateFormatter.format(date)
    }
}