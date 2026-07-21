package com.tamin.taminhamrah.ui.home.services.employer.payment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentLinkResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentUrlRequest
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TFHViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldPaymentLink = MutableLiveData<PaymentLinkResponse>()

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

}

