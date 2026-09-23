package com.tamin.taminhamrah.feature.taminServices

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui.BeneficiariesRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui.BeneficiariesViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.ui.InstallmentManagementRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.ui.InstallmentManagementViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.ui.InstallmentDebitListRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.ui.InstallmentDebitListViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui.InstallmentLetterRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui.InstallmentLetterViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui.PaymentSheetRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui.PaymentSheetViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.ConstructionInsuranceRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.ConstructionInsuranceViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui.ViewDetailRequestRoute
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui.ViewDetailRequestViewModel
import com.tamin.taminhamrah.feature.taminServices.occurrence.OccurrenceScreen
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesRoute
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.EmployerOnlineServicesViewModel
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionRoute
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionViewModel
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.WorkshopInspectionRoute
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.WorkshopInspectionViewModel
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.SendHistoryToInstitutionsScreen
import com.tamin.taminhamrah.feature.taminServices.ui.TaminServicesRoute
import com.tamin.taminhamrah.feature.taminServices.ui.TamminServicesViewModel
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.FuneralAllowanceViewModel
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.FuneralAllowanceRoute
import com.tamin.taminhamrah.feature.taminServices.workersPayment.WorkersPaymentViewModel
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.WorkersPaymentRoute
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.payment.PaymentRequestDN
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object TaminServicesRoute

@Serializable
data object SendInsuranceHistoryToInstitutionsRoute

@Serializable
data object InspectionRoute

@Serializable
data object WorkshopInspectionRoute

@Serializable
data object OccurrenceRoute


@Serializable
data object FuneralAllowanceRoute

@Serializable
data object EmployerOnlineServicesRoute

@Serializable
data object ConstructionInsuranceRoute

/** نمایش جزییات درخواست — عملیات option "۱" / the list row's «جزئیات درخواست» button. */
@Serializable
data class ViewDetailRequestRoute(val fileNumber: Long?, val requestNumber: Long?)

/** صدور و مدیریت برگه پرداخت — عملیات option "۲". */
@Serializable
data class PaymentSheetRoute(val debitNumber: String, val branchCode: String)

/** مدیریت پرداخت اقساط — عملیات option "۳". */
@Serializable
data class InstallmentLetterRoute(val fileNumber: Long?, val workshopId: String, val branchId: String)

/** مدیریت اقساط و برگ پرداخت — one تقسیط‌نامه row's own عملیات option "۱". */
@Serializable
data class InstallmentManagementRoute(
    val fileNumber: Long?,
    val workshopId: String?,
    val branchId: String,
    val debitNumber: String,
    val debitStepDescription: String? = null,
)

/** بدهی‌های تقسیط‌شده — one تقسیط‌نامه row's own عملیات option "۲". */
@Serializable
data class InstallmentDebitListRoute(
    val fileNumber: Long?,
    val workshopId: String?,
    val branchId: String,
    val debitNumber: String,
)

/** ذینفعان کارگاه — عملیات option "۴". */
@Serializable
data class BeneficiariesRoute(
    val requestNumber: Long?,
    val fileNumber: Long?,
    val requestDate: String?,
    val workshopId: String?,
    val branchCode: String?,
)

@Serializable
data object WorkersPaymentInfoRoute

fun NavController.navigateToTaminServices(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(TaminServicesRoute, builder)
}

fun NavController.navigateToConstructionInsurance(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(ConstructionInsuranceRoute, builder)
}

fun NavController.navigateToViewDetailRequest(fileNumber: Long?, requestNumber: Long?) {
    navigate(ViewDetailRequestRoute(fileNumber, requestNumber))
}

fun NavController.navigateToPaymentSheet(debitNumber: String, branchCode: String) {
    navigate(PaymentSheetRoute(debitNumber, branchCode))
}

fun NavController.navigateToInstallmentLetter(fileNumber: Long?, workshopId: String, branchId: String) {
    navigate(InstallmentLetterRoute(fileNumber, workshopId, branchId))
}

