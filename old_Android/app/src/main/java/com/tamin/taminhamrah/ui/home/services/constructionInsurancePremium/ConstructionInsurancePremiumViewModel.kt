package com.tamin.taminhamrah.ui.home.services.constructionInsurancePremium

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants.BRANCH_ID
import com.tamin.taminhamrah.Constants.DEBIT_NUMBER
import com.tamin.taminhamrah.Constants.FILE_ID
import com.tamin.taminhamrah.Constants.REQUEST_DATE
import com.tamin.taminhamrah.Constants.REQUEST_ID
import com.tamin.taminhamrah.Constants.WORKSHOP_ID
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.BeneficiariesConstructionModel
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.ConstructionFileModel
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.ConstructionFileResponse
import com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium.PaymentSheetConstructionFilesResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConstructionInsurancePremiumViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    //for don't use paging
    val mldConstructionFile = MutableLiveData<ConstructionFileResponse>()
    val mldPDF = MutableLiveData<PdfDownloadResponse>()
    val mldIssuancePaymentSheet = MutableLiveData<GeneralStringRes>()
    val mldPaymentSheetConstructionInfo = MutableLiveData<PaymentSheetConstructionFilesResponse>()

    fun getConstructionFiles(
        workshopId: String? = "",
        branchCode: String? = "",
        requestNumber: String? = null,
        fileNumber: String? = null,
        requestDate: String? = null,
        workshopStatus: String? = ""
    ): Flow<PagingData<ConstructionFileModel>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap[WORKSHOP_ID] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap[BRANCH_ID] = branchCode
        if (!fileNumber.isNullOrEmpty())
            paramsMap[FILE_ID] = fileNumber
        if (!requestNumber.isNullOrEmpty())
            paramsMap[REQUEST_ID] = requestNumber
        if (!requestDate.isNullOrEmpty())
            paramsMap[REQUEST_DATE] = requestDate
        if (!workshopStatus.isNullOrEmpty())
            paramsMap["workshopStatusCode"] = workshopStatus
        paramsMap["position"] = "1"

        return createPager(
            repository::getConstructionFiles,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    fun getConstructionFilesWhitOutPaging(
        workshopId: String? = "",
        branchCode: String? = "",
        requestNumber: String? = null,
        fileNumber: String? = null,
        requestDate: String? = null,
        workshopStatus: String? = ""
    ) {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap[WORKSHOP_ID] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap[BRANCH_ID] = branchCode
        if (!fileNumber.isNullOrEmpty())
            paramsMap[FILE_ID] = fileNumber
        if (!requestNumber.isNullOrEmpty())
            paramsMap[REQUEST_ID] = requestNumber
        if (!requestDate.isNullOrEmpty())
            paramsMap[REQUEST_DATE] = requestDate
        if (!workshopStatus.isNullOrEmpty())
            paramsMap["workshopStatusCode"] = workshopStatus
        paramsMap["position"] = "1"
        viewModelScope.launch {
            mldConstructionFile.postValue(callService { repository.getConstructionFiles(paramsMap) })
        }
    }

    fun getBeneficiariesWorkshop(
        requestNumber: Long? = 0,
        fileNumber: Long? = 0,
        requestDate: String? = ""
    ): Flow<PagingData<BeneficiariesConstructionModel>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()

        if (requestNumber != 0L) {
            paramsMap[REQUEST_ID] = requestNumber.toString()
        }
        if (fileNumber != 0L) {
            paramsMap[FILE_ID] = fileNumber.toString()
        }
        if (!requestDate.isNullOrEmpty()) {
            paramsMap[REQUEST_DATE] = requestDate
        }

        paramsMap["position"] = "1"
        return createPager(
            repository::getBeneficiariesWorkshop,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }


    fun getCashListAction(isInstallment: Boolean = false) =
        ArrayList<MenuModel>().apply {
            add(
                MenuModel(
                    titleStringResId = R.string.view_request_detail,
                    id = "1",
                    iconRes = R.drawable.ic_doc
                )
            )
            if (!isInstallment) {
                add(
                    MenuModel(
                        titleStringResId = R.string.issuing_managing_payment_slips,
                        id = "2",
                        iconRes = R.drawable.ic_home_storage
                    )
                )
            } else {
                add(
                    MenuModel(
                        titleStringResId = R.string.installment_payment_management,
                        id = "3",
                        iconRes = R.drawable.ic_home_storage
                    )
                )
            }
            add(
                MenuModel(
                    titleStringResId = R.string.workshop_stack_holders,
                    id = "4",
                    iconRes = R.drawable.ic_groups
                )
            )
        }


    fun getPaymentSheetConstructionInfo(debitNumber: String) {
        viewModelScope.launch {
            mldPaymentSheetConstructionInfo.postValue(callService {
                repository.getPaymentSheetConstructionInfo(
                    debitNumber = debitNumber
                )
            })
        }
    }

    fun getCertificatePaymentSheetPDF(debitNumber: String, branchCode: String) {
        viewModelScope.launch {
            mldPDF.postValue(callService {
                repository.getCertificatePaymentSheetPDF(
                    debitNumber = debitNumber,
                    branchCode = branchCode
                )
            })
        }
    }

    fun issuancePaymentSheet(debitNumber: String) {
        viewModelScope.launch {
            mldIssuancePaymentSheet.postValue(callService {
                repository.issuancePaymentSheet(
                    debitNumber
                )
            })
        }
    }

    fun getInstallmentLetterList(workShopId: String, branchId: String) =
        createPager(
            repository::getInstallmentLetterList,
            paramsMap = mutableMapOf(WORKSHOP_ID to workShopId, BRANCH_ID to branchId)
        ).flow.cachedIn(viewModelScope)

    fun getDetailDebitList(debitNumber: String, branchId: String) =
        createPager(
            repository::getDetailDebitList,
            paramsMap = mutableMapOf(DEBIT_NUMBER to debitNumber, BRANCH_ID to branchId)
        ).flow.cachedIn(viewModelScope)


    fun getInstallmentList(debitNumber: String, branchId: String) =
        createPager(
            repository::getInstallmentConstructionList,
            paramsMap = mutableMapOf(DEBIT_NUMBER to debitNumber, BRANCH_ID to branchId)
        ).flow.cachedIn(viewModelScope)

    /* The api answer is not important to us,
       only this api should be called once when
       entering this service to update the information in the database*/
/*    fun updateInstallmentConstruction(oldDebitNumber: String) {
        viewModelScope.launch {
            repository.updateInstallmentConstruction(
                oldDebitNumber = oldDebitNumber
            )
        }
    }*/

    fun getInstallmentListAction() =
        arrayListOf(
            MenuModel(
                titleStringResId = R.string.installment_management_and_issuance_payment_sheet,
                id = "1",
                iconRes = R.drawable.ic_doc
            ),
            MenuModel(
                titleStringResId = R.string.installment_debts_list,
                id = "2",
                iconRes = R.drawable.ic_home_storage
            )
        )

/*
    fun getInstallmentManagementInfo(
        debitNumber: String,
        branchId: String,
        oldDebitNumber: String
    ): Triple<Flow<PagingData<InstallmentConstructionListModel>>,,GeneralStringRes> {
    }
*/

}


