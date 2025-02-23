package eu.ase.travelcompanionapp.hotel.data.mappers

import eu.ase.travelcompanionapp.hotel.data.amadeusApi.AcceptedPaymentsDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.AverageDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.CancellationsDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.ChangesDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.DepositDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.GuestsDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.HotelOffersDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.OffersDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.PoliciesDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.PriceDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.RoomDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.TaxesDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.TypeEstimatedDto
import eu.ase.travelcompanionapp.hotel.data.amadeusApi.VariationsDto
import eu.ase.travelcompanionapp.hotel.domain.model.components.AcceptedPayments
import eu.ase.travelcompanionapp.hotel.domain.model.components.Average
import eu.ase.travelcompanionapp.hotel.domain.model.components.Cancellation
import eu.ase.travelcompanionapp.hotel.domain.model.components.Change
import eu.ase.travelcompanionapp.hotel.domain.model.components.Deposit
import eu.ase.travelcompanionapp.hotel.domain.model.components.Guests
import eu.ase.travelcompanionapp.hotel.domain.model.HotelOffer
import eu.ase.travelcompanionapp.hotel.domain.model.components.Offer
import eu.ase.travelcompanionapp.hotel.domain.model.components.Policies
import eu.ase.travelcompanionapp.hotel.domain.model.components.Price
import eu.ase.travelcompanionapp.hotel.domain.model.components.Room
import eu.ase.travelcompanionapp.hotel.domain.model.components.Tax
import eu.ase.travelcompanionapp.hotel.domain.model.components.TypeEstimated
import eu.ase.travelcompanionapp.hotel.domain.model.components.Variations

fun HotelOffersDto.toHotelOffer(): HotelOffer {
    return HotelOffer(
        type = this.type,
        hotel = this.hotel?.toHotel(),
        available = this.available,
        offers = this.offers.map { it.toOffer() }
    )
}

fun OffersDto.toOffer(): Offer {
    return Offer(
        id = this.id,
        checkInDate = this.checkInDate,
        checkOutDate = this.checkOutDate,
        rateCode = this.rateCode,
        description = this.description?.text,
        boardType = this.boardType,
        room = this.room?.toRoom(),
        guests = this.guests?.toGuests(),
        price = this.price?.toPrice(),
        policies = this.policies?.toPolicies()
    )
}

fun RoomDto.toRoom(): Room {
    return Room(
        type = this.type,
        name = this.name,
        typeEstimated = this.typeEstimated?.toTypeEstimated(),
        description = this.description?.text
    )
}

fun TypeEstimatedDto.toTypeEstimated(): TypeEstimated {
    return TypeEstimated(
        category = this.category,
        beds = this.beds,
        bedType = this.bedType
    )
}

fun GuestsDto.toGuests(): Guests {
    return Guests(
        adults = this.adults
    )
}

fun PriceDto.toPrice(): Price{
    return Price(
        currency = this.currency,
        base = this.base,
        total = this.total,
        taxes = this.taxes.map { it.toTax() },
        variations = this.variations?.toVariations()
    )
}

fun TaxesDto.toTax(): Tax {
    return Tax(
        amount = this.amount,
        currency = this.currency,
        code = this.code,
        pricingFrequency = this.pricingFrequency,
        pricingMode = this.pricingMode,
        percentage = this.percentage,
        included = this.included
    )
}

fun VariationsDto.toVariations(): Variations {
    return Variations(
        average = this.average?.toAverage(),
        changes = this.changes.map { it.toChange() }
    )
}

fun AverageDto.toAverage(): Average{
    return Average(
        base = this.base
    )
}

fun ChangesDto.toChange(): Change {
    return Change(
        startDate = this.startDate,
        endDate = this.endDate,
        total = this.total
    )
}

fun PoliciesDto.toPolicies(): Policies {
    return Policies(
        cancellations = this.cancellations.map { it.toCancellation() },
        deposit = this.deposit?.toDeposit(),
        paymentType = this.paymentType
    )
}

fun CancellationsDto.toCancellation(): Cancellation {
    return Cancellation(
        type = this.type,
        description = this.description?.text
    )
}

fun DepositDto.toDeposit(): Deposit {
    return Deposit(
        acceptedPayments = this.acceptedPayments?.toAcceptedPayments()
    )
}

fun AcceptedPaymentsDto.toAcceptedPayments(): AcceptedPayments {
    return AcceptedPayments(
        creditCards = this.creditCards,
        methods = this.methods
    )
}