fun NavController.navigateToInstallmentManagement(
    fileNumber: Long?,
    workshopId: String?,
    branchId: String,
    debitNumber: String,
    debitStepDescription: String? = null,
) {
    navigate(InstallmentManagementRoute(fileNumber, workshopId, branchId, debitNumber, debitStepDescription))
}

fun NavController.navigateToInstallmentDebitList(
    fileNumber: Long?,
    workshopId: String?,
    branchId: String,
    debitNumber: String,
) {
    navigate(InstallmentDebitListRoute(fileNumber, workshopId, branchId, debitNumber))
}

fun NavController.navigateToBeneficiaries(
    requestNumber: Long?,
    fileNumber: Long?,
    requestDate: String?,
    workshopId: String?,
    branchCode: String?,
) {
    navigate(BeneficiariesRoute(requestNumber, fileNumber, requestDate, workshopId, branchCode))
}

fun NavController.navigateToSendInsuranceHistoryToInstitutions(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(SendInsuranceHistoryToInstitutionsRoute, builder)
}

fun NavController.navigateToOccurrence(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(OccurrenceRoute, builder)
}

fun NavController.navigateToFuneralAllowance(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(FuneralAllowanceRoute, builder)
}

fun NavController.navigateToWorkersPaymentInfo(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(WorkersPaymentInfoRoute, builder)
}

fun NavGraphBuilder.taminServicesScreen(
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    composableWithFadeTransitions<TaminServicesRoute> {
        val TaminServicesViewModel: TamminServicesViewModel = koinViewModel()
        TaminServicesRoute(
            viewModel = TaminServicesViewModel,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl,
            onBackClicked = onBackClicked
        )
    }
}

fun NavGraphBuilder.sendInsuranceHistoryToInstitutionsScreen(
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    composableWithFadeTransitions<SendInsuranceHistoryToInstitutionsRoute> {
        SendHistoryToInstitutionsScreen(
            onBack = onBack,
            onDone = onDone
        )
    }
}

fun NavController.navigateToInspection(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(InspectionRoute, builder)
}

fun NavGraphBuilder.inspectionScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<InspectionRoute> {
        val viewModel: InspectionViewModel = koinViewModel()
        InspectionRoute(
            viewModel = viewModel,
            onBackClicked = onBack
        )
    }
}

fun NavController.navigateToWorkshopInspection(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(WorkshopInspectionRoute, builder)
}

fun NavGraphBuilder.workshopInspectionScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<WorkshopInspectionRoute> {
        val viewModel: WorkshopInspectionViewModel = koinViewModel()
        WorkshopInspectionRoute(
            viewModel = viewModel,
            onBackClicked = onBack
        )
    }
}

fun NavGraphBuilder.occurrenceScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    composableWithFadeTransitions<OccurrenceRoute> {
        OccurrenceScreen(
            onBack = onBack,
            onDone = onDone,
        )
    }
}


fun NavGraphBuilder.funeralAllowanceScreen(
    onBack: () -> Unit,
    onNavigateToBankAccount: () -> Unit,
) {
    composableWithFadeTransitions<FuneralAllowanceRoute> {
        val viewModel: FuneralAllowanceViewModel = koinViewModel()
        FuneralAllowanceRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
            onNavigateToBankAccount = onNavigateToBankAccount,
        )
    }
}

fun NavController.navigateToEmployerOnlineServices(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(EmployerOnlineServicesRoute, builder)
}

fun NavGraphBuilder.employerOnlineServicesScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<EmployerOnlineServicesRoute> {
        val viewModel: EmployerOnlineServicesViewModel = koinViewModel()
        EmployerOnlineServicesRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
        )
    }
}

fun NavGraphBuilder.workersPaymentInfoScreen(
    onBack: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onNavigateToPayment: (PaymentRequestDN) -> Unit,
) {
    composableWithFadeTransitions<WorkersPaymentInfoRoute> {
        val viewModel: WorkersPaymentViewModel = koinViewModel()
        WorkersPaymentRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
            onOpenUrl = onOpenUrl,
            onNavigateToPayment = onNavigateToPayment,
        )
    }
}

