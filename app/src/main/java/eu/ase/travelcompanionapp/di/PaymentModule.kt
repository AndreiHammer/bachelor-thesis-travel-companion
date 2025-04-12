package eu.ase.travelcompanionapp.di

import eu.ase.travelcompanionapp.payment.data.FirebasePaymentService
import eu.ase.travelcompanionapp.payment.data.PaymentRepositoryImpl
import eu.ase.travelcompanionapp.payment.data.PaymentService
import eu.ase.travelcompanionapp.payment.data.booking.BookingServiceImpl
import eu.ase.travelcompanionapp.payment.domain.repository.BookingService
import eu.ase.travelcompanionapp.payment.domain.repository.PaymentRepository
import eu.ase.travelcompanionapp.payment.presentation.PaymentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val paymentModule = module {
    single<BookingService> { BookingServiceImpl(get()) }
    single<PaymentService> { FirebasePaymentService() }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
    viewModel { PaymentViewModel(get()) }
}