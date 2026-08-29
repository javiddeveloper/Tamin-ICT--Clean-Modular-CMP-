package com.tamin.taminhamrah.feature.pensionSurvivor

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.PensionSurvivorScreen
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.relation.SharedDeceasedDocument
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.SurvivorInfoScreen
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Serializable
data object PensionSurvivorRoute

@Serializable
data class SurvivorInfoRoute(
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    val fatherName: String,
    val idCardNumber: String,
    val cityOfIssue: String,
    val genderCode: String,
    val genderDesc: String,
    val dateOfBirth: String,
    val insuranceId: String,
    val tendencyCode: String,
    val deceasedNationalId: String,
    val branchCode: String = "",
    val deceasedInsuranceId: String = "",
    val sharedDeceasedDocsPayload: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val mobileNumber: String = "",
)

fun NavController.navigateToPensionSurvivor(navOptions: NavOptions? = null) {
    navigate(PensionSurvivorRoute, navOptions)
}

fun NavGraphBuilder.pensionSurvivorScreen(
    navController: NavController,
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<PensionSurvivorRoute> { backStackEntry ->
        val savedNationalId by backStackEntry.savedStateHandle
            .getStateFlow(PENSION_SURVIVOR_RESULT_NATIONAL_ID_KEY, "")
            .collectAsStateWithLifecycle()
        val savedAddress by backStackEntry.savedStateHandle
            .getStateFlow(PENSION_SURVIVOR_RESULT_ADDRESS_KEY, "")
            .collectAsStateWithLifecycle()
        val savedPhoneNumber by backStackEntry.savedStateHandle
            .getStateFlow(PENSION_SURVIVOR_RESULT_PHONE_KEY, "")
            .collectAsStateWithLifecycle()
        val savedMobileNumber by backStackEntry.savedStateHandle
            .getStateFlow(PENSION_SURVIVOR_RESULT_MOBILE_KEY, "")
            .collectAsStateWithLifecycle()

        PensionSurvivorScreen(
            onBack = onBack,
            onNavigateToSurvivorInfo = { args ->
                navController.navigate(
                    SurvivorInfoRoute(
                        firstName = args.survivor.firstName,
                        lastName = args.survivor.lastName,
                        nationalId = args.survivor.nationalId,
                        fatherName = args.survivor.fatherName,
                        idCardNumber = args.survivor.idCardNumber,
                        cityOfIssue = args.survivor.cityOfIssue,
                        genderCode = args.survivor.genderCode,
                        genderDesc = args.survivor.genderDesc,
                        dateOfBirth = args.survivor.dateOfBirth,
                        insuranceId = args.survivor.insuranceId,
                        tendencyCode = args.survivor.tendencyCode,
                        deceasedNationalId = args.deceasedNationalId,
                        branchCode = args.branchCode,
                        deceasedInsuranceId = args.deceasedInsuranceId,
                        sharedDeceasedDocsPayload = encodeSharedDeceasedDocs(args.sharedDeceasedDocuments),
                        address = args.draft?.address.orEmpty(),
                        phoneNumber = args.draft?.phoneNumber.orEmpty(),
                        mobileNumber = args.draft?.mobileNumber.orEmpty(),
                    ),
                )
            },
            savedDraftNationalId = savedNationalId.takeIf(String::isNotBlank),
            savedDraft = savedNationalId.takeIf(String::isNotBlank)?.let {
                SurvivorContactDraft(
                    address = savedAddress,
                    phoneNumber = savedPhoneNumber,
                    mobileNumber = savedMobileNumber,
                )
            },
            onSavedDraftConsumed = {
                backStackEntry.savedStateHandle[PENSION_SURVIVOR_RESULT_NATIONAL_ID_KEY] = ""
                backStackEntry.savedStateHandle[PENSION_SURVIVOR_RESULT_ADDRESS_KEY] = ""
                backStackEntry.savedStateHandle[PENSION_SURVIVOR_RESULT_PHONE_KEY] = ""
                backStackEntry.savedStateHandle[PENSION_SURVIVOR_RESULT_MOBILE_KEY] = ""
            },
        )
    }

    composableWithFadeTransitions<SurvivorInfoRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<SurvivorInfoRoute>()
        SurvivorInfoScreen(
            survivor = route.toSurvivor(),
            deceasedNationalId = route.deceasedNationalId,
            branchCode = route.branchCode,
            deceasedInsuranceId = route.deceasedInsuranceId,
            sharedDeceasedDocuments = decodeSharedDeceasedDocs(route.sharedDeceasedDocsPayload),
            address = route.address,
            phoneNumber = route.phoneNumber,
            mobileNumber = route.mobileNumber,
            onSaved = { nationalId, draft ->
                navController.previousBackStackEntry
                    ?.let { entry ->
                        entry.savedStateHandle[PENSION_SURVIVOR_RESULT_NATIONAL_ID_KEY] = nationalId
                        entry.savedStateHandle[PENSION_SURVIVOR_RESULT_ADDRESS_KEY] = draft.address
                        entry.savedStateHandle[PENSION_SURVIVOR_RESULT_PHONE_KEY] = draft.phoneNumber
                        entry.savedStateHandle[PENSION_SURVIVOR_RESULT_MOBILE_KEY] = draft.mobileNumber
                    }
            },
            onBack = { navController.popBackStack() },
        )
    }
}

data class NavigateToSurvivorInfoArgs(
    val survivor: SurvivorDependentPR,
    val deceasedNationalId: String,
    val draft: SurvivorContactDraft?,
    val branchCode: String,
    val deceasedInsuranceId: String,
    val sharedDeceasedDocuments: ImmutableList<SharedDeceasedDocument>,
)

internal fun encodeSharedDeceasedDocs(docs: List<SharedDeceasedDocument>): String {
    if (docs.isEmpty()) return ""
    return docs.joinToString(DOC_ENTRY_SEPARATOR) { "${it.documentTypeCode}$DOC_FIELD_SEPARATOR${it.guid}" }
}

internal fun decodeSharedDeceasedDocs(payload: String): List<SharedDeceasedDocument> {
    if (payload.isBlank()) return emptyList()
    return payload.split(DOC_ENTRY_SEPARATOR).mapNotNull { entry ->
        val parts = entry.split(DOC_FIELD_SEPARATOR, limit = 2)
        if (parts.size != 2) return@mapNotNull null
        val type = parts[0].takeIf(String::isNotBlank) ?: return@mapNotNull null
        val guid = parts[1].takeIf(String::isNotBlank) ?: return@mapNotNull null
        SharedDeceasedDocument(documentTypeCode = type, guid = guid)
    }
}

private fun SurvivorInfoRoute.toSurvivor(): SurvivorDependentPR {
    return SurvivorDependentPR(
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        fatherName = fatherName,
        idCardNumber = idCardNumber,
        cityOfIssue = cityOfIssue,
        genderCode = genderCode,
        genderDesc = genderDesc,
        dateOfBirth = dateOfBirth,
        insuranceId = insuranceId,
        tendencyCode = tendencyCode,
    )
}

private const val PENSION_SURVIVOR_RESULT_NATIONAL_ID_KEY = "pensionSurvivorSavedNationalId"
private const val PENSION_SURVIVOR_RESULT_ADDRESS_KEY = "pensionSurvivorSavedAddress"
private const val PENSION_SURVIVOR_RESULT_PHONE_KEY = "pensionSurvivorSavedPhone"
private const val PENSION_SURVIVOR_RESULT_MOBILE_KEY = "pensionSurvivorSavedMobile"
private const val DOC_ENTRY_SEPARATOR = ";"
private const val DOC_FIELD_SEPARATOR = "|"
