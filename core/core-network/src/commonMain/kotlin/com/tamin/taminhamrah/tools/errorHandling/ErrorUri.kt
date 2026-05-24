/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools.errorHandling

enum class ErrorUri {
    ERROR_DOMAIN,
    RESOURCE_NOT_FOUND,
    NO_CONNECTION_ERROR,
    INVALID_REQUEST,
    INVALID_PARAMETER,
    REGISTRATION_ALREADY_REGISTERED,
    INVALID_CREDENTIALS,
    METHOD_NOT_FOUND,
    INTERNAL_ERROR,
    SERVICE_TIMEOUT,
    FORBIDDEN,
    SERVER_ACCESS_DENIED,
    SERVER_ATTEMPTS_LIMIT,
    SERVER_SERVICE_UNAVAILABLE,
    SERVER_ERROR_INVALID_AUTH,
    ERROR_AUTH_PENDING,
    ERROR_WRONG_ANSWER,
    CANNOT_CONNECT,
    REQUESTS_LIMIT,
    ATTEMPTS_LIMIT,
    INTERNAL,
    INVALID_AUTH,
    SERVICE_UNAVAILABLE,
    ALREADY_BOOKMARKED,
    UNAUTHORIZED,
    UNKNOWN
    ;

    companion object {
        fun fromString(value: String? = null): ErrorUri {
            return when (value?.uppercase()) {
                UNAUTHORIZED.name -> UNAUTHORIZED
                ERROR_DOMAIN.name -> ERROR_DOMAIN
                RESOURCE_NOT_FOUND.name -> RESOURCE_NOT_FOUND
                NO_CONNECTION_ERROR.name -> NO_CONNECTION_ERROR
                INVALID_REQUEST.name -> INVALID_REQUEST
                INVALID_PARAMETER.name -> INVALID_PARAMETER
                REGISTRATION_ALREADY_REGISTERED.name -> REGISTRATION_ALREADY_REGISTERED
                INVALID_CREDENTIALS.name -> INVALID_CREDENTIALS
                METHOD_NOT_FOUND.name -> METHOD_NOT_FOUND
                INTERNAL_ERROR.name -> INTERNAL_ERROR
                SERVICE_TIMEOUT.name -> SERVICE_TIMEOUT
                FORBIDDEN.name -> FORBIDDEN
                SERVER_ACCESS_DENIED.name -> SERVER_ACCESS_DENIED
                SERVER_ATTEMPTS_LIMIT.name -> SERVER_ATTEMPTS_LIMIT
                SERVER_SERVICE_UNAVAILABLE.name -> SERVER_SERVICE_UNAVAILABLE
                SERVER_ERROR_INVALID_AUTH.name -> SERVER_ERROR_INVALID_AUTH
                ERROR_AUTH_PENDING.name -> ERROR_AUTH_PENDING
                ERROR_WRONG_ANSWER.name -> ERROR_WRONG_ANSWER
                CANNOT_CONNECT.name -> CANNOT_CONNECT
                REQUESTS_LIMIT.name -> REQUESTS_LIMIT
                ALREADY_BOOKMARKED.name -> ALREADY_BOOKMARKED
                ATTEMPTS_LIMIT.name -> ATTEMPTS_LIMIT
                INTERNAL.name -> INTERNAL
                INVALID_AUTH.name -> INVALID_AUTH
                SERVICE_UNAVAILABLE.name -> SERVICE_UNAVAILABLE
                else -> UNKNOWN
            }
        }
    }
}
