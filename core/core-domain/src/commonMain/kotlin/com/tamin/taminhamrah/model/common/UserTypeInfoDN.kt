package com.tamin.taminhamrah.model.common

/** [message] is an optional server-supplied override, used for the [UserType.PENSIONER] denial dialog. */
data class UserTypeInfoDN(
    val userType: UserType,
    val message: String? = null,
)
