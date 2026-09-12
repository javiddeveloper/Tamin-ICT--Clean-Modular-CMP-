package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionPR

/**
 * State of پیگیری وضعیت اعتراض.
 *
 * Unlike every other list under کارگاه‌های کارفرما, this one has no required workshop/branch
 * identity — it is reached straight from the services menu (`FeatureFlag.FOLLOW_PROTEST_STATUS`),
 * not from a picked workshop row, and shows every objection the employer has filed. [draft] is what
 * the search sheet is editing; [applied] is what the visible page was fetched with.
 */
@Immutable
data class ObjectionStatusUiState(
    val list: PagedListState<WorkShopObjectionPR> = PagedListState(),
    val totalCount: Int = 0,
    val identityName: String = "",
    val identityNationalId: String = "",
    val draft: ObjectionStatusFilters = ObjectionStatusFilters(),
    val applied: ObjectionStatusFilters = ObjectionStatusFilters(),
    val isSearchOpen: Boolean = false,
) {
    sealed interface PartialState {
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkShopObjectionPR>, val total: Int) : PartialState
        data class DraftChanged(val draft: ObjectionStatusFilters) : PartialState
        data class Applied(val filters: ObjectionStatusFilters) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class IdentityLoaded(val name: String, val nationalId: String) : PartialState
    }
}

/** The three optional filters of جستجو در اعتراض‌ها. All optional, all matched exactly. */
@Immutable
data class ObjectionStatusFilters(
    val objectionNumber: String = "",
    val workshopId: String = "",
    val debitNumber: String = "",
) {
    val isNotEmpty: Boolean
        get() = objectionNumber.isNotBlank() || workshopId.isNotBlank() || debitNumber.isNotBlank()
}

sealed interface ObjectionStatusIntent {
    data object Load : ObjectionStatusIntent
    data object LoadMore : ObjectionStatusIntent
    data class SearchOpenChanged(val isOpen: Boolean) : ObjectionStatusIntent
    data class DraftChanged(val draft: ObjectionStatusFilters) : ObjectionStatusIntent
    data object ApplyFilters : ObjectionStatusIntent
    data object ClearFilters : ObjectionStatusIntent

    /** Removes just one applied filter field, from a chip's own "حذف". */
    data class RemoveFilter(val field: ObjectionStatusFilterField) : ObjectionStatusIntent
}

enum class ObjectionStatusFilterField { OBJECTION_NUMBER, WORKSHOP_ID, DEBIT_NUMBER }

sealed interface ObjectionStatusEvent
