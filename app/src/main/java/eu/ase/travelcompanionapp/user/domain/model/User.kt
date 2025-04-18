package eu.ase.travelcompanionapp.user.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val phoneNumber: String = ""
)
