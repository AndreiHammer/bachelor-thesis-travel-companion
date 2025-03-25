package eu.ase.travelcompanionapp.user.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.ase.travelcompanionapp.R

@Composable
fun CurrencyIcon(
    currencyCode: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val iconColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        when {
            currencyCode.isEmpty() -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_currency),
                    contentDescription = contentDescription ?: "Currency",
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            currencyCode == "USD" -> CurrencyText("$", iconColor)
            currencyCode == "EUR" -> CurrencyText("€", iconColor)
            currencyCode == "GBP" -> CurrencyText("£", iconColor)
            currencyCode == "JPY" -> CurrencyText("¥", iconColor)
            currencyCode == "RON" -> CurrencyText("L", iconColor)
            currencyCode == "RUB" -> CurrencyText("₽", iconColor)
            currencyCode == "INR" -> CurrencyText("₹", iconColor)
            currencyCode == "CNY" -> CurrencyText("¥", iconColor)
            currencyCode == "CHF" -> CurrencyText("Fr", iconColor)
            currencyCode == "TRY" -> CurrencyText("₺", iconColor)
            currencyCode == "BTC" -> CurrencyText("₿", iconColor)
            else -> {
                CurrencyText(currencyCode.take(1), iconColor)
            }
        }
    }
}

@Composable
private fun CurrencyText(symbol: String, color: Color) {
    Text(
        text = symbol,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(2.dp)
    )
} 