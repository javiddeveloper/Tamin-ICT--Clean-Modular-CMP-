package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SurvivorRelationClassifier
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.requiredDocuments
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoUiState.PartialState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorUploadedDocument
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.PensionDocDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.personal.SaveSurvivorInfoUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_doc_format_error
import taminx.core.core_ui.pension_survivor_error_address_required
import taminx.core.core_ui.pension_survivor_error_mobile_invalid
import taminx.core.core_ui.pension_survivor_error_need_upload_documents
import taminx.core.core_ui.pension_survivor_error_phone_invalid
import taminx.core.core_ui.pension_survivor_survivor_info_save_success

class SurvivorInfoViewModel(
    private val saveSurvivorInfoUseCase: SaveSurvivorInfoUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
) : BaseViewModel<SurvivorInfoUiState, PartialState, SurvivorInfoEvent, SurvivorInfoIntent>(
    initialState = SurvivorInfoUiState(),
) {

    override fun handleIntent(intent: SurvivorInfoIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { error ->
            sendEvent(SurvivorInfoEvent.ShowError(error.toSingleLineMessage()))
            emit(createErrorState(error.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: SurvivorInfoIntent): Flow<PartialState> = flow {
        when (intent) {
            is SurvivorInfoIntent.Init -> {
                if (uiState.value.survivor == null) {
                    val nowMs = Clock.System.now().toEpochMilliseconds()
                    val ageYears = SurvivorRelationClassifier.ageYearsFromBirthDateString(
                        dateOfBirth = intent.survivor.dateOfBirth,
                        nowMs = nowMs,
                    )
                    val classification = SurvivorRelationClassifier.classify(
                        tendencyCode = intent.survivor.tendencyCode,
                        genderCode = intent.survivor.genderCode,
                        ageYears = ageYears,
                    )
                    emit(
                        PartialState.Initialized(
                            survivor = intent.survivor,
                            deceasedNationalId = intent.deceasedNationalId,
                            branchCode = intent.branchCode,
                            deceasedInsuranceId = intent.deceasedInsuranceId,
                            address = intent.address,
                            phoneNumber = intent.phoneNumber,
                            mobileNumber = intent.mobileNumber,
                            relationKind = classification.kind,
                            dependencyType = classification.dependencyType,
                            ageYears = ageYears,
                            requiredDocuments = classification.kind.requiredDocuments(),
                            sharedDeceasedDocuments = intent.sharedDeceasedDocuments.toImmutableList(),
                        ),
                    )
                }
            }

            is SurvivorInfoIntent.AddressChanged -> {
                emit(PartialState.AddressChanged(intent.value))
                emit(PartialState.FieldError(null))
            }

            is SurvivorInfoIntent.PhoneNumberChanged -> {
                emit(
                    PartialState.PhoneNumberChanged(
                        intent.value.filter(Char::isDigit).take(MAX_PHONE_LENGTH),
                    ),
                )
                emit(PartialState.FieldError(null))
            }

            is SurvivorInfoIntent.MobileNumberChanged -> {
                emit(
                    PartialState.MobileNumberChanged(
                        intent.value.filter(Char::isDigit).take(MAX_MOBILE_LENGTH),
                    ),
                )
                emit(PartialState.FieldError(null))
            }

            is SurvivorInfoIntent.DocumentClicked -> {
                emit(PartialState.DocumentSourceRequested(intent.type))
            }

            is SurvivorInfoIntent.DocumentImagePicked -> uploadDocument(intent)

            SurvivorInfoIntent.DismissDocumentSource -> {
                emit(PartialState.DocumentSourceDismissed)
            }

            is SurvivorInfoIntent.RemoveDocument -> {
                emit(PartialState.DocumentRemoved(intent.type))
            }

            SurvivorInfoIntent.ShowIdentity -> {
                emit(PartialState.IdentitySheetVisibility(true))
            }

            SurvivorInfoIntent.DismissIdentity -> {
                emit(PartialState.IdentitySheetVisibility(false))
            }

            SurvivorInfoIntent.Save -> saveSurvivor()
            SurvivorInfoIntent.OnBack -> sendEvent(SurvivorInfoEvent.NavigateBack)
        }
    }

    override fun reduceState(
        currentState: SurvivorInfoUiState,
        partialState: PartialState,
    ): SurvivorInfoUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Initialized -> currentState.copy(
            survivor = partialState.survivor,
            deceasedNationalId = partialState.deceasedNationalId,
            branchCode = partialState.branchCode,
            deceasedInsuranceId = partialState.deceasedInsuranceId,
            address = partialState.address,
            phoneNumber = partialState.phoneNumber,
            mobileNumber = partialState.mobileNumber,
            relationKind = partialState.relationKind,
            dependencyType = partialState.dependencyType,
            ageYears = partialState.ageYears,
            requiredDocuments = partialState.requiredDocuments,
            sharedDeceasedDocuments = partialState.sharedDeceasedDocuments,
            fieldError = null,
            documentError = null,
        )
        is PartialState.AddressChanged -> currentState.copy(address = partialState.value)
        is PartialState.PhoneNumberChanged -> currentState.copy(phoneNumber = partialState.value)
        is PartialState.MobileNumberChanged -> currentState.copy(mobileNumber = partialState.value)
        is PartialState.FieldError -> currentState.copy(fieldError = partialState.message)
        is PartialState.DocumentSourceRequested -> currentState.copy(
            activeDocument = partialState.type,
            documentError = null,
        )
        PartialState.DocumentSourceDismissed -> currentState.copy(activeDocument = null)
        is PartialState.DocumentUploadStarted -> currentState.copy(
            uploadingDocument = partialState.type,
            failedDocument = null,
            documentError = null,
            activeDocument = null,
        )
        is PartialState.DocumentUploaded -> currentState.copy(
            documents = currentState.documents.toPersistentMap().put(
                partialState.document.type,
                partialState.document,
            ),
            uploadingDocument = null,
            failedDocument = null,
            documentError = null,
        )
        is PartialState.DocumentUploadFailed -> currentState.copy(
            uploadingDocument = null,
            failedDocument = partialState.type,
            documentError = partialState.message,
        )
        is PartialState.DocumentRemoved -> currentState.copy(
            documents = currentState.documents.toPersistentMap().remove(partialState.type),
            failedDocument = if (currentState.failedDocument == partialState.type) {
                null
            } else {
                currentState.failedDocument
            },
            documentError = if (currentState.failedDocument == partialState.type) {
                null
            } else {
                currentState.documentError
            },
            uploadingDocument = if (currentState.uploadingDocument == partialState.type) {
                null
            } else {
                currentState.uploadingDocument
            },
            activeDocument = null,
        )
        is PartialState.IdentitySheetVisibility -> currentState.copy(
            showIdentitySheet = partialState.visible,
        )
        is PartialState.Error -> currentState.copy(isLoading = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private suspend fun FlowCollector<PartialState>.uploadDocument(
        intent: SurvivorInfoIntent.DocumentImagePicked,
    ) {
        if (uiState.value.isDocumentUploading) return

        if (!isJpegFileName(intent.fileName)) {
            emit(
                PartialState.DocumentUploadFailed(
                    type = intent.type,
                    message = getString(Res.string.occurrence_doc_format_error),
                ),
            )
            return
        }

        if (intent.bytes.size > MAX_DOCUMENT_SIZE_BYTES) {
            emit(
                PartialState.DocumentUploadFailed(
                    type = intent.type,
                    message = getString(Res.string.occurrence_doc_format_error),
                ),
            )
            return
        }

        emit(PartialState.DocumentUploadStarted(intent.type))
        try {
            val guid = uploadImageUseCase(
                UploadImageRequestDN(
                    fileName = intent.fileName,
                    bytes = intent.bytes,
                ),
            ).first()
            emit(
                PartialState.DocumentUploaded(
                    SurvivorUploadedDocument(
                        type = intent.type,
                        fileName = intent.fileName,
                        bytes = intent.bytes,
                        guid = guid,
                    ),
                ),
            )
        } catch (e: Exception) {
            emit(
                PartialState.DocumentUploadFailed(
                    type = intent.type,
                    message = e.toSingleLineMessage(),
                ),
            )
        }
    }

    private suspend fun FlowCollector<PartialState>.saveSurvivor() {
        val state = uiState.value
        val survivor = state.survivor ?: return
        if (state.isLoading) return

        val validationError = validationError(state)
        if (validationError != null) {
            emit(PartialState.FieldError(validationError))
            sendEvent(SurvivorInfoEvent.ShowError(validationError))
            return
        }

        emit(PartialState.Loading(true))
        val relationDocs = state.requiredDocuments.mapNotNull { type ->
            state.documents[type]?.let { uploaded ->
                PensionDocDN(
                    documentType = uploaded.type.code,
                    guid = uploaded.guid,
                )
            }
        }
        val deceasedDocs = state.sharedDeceasedDocuments.map { doc ->
            PensionDocDN(
                documentType = doc.documentTypeCode,
                guid = doc.guid,
            )
        }

        saveSurvivorInfoUseCase(
            body = SaveSurvivorInfoDN(
                address = state.address,
                age = state.ageYears.toString(),
                birthDate = survivor.dateOfBirth.toLongOrNull(),
                branchCode = state.branchCode.ifBlank { null },
                survivorInsuranceId = survivor.insuranceId.ifBlank { null },
                survivorNationalId = survivor.nationalId.ifBlank { null },
                deathType = DEATH_TYPE_INSURED,
                dependencyType = state.dependencyType?.let { DependencyTypeDN(code = it.code) },
                fatherName = survivor.fatherName.ifBlank { null },
                firstName = survivor.firstName.ifBlank { null },
                gender = survivor.genderCode.ifBlank { null },
                idCardNumber = survivor.idCardNumber.ifBlank { null },
                insuranceNumber = state.deceasedInsuranceId.ifBlank { null },
                issuePlace = survivor.cityOfIssue.ifBlank { null },
                lastName = survivor.lastName.ifBlank { null },
                mobileNumber = state.mobileNumber,
                deceasedNationalId = state.deceasedNationalId.ifBlank { null },
                pensionRequestDocList = deceasedDocs + relationDocs,
                phoneNumber = state.phoneNumber,
                status = INITIAL_STATUS,
            ),
        ).collect { message ->
            emit(PartialState.Loading(false))
            sendEvent(
                SurvivorInfoEvent.Saved(
                    nationalId = survivor.nationalId,
                    draft = SurvivorContactDraft(
                        address = state.address,
                        phoneNumber = state.phoneNumber,
                        mobileNumber = state.mobileNumber,
                    ),
                ),
            )
            sendEvent(
                SurvivorInfoEvent.ShowSuccess(
                    message = message?.takeIf(String::isNotBlank)
                        ?: getString(Res.string.pension_survivor_survivor_info_save_success),
                ),
            )
            sendEvent(SurvivorInfoEvent.NavigateBack)
        }
    }

    private suspend fun validationError(state: SurvivorInfoUiState): String? {
        val mobile = state.mobileNumber
        val phone = state.phoneNumber
        val address = state.address
        return when {
            mobile.isBlank() || mobile.length < MAX_MOBILE_LENGTH || !mobile.startsWith(MOBILE_PREFIX) -> {
                getString(Res.string.pension_survivor_error_mobile_invalid)
            }
            phone.isBlank() || phone.length < MIN_PHONE_LENGTH || !phone.startsWith(PHONE_PREFIX) -> {
                getString(Res.string.pension_survivor_error_phone_invalid)
            }
            address.isBlank() -> getString(Res.string.pension_survivor_error_address_required)
            !state.areRequiredDocumentsComplete -> {
                getString(Res.string.pension_survivor_error_need_upload_documents)
            }
            else -> null
        }
    }

    private companion object {
        const val INITIAL_STATUS = "3"
        const val DEATH_TYPE_INSURED = "1"
        const val MAX_PHONE_LENGTH = 11
        const val MAX_MOBILE_LENGTH = 11
        const val MIN_PHONE_LENGTH = 10
        const val MOBILE_PREFIX = "09"
        const val PHONE_PREFIX = "0"
        const val MAX_DOCUMENT_SIZE_BYTES = 2 * 1024 * 1024

        fun isJpegFileName(fileName: String): Boolean {
            val lower = fileName.lowercase()
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
        }
    }
}
