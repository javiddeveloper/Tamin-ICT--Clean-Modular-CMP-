package com.tamin.taminhamrah.ui.home.services.employer.payment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentLinkResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentUrlRequest
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldPaymentLink = MutableLiveData<PaymentLinkResponse>()
    val mldPaymentResult = MutableLiveData<PaymentInfoResponse>()
    val mldCheckSuccessPayment = MutableLiveData<GeneralRes>()

    fun getPaymentLink(ticket: String,body: PaymentUrlRequest) {
        viewModelScope.launch {
            mldPaymentLink.postValue(
                callService {
//                    repository.getPaymentLink("https://tfh.tamin.ir/api/v1.1/payment/payment-link-get/$ticket")
                   // repository.getPaymentLink("http://172.16.15.12.:7001/tfh/api/v1.1/payment/payment-link/7a5220b4-5cc4-48ea-b943-a657ebb72fe7",body = body)
                    repository.getPaymentLink("${Constants.TFH_URL}${Constants.TFH_URL_PAYMENT_POST}$ticket",body = body)
                })
        }
    }

    fun getPaymentResult(ticket: String) {

        viewModelScope.launch {
            mldPaymentResult.postValue(
                callService {
//                    https://tfh.tamin.ir/api/v1.1/payment/ticket/current-user/$ticket
                    repository.getPaymentInfo("${Constants.TFH_URL}${Constants.TFH_URL_PREVIEW}$ticket")
                })
        }
    }

    fun cancelPayment(ticket: String) {
        viewModelScope.launch {
            mldPaymentResult.postValue(
                callService {
//                    https://tfh.tamin.ir/api/v1.1/payment/ticket/current-user/$ticket
                    repository.getPaymentInfo("${Constants.TFH_URL}${Constants.TFH_URL_CANCEL_PAYMENT}$ticket")
                })
        }
    }

    fun updatePaymentStatus() {
            viewModelScope.launch {
                getSystemType()?.let {
                    if (it == EnumInsuranceType.TYPE_DEBT.systemType) {
                        getEmployerDebtSerialNumber()?.let {debtSerialNumber->
                            val result =  repository.checkPaymentDebt(debtSerialNumber)
                            if (result.isSuccess)
                                saveEmployerDebtSerialNumber("")
                        }
                    } else {
                        val result = callService {
                            repository.checkSuccessPaymentStatus(it)
                        }

                        if (result.isSuccess)
                            saveSystemType("")

                        mldCheckSuccessPayment.postValue(result)
                    }
                }
            }

    }

    fun updateWorkersPaymentStatus() {
        viewModelScope.launch {
            getSystemType()?.let {
                val result  =  callService {
                    val ticket = repository.getWorkerPayTicket() ?: ""
                    val info = repository.getWorkerPayInfo()?: ""
                    repository.saveWorkerPayInfo(null ,null)
//                    TODO: We Must Received this info from DeepLink Data
                    repository.inspectTicket(ticket , info)
                }
                if(result.isSuccess) {
                    saveSystemType("")
                }
                mldCheckSuccessPayment.postValue(result)
            }
        }
    }
}

