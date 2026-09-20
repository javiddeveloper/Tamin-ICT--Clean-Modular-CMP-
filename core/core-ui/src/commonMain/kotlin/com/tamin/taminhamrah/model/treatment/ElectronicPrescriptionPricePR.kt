package com.tamin.taminhamrah.model.treatment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * One record's price, as `patient-history/price` reports it.
 *
 * The field names do not say whose share is whose, and the KMP port originally read them the wrong
 * way round (the legacy app displayed them correctly despite its confusing view-binding names).
 * Checked against live data: every item's `ssoPayment` is its patient share, and they add up to
 * [headInsuPayment] exactly, while [headSsoPayment] is [requestPrice] less that.
 */
@Immutable
@Serializable
data class ElectronicPrescriptionPricePR(
    /** «سهم شما» — the insured person's share of the whole record. */
    val headInsuPayment: String,
    /** «سهم سازمان» — the organization's share of the whole record. */
    val headSsoPayment: String,
    val noteHeadEprescID: String,
    /** «جمع کل» — can exceed the items added up, since it carries charges no single item does. */
    val requestPrice: String
)
