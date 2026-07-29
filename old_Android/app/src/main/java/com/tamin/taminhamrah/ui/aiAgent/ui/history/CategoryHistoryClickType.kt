package com.tamin.taminhamrah.ui.aiAgent.ui.history

import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory

sealed interface CategoryHistoryClickType {
    data class Edit(val item: AiHistoryCategory) : CategoryHistoryClickType
    data class Delete(val id: String) : CategoryHistoryClickType
    data class LoadItems(val id: String) : CategoryHistoryClickType

}