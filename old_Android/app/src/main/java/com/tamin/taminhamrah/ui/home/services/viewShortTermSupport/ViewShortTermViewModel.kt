package com.tamin.taminhamrah.ui.home.services.viewShortTermSupport

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewShortTermViewModel @Inject constructor(val repository: ServiceRepository) : BaseViewModel() {

   // val mldViewShortTermRequest = MutableLiveData<Resource<BaseListResponse<ViewShortTermRequestResponse>?>>()
    val getViewShortTermRequest = createPager(repository::getViewShorttermRequestList, Constants.QUERY_PAGE_SIZE_10.toString()).flow.cachedIn(viewModelScope)

}