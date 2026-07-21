package com.tamin.taminhamrah.ui.home.services.studentContract.payment.insurance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CalculateFreelanceDebitResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckContractStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.FreelanceLastPaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.OptionalInsuranceLastPaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.CalculationModel
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsurancePaymentViewModel @Inject constructor(private val repository: ServiceRepository) :
    BaseViewModel() {

    val mldUserInfo = MutableLiveData<ConcludingStudentInsuranceContractResponse>()
    val mldCheckAgeAndHistory = MutableLiveData<CheckAgeAndHistoryResponse>()
    val mldCheckContractStatus = MutableLiveData<CheckContractStatusResponse>()
    val mldCheckSuccessPayment = MutableLiveData<GeneralRes>()

    val mldLastPayment = MutableLiveData<FreelanceLastPaymentResponse>()
    val mldLastPaymentOptional = MutableLiveData<OptionalInsuranceLastPaymentResponse>()

    val mldCalculateDebit = MutableLiveData<Event<CalculateFreelanceDebitResponse>>()

    val mldCheckAndCalculateSalaryForContract = MutableLiveData<GeneralRes>()
    val mldPayment = MutableLiveData<Event<PaymentResponse>>()

    val mldPaymentPreview = MutableLiveData<Event<PaymentInfoResponse>>()

    val mldShowInfoLoading = MutableLiveData<Boolean>()


    fun getInitData() {
        viewModelScope.launch {
            //   mldLoginInfo.postValue(Resource.loading(null))
            mldShowInfoLoading.postValue(true)

            val userInfoRes = async(Dispatchers.IO) {
                repository.getRegistrationInfo()
            }

            val lastStatusRes = async(Dispatchers.IO) {
                repository.getFreelanceLastPayment()
            }

            val paymentResponse = lastStatusRes.await()
            val userInfoResponse = userInfoRes.await()

            mldLastPayment.postValue(paymentResponse)
            mldUserInfo.postValue(userInfoResponse)
            mldShowInfoLoading.postValue(false)
        }
    }

    fun getFractionData() {

        viewModelScope.launch {
            mldShowInfoLoading.postValue(true)

            val userInfoRes = async(Dispatchers.IO) {
                repository.getRegistrationInfo()
            }
            //TODO::

            val userInfoResponse = userInfoRes.await()



            mldShowInfoLoading.postValue(false)


            mldUserInfo.postValue(userInfoResponse)

        }
    }

    fun getInitDataOptional() {

        viewModelScope.launch {
            //   mldLoginInfo.postValue(Resource.loading(null))
            mldShowInfoLoading.postValue(true)

            val contractStatusRes = async(Dispatchers.IO) {
                repository.checkOptionalInsuranceContractStatus()
            }
            val userInfoRes = async(Dispatchers.IO) {
                repository.getRegistrationInfo()
            }
            val lastStatusRes = async(Dispatchers.IO) {
                repository.getOptionalInsuranceLastPayment()
            }

            val checkContractResponse = contractStatusRes.await()
            val lastPaymentResponse = lastStatusRes.await()
            val userInfoResponse = userInfoRes.await()

            mldCheckContractStatus.postValue(checkContractResponse)
            mldLastPaymentOptional.postValue(lastPaymentResponse)
            mldUserInfo.postValue(userInfoResponse)
            mldShowInfoLoading.postValue(false)

        }
    }


    fun getRegistrationInfo() {
        viewModelScope.launch {
            mldUserInfo.postValue(callService { repository.getRegistrationInfo() })
        }
    }

    fun checkAgeAndHistory(type: EnumInsuranceType) {
        viewModelScope.launch {
            mldCheckAgeAndHistory.postValue(callService {
                when (type) {
                    EnumInsuranceType.TYPE_OPTIONAL -> repository.checkOptionalAgeAndHistory()
                    EnumInsuranceType.TYPE_FRACTION -> repository.checkFractionAgeAndHistory()
                    else -> repository.checkAgeAndHistory()
                }
            })

        }
    }

    fun checkContractStatus() {
        viewModelScope.launch {

            mldCheckContractStatus.postValue(callService { repository.checkContractStatus() })

        }
    }

    fun updatePaymentStatus(systemType: String) {
        viewModelScope.launch {
            mldCheckSuccessPayment.postValue(callService {
                repository.checkSuccessPaymentStatus(
                    systemType = systemType
                )
            })

        }
    }

    fun getFreelanceLastPayment() {

        viewModelScope.launch {

            mldLastPayment.postValue(callService { repository.getFreelanceLastPayment() })

        }
    }

    fun calculateDebitByMonth(month: Int, insuranceType: EnumInsuranceType?) {
        viewModelScope.launch {
            if (insuranceType == EnumInsuranceType.TYPE_OPTIONAL) {
                mldCalculateDebit.postValue(Event(callService {
                    repository.calculateOptionalInsuranceDebitByMonth(
                        month
                    )
                }))
            } else {
                mldCalculateDebit.postValue(Event(callService {
                    repository.calculateFreelanceDebitByMonth(month)
                }))
            }
        }
    }

    fun insurancePayment(
        startDate: Long?,
        endDate: Long?,
        amount: Long?,
        month: Int?,
        systemType: String?,
        redirectUrl: String
    ) {
        viewModelScope.launch {
            val result = callService {
                repository.insurancePayment(
                    startDate,
                    endDate,
                    amount,
                    systemType,
                    "0",
                    month = month,
                    redirectUrl = redirectUrl
                )
            }
            mldPayment.postValue(Event(result))
        }
    }

    fun normalDebitPaymentPreview() {
        viewModelScope.launch {
            mldPayment.value?.peekContent()?.data?.apply {
                val ticket = if (paymentTicket != null) paymentTicket
                else paymentURL?.split("/")?.lastOrNull()

                val result = callService {
                    repository.getPaymentInfo("${Constants.TFH_URL}${Constants.TFH_URL_PREVIEW}$ticket")
                }

                if (result.isSuccess) result.data?.ticket = ticket

                mldPaymentPreview.postValue(Event(result))
            }
        }
    }

    var mldCalculationList: Pager<Int, CalculationModel>? = null
    fun getPaymentCalculationDetailList(
        startDate: Long?, endDate: Long?
    ): Flow<PagingData<CalculationModel>>? {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        paramsMap["start_date"] = startDate.toString()
        paramsMap["end_date"] = endDate.toString()
        if (mldCalculationList == null) mldCalculationList =
            createPager(repository::getPaymentCalculationDetailList, paramsMap = paramsMap)
        return mldCalculationList?.flow?.cachedIn(viewModelScope)
    }

    fun getOptionalInsurancePaymentCalculationDetailList(
        startDate: Long?, endDate: Long?
    ): Flow<PagingData<CalculationModel>>? {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        paramsMap["start_date"] = startDate.toString()
        paramsMap["end_date"] = endDate.toString()
        if (mldCalculationList == null) mldCalculationList = createPager(
            repository::getOptionalInsurancePaymentCalculationDetailList, paramsMap = paramsMap
        )
        return mldCalculationList?.flow?.cachedIn(viewModelScope)
    }

    fun getRedirectUrl(insuranceType: EnumInsuranceType?): String {
        return when (insuranceType) {
            EnumInsuranceType.TYPE_STUDENT,
            EnumInsuranceType.TYPE_FREELANCE,
            EnumInsuranceType.TYPE_WOMAN ->
                Constants.REDIRECT_SCHEMA + Constants.REDIRECT_ADDR + Constants.REDIRECT_HOST_FREELANCE

            EnumInsuranceType.TYPE_OPTIONAL ->
                Constants.REDIRECT_SCHEMA + Constants.REDIRECT_ADDR + Constants.REDIRECT_HOST_OPTIONAL

            EnumInsuranceType.TYPE_FRACTION ->
                Constants.REDIRECT_SCHEMA + Constants.REDIRECT_ADDR + Constants.REDIRECT_HOST_FRACTION

            else ->
                Constants.REDIRECT_SCHEMA + Constants.REDIRECT_ADDR + Constants.REDIRECT_HOST_GENERAL
        }
    }
}