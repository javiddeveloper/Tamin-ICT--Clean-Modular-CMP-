package com.tamin.taminhamrah.feature.contractaffair

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import com.tamin.taminhamrah.feature.contractaffair.ui.ContractAffairsRoute
import com.tamin.taminhamrah.feature.contractaffair.ui.ContractAffairsViewModel
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.ContractPaymentCalcDetailRoute
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.ContractPaymentCalcDetailViewModel
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.ContractPremiumPaymentRoute
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.ContractPremiumPaymentViewModel
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.ContractPaymentHistoryRoute
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.ContractPaymentHistoryViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.serialization.Serializable

import com.tamin.taminhamrah.model.payment.PaymentRequestDN
import com.tamin.taminhamrah.model.common.FeatureFlag

@Serializable
data object ContractAffairsRoute

/** سوابق پرداخت — opened from «مشاهدهٔ پرداخت‌ها» in the امور قرارداد sheet. */
@Serializable
data class ContractPaymentHistoryRoute(
    val contractNumber: String,
    val insuranceType: String,
)

/** پرداخت حق بیمه — opened from «پرداخت حق بیمه» in the امور قرارداد sheet / card. */
@Serializable
data class ContractPremiumPaymentRoute(
    val contractNumber: String,
    val premiumTypeCode: String,
    val insuranceType: String,
)

/** جزئیات برگ پرداخت — opened from the محاسبهٔ حق بیمه result card. */
@Serializable
data class ContractPaymentCalcDetailRoute(
    val premiumTypeCode: String,
    val startDate: Long,
    val endDate: Long,
)

fun NavController.navigateToContractAffairs() {
    navigate(ContractAffairsRoute)
}

fun NavController.navigateToContractPaymentHistory(
    contractNumber: String,
    insuranceType: String,
) {
    navigate(ContractPaymentHistoryRoute(contractNumber, insuranceType))
}

fun NavController.navigateToContractPremiumPayment(
    contractNumber: String,
    premiumTypeCode: String,
    insuranceType: String,
) {
    navigate(ContractPremiumPaymentRoute(contractNumber, premiumTypeCode, insuranceType))
}

fun NavController.navigateToContractPaymentCalcDetail(
    premiumTypeCode: String,
    startDate: Long,
    endDate: Long,
) {
    navigate(ContractPaymentCalcDetailRoute(premiumTypeCode, startDate, endDate))
}

fun NavGraphBuilder.contractAffairsScreen(
    onBack: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onNavigateToPaymentHistory: (contractNumber: String, insuranceType: String) -> Unit,
    onNavigateToPremiumPayment: (
        contractNumber: String,
        premiumTypeCode: String,
        insuranceType: String,
    ) -> Unit,
) {
    composableWithFadeTransitions<ContractAffairsRoute> {
        val viewModel: ContractAffairsViewModel = koinViewModel()
        ContractAffairsRoute(
            viewModel = viewModel,
            onBackClicked = onBack,
            onNavigateToService = onNavigateToService,
            onOpenUrl = onOpenUrl,
            onNavigateToPaymentHistory = onNavigateToPaymentHistory,
            onNavigateToPremiumPayment = onNavigateToPremiumPayment,
        )
    }
}

fun NavGraphBuilder.contractPaymentHistoryScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<ContractPaymentHistoryRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ContractPaymentHistoryRoute>()
        val viewModel: ContractPaymentHistoryViewModel = koinViewModel()
        ContractPaymentHistoryRoute(
            viewModel = viewModel,
            contractNumber = route.contractNumber,
            insuranceType = route.insuranceType,
            onBackClicked = onBack,
        )
    }
}
fun NavGraphBuilder.contractPremiumPaymentScreen(
    onBack: () -> Unit,
    onNavigateToPaymentDetails: (
        premiumTypeCode: String,
        startDate: Long,
        endDate: Long,
    ) -> Unit,
    onNavigateToPayment: (PaymentRequestDN) -> Unit,
) {
    composableWithFadeTransitions<ContractPremiumPaymentRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ContractPremiumPaymentRoute>()
        val viewModel: ContractPremiumPaymentViewModel = koinViewModel()
        ContractPremiumPaymentRoute(
            viewModel = viewModel,
            contractNumber = route.contractNumber,
            premiumTypeCode = route.premiumTypeCode,
            insuranceType = route.insuranceType,
            onBackClicked = onBack,
            onNavigateToPaymentDetails = onNavigateToPaymentDetails,
            onNavigateToPayment = onNavigateToPayment,
        )
    }
}

fun NavGraphBuilder.contractPaymentCalcDetailScreen(
    onBack: () -> Unit,
) {
    composableWithFadeTransitions<ContractPaymentCalcDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<ContractPaymentCalcDetailRoute>()
        val viewModel: ContractPaymentCalcDetailViewModel = koinViewModel()
        ContractPaymentCalcDetailRoute(
            viewModel = viewModel,
            premiumTypeCode = route.premiumTypeCode,
            startDate = route.startDate,
            endDate = route.endDate,
            onBackClicked = onBack,
        )
    }
}
