package eu.ase.travelcompanionapp.hotel.presentation.hotelOffers.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.ase.travelcompanionapp.R
import eu.ase.travelcompanionapp.user.domain.model.Currency

@Composable
fun PriceDisplay(
    originalCurrency: String?,
    originalAmount: String?,
    convertedPrice: Currency?,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    val showShimmer = convertedPrice == null && originalCurrency != null && originalAmount != null

    if (showShimmer) {
        ShimmerPricePlaceholder(style = style)
    } else {
        Box {
            val showOriginal = originalCurrency != null && originalAmount != null
            val showConverted = convertedPrice != null &&
                    convertedPrice.code != convertedPrice.originalCurrency

            if (showOriginal && !showConverted) {
                Text(
                    text = "$originalCurrency $originalAmount",
                    style = style,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            AnimatedVisibility(
                visible = showConverted,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                convertedPrice?.let {
                    Text(
                        text = "${it.code} ${String.format(stringResource(R.string._2f), it.convertedAmount)}",
                        style = style,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun ShimmerPricePlaceholder(style: TextStyle) {
    val transition = rememberInfiniteTransition(label = stringResource(R.string.shimmer))
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        ),
        label = stringResource(R.string.shimmer_alpha)
    )

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(style.fontSize.value.dp)
            .alpha(alpha)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            )
    )
}