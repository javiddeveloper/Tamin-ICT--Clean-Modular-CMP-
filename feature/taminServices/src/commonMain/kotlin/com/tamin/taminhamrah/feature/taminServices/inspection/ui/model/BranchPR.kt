package com.tamin.taminhamrah.feature.taminServices.inspection.ui.model

data class BranchPR(
    val operation: String,
    val code: String,
    val name: String,
    val minCode: String,
    val maxCode: String,
    val type: String,
    val branchAddress: String,
    val cityCode: String,
    val status: String
)
