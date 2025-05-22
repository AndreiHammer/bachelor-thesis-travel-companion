package eu.ase.travelcompanionapp.hotel.presentation.hotelDetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.core.domain.utils.DateUtils
import eu.ase.travelcompanionapp.core.presentation.DatePickerWithDialog
import eu.ase.travelcompanionapp.hotel.domain.model.BookingDetails
import eu.ase.travelcompanionapp.hotel.presentation.hotelSearch.components.custom.GuestSelector

@Composable
fun BookingDetailsDialog(
    initialBookingDetails: BookingDetails? = null,
    title: String = stringResource(R.string.enter_booking_details),
    description: String = stringResource(R.string.please_enter_booking_details_to_view_offers),
    confirmButtonText: String = stringResource(R.string.view_offers),
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    val dateUtils = remember { DateUtils() }
    val today = remember { System.currentTimeMillis() }

    var checkInDate by remember { mutableStateOf(initialBookingDetails?.checkInDate ?: "") }
    var checkOutDate by remember { mutableStateOf(initialBookingDetails?.checkOutDate ?: "") }
    var adults by remember { mutableIntStateOf(initialBookingDetails?.adults ?: 1) }
    
    var isFormValid by remember { mutableStateOf(false) }

    isFormValid = checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && adults > 0
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DatePickerWithDialog(
                        label = stringResource(R.string.check_in_date),
                        selectedDate = checkInDate,
                        minDate = today,
                        onDateSelected = { dateString ->
                            checkInDate = dateString
                            if (checkOutDate.isNotEmpty()) {
                                val checkInDateTime = dateUtils.parseDisplayDate(dateString)
                                val checkOutDateTime = dateUtils.parseDisplayDate(checkOutDate)
                                if (checkOutDateTime != null && checkInDateTime != null &&
                                    !checkOutDateTime.isAfter(checkInDateTime)) {
                                    checkOutDate = ""
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    DatePickerWithDialog(
                        label = stringResource(R.string.check_out_date),
                        selectedDate = checkOutDate,
                        minDate = if (checkInDate.isNotEmpty()) {
                            val checkInDateTime = dateUtils.parseDisplayDate(checkInDate)
                            checkInDateTime?.plusDays(1)?.toInstant()?.toEpochMilli() ?: today
                        } else {
                            dateUtils.convertMillisToLocalDate(today).plusDays(1).toInstant().toEpochMilli()
                        },
                        onDateSelected = { dateString ->
                            checkOutDate = dateString
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.number_of_guests_colon),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    GuestSelector(
                        value = adults,
                        onValueChange = { adults = it }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Button(
                        onClick = { onConfirm(checkInDate, checkOutDate, adults) },
                        enabled = isFormValid,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = confirmButtonText)
                    }
                }
            }
        }
    }
} 