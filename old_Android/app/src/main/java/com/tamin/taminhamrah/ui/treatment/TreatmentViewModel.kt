package com.tamin.taminhamrah.ui.treatment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.profile.RelatedPersonInfo
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.DependantUserUnder18Response
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescription
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescriptionDetail
import com.tamin.taminhamrah.data.remote.models.services.electronicPrescription.ElectronicPrescriptionPriceResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserResponse
import com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved.EligibilityTreatmentResponse
import com.tamin.taminhamrah.data.remote.models.user.LackEntitlementResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.LoadingState
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.treatment.electronicPrescription.model.PrescriptionQuery
import com.tamin.taminhamrah.utils.ConfigApp
import com.tamin.taminhamrah.utils.MultipleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TreatmentViewModel
@Inject constructor(private val repository: ServiceRepository) : BaseViewModel() {

    val mldDeservedMainUser = MutableLiveData<LackEntitlementResponse>()
    val mldDependents18 = MutableLiveData<DependantUserUnder18Response>()
    val mldPrice = MutableLiveData<ElectronicPrescriptionPriceResponse>()
    val mldCurrentUser = MutableLiveData<CurrentUserResponse>()
    val mldPDF = MultipleLiveData<PdfDownloadResponse>()
    val mldTestResultPDF = MultipleLiveData<PdfDownloadResponse>()
    val mldEligibility = MutableLiveData<EligibilityTreatmentResponse>()
    val usersInfo = ArrayList<MenuModel>()
    var navigateToPDF = false


    var selectedUserNationalId: String? = null
    var selectedDependantUserTitle: String? = null

    var listLabel: String = ""

    private val _query = MutableStateFlow<PrescriptionQuery?>(null)
    val prescriptionTypeL: String get() = _query.value?.type ?: "1"

    var startDate = System.currentTimeMillis() - Constants.A_WEEK_IN_MILLI_SECONDS
    var endDate = System.currentTimeMillis()

    init {
        Timber.tag("LOG").d("init")
    }

    fun getCurrentUserInfo() {
        if (mldCurrentUser.value == null)
            viewModelScope.launch {
                mldCurrentUser.postValue(callService {
                    repository.getCurrentUser()
                })
            }
    }

    private var dependantUserFlowPaging: Pager<Int, RelatedPersonInfo>? = null
    fun getDependantUserUnder18Paging(): Flow<PagingData<RelatedPersonInfo>>? {
        val paramsMap: MutableMap<String, String> = mutableMapOf()
        paramsMap["nationalCode"] = getMainUserNationalId()
        if (dependantUserFlowPaging == null)
            dependantUserFlowPaging =
                createPager(repository::getDependantUserUnder18, paramsMap = paramsMap)
        return dependantUserFlowPaging?.flow?.cachedIn(viewModelScope)
    }

    fun getElectronicPrescriptionDetail(
        noteHeadID: Long,
        type: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String
    ): Flow<PagingData<ElectronicPrescriptionDetail>> {

        val result = createPager(
            repository::getElectronicPrescriptionDetail, paramsMap = mutableMapOf(
                "noteHeadID" to noteHeadID.toString(),
                "type" to type,
                "nationalCode" to nationalCode,
                "childNationalCode" to childNationalCode,
                "flagSata" to flagSata
            ), queryPageSize = "50"
        )
        return result.flow.cachedIn(viewModelScope)
    }

    fun getElectronicPrescriptionPrice(noteHeadID: Long, nationalCode: String) {
        viewModelScope.launch {
            mldPrice.postValue(callService {
                repository.getElectronicPrescriptionPrice(noteHeadID.toString(), nationalCode)
            })
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val prescriptionFlow: Flow<PagingData<ElectronicPrescription>> =
        _query
            .filterNotNull()
            .flatMapLatest { query ->
                createPager(
                    repository::getElectronicPrescriptionList,
                    paramsMap = mutableMapOf(
                        "requestTypeId" to query.type,
                        "startDate" to query.startDate.toString(),
                        "endDate" to query.endDate.toString(),
                        "nationalCode" to query.nationalCode,
                        "dependantUserNationalCode" to query.dependantCode
                    )
                ).flow
            }.cachedIn(viewModelScope)

    fun setPrescriptionType(type: String) {
        _query.update { it?.copy(type = type) }
    }

    fun doSearch() {
        _query.update {
            it?.copy(
                startDate = startDate,
                endDate = endDate,
                nationalCode = getMainUserNationalId(),
                dependantCode = selectedUserNationalId ?: "0",
                trigger = it.trigger + 1
            )
        }
    }

    fun initQuery() {
        val nationalCode = getMainUserNationalId()
        if (nationalCode != "0" && nationalCode.isNotEmpty()) {
            _query.update {
                it ?: PrescriptionQuery(
                    nationalCode = nationalCode,
                    dependantCode = selectedUserNationalId ?: "0",
                    startDate = startDate,
                    endDate = endDate
                )
            }
        }
    }

    fun getPrescriptionPdfFile(prescriptionID: String, showPDF: Boolean = false) {
        viewModelScope.launch {
            navigateToPDF = showPDF
            mldPDF.postValue(callService {
                repository.getPrescriptionPdfFile(prescriptionID = prescriptionID)
            })

        }
    }

    fun getTestResultPdfFile(
        patientID: String,
        noteHeadEprescID: String,
        currentUserNationalCode: String
    ) {
        viewModelScope.launch {
            mldTestResultPDF.postValue(callService {
                repository.downloadTestResultPdf(
                    patientID,
                    noteHeadEprescID,
                    currentUserNationalCode
                )
            })
        }
    }

    fun getUserNationalCode(): String {
        return mldCurrentUser.value?.data?.nationalCode ?: "0"
    }

    fun getItemsList(): ArrayList<MenuModel> {
        return arrayListOf(
            MenuModel(
                id = "1",
                titleStringResId = R.string.my_prescriptions,
                iconRes = R.drawable.ic_ep,
                descStringResId = R.string.my_prescriptions_desc
            ),
            MenuModel(
                id = "2",
                titleStringResId = R.string.confirmations_medical_authorities,
                iconRes = R.drawable.ic_confirmation,
                descStringResId = R.string.desc_confirmations_medical_authorities
            ),
            MenuModel(
                id = "3",
                titleStringResId = R.string.miscellaneous_damages,
                iconRes = R.drawable.ic_loss,
                descStringResId = R.string.reimbursement_medical
            ),
        )

    }

    fun getPrescriptionItemsList() = arrayListOf(
        MenuModel(
            id = "2",
            titleStringResId = R.string.para_clinic,
            iconRes = R.drawable.ic_paraclinic_treatment
        ),
        MenuModel(
            id = "5",
            titleStringResId = R.string.medical_service,
            iconRes = R.drawable.ic_medical_treatment
        ),
        MenuModel(
            id = "3",
            titleStringResId = R.string.visit,
            iconRes = R.drawable.ic_visit_treatment
        ),
        MenuModel(
            id = "1",
            titleStringResId = R.string.medical,
            iconRes = R.drawable.ic_drug_treatment
        )
    )

    fun getUserAndDependentsInfo() {
        if (mldDeservedMainUser.value == null)
            viewModelScope.launch {
                mldLoadingState.postValue(LoadingState.LOADING)

                val mainUserInfo = async(Dispatchers.IO) {
                    repository.getBookletStatus()
                }


                val responseMainUserInfo = mainUserInfo.await()

                if (responseMainUserInfo.isSuccess) {
                    mldDeservedMainUser.postValue(responseMainUserInfo)
                    val list = responseMainUserInfo.data?.list ?: emptyList()
                    if (list.isNotEmpty()) {
                        val nationalId = list[0].natCode ?: ""
                        val dependentsInfo = async(Dispatchers.IO) {
                            repository.getDependantUserUnder18(mutableMapOf("nationalCode" to nationalId))
                        }
                        list[0].apply {
                            val mainUser = MenuModel("$firstName $lastName- $natCode", natCode)
                            if (mainUser !in usersInfo)
                                usersInfo.add(mainUser)
                        }
                        val responseDependentsInfo = dependentsInfo.await()
                        mldLoadingState.postValue(LoadingState.NOT_LOADING)
                        when {
                            !responseDependentsInfo.isSuccess -> {
                                mldErrorState.postValue(responseDependentsInfo)
                            }

                            else -> {
                                mldDependents18.postValue(responseDependentsInfo)

                                responseDependentsInfo.data?.list?.forEach { dependentsInfo ->
                                    usersInfo.addAll(
                                        dependentsInfo.getDependantList()
                                            .filter { it !in usersInfo })
                                }
                            }
                        }
                    }

                } else {
                    responseMainUserInfo.isBackToPrevious = true
                    mldErrorState.postValue(responseMainUserInfo)
                }
            }
    }

    fun getMainUserNationalId() = getNationalCode()


    private fun createUrlRequest(nationalCode: String): String {
        return Constants.BASE_URL_MEDICAL +
                ConfigApp.PostfixUrlEligibility +
                nationalCode
    }

    fun getUsersInfoLocalPaging(usersInfo: ArrayList<MenuModel>) =
        createLocalPager(usersInfo).flow.cachedIn(viewModelScope)


}

