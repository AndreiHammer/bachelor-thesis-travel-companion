package eu.ase.travelcompanionapp.booking.presentation.bookinghistory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.booking.domain.models.BookingInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookingHistoryItem(
    booking: BookingInfo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = booking.hotelName ?: stringResource(R.string.unknown_hotel),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Booking #${booking.bookingReference.takeLast(6)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            BookingDetailRow(
                label = stringResource(R.string.check_in),
                value = booking.checkInDate ?: stringResource(R.string.not_specified)
            )

            BookingDetailRow(
                label = stringResource(R.string.check_out),
                value = booking.checkOutDate ?: stringResource(R.string.not_specified)
            )
            
            BookingDetailRow(
                label = stringResource(R.string.room_type),
                value = booking.roomType ?: stringResource(R.string.standard_room)
            )
            
            BookingDetailRow(
                label = stringResource(R.string.guests),
                value = booking.guests.toString()
            )
            
            BookingDetailRow(
                label = stringResource(R.string.total_booking),
                value = formatCurrency(booking.amount, booking.currency),
                isHighlighted = true
            )
            
            if (booking.paymentStatus.isNotEmpty()) {
                BookingDetailRow(
                    label = stringResource(R.string.status),
                    value = booking.paymentStatus,
                    isHighlighted = booking.paymentStatus == "COMPLETED"
                )
            }
            
            Text(
                text = stringResource(R.string.booked_on, formatDate(booking.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun BookingDetailRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatCurrency(amount: Long, currency: String): String {
    return "${amount / 100.0} $currency"
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
} 