package com.tamin.taminhamrah.ui.home.services.employer.protestStatus

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.services.workshop.SmsModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class MessageFragmentViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {


    fun getObjectionSms(objectionId: String): Flow<PagingData<SmsModel>> {
        val paramsMap = mutableMapOf<String, String>()
        if (!objectionId.isNullOrEmpty())
            paramsMap["objectionId"] = "$objectionId"

        val result = createPager(repository::getObjectionSms, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

}