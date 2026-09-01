package com.tamin.taminhamrah.feature.workshops

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document.ObjectionDocumentScreen
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list.ObjectionStatusScreen
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms.ObjectionSmsScreen
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

@Serializable
data object DebtObjectionStatusRoute

@Serializable
data class ObjectionSmsRoute(
    val seqNo: Long,
    val debitNumber: String,
    val workshopId: String,
    val objectionDate: String,
    val objectionTypeCode: String,
    val statusCode: String,
)

@Serializable
data class ObjectionDocumentRoute(
    val seqNo: Long,
    val debitNumber: String,
    val workshopId: String,
    val objectionDate: String,
    val objectionTypeCode: String,
    val statusCode: String,
)

fun NavController.navigateToDebtObjectionStatus() {
    navigate(DebtObjectionStatusRoute)
}

private fun NavController.navigateToObjectionSms(
    seqNo: Long,
    debitNumber: String,
    workshopId: String,
    objectionDate: String,
    objectionType: WorkShopObjectionType,
    status: WorkShopObjectionStatus,
) {
    navigate(
        ObjectionSmsRoute(
            seqNo, debitNumber, workshopId, objectionDate,
            objectionType.code.orEmpty(), status.code.orEmpty(),
        )
    )
}

private fun NavController.navigateToObjectionDocument(
    seqNo: Long,
    debitNumber: String,
    workshopId: String,
    objectionDate: String,
    objectionType: WorkShopObjectionType,
    status: WorkShopObjectionStatus,
) {
    navigate(
        ObjectionDocumentRoute(
            seqNo, debitNumber, workshopId, objectionDate,
            objectionType.code.orEmpty(), status.code.orEmpty(),
        )
    )
}

/**
 * پیگیری وضعیت اعتراض — reached from the services menu (`FeatureFlag.FOLLOW_PROTEST_STATUS`), not
 * from a workshop row, same reasoning as [CompleteEmployerInfoRoute]. Its two child screens (SMS,
 * document) are kept in this same file rather than `Navigation.kt`: neither has a workshop-row
 * entry point either, and they are reachable only from the list this file also owns.
 */
fun NavGraphBuilder.debtObjectionStatusScreen(navController: NavController) {
    composableWithFadeTransitions<DebtObjectionStatusRoute> {
        ObjectionStatusScreen(
            onBack = { navController.popBackStack() },
            onOpenSms = { objection ->
                val seqNo = objection.seqNo ?: return@ObjectionStatusScreen
                navController.navigateToObjectionSms(
                    seqNo, objection.debitNumber, objection.workshopId, objection.objectionDate,
                    objection.objectionType, objection.status,
                )
            },
            onOpenDocument = { objection ->
                val seqNo = objection.seqNo ?: return@ObjectionStatusScreen
                navController.navigateToObjectionDocument(
                    seqNo, objection.debitNumber, objection.workshopId, objection.objectionDate,
                    objection.objectionType, objection.status,
                )
            },
        )
    }

    composableWithFadeTransitions<ObjectionSmsRoute> { entry ->
        val route = entry.toRoute<ObjectionSmsRoute>()
        ObjectionSmsScreen(
            seqNo = route.seqNo,
            debitNumber = route.debitNumber,
            objectionType = WorkShopObjectionType.fromCode(route.objectionTypeCode),
            objectionStatus = WorkShopObjectionStatus.fromCode(route.statusCode),
            onBack = { navController.popBackStack() },
            onOpenDocument = {
                navController.navigateToObjectionDocument(
                    seqNo = route.seqNo,
                    debitNumber = route.debitNumber,
                    workshopId = route.workshopId,
                    objectionDate = route.objectionDate,
                    objectionType = WorkShopObjectionType.fromCode(route.objectionTypeCode),
                    status = WorkShopObjectionStatus.fromCode(route.statusCode),
                )
            },
        )
    }

    composableWithFadeTransitions<ObjectionDocumentRoute> { entry ->
        val route = entry.toRoute<ObjectionDocumentRoute>()
        ObjectionDocumentScreen(
            seqNo = route.seqNo,
            debitNumber = route.debitNumber,
            workshopId = route.workshopId,
            objectionDate = route.objectionDate,
            objectionType = WorkShopObjectionType.fromCode(route.objectionTypeCode),
            objectionStatus = WorkShopObjectionStatus.fromCode(route.statusCode),
            onBack = { navController.popBackStack() },
            onOpenSms = {
                navController.navigateToObjectionSms(
                    seqNo = route.seqNo,
                    debitNumber = route.debitNumber,
                    workshopId = route.workshopId,
                    objectionDate = route.objectionDate,
                    objectionType = WorkShopObjectionType.fromCode(route.objectionTypeCode),
                    status = WorkShopObjectionStatus.fromCode(route.statusCode),
                )
            },
        )
    }
}
