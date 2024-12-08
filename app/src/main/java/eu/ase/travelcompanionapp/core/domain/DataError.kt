package eu.ase.travelcompanionapp.core.domain

sealed interface DataError: Error {
    enum class Remote: DataError {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        TOO_MANY_REQUESTS,
        SERIALIZATION,
        SERVER,
        UNKNOWN
    }

    enum class Local: DataError {
        DISK_FULL,
        UNKNOWN
    }

}