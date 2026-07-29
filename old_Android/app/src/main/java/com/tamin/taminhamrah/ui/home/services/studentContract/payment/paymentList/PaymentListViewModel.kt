package com.tamin.taminhamrah.ui.home.services.studentContract.payment.paymentList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.PaymentListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment.DetailPaymentListResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaymentListViewModel @Inject constructor (private val repository:ServiceRepository) : BaseViewModel() {

    val mldPaymentList =MutableLiveData<PaymentListResponse>()
    fun getContractsPaymentsListFreelance(contractNumber : String) {
        viewModelScope.launch {
            mldPaymentList.postValue(callService { repository.getContractsPaymentsListFreelance(contractNumber)})
        }
    }

    val mldDetailPaymentList =MutableLiveData<DetailPaymentListResponse>()
    fun getDetailPaymentList(contractNumber : String,debitNumber : String) {
        viewModelScope.launch {
            mldDetailPaymentList.postValue(callService { repository.getDetailPaymentFreelance(contractNumber,debitNumber)})
        }
    }

    val mldCheckSuccessPayment = MutableLiveData<GeneralRes>()
    fun checkSuccessPayment(systemType:String) {
        viewModelScope.launch {

            mldCheckSuccessPayment.postValue(callService {
                repository.checkSuccessPaymentStatus(
                    systemType = systemType
                )
            })

        }
    }
}

/*

val list = ArrayList<PaymentListModel>()
result.data?.list?.forEach { item->
    list.add(
        PaymentListModel(
            nationalId = item?.get(1) as String?,
            insuranceId =item?.get(2) as String?,
            debtNumber =item?.get(3) as String?,
            startTermPayment =item?.get(5) as String?,
            endTermPayment =item?.get(6) as String?,
            totalDebt =item?.get(7) as Long?,
            paymentDeadLine =item?.get(8) as String?,
            amountPayment =item?.get(9) as Long?,
            datePayment =item?.get(10) as String?,
            statusContract =item?.get(11) as String?,
            statusRecipient =item?.get(12) as String?
        )
    )
}*/
