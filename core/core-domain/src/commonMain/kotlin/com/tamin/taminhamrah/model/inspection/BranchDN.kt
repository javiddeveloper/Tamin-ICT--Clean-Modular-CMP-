package com.tamin.taminhamrah.model.inspection

data class BranchDN(
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
