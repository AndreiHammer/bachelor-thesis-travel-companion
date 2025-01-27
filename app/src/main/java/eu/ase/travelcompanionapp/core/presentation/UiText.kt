package eu.ase.travelcompanionapp.core.presentation

import android.content.Context

sealed interface UiText {
    data class DynamicString(val value: String): UiText
    class StringResourceId(
        val id: Int,
    ): UiText

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> value
            is StringResourceId -> context.getString(id)
        }
    }
}