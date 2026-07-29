package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param

sealed interface DeepLinkData {
    data class Patient(
        val noteHeadEprescID: Long?,
        val prescType: String?,
        val nationalCode: String,
        val childNationalCode: String,
        val flagSata: String?,
        val prescName: String?,
        val docName: String?,
        val iconRes: Int?
    ) : DeepLinkData

}