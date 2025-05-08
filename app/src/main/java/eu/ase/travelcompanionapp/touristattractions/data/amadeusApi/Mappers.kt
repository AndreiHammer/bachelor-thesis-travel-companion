package eu.ase.travelcompanionapp.touristattractions.data.amadeusApi

import eu.ase.travelcompanionapp.touristattractions.domain.model.Price
import eu.ase.travelcompanionapp.touristattractions.domain.model.Self
import eu.ase.travelcompanionapp.touristattractions.domain.model.TouristAttraction

fun TouristAttractionDto.toTouristAttraction(): TouristAttraction {
    return TouristAttraction(
        id = this.id,
        type = this.type,
        self = this.self?.toSelf(),
        name = this.name,
        description = this.description,
        latitude = this.geoCode?.latitude,
        longitude = this.geoCode?.longitude,
        rating = this.rating,
        pictures = this.pictures,
        bookingLink = this.bookingLink,
        price = this.price?.toPrice()
    )
}

fun SelfDto.toSelf(): Self {
    return Self(
        href = this.href,
        methods = this.methods
    )
}

fun PriceDto.toPrice(): Price {
    return Price(
        amount = this.amount,
        currencyCode = this.currency
    )
}