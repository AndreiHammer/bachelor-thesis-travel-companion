package eu.ase.travelcompanionapp.core.data.localDB

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class UserEntity(
    @Id var id: Long = 0,
    @Unique var firebaseId: String = "",
    var email: String = "",
    var name: String = "",
    var birthDate: String = "",
    var gender: String = "",
    var phoneNumber: String = ""
)