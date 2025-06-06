package eu.ase.travelcompanionapp.core.domain.resulthandlers

sealed interface DataError: Error {
    enum class Remote: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN,
        NOT_FOUND
    }
    
    enum class Local: DataError {
        QUESTIONNAIRE_NOT_COMPLETED,
        INSUFFICIENT_DATA
    }
}