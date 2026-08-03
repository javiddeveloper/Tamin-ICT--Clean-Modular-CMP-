package com.tamin.taminhamrah.feature.profile.ui.versionHistory

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryEvent
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryIntent
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryItem
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryUiState
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryUiState.PartialState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VersionHistoryViewModel : BaseViewModel<VersionHistoryUiState, PartialState, VersionHistoryEvent, VersionHistoryIntent>(
    initialState = VersionHistoryUiState(
        items = getMockVersionHistory()
    )
) {

    override fun handleIntent(intent: VersionHistoryIntent): Flow<PartialState> {
        return when (intent) {
            VersionHistoryIntent.LoadVersionHistory -> handleLoadVersionHistory()
            is VersionHistoryIntent.ToggleExpand -> flow { emit(PartialState.ToggleExpand(intent.version)) }
            VersionHistoryIntent.OnBackClicked -> flow { sendEvent(VersionHistoryEvent.NavigateBack) }
        }
    }

    private fun handleLoadVersionHistory(): Flow<PartialState> = flow {
        emit(PartialState.SetLoading(true))
        emit(PartialState.SetItems(getMockVersionHistory()))
        emit(PartialState.SetLoading(false))
    }

    override fun reduceState(
        currentState: VersionHistoryUiState,
        partialState: PartialState
    ): VersionHistoryUiState = when (partialState) {
        is PartialState.SetLoading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.SetItems -> currentState.copy(items = partialState.items)
        is PartialState.ToggleExpand -> {
            val updatedItems = currentState.items.map { item ->
                if (item.version == partialState.version) {
                    item.copy(isExpanded = !item.isExpanded)
                } else {
                    item
                }
            }.toImmutableList()
            currentState.copy(items = updatedItems)
        }
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.SetLoading(false)

    companion object {
        private fun getMockVersionHistory() = listOf(
            VersionHistoryItem(
                version = "1.12.3",
                releaseDate = "یکشنبه ۳۰ فروردین ۱۴۰۵",
                isLatest = true,
                categoryTitle = "رفع اشکال و بهینه‌سازی",
                changes = listOf(
                    "بهبود فرایند ورود به اپلیکیشن",
                    "بهبود رابط کاربری",
                    "رفع برخی مشکلات گزارش‌شده"
                ),
                isExpanded = true
            ),
            VersionHistoryItem(
                version = "1.12.2",
                releaseDate = "شنبه ۴ بهمن ۱۴۰۴",
                isLatest = false,
                categoryTitle = "بهینه‌سازی و بهبود کارایی",
                changes = listOf(
                    "افزایش سرعت دریافت استعلام‌ها",
                    "بهبود پایداری اتصال به سامانه"
                ),
                isExpanded = false
            ),
            VersionHistoryItem(
                version = "1.12.0",
                releaseDate = "یکشنبه ۱۰ تیر ۱۴۰۴",
                isLatest = false,
                categoryTitle = "ارائه خدمات جدید",
                changes = listOf(
                    "افزودن امکان پیگیری پرونده الکترونیک",
                    "بهبود و توسعه خدمات سلامت و درمان"
                ),
                isExpanded = false
            )
        ).toImmutableList()
    }
}
