package com.tamin.taminhamrah.ui.mytamin.myrequest

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.remote.models.user.MyRequestItem
import com.tamin.taminhamrah.data.remote.models.user.MyRequestType
import com.tamin.taminhamrah.data.remote.models.user.RequestErrorResponse
import com.tamin.taminhamrah.data.remote.models.user.SmartGuideResponse
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.MultipleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyRequestListViewModel @Inject constructor(
    private val repository: LoginRepository
) : BaseViewModel() {
    private var RequestPage = 0

    val mldRequestErrorList = MultipleLiveData<RequestErrorResponse>()
    val mldSmartGuideList = MultipleLiveData<SmartGuideResponse>()

    fun getRequestPage() = RequestPage

    fun getRequestTypes(): Flow<PagingData<MyRequestType>> {

        val result = createPager(repository::getRequestTypeList)
        return result.flow.cachedIn(viewModelScope)

    }

    fun getMyRequestListFlow(
        requestTypeId: String? = "",
        refCode: String? = ""
    ): Flow<PagingData<MyRequestItem>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!requestTypeId.isNullOrEmpty())
            paramsMap["requestType.id"] = requestTypeId
        if (!refCode.isNullOrEmpty())
            paramsMap["refCode"] = refCode
        Timber.tag("getWorkshopListFlow: ").i(paramsMap.toString())
        return createPager(repository::getMyRequestList, paramsMap = paramsMap).flow.cachedIn(
            viewModelScope
        )
    }

    fun getMyRequestErrorList(requestId: Long?) {
        viewModelScope.launch {
            Timber.tag("getMyRequestErrorList").e("getMyRequestErrorList: CALLED")
            mldRequestErrorList.postValue(callService {
                repository.getMyRequestErrorList(requestId) })
        }
    }

    fun getSmartGuideList(requestType: Int?, requestStatus: String?, isPublic: Boolean?) {
        viewModelScope.launch {
            Timber.tag("getSmartGuideList").e("getSmartGuideList: CALLED")
            mldSmartGuideList.postValue(callService {
                repository.getSmartGuideList(requestType, requestStatus, isPublic) })
        }
    }

    fun hasEmptyErrorList(): Boolean {
        return mldRequestErrorList.value?.data?.list.isNullOrEmpty() == true
    }
    fun hasEmptySmartGuide(): Boolean {
        return mldSmartGuideList.value?.data?.list.isNullOrEmpty() == true
    }
}

