package eu.ase.travelcompanionapp.user.domain.model

data class Currency(
    val code: String,
    val name: String
) {
    companion object {
        private val currencies = mapOf(
            "CAD" to "Canadian Dollar",
            "HRK" to "Croatian Kuna",
            "AUD" to "Australian Dollar",
            "RON" to "Romanian Leu",
            "CHF" to "Swiss Franc",
            "EGP" to "Egyptian Pound",
            "LVL" to "Latvian Lats",
            "ILS" to "Israeli New Sheqel",
            "GBP" to "British Pound Sterling",
            "LBP" to "Lebanese Pound",
            "EUR" to "Euro",
            "RSD" to "Serbian Dinar",
            "INR" to "Indian Rupee",
            "RUB" to "Russian Ruble",
            "BGN" to "Bulgarian Lev",
            "BRL" to "Brazilian Real",
            "NZD" to "New Zealand Dollar",
            "PLN" to "Polish Zloty",
            "IDR" to "Indonesian Rupiah",
            "ALL" to "Albanian Lek",
            "HKD" to "Hong Kong Dollar",
            "QAR" to "Qatari Rial",
            "SAR" to "Saudi Riyal",
            "SEK" to "Swedish Krona",
            "ARS" to "Argentine Peso",
            "CZK" to "Czech Republic Koruna",
            "BTC" to "Bitcoin",
            "IRR" to "Iranian Rial",
            "NOK" to "Norwegian Krone",
            "CNY" to "Chinese Yuan",
            "USD" to "United States Dollar",
            "SGD" to "Singapore Dollar",
            "THB" to "Thai Baht",
            "AED" to "United Arab Emirates Dirham",
            "JPY" to "Japanese Yen",
            "TRY" to "Turkish Lira",
            "HUF" to "Hungarian Forint",
            "DKK" to "Danish Krone"
        )
        
        fun getAll(): List<Currency> {
            return currencies.map { (code, name) ->
                Currency(code, name)
            }.sortedBy { it.code }
        }
        
        fun getCurrencyName(code: String): String {
            return currencies[code] ?: code
        }
    }
} 