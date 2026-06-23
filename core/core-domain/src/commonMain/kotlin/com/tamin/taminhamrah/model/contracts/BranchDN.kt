package com.tamin.taminhamrah.model.contracts

data class BranchDN(
    val code: String?,
    val name: String?,
    val branchAddress: String?,
    val cityCode: String?,
    val minCode: String?,
    val maxCode: String?,
) {
    val displayName: String
        get() = "$name - $branchAddress"
}
