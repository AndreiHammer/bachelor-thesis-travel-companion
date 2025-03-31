package eu.ase.travelcompanionapp.hotel.presentation.hotelFavourites.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.Bitmap
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.hotel.domain.model.HotelPrice
import eu.ase.travelcompanionapp.hotel.domain.model.HotelWithBookingDetails
import kotlinx.coroutines.delay

@Composable
fun FavouriteHotelItemWithDelete(
    hotelWithDetails: HotelWithBookingDetails,
    hotelImage: Bitmap? = null,
    priceInfo:  HotelPrice?,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRemoved by remember { mutableStateOf(false) }

    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            delay(300)
            onDelete()
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Box(modifier = modifier.fillMaxWidth()) {
            FavouriteHotelItem(
                hotelWithDetails = hotelWithDetails,
                priceInfo = priceInfo,
                onClick = onClick,
                hotelImage = hotelImage,
                modifier = Modifier.fillMaxWidth()
            )
            IconButton(
                onClick = { isRemoved = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}