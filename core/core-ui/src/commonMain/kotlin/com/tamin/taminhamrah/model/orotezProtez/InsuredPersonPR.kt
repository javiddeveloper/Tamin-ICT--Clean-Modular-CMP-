package com.tamin.taminhamrah.model.orotezProtez

import androidx.compose.runtime.Immutable

@Immutable
data class InsuredPersonPR(
    val id: String,
    val label: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val relation: String,
    val relationCode: String,
    val nationalCode: String,
    val birthCertificateNumber: String,
    val issuePlace: String,
    val birthDateLabel: String,
    val bookletValidUntilLabel: String,
)
