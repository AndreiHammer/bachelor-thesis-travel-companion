package eu.ase.travelcompanionapp.booking.presentation.payment

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val scrollState = rememberScrollState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = { result ->
            viewModel.handlePaymentResult(result)
        }
    )

    BackHandler {
        navController.popBackStack()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { 
                    Column {
                        Text(
                            stringResource(R.string.booking_payment),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = stringResource(R.string.complete_your_payment),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    Icon(
                        painter = painterResource(R.drawable.ic_currency),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = bookingDetails != null,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 })
                ) {
                    if (bookingDetails != null) {
                        BookingSummaryCard(bookingDetails!!)
                    }
                }
                
                if (bookingDetails == null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            stringResource(R.string.booking_details_not_available),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                AnimatedContent(
                    targetState = paymentState,
                    transitionSpec = {
                        fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) togetherWith
                        fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow))
                    },
                    label = "Payment State Animation"
                ) { state ->
                    when (state) {
                        is PaymentState.Error -> PaymentErrorState(
                            errorMessage = state.message,
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
                            Column {
                                PaymentStripeHandler(
                                    clientSecret = state.clientSecret,
                                    publishableKey = state.publishableKey,
                                    context = context,
                                    paymentSheet = paymentSheet
                                )
                                PaymentLoadingState(message = stringResource(R.string.preparing_payment_terminal))
                            }
                        }
                    }
                }
            }
        }
    }
}

