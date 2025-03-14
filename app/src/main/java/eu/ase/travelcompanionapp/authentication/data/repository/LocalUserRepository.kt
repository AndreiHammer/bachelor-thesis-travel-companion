package eu.ase.travelcompanionapp.authentication.data.repository

import eu.ase.travelcompanionapp.authentication.domain.model.User
import eu.ase.travelcompanionapp.core.data.localDB.UserEntity
import io.objectbox.Box

class LocalUserRepository(
    private val userBox: Box<UserEntity>
) {
    fun findUserByFirebaseId(firebaseId: String): UserEntity? {
        return userBox.all.firstOrNull { it.firebaseId == firebaseId }
    }

    private fun User.toEntity(): UserEntity {
        return UserEntity(
            firebaseId = id,
            email = email,
            name = name,
            birthDate = birthDate,
            gender = gender,
            phoneNumber = phoneNumber
        )
    }

    fun UserEntity.toUser(): User {
        return User(
            id = firebaseId,
            email = email,
            name = name,
            birthDate = birthDate,
            gender = gender,
            phoneNumber = phoneNumber
        )
    }

    fun saveUser(user: User) {
        val existingUser = findUserByFirebaseId(user.id)
        if (existingUser != null) {
            existingUser.apply {
                email = user.email
                name = user.name
                birthDate = user.birthDate
                gender = user.gender
                phoneNumber = user.phoneNumber
            }
            userBox.put(existingUser)
        } else {
            userBox.put(user.toEntity())
        }
    }

    fun deleteUser(firebaseId: String) {
        findUserByFirebaseId(firebaseId)?.let { userBox.remove(it) }
    }
}