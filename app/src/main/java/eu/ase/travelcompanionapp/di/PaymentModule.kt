package eu.ase.travelcompanionapp.di

import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import eu.ase.travelcompanionapp.booking.data.payment.FirebasePaymentService
import eu.ase.travelcompanionapp.booking.data.payment.PaymentRepositoryImpl
import eu.ase.travelcompanionapp.booking.data.payment.PaymentService
import eu.ase.travelcompanionapp.booking.data.BookingServiceImpl
import eu.ase.travelcompanionapp.booking.data.repository.BookingRecordRepositoryImpl
import eu.ase.travelcompanionapp.booking.domain.repository.BookingRecordRepository
import eu.ase.travelcompanionapp.booking.domain.repository.BookingService
import eu.ase.travelcompanionapp.booking.domain.repository.PaymentRepository
import eu.ase.travelcompanionapp.booking.presentation.payment.PaymentViewModel
import eu.ase.travelcompanionapp.booking.presentation.bookinghistory.BookingHistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val paymentModule = module {
    single<BookingService> { BookingServiceImpl(get()) }
    single<PaymentService> { FirebasePaymentService() }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
    single { FirebaseFirestore.getInstance() }
    single<BookingRecordRepository> { BookingRecordRepositoryImpl(get(), get()) }
    viewModelOf(::PaymentViewModel)
    
    viewModel { (navController: NavController) ->
        BookingHistoryViewModel(
            bookingRecordRepository = get(),
            navController = navController
        )
    }
}