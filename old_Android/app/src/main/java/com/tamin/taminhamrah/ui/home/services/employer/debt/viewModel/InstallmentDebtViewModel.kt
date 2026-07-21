package com.tamin.taminhamrah.ui.home.services.employer.debt.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.employer.DebtDiscountResponse
import com.tamin.taminhamrah.data.remote.models.employer.debit.DebtPaidListResponse
import com.tamin.taminhamrah.data.remote.models.employer.debit.InstallmentPaymentResponse
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.DebtInstallmentResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtModel
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.debt.DebtDetailModel
import com.tamin.taminhamrah.ui.home.services.employer.debt.InstallmentRequestModel
import com.tamin.taminhamrah.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InstallmentDebtViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val mldDebtDiscount = MutableLiveData<DebtDetailModel>()
    val mldDebtInstallment = MutableLiveData<DebtInstallmentResponse>()
    val mldPdf = MutableLiveData<PdfDownloadResponse>()
    val mldInstallmentPayment = MutableLiveData<InstallmentPaymentResponse>()
    val mldPaymentDebitToken = MutableLiveData<Event<PaymentResponse>>()
    val mldPaymentPreview = MutableLiveData<Event<PaymentInfoResponse>>()
    val mldDebtPaidList = MutableLiveData<DebtPaidListResponse>()

    fun getWorkshopItemsFlow(
        workshopId: String = "",
        contractRow: String = ""
    ): Flow<PagingData<WorkshopInfo>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (workshopId.isNotBlank())
            paramsMap["workshopId"] = workshopId
        if (contractRow.isNotBlank())
            paramsMap["contractRow"] = contractRow

        return createPager(repository::getSpecialContactList, paramsMap = paramsMap)
            .flow
            .cachedIn(viewModelScope)
    }

    fun getDebtDetails(
        workshopId: String,
        branchCode: String,
        contractRow: String
    ): Flow<PagingData<WorkShopDebt>> {
        Timber.tag("getDebtDetailsTag")
            .i("workshopId=$workshopId branchCode=$branchCode contractRow=$contractRow")
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        paramsMap["peymanSequence"] = contractRow
        paramsMap["workshopId"] = workshopId
        paramsMap["branchCode"] = branchCode

        return createPager(repository::getWorkshopDebtInfo, paramsMap = paramsMap)
            .flow
            .cachedIn(viewModelScope)
    }

    fun getAllInstallmentList(
        workshopId: String,
        letterDate: String
    ): Flow<PagingData<WorkShopDebtModel>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (workshopId.isNotBlank())
            paramsMap["workshopId"] = workshopId

        if (letterDate.isNotBlank())
            paramsMap["letterDate"] = letterDate

        return createPager(repository::getAllInstallmentList, paramsMap = paramsMap)
            .flow
            .cachedIn(viewModelScope)
    }

    fun getDebDiscount(branchCode: String, workshopDebt: WorkShopDebt) {

        viewModelScope.launch {
            val response: DebtDiscountResponse = callService {
                repository.getDebtDiscount(
                    branchCode,
                    workshopDebt.debitNumber ?: "",
                    workshopDebt.debitRemain ?: 0
                )
            }
            mldDebtDiscount.postValue(DebtDetailModel(workshopDebt, response.data ?: 0))
        }

    }

    fun getPaymentDebitList(debtNumber: String, branchCode: String) {
        viewModelScope.launch {
            mldInstallmentPayment.postValue(callService {
                repository.getPaymentDebitList(debtNumber, branchCode)
            })
        }
    }

    fun installmentDebt(request: InstallmentRequestModel) {
        viewModelScope.launch {
            mldDebtInstallment.postValue(callService {
                repository.installmentDebt(
                    request
                )
            })
        }
    }

    fun getActionList(isNotPaid: Boolean) =
        createLocalPager(if (isNotPaid) actionList else actionList.drop(1))

    private val actionList = listOf(
        MenuModel("پرداخت قسط", "0", iconRes = R.drawable.ic_credit_card),
        MenuModel("مشاهده پرداخت ها", "1", iconRes = R.drawable.ic_payments),
        MenuModel("مشاهده درخواست تقسیط", "2", iconRes = R.drawable.ic_file),
    )

    fun downloadInstallmentReport(letterNumber: String) {
        viewModelScope.launch {
            mldPdf.postValue(callService { repository.downloadInstallmentReport(letterNumber) })
        }
    }

    fun getPaymentDebitToken(debitNumber: String) {
        viewModelScope.launch {
            mldPaymentDebitToken.postValue(Event(callService {
                repository.getPaymentDebitToken(
                    debitNumber
                )
            }))
        }
    }

    fun getPaidInfo(debtSerialNumber: String) {
        viewModelScope.launch {
            mldDebtPaidList.postValue(callService { repository.getDebtPaidList(debtSerialNumber) })
            checkPaymentDebt(debtSerialNumber)
        }
    }

    fun checkPaymentDebt(debtSerialNumber: String) {
        viewModelScope.launch {
           callService { repository.checkPaymentDebt(debtSerialNumber) }
        }
    }


    fun normalDebitPaymentPreview() {
        viewModelScope.launch {
            mldPaymentDebitToken.value?.peekContent()?.data?.apply {
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

}

