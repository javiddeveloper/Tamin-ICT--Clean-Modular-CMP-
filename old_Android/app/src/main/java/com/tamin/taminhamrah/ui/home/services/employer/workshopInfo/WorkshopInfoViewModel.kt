package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.PaymentModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.data.remote.models.employer.employerAgreement.EmployerAgreement
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.DebitObjection
import com.tamin.taminhamrah.data.remote.models.services.DebitObjectionResponse
import com.tamin.taminhamrah.data.remote.models.services.ObjectionPhoto
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.payment.PaymentResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.ObjectionTypeNameValue
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt.ObjectionType.BADVI
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebt.ObjectionType.BARAVORDI
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtInquiryResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDebtResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkShopDemandDoc
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopDebitReason
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopInfo
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopMember
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopMemberResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopPaymentSheet
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopStackHolder
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopStackHolderResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16.WorkshopsDebtListResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model.FilterWorkshopEnumClass
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.model.WorkshopActionsEnumClass
import com.tamin.taminhamrah.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkshopInfoViewModel @Inject constructor(
    private val repository: ServiceRepository
) : BaseViewModel() {

    val ARG_WORKSHOP_DEBIT = "ARG_WORKSHOP_DEBIT"
    val ARG_NATIONAL_CODE = "ARG_NATIONAL_CODE"
    val ARG_INSURANCE_NUMBER = "ARG_INSURANCE_NUMBER"
    val ARG_DEBT_NUMBER = "ARG_DEBT_ID"

    val mldWorkshopDepInquiry = MutableLiveData<WorkShopDebtInquiryResponse>()
    val mldWorkShopDebtResponse = MutableLiveData<WorkShopDebtResponse>()
    val mldPdf = MutableLiveData<PdfDownloadResponse>()
    val mldPaymentPreCheck = MutableLiveData<Event<PaymentModel>>()
    val mldPayment = MutableLiveData<Event<PaymentResponse>>()
    val mldPaymentPreview = MutableLiveData<Event<PaymentInfoResponse>>()
    val mldObjectionPermission = MutableLiveData<WorkShopDebt>()
    val mldObjectionInfo = MutableLiveData<Resource<Boolean?>>()
    var workshopInfoPager: Pager<Int, WorkshopInfo>? = null
    val mldDebtList = MutableLiveData<WorkshopsDebtListResponse>()

    var tempImageUri: Uri? = null
    var tempImageOriginalUri: Uri? = null

    var  tempImageType: String =""
    var  tempImageName: String =""

    var fileListUploaded : java.util.ArrayList<UploadedImageModel> = arrayListOf()

    fun getEmployerAgreementInfoList(
        workshopId: String? = "",
        branchCode: String? = "",
        workshopStatus:String?=""
    ): Flow<PagingData<EmployerAgreement>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode
        if (!workshopStatus.isNullOrEmpty())
            paramsMap["workshopStatusCode"] = workshopStatus

        return createPager(
            repository::getEmployerAgreementInfoList,
            paramsMap = paramsMap
        ).flow.cachedIn(viewModelScope)
    }

    fun getWorkshopListFlow(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<WorkshopInfo>>? {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode
        if (workshopInfoPager == null)
            Timber.tag("getWorkshopListFlow: ").i(paramsMap.toString())
        workshopInfoPager = createPager(repository::getWorkshopInfo, paramsMap = paramsMap)
        return workshopInfoPager?.flow?.cachedIn(viewModelScope)
    }

    private fun getMemberRequestFilters(
        workshopId: String? = "",
        branchCode: String? = "",
        insuranceId: String? = "",
        nationalId: String? = "",
        isStackHolderMap: Boolean = false
    ): MutableMap<String, String> {
        val map = HashMap<String, String>()
        if (!workshopId.isNullOrBlank())
            if (isStackHolderMap) {
                map["workshopId.workshopId"] = workshopId
            } else {
                map["workshop.workshopId"] = workshopId
            }
        if (!branchCode.isNullOrBlank())
            if (isStackHolderMap) {
                map["workshopId.branchCode"] = branchCode
            } else {
                map["workshop.branchCode"] = branchCode
            }
        if (!insuranceId.isNullOrBlank())
            map["insurance.id"] = insuranceId

        if (!nationalId.isNullOrBlank())
            map["insurance.nationalId"] = nationalId

        return map
    }

    private fun getStackHoldersRequestFilters(
        workshopId: String? = "",
        branchCode: String? = "",
        insuranceId: String? = "",
        nationalId: String? = ""
    ): MutableMap<String, String> {
        val map = HashMap<String, String>()

        if (!workshopId.isNullOrBlank()) {
            map["workshopId.workshopId"] = workshopId
        }
        if (!branchCode.isNullOrBlank()) {
            map["workshopId.branchCode"] = branchCode
        }
        return map
    }


    var mldWorkshopMemberList = MutableLiveData<WorkshopMemberResponse>()
    var mldWorkshopStackHolderList = MutableLiveData<WorkshopStackHolderResponse>()
    fun getWorkshopDetailsInfo(
        workshopId: String,
        branchCode: String
    ) {
        viewModelScope.launch {
            supervisorScope {

                mldWorkshopStackHolderList.postValue(callService {
                    repository.getWorkshopStackHolderList(
                        getStackHoldersRequestFilters(workshopId, branchCode)
                    )
                })

                mldWorkshopMemberList.postValue(
                    callService {
                        repository.getWorkshopMemberList(
                            getMemberRequestFilters(workshopId, branchCode)
                        )
                    })
            }
        }
    }

    fun getWorkshopMemberFlow(
        workshopId: String? = "",
        branchCode: String? = "",
        insuranceId: String? = "",
        nationalId: String? = "",
    ): Flow<PagingData<WorkshopMember>> {

        val paramsMap = getMemberRequestFilters(workshopId, branchCode, insuranceId, nationalId)

        val result = createPager(repository::getWorkshopMemberList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getWorkshopStackHolderFlow(
        workshopId: String? = "",
        branchCode: String? = "",
        insuranceId: String? = "",
        nationalId: String? = "",
    ): Flow<PagingData<WorkshopStackHolder>> {

        val paramsMap =
            getMemberRequestFilters(workshopId, branchCode, insuranceId, nationalId, true)

        val result = createPager(repository::getWorkshopStackHolderList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    var workshopDebtPager: Pager<Int, WorkShopDebt>? = null
    fun getWorkshopDebtListFlow(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<WorkShopDebt>>? {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode
        workshopDebtPager = createPager(repository::getWorkshopDebtList, paramsMap = paramsMap)
        return workshopDebtPager?.flow?.cachedIn(viewModelScope)
    }

    fun getWorkshopDemandDocsFlow(
        debtNumber: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<WorkShopDemandDoc>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!debtNumber.isNullOrEmpty())
            paramsMap["debtNumber"] = debtNumber
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode
        val result = createPager(repository::getWorkshopDemandDocs, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    var paymentSheetPager: Pager<Int, WorkshopPaymentSheet>? = null


    fun getWorkshopPaymentSheets(
        workshopId: String? = "",
        branchCode: String? = "",
        debitCause: String? = "",
        paymentType: String? = "",
        payNumberFrom: String? = "",
        payNumberTo: String? = "",
        dateFrom: String? = "",
        dateTo: String? = ""

    ): Flow<PagingData<WorkshopPaymentSheet>>? {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        addParamToMap(paramsMap, "workshopId", workshopId)
        addParamToMap(paramsMap, "branchCode", branchCode)
        addParamToMap(paramsMap, "payIdFrom", payNumberFrom)
        addParamToMap(paramsMap, "payIdTo", payNumberTo)
        addParamToMap(paramsMap, "docDateFrom", dateFrom)
        addParamToMap(paramsMap, "docDateTo", dateTo)
        addParamToMap(paramsMap, "debitReason", debitCause)
        addParamToMap(paramsMap, "paymentSheetStatus", paymentType)

        paymentSheetPager = createPager(repository::getWorkshopPaymentSheets, paramsMap = paramsMap)
        return paymentSheetPager?.flow?.cachedIn(viewModelScope)
    }

    private fun addParamToMap(paramsMap: MutableMap<String, String>, key: String, value: String?) {
        if (!value.isNullOrEmpty())
            paramsMap[key] = value
    }

    fun downloadPdf(debtNumber: String, branchCode: String) {
        viewModelScope.launch {
            mldPdf.postValue(callService {
                repository.downloadDebtDocumentPdf(
                    debtNumber,
                    branchCode
                )
            })
        }
    }

    fun getPaymentType(): Flow<PagingData<MenuModel>> {
        val itemList = ArrayList<MenuModel>()
        itemList.add(MenuModel("باطل", "1"))
        itemList.add(MenuModel("وصول", "2"))
        itemList.add(MenuModel("موثر", "3"))

        return createLocalPager(itemList).flow.cachedIn(viewModelScope)
    }


    fun getWorkshopDebitCauseFlow(): Flow<PagingData<WorkshopDebitReason>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        val result = createPager(repository::getDebitReasonList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getWorkshopDebtInquiry(workshopId: String = "", branchCode: String = "") {
        viewModelScope.launch {
            mldWorkshopDepInquiry.postValue(callService {
                repository.getWorkshopDebtInquiry(workshopId, branchCode)
            })
        }
    }

    fun checkPaymentStatus(
        branchCode: String,
        workshopId: String,
        debitNumber: String,
        peymanSequence: String?,
        seporde: Boolean,
        debitRemain: Long,
        debitCreateReason: String
    ) {
        viewModelScope.launch {

            val result = callService {
                repository.getDebitPaymentStatus(branchCode, debitNumber)
            }

            if (result.isSuccess)
                mldPaymentPreCheck.postValue(
                    Event(
                        PaymentModel(
                            branchCode = branchCode,
                            workshopId = workshopId,
                            debitNumber = debitNumber,
                            peymanSequence = peymanSequence,
                            seporde = seporde,
                            amount = debitRemain,
                            reason = debitCreateReason,
                            preCheck = result.data?.functionResult == "1"
                        )
                    )
                )
        }
    }

    fun normalDebitPayment(
        branchCode: String?,
        workshopId: String?,
        debitNumber: String?,
        peymanSequence: String?,
        seporde: Boolean
    ) {

        viewModelScope.launch {
            val result = callService {
                repository.normalDebitPayment(
                    branchCode, workshopId, debitNumber, peymanSequence, seporde
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

                if (result.isSuccess)
                    result.data?.ticket = ticket

                mldPaymentPreview.postValue(Event(result))
            }
        }
    }

    fun getWorkshopObjectionableDebitList(
        workshopId: String = "",
        branchCode: String = ""
    ): Flow<PagingData<WorkShopDebt>> {

        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (workshopId.isNotEmpty())
            paramsMap["workshopId"] = workshopId
        if (branchCode.isNotEmpty())
            paramsMap["branchCode"] = branchCode
        val result =
            createPager(
                repository::getWorkshopObjectionableDebtList,
                Constants.QUERY_PAGE_SIZE_1000.toString(),
                paramsMap = paramsMap
            )
        return result.flow.cachedIn(viewModelScope)

    }

    fun checkWorkshopDebitObjectionPermission(item: WorkShopDebt) {
        viewModelScope.launch {
            val result = callService {
                repository.checkWorkshopDebitObjectionPermission(item.orderRecipeDate ?: "0")
            }

            (result.data as? Int)?.let {
                item.hasPermission = !((item.objectionType2 == BARAVORDI && it > 31) ||
                        (item.objectionType2 == BADVI && it > 21))
            }

            mldObjectionPermission.postValue(item)
        }
    }

    fun getDebitObjectionPdf(seqNo: Long) {
        viewModelScope.launch {
            mldPdf.postValue(callService { repository.downloadDebitObjectionPDF(seqNo) })
        }
    }

    fun getObjectionType(): Flow<PagingData<ObjectionTypeNameValue>> {
        return createLocalPager(repository.getObjectionType().items).flow.cachedIn(viewModelScope)
    }

    val mldUploadImage = MutableLiveData<UploadImageResponse>()
    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            mldUploadImage.postValue(callService {
                repository.uploadImage(image)
            })
        }
    }

    val mldDebitObjectionResult = MutableLiveData<DebitObjectionResponse>()
    fun sendDebitObjection(
        fileList: ArrayList<UploadedImageModel?>,
        debitInfo: WorkShopDebt?,
        workshopId: String?,
        branchCode: String?,
        seporde: Boolean,
        status: Boolean,
        description: String
    ) {
        viewModelScope.launch {

            val request = DebitObjection(
                debitInfo?.badviDate,
                debitInfo?.badviNo,
                branchCode,
                debitInfo?.debitNumber,
                debitInfo?.debitStatCode,
                debitInfo?.debitStepCode,
                "",
                description,
                getImageList(fileList),
                debitInfo?.objectionType2?.methodName,
                debitInfo?.peymanSequence,
                if (seporde) "1" else "0",
                if (status) "1" else "0",
                if (fileList[0]?.guid.isNullOrEmpty()) "" else fileList[0]?.imageType,
                if (fileList[1]?.guid.isNullOrEmpty()) "" else fileList[1]?.imageType,
                if (fileList[2]?.guid.isNullOrEmpty()) "" else fileList[2]?.imageType,
                if (fileList[3]?.guid.isNullOrEmpty()) "" else fileList[3]?.imageType,
                if (fileList[4]?.guid.isNullOrEmpty()) "" else fileList[4]?.imageType,
                if (fileList[5]?.guid.isNullOrEmpty()) "" else fileList[5]?.imageType,
                if (fileList[6]?.guid.isNullOrEmpty()) "" else fileList[6]?.imageType,
                if (fileList[7]?.guid.isNullOrEmpty()) "" else fileList[7]?.imageType,
                if (fileList[8]?.guid.isNullOrEmpty()) "" else fileList[8]?.imageType,
                if (fileList[9]?.guid.isNullOrEmpty()) "" else fileList[9]?.imageType,
                if (fileList[10]?.guid.isNullOrEmpty()) "" else fileList[10]?.imageType,
                if (fileList[11]?.guid.isNullOrEmpty()) "" else fileList[11]?.imageType,
                if (fileList[12]?.guid.isNullOrEmpty()) "" else fileList[12]?.imageType,
                if (fileList[13]?.guid.isNullOrEmpty()) "" else fileList[13]?.imageType,
                if (fileList[14]?.guid.isNullOrEmpty()) "" else fileList[14]?.imageType,
                if (fileList[15]?.guid.isNullOrEmpty()) "" else fileList[15]?.imageType,
                if (fileList[16]?.guid.isNullOrEmpty()) "" else fileList[16]?.imageType,
                if (fileList[17]?.guid.isNullOrEmpty()) "" else fileList[17]?.imageType,
                workshopId
            )


            viewModelScope.launch {
                mldDebitObjectionResult.postValue(callService {
                    repository.sendDebitObjection(request)
                })
            }

        }

    }

    private fun getImageList(fileList: java.util.ArrayList<UploadedImageModel?>): List<ObjectionPhoto> {

        val itemList = ArrayList<ObjectionPhoto>()
        fileList.forEach { item ->
            if (!item?.guid.isNullOrBlank()) {
                itemList.add(ObjectionPhoto(item?.guid, item?.imageType))
            }
        }
        return itemList
    }

    fun getSpecialContactListFlow(
        workshopId: String? = "",
        branchCode: String? = ""
    ): Flow<PagingData<WorkshopInfo>> {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        if (!workshopId.isNullOrEmpty())
            paramsMap["workshopId"] = workshopId
        if (!branchCode.isNullOrEmpty())
            paramsMap["branchCode"] = branchCode
        val result = createPager(repository::getSpecialContactList, paramsMap = paramsMap)
        return result.flow.cachedIn(viewModelScope)
    }

    fun getWorkshopFilterList() = listOf(
        MenuModel(titleStringResId = FilterWorkshopEnumClass.DELETE_FILTER.title, id = null),
        MenuModel(titleStringResId = FilterWorkshopEnumClass.ACTIVE_WORKSHOP.title, id = FilterWorkshopEnumClass.ACTIVE_WORKSHOP.id),
        MenuModel(titleStringResId = FilterWorkshopEnumClass.SEMI_ACTIVE_WORKSHOP.title, id = FilterWorkshopEnumClass.SEMI_ACTIVE_WORKSHOP.id),
        MenuModel(titleStringResId = FilterWorkshopEnumClass.INACTIVE_WORKSHOP.title, id = FilterWorkshopEnumClass.INACTIVE_WORKSHOP.id),
    )

    fun getMainWorkshopActions() = listOf(
        MenuModel(titleStringResId = WorkshopActionsEnumClass.PAYMENT_SHEET.titleResource, id = WorkshopActionsEnumClass.PAYMENT_SHEET.id,iconRes = R.drawable.ic_doc),
        MenuModel(titleStringResId = WorkshopActionsEnumClass.DEBIT_ACCOUNT_TURNOVER_DETAILS.titleResource, id =  WorkshopActionsEnumClass.DEBIT_ACCOUNT_TURNOVER_DETAILS.id, iconRes = R.drawable.ic_home_storage),
        MenuModel(titleStringResId = WorkshopActionsEnumClass.INQUIRY_DEBITS_WORKSHOP.titleResource, id = WorkshopActionsEnumClass.INQUIRY_DEBITS_WORKSHOP.id,iconRes=R.drawable.ic__chart),
        MenuModel(titleStringResId = WorkshopActionsEnumClass.OBJECTION_TO_DEBIT.titleResource, id =  WorkshopActionsEnumClass.OBJECTION_TO_DEBIT.id, iconRes = R.drawable.ic_megaphone),
        MenuModel(titleStringResId = WorkshopActionsEnumClass.INSURED_ABSENTEE_REGISTRATION.titleResource, id =  WorkshopActionsEnumClass.INSURED_ABSENTEE_REGISTRATION.id,iconRes = R.drawable.ic_people),
        MenuModel(titleStringResId = WorkshopActionsEnumClass.REGISTRATION_DEBIT_ARTICLE16.titleResource, id = WorkshopActionsEnumClass.REGISTRATION_DEBIT_ARTICLE16.id, iconRes = R.drawable.ic_debt16),
        MenuModel(titleStringResId = WorkshopActionsEnumClass.EMPLOYEES.titleResource, id =  WorkshopActionsEnumClass.EMPLOYEES.id,iconRes = R.drawable.ic_staff),
        MenuModel(titleStringResId = WorkshopActionsEnumClass.STACK_HOLDERS.titleResource, id =  WorkshopActionsEnumClass.STACK_HOLDERS.id,iconRes = R.drawable.ic_stackholder),
//        MenuModel(titleStringResId = WorkshopActionsEnumClass.DISTANT_CORRESPONDENCE.titleResource, id =  WorkshopActionsEnumClass.DISTANT_CORRESPONDENCE.id,iconRes = R.drawable.ic_correspondence),
//        MenuModel(titleStringResId = WorkshopActionsEnumClass.CORRESPONDENCE_AND_ANNOUNCEMENT_LIST.titleResource, id =  WorkshopActionsEnumClass.CORRESPONDENCE_AND_ANNOUNCEMENT_LIST.id,iconRes = R.drawable.ic_view_correspondence, isNew = true)
    )

    fun getWorkshopsDebtsList(workshopId: String, branchCode: String) {
        viewModelScope.launch {
            val paramsMap: MutableMap<String, String> = mutableMapOf()
            paramsMap[Constants.WORKSHOP_ID] = workshopId
            paramsMap[Constants.BRANCH_ID] = branchCode
            mldDebtList.postValue(callService { repository.getWorkshopsDebtsList(paramsMap) })
        }
    }
}

