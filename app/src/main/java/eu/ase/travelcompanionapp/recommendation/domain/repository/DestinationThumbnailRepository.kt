package eu.ase.travelcompanionapp.recommendation.domain.repository

import coil3.Bitmap
import eu.ase.travelcompanionapp.recommendation.domain.model.Destination

interface DestinationThumbnailRepository {
    suspend fun getDestinationThumbnail(destination: Destination): Bitmap?
} 