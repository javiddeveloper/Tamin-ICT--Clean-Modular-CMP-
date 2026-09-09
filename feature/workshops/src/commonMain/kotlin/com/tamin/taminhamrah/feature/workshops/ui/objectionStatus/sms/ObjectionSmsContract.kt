package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.SmsMessagePR
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType

@Immutable
data class ObjectionSmsUiState(
    val seqNo: Long = 0,
    val debitNumber: String = "",
    val objectionType: WorkShopObjectionType = WorkShopObjectionType.UNKNOWN,
    val objectionStatus: WorkShopObjectionStatus = WorkShopObjectionStatus.UNKNOWN,
    val list: PagedListState<SmsMessagePR> = PagedListState(),
) {
    sealed interface PartialState {
        data class Opened(
            val seqNo: Long,
            val debitNumber: String,
            val objectionType: WorkShopObjectionType,
            val objectionStatus: WorkShopObjectionStatus,
        ) : PartialState

        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<SmsMessagePR>) : PartialState
    }
}

sealed interface ObjectionSmsIntent {
    data class Open(
        val seqNo: Long,
        val debitNumber: String,
        val objectionType: WorkShopObjectionType,
        val objectionStatus: WorkShopObjectionStatus,
    ) : ObjectionSmsIntent

    data object LoadMore : ObjectionSmsIntent
}

sealed interface ObjectionSmsEvent
