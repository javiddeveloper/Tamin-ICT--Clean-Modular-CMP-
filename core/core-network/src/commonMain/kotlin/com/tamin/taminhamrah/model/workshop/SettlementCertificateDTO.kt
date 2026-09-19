package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One مفاصاحساب ماده ۳۸ of `workshop-services/mad38-head/{workshop}/{branch}/{row}/-/-`.
 *
 * The old app's `Clause38Info`. Only [clearanceSerial] is needed downstream — it addresses the
 * detail call that carries the certificate's number and date — and [contractNumber] picks the
 * right row when one ردیف holds more than one پیمان. Every field is nullable with a default, so a
 * column the service leaves out is simply absent rather than a decoding failure.
 */
@Serializable
data class SettlementCertificateDTO(
    @SerialName("clearanceSerial") val clearanceSerial: String? = null,
    @SerialName("contractNumber") val contractNumber: String? = null,
    @SerialName("contractRow") val contractRow: String? = null,
)

/**
 * The certificate itself, from `workshop-services/mad38-detail/{workshop}/{branch}/{row}/{serial}`.
 *
 * The old app's `Clause38Detail` declares sixty columns; these three are what «گواهی صادرشده»
 * reports. [clearanceDate] is compact Jalali (`14020103`), separated at the presentation edge.
 */
@Serializable
data class SettlementCertificateDetailDTO(
    @SerialName("clearanceSerial") val clearanceSerial: String? = null,
    @SerialName("clearanceNumber") val clearanceNumber: String? = null,
    @SerialName("clearanceDate") val clearanceDate: String? = null,
)
