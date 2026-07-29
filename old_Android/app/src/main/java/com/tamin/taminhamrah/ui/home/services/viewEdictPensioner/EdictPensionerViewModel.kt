package com.tamin.taminhamrah.ui.home.services.viewEdictPensioner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.PensionIdModel
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.remote.models.services.edict.EdictPensionerResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EdictPensionerViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {
    val mldEdictList = MutableLiveData<Event<EdictPensionerResponse>>()
    var isPensioner = false

    val pensionIdModelList by lazy{
        ArrayList<PensionIdModel>()
    }

    fun getPaymentMonthFlow(is1399: Boolean) = createLocalPager(getMonthList(is1399))
        .flow.cachedIn(viewModelScope)

    private fun getMonthList(is1399: Boolean): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("فروردین", "01"))
        if (is1399)
            itemList.add(MenuModel("مرداد", "05"))

        return itemList
    }

    fun getSingleMonthIfOnly(is1399: Boolean): MenuModel? {
        val list = getMonthList(is1399)
        return if (list.size == 1) list[0] else null
    }

    val mldEdictInfo by lazy { MutableLiveData<EdictPensionerResponse>() }
    val mldPensionIds by lazy { MutableLiveData<PensionerIdResponse>() }
    val mldPdf by lazy { MutableLiveData<PdfDownloadResponse>() }
    val mldSendCertificateToInbox by lazy { MutableLiveData<GeneralRes>() }

    fun getPensionerIdList() {
        viewModelScope.launch {
            mldPensionIds.postValue(callService { repository.getPensionerId() })
        }
    }
    fun getEdictPensioner(pensionerId: String, date: String) {
        viewModelScope.launch {
            mldEdictInfo.postValue(callService { repository.getEdictPensioner(hashMapOf("startDate" to date , "pensionerId" to pensionerId)) })
        }
    }
    fun downloadEdictPdf(pensionerId: String, date: String) {
        viewModelScope.launch {
            mldPdf.postValue(callService { repository.downloadEdictPdf(hashMapOf("startDate" to date ,"pensionerId" to pensionerId)) })
        }
    }
    fun sendRequestInquirePensionCertificate(startDate: String,pensionerId: String) {
        viewModelScope.launch {
            mldSendCertificateToInbox.postValue( callService { repository.sendEdictPensionerToMyInbox(hashMapOf("startDate" to startDate ,"pensionerId" to pensionerId)) })
        }
    }

}