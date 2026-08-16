package com.tamin.taminhamrah.model.bankAccount

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The register body, spelled exactly as the service reads it.
 *
 * `accounttype` is one word with a lower-case `t`. That is the service's spelling — [BankAccountDTO]
 * carries the same one — and it is not to be "corrected".
 *
 * [dateOfStart] is epoch milliseconds as a string, which is what the previous app sent and what the
 * service echoes back as a number.
 */
@Serializable
data class BankAccountRequestDTO(
    @SerialName("accountNumber") val accountNumber: String,
    @SerialName("bank") val bank: String,
    @SerialName("accounttype") val accountType: String,
    @SerialName("dateOfStart") val dateOfStart: String,
)

/**
 * What comes back from a successful register.
 *
 * Deliberately not [BankAccountDTO]: the create response spells `bank` and `accounttype` as plain
 * code strings, where the list endpoint returns them as objects.
 *
 * Registering files a request titled «درخواست اعلام شماره حساب بانکی» rather than inserting an
 * account — a freshly created one does not come back from the list endpoint. The tracking code is
 * therefore the only thing the user can be shown afterward, which is why it is read out of the
 * otherwise ignored `personal` envelope.
 */
@Serializable
data class BankAccountCreatedDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("accountNumber") val accountNumber: String? = null,
    @SerialName("personal") val personal: BankAccountCreatedPersonalDTO? = null,
)

@Serializable
data class BankAccountCreatedPersonalDTO(
    @SerialName("refrenceCode") val referenceCode: String? = null,
)
