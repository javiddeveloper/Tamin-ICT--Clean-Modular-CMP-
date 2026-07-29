package com.tamin.taminhamrah.ui.mytamin.inbox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.MessageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.responses.DeleteItemResponse
import com.tamin.taminhamrah.data.remote.models.responses.InquiryLicenseResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxItem
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxSizeResponse
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.MultipleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val repository: LoginRepository
) : BaseViewModel() {

    var selectedItem: MenuModel?=null
    private val _expandedItemIds = MutableStateFlow(emptySet<Int>())
    val mldInboxSize = MutableLiveData<InboxSizeResponse>()
    val mldSelectedItem = MutableLiveData<InboxItem>()
    val mldDeleteRequest = MultipleLiveData<DeleteItemResponse>()
    val mldInboxInquiry = MultipleLiveData<InquiryLicenseResponse>()
    val mldPDF = MultipleLiveData<InboxPdfItem>()

    val inboxItems: Flow<PagingData<InboxItem>> = createPager(
        function = { params: MutableMap<String, String>? ->
            repository.getInbox(params)
        }
    ).flow
        .cachedIn(viewModelScope)
        .combine(_expandedItemIds) { pagingData, expandedIds ->
            pagingData.map { item ->
                item.copy(expanded = expandedIds.contains(item.id))
            }
        }

    val mldSystemList = MutableLiveData<Resource<List<MenuModel>?>>()
    val mldSubjectList = MutableLiveData<Resource<List<MenuModel>?>>()


    fun getInboxSize() {
        viewModelScope.launch {
            mldInboxSize.postValue(callService { repository.getInboxSize() })
        }
    }

    fun toggleItemExpanded(itemId: Int) {
        val currentIds = _expandedItemIds.value.toMutableSet()
        if (currentIds.contains(itemId)) {
            currentIds.remove(itemId)
        } else {
            currentIds.add(itemId)
        }
        _expandedItemIds.value = currentIds
    }

    fun getPDF(requestId: Int?) {
        viewModelScope.launch {
            Timber.tag("inboxDebugUpdate").i("getPDF: requestId=" + requestId)
            mldPDF.postValue(callService { repository.getMyRequestPDF(requestId) })
        }
    }

    fun createKeyValue(it: InboxItem): List<KeyValueModel> {

        val keyValueList = ArrayList<KeyValueModel>()
        keyValueList.add(KeyValueModel("تاریخ ارسال", it.getPersianDate(it.receiveDate)))
        keyValueList.add(KeyValueModel("سیستم", it.type?.typeDesc ?: "-"))
        keyValueList.add(KeyValueModel("کد ملی", it.nationalCode ?: "-"))
        keyValueList.add(KeyValueModel("موبایل", it.mobileNumber ?: "-"))
        keyValueList.add(KeyValueModel("پست الکترونیک", it.email ?: "-"))
        keyValueList.add(KeyValueModel("تاریخ مشاهده", it.getPersianDate(it.seenDate)))
//        keyValueList.add(KeyValueModel("قابلیت استعلام", it. ?: "-"))
//        keyValueList.add(KeyValueModel("کد رمز", if (it.nationCode == "01") "ایرانی" else "غیر ایرانی"))
//        keyValueList.add(KeyValueModel("شهر محل صدور", it.issueplaceName ?: "-"))
        return keyValueList
    }

    fun deleteItem(requestId: Int?) {
        viewModelScope.launch {
            mldDeleteRequest.postValue(callService { repository.deleteMyRequest(requestId) })
        }
    }

    fun getLicensePeriodList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("یک روز", "0"))
        itemList.add(MenuModel("یک هفته", "1"))
        itemList.add(MenuModel("یک ماه", "2"))
        itemList.add(MenuModel("یک سال", "3"))
        return itemList
    }

    fun inboxInquiryLicense(requestId: String, inquiryId: String? = "") {
        Timber.tag("InquiryLicense")
            .i("correspondenceInquiryLicense: requestId=" + requestId + " inquiryId=" + inquiryId)

        viewModelScope.launch {
            mldInboxInquiry.postValue(callService {
                repository.inboxInquiryLicense(
                    requestId,
                    inquiryId
                )
            })

        }
    }

    fun getSystemList() {
        viewModelScope.launch {
            mldSystemList.postValue(Resource.loading(null))
            try {
                val result = repository.getInboxSystemList()
                mldSystemList.postValue(result)

            } catch (e: Exception) {
                mldSystemList.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }
    }

    fun getSubjectList() {
        viewModelScope.launch {
            mldSubjectList.postValue(Resource.loading(null))
            try {
                val result = repository.getInboxSubjectList()
                mldSubjectList.postValue(result)

            } catch (e: Exception) {
                mldSubjectList.postValue(Resource.error(MessageModel(e.message ?: e.toString(), 0)))
            }
        }
    }
}