fun NavGraphBuilder.constructionInsuranceScreen(
    onBack: () -> Unit,
    onNavigateToViewDetail: (fileNumber: Long?, requestNumber: Long?) -> Unit,
    onNavigateToPaymentSheet: (debitNumber: String, branchCode: String) -> Unit,
    onNavigateToInstallmentLetter: (fileNumber: Long?, workshopId: String, branchId: String) -> Unit,
    onNavigateToBeneficiaries: (
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String?,
        workshopId: String?,
        branchCode: String?,
    ) -> Unit,
) {
    composableWithFadeTransitions<ConstructionInsuranceRoute> {
        val viewModel: ConstructionInsuranceViewModel = koinViewModel()
        ConstructionInsuranceRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
            onNavigateToViewDetail = onNavigateToViewDetail,
            onNavigateToPaymentSheet = onNavigateToPaymentSheet,
            onNavigateToInstallmentLetter = onNavigateToInstallmentLetter,
            onNavigateToBeneficiaries = onNavigateToBeneficiaries,
        )
    }
}

fun NavGraphBuilder.viewDetailRequestScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<ViewDetailRequestRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ViewDetailRequestRoute>()
        val viewModel: ViewDetailRequestViewModel = koinViewModel()
        ViewDetailRequestRoute(
            viewModel = viewModel,
            fileNumber = route.fileNumber,
            requestNumber = route.requestNumber,
            onBackClicked = onBack,
        )
    }
}

fun NavGraphBuilder.paymentSheetScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<PaymentSheetRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<PaymentSheetRoute>()
        val viewModel: PaymentSheetViewModel = koinViewModel()
        PaymentSheetRoute(
            viewModel = viewModel,
            debitNumber = route.debitNumber,
            branchCode = route.branchCode,
            onBackClicked = onBack,
        )
    }
}

fun NavGraphBuilder.installmentLetterScreen(
    onBack: () -> Unit,
    onNavigateToInstallmentManagement: (
        fileNumber: Long?,
        workshopId: String?,
        branchId: String,
        debitNumber: String,
        debitStepDescription: String?,
    ) -> Unit,
    onNavigateToInstallmentDebitList: (fileNumber: Long?, workshopId: String?, branchId: String, debitNumber: String) -> Unit,
) {
    composableWithFadeTransitions<InstallmentLetterRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<InstallmentLetterRoute>()
        val viewModel: InstallmentLetterViewModel = koinViewModel()
        InstallmentLetterRoute(
            viewModel = viewModel,
            fileNumber = route.fileNumber,
            workshopId = route.workshopId,
            branchId = route.branchId,
            onBackClicked = onBack,
            onNavigateToInstallmentManagement = onNavigateToInstallmentManagement,
            onNavigateToInstallmentDebitList = onNavigateToInstallmentDebitList,
        )
    }
}

fun NavGraphBuilder.installmentManagementScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<InstallmentManagementRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<InstallmentManagementRoute>()
        val viewModel: InstallmentManagementViewModel = koinViewModel()
        InstallmentManagementRoute(
            viewModel = viewModel,
            fileNumber = route.fileNumber,
            workshopId = route.workshopId,
            branchId = route.branchId,
            debitNumber = route.debitNumber,
            debitStepDescription = route.debitStepDescription,
            onBackClicked = onBack,
        )
    }
}

fun NavGraphBuilder.installmentDebitListScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<InstallmentDebitListRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<InstallmentDebitListRoute>()
        val viewModel: InstallmentDebitListViewModel = koinViewModel()
        InstallmentDebitListRoute(
            viewModel = viewModel,
            fileNumber = route.fileNumber,
            workshopId = route.workshopId,
            branchId = route.branchId,
            debitNumber = route.debitNumber,
            onBackClicked = onBack,
        )
    }
}

fun NavGraphBuilder.beneficiariesScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<BeneficiariesRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<BeneficiariesRoute>()
        val viewModel: BeneficiariesViewModel = koinViewModel()
        BeneficiariesRoute(
            viewModel = viewModel,
            requestNumber = route.requestNumber,
            fileNumber = route.fileNumber,
            requestDate = route.requestDate,
            workshopId = route.workshopId,
            branchCode = route.branchCode,
            onBackClicked = onBack,
        )
    }
}
