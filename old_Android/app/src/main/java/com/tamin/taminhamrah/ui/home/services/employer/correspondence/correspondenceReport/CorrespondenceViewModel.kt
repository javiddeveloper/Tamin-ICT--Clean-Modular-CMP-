package com.tamin.taminhamrah.ui.home.services.employer.correspondence.correspondenceReport

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.responses.DeleteItemResponse
import com.tamin.taminhamrah.data.remote.models.responses.InquiryLicenseResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxSizeResponse
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class CorrespondenceViewModel  @Inject constructor(
private val repository: LoginRepository
) : BaseViewModel() {
    val mldCorrespondenceSize = MutableLiveData<InboxSizeResponse>()
    val mldPDF = MutableLiveData<InboxPdfItem>()
    val mldIssuedCorrespondenceInquiry = MutableLiveData<InquiryLicenseResponse>()
    val mldCancelCorrespondenceInquiry = MutableLiveData<InquiryLicenseResponse>()
    val mldDeleteRequest =  MutableLiveData<DeleteItemResponse>()
    val correspondencePager = createPager(repository::getInbox).flow.cachedIn(viewModelScope)


    fun getCorrespondenceSize() {
        viewModelScope.launch {
            mldCorrespondenceSize.postValue(callService { repository.getInboxSize() })
        }
    }

    fun getPDF(requestId: Int?) {
        viewModelScope.launch {
            mldPDF.postValue(callService { repository.getMyRequestPDF(requestId) })
        }
    }

    fun issuedCorrespondenceInquiryLicense(requestId: String, inquiryId: String? = "") {
        Timber.tag("InquiryLicense")
            .i("correspondenceInquiryLicense: requestId=" + requestId + " inquiryId=" + inquiryId)
        viewModelScope.launch {
            mldIssuedCorrespondenceInquiry.postValue(callService {
                repository.inboxInquiryLicense(
                    requestId,
                    inquiryId
                )
            })

        }
    }
    fun cancelCorrespondenceInquiryLicense(requestId: String) {
        Timber.tag("InquiryLicense").i("correspondenceInquiryLicense: requestId=" + requestId)
        viewModelScope.launch {
            mldCancelCorrespondenceInquiry.postValue(callService {
                repository.inboxInquiryLicense(
                    requestId,
                    ""
                )
            })

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
    fun deleteItem(requestId: Int?) {
        viewModelScope.launch {
            mldDeleteRequest.postValue(callService { repository.deleteMyRequest(requestId) })
        }
    }

}