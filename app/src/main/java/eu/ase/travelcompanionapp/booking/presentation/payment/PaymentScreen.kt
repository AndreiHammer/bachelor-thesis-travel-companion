package eu.ase.travelcompanionapp.booking.presentation.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.stripe.android.paymentsheet.rememberPaymentSheet
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.booking.presentation.payment.components.BookingSummaryCard
import eu.ase.travelcompanionapp.booking.presentation.payment.components.PaymentErrorState
import eu.ase.travelcompanionapp.booking.presentation.payment.components.PaymentLoadingState
import eu.ase.travelcompanionapp.booking.presentation.payment.components.PaymentReadyState
import eu.ase.travelcompanionapp.booking.presentation.payment.components.PaymentStripeHandler
import eu.ase.travelcompanionapp.booking.presentation.payment.components.PaymentSuccessState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = koinViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val paymentState by viewModel.paymentState.collectAsState()
    val bookingDetails by viewModel.currentBooking.collectAsState()

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            viewModel.handlePaymentResult(result)
        }
    )

    BackHandler {
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking_payment)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display booking summary or error
            if (bookingDetails != null) {
                BookingSummaryCard(bookingDetails!!)
            } else {
                Text(
                    stringResource(R.string.booking_details_not_available),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Payment state handling
            when (paymentState) {
                is PaymentState.Error -> PaymentErrorState(
                    errorMessage = (paymentState as PaymentState.Error).message,
                    onRetry = { viewModel.startPayment() },
                    onCancel = { navController.popBackStack() }
                )
                
                PaymentState.Loading -> PaymentLoadingState()
                
                PaymentState.Ready -> PaymentReadyState(
                    hasBookingDetails = bookingDetails != null,
                    onStartPayment = { viewModel.startPayment() },
                    onCancel = { navController.popBackStack() }
                )
                
                PaymentState.Success -> PaymentSuccessState(
                    onReturn = { navController.popBackStack() }
                )
                
                is PaymentState.ClientSecretReceived -> {
                    val clientSecretState = paymentState as PaymentState.ClientSecretReceived
                    PaymentStripeHandler(
                        clientSecret = clientSecretState.clientSecret,
                        publishableKey = clientSecretState.publishableKey,
                        context = context,
                        paymentSheet = paymentSheet
                    )
                    PaymentLoadingState(message = stringResource(R.string.preparing_payment_terminal))
                }
            }
        }
    }
}

