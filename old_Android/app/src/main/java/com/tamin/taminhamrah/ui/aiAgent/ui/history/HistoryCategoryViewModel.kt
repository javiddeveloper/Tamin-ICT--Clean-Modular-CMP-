package com.tamin.taminhamrah.ui.aiAgent.ui.history

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AiHistoryRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryCategoryViewModel @Inject constructor(
    private val aiHistoryRepository: AiHistoryRepository,
) : BaseViewModel() {

    private val _historyCategory = MutableStateFlow<PagingData<AiHistoryCategory>>(PagingData.empty())
    val historyCategory: StateFlow<PagingData<AiHistoryCategory>> = _historyCategory.asStateFlow()

    fun getHistoryCategory() {
        val nationalId = getNationalCode()
        if (nationalId != "0" && nationalId.isNotBlank()) {
            viewModelScope.launch {
                aiHistoryRepository.getAllCategoriesPaging(nationalId)
                    .cachedIn(viewModelScope)
                    .collectLatest { categories ->
                        _historyCategory.value = categories
                    }
            }
        } else {
            _historyCategory.value = PagingData.empty()
        }
    }


    fun deleteCategory(id: String) {
        viewModelScope.launch {
            aiHistoryRepository.deleteCategory(id)
        }
    }
}
