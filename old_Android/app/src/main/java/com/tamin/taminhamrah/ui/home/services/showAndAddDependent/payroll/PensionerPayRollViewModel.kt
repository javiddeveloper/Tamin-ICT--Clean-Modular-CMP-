package com.tamin.taminhamrah.ui.home.services.showAndAddDependent.payroll

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.PayRollResponse
import com.tamin.taminhamrah.data.remote.models.services.PensionIdModel
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PensionerPayRollViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val pensionIdModelList by lazy{
        ArrayList<PensionIdModel>()
    }

    val mldPayRoll = MutableLiveData<PayRollResponse>()
    val mldSendToInboxResult = MutableLiveData<GeneralRes>()
    val mldPdf = MutableLiveData<PdfDownloadResponse>()
    val mldPensionerIdList = MutableLiveData<PensionerIdResponse>()

    fun getPensionerIdList() {
        viewModelScope.launch {
            mldPensionerIdList.postValue(callService { repository.getPensionerId() })
        }
    }

    val paymentTypeFlow = createLocalPager(getPaymentTypeList()).flow.cachedIn(viewModelScope)
    private fun getPaymentTypeList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("پرداخت ماهانه", "01"))
        itemList.add(MenuModel("عیدی", "03"))
        itemList.add(MenuModel("تفاوت افزایش معوقه", "08"))
        return itemList
    }

    val paymentMonthFlow = createLocalPager(getMonthList()).flow.cachedIn(viewModelScope)
    private fun getMonthList(): ArrayList<MenuModel> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("فروردین", "01"))
        itemList.add(MenuModel("اردیبهشت", "02"))
        itemList.add(MenuModel("خرداد", "03"))
        itemList.add(MenuModel("تیر", "04"))
        itemList.add(MenuModel("مرداد", "05"))
        itemList.add(MenuModel("شهریور", "06"))
        itemList.add(MenuModel("مهر", "07"))
        itemList.add(MenuModel("آبان", "08"))
        itemList.add(MenuModel("آذر", "09"))
        itemList.add(MenuModel("دی", "10"))
        itemList.add(MenuModel("بهمن", "11"))
        itemList.add(MenuModel("اسفند", "12"))
        return itemList
    }

    fun getPayRoll(
        pensionerId: String,
        date: String,
        paymentType: String
    ) {
        viewModelScope.launch {
            val map = HashMap<String, String>()
            map["startDate"] = date
            map["pensionerId"] = pensionerId
            map["paymentType"] = paymentType
            mldPayRoll.postValue(callService {repository.getPensionerPayRoll(map)})
        }
    }

    fun sendPayRollToInbox(
        pensionerId: String,
        date: String,
        paymentType: String
    ) {
        viewModelScope.launch {
               val map = HashMap<String,String>()
                map["startDate"] = date
                map["pensionerId"] = pensionerId
                map["paymentType"] = paymentType
                mldSendToInboxResult.postValue(callService { repository.sendPayRollToInbox(map) })
        }
    }

    fun pensionerPayRollPDF(startDate:String,pensionerId:String,paymentType:String ) {
        viewModelScope.launch {
            val map = HashMap<String, String?>()
            map["startDate"] = startDate
            map["pensionerId"] = pensionerId
            map["paymentType"] = paymentType
            mldPdf.postValue(callService { repository.pensionerPayRollPDF(map) })
        }
    }
}

