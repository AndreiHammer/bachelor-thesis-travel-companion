package eu.ase.travelcompanionapp.user.data.repository

import eu.ase.travelcompanionapp.user.domain.model.User
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
        try {
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
                val newEntity = user.toEntity()
                userBox.put(newEntity)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun deleteUser(firebaseId: String) {
        findUserByFirebaseId(firebaseId)?.let { userBox.remove(it) }
    }
}