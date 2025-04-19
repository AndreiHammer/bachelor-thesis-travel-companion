package eu.ase.travelcompanionapp.booking.presentation.bookinghistory.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BookingFormatter {

    @SuppressLint("DefaultLocale")
    fun formatCurrency(amount: Long, currency: String): String {
        return String.format("%.2f %s", amount / 100.0, currency)
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDisplayDate(dateString: String?): String? {
        if (dateString.isNullOrEmpty()) return null
        return try {
            dateString
        } catch (e: Exception) {
            dateString
        }
    }

    fun getGuestText(guestCount: Int): String {
        return "$guestCount ${if (guestCount > 1) "guests" else "guest"}"
    }

    fun formatBookingReference(reference: String): String {
        return "#${reference.takeLast(6)}"
    }
} 