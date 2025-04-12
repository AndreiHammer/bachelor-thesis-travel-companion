package eu.ase.travelcompanionapp.payment.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.payment.domain.models.BookingInfo

@Composable
fun BookingSummaryCard(bookingDetails: BookingInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.booking_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            BookingDetailRow(stringResource(R.string.hotel), bookingDetails.hotelName ?: "")
            BookingDetailRow(stringResource(R.string.check_in), bookingDetails.checkInDate?: stringResource(R.string.not_specified))
            BookingDetailRow(stringResource(R.string.check_out), bookingDetails.checkOutDate?: stringResource(R.string.not_specified))
            BookingDetailRow(stringResource(R.string.guests), "${bookingDetails.guests}")
            BookingDetailRow(stringResource(R.string.room_type), bookingDetails.roomType ?: stringResource(R.string.standard_room))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                stringResource(
                    R.string.total,
                    formatCurrency(bookingDetails.amount, bookingDetails.currency)
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BookingDetailRow(label: String, value: String) {
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
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatCurrency(amount: Long, currency: String): String {
    return "${amount / 100.0} $currency"
}