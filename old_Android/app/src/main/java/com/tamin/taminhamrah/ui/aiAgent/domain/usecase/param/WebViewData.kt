package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param

sealed interface WebViewData {

    data class Appointment(val url : String)

}