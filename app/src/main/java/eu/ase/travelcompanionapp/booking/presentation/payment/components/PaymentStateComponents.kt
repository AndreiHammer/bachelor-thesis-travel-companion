package eu.ase.travelcompanionapp.booking.presentation.payment.components

import android.content.Context
import android.content.res.ColorStateList
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import eu.ase.travelcompanionapp.R

@Composable
fun PaymentErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Text(
        stringResource(R.string.payment_failed, errorMessage),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )

    Row(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onRetry) {
            Text(stringResource(R.string.try_again))
        }

        OutlinedButton(onClick = onCancel) {
            Text(stringResource(R.string.cancel))
        }
    }
}

@Composable
fun PaymentLoadingState(
    message: String = stringResource(R.string.processing_payment_request)
) {
    CircularProgressIndicator()
    Text(
        message,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun PaymentReadyState(
    hasBookingDetails: Boolean,
    onStartPayment: () -> Unit,
    onCancel: () -> Unit
) {
    if (hasBookingDetails) {
        Button(
            onClick = onStartPayment,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                stringResource(R.string.proceed_to_payment),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
    
    OutlinedButton(
        onClick = onCancel,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.cancel))
    }
}

@Composable
fun PaymentSuccessState(
    onReturn: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = stringResource(R.string.success),
        tint = MaterialTheme.colorScheme.primary
    )
    
    Text(
        stringResource(R.string.payment_successful),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 16.dp)
    )
    
    Text(
        stringResource(R.string.your_booking_is_confirmed),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Button(onClick = onReturn) {
        Text(stringResource(R.string.return_to_booking))
    }
}

@Composable
fun PaymentStripeHandler(
    clientSecret: String?,
    publishableKey: String?,
    context: Context,
    paymentSheet: PaymentSheet
) {
    val buttonColor = ColorStateList.valueOf(MaterialTheme.colorScheme.primary.toArgb())
    LaunchedEffect(clientSecret, publishableKey) {
        if (clientSecret != null && publishableKey != null) {
            PaymentConfiguration.init(context, publishableKey)
            
            paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret = clientSecret,
                configuration = PaymentSheet.Configuration(
                    merchantDisplayName = context.getString(R.string.s_c_travel_companion_s_r_l),
                    primaryButtonColor = buttonColor
                )
            )
        }
    }
} 