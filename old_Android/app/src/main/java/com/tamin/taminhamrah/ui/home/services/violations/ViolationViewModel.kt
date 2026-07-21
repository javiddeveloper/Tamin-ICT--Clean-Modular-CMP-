package com.tamin.taminhamrah.ui.home.services.violations

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPayDebitResponse
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPaymentInfo
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViolationViewModel  @Inject constructor(private val repository: ServiceRepository) : BaseViewModel() {


//    val mldViolationInfo =
//        createPager(repository::getViolationInfo).flow.cachedIn(viewModelScope)

    val mldPayment = MutableLiveData<Event<WorkersPayDebitResponse>>()
    val mldPaymentPreview = MutableLiveData<Event<PaymentInfoResponse>>()
    val mldCheckSuccessPayment = MutableLiveData<Event<GeneralRes>>()
    private var mWorkersPaymentInfo: WorkersPaymentInfo? = null

    //Constants.REDIRECT_SCHEMA + Constants.REDIRECT_ADDR + Constants.REDIRECT_HOST_GENERAL
    fun doPayment(info: WorkersPaymentInfo) {
        mWorkersPaymentInfo = info
        viewModelScope.launch {
            val result = callService {
                repository.getWorkersPayDebit(info.makePayDebitRequest()) }
//            result.data?.let {
//                repository.saveWorkerPayInfo(it.ticket(), it.paymentInfo())
//            }
            mldPayment.postValue(Event(result))
        }
    }

    fun getPayType() = mWorkersPaymentInfo?.type

    fun normalDebitPaymentPreview() {
        viewModelScope.launch {
            mldPayment.value?.peekContent()?.data?.apply {
                /* val ticket = if (paymentTicket != null) paymentTicket
                 else paymentURL?.split("/")?.lastOrNull()*/
                val result = callService {
                    repository.getPaymentInfo("${Constants.TFH_URL}${Constants.TFH_URL_PREVIEW}${ticket()}")
                }
                if (result.isSuccess) result.data?.ticket = ticket()
                mldPaymentPreview.postValue(Event(result))
            }
        }
    }
}