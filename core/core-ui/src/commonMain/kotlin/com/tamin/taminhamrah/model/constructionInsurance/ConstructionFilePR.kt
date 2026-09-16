package com.tamin.taminhamrah.model.constructionInsurance

import taminx.core.core_ui.Res
import taminx.core.core_ui.apartment
import taminx.core.core_ui.application_fee
import taminx.core.core_ui.article_100
import taminx.core.core_ui.block
import taminx.core.core_ui.branch
import taminx.core.core_ui.cash_type
import taminx.core.core_ui.covered_clause_3_fees
import taminx.core.core_ui.debit_number_allocating
import taminx.core.core_ui.excess_density_surcharge
import taminx.core.core_ui.file_number
import taminx.core.core_ui.increase_property_value
import taminx.core.core_ui.installment_type
import taminx.core.core_ui.issuance_fencing_wall_construction_fees
import taminx.core.core_ui.label_address
import taminx.core.core_ui.label_calculated_amount
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_payment_dead_line
import taminx.core.core_ui.label_payment_type
import taminx.core.core_ui.label_registration_date
import taminx.core.core_ui.label_request_number
import taminx.core.core_ui.meterage
import taminx.core.core_ui.original_registration_plate
import taminx.core.core_ui.part_plaque
import taminx.core.core_ui.postalCode
import taminx.core.core_ui.property_construction
import taminx.core.core_ui.protrusion_fee
import taminx.core.core_ui.residential_service_infrastructure_fees
import taminx.core.core_ui.sub_registration_plate
import taminx.core.core_ui.trade
import taminx.core.core_ui.workshop_number
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate

/** The literal every money line in this screen appends to a grouped amount — see `Extentions.kt`. */
private const val RIAL_UNIT = "ریال"

data class WorkshopIdInfoPR(
    val workshopRegisterDate: String? = null,
    val workshopId: String? = null,
    val brhCode: String? = null,
)

data class ConstructionFilePR(
    val fileNumber: Long? = null,
    val requestNumber: Long? = null,
    val requestDate: String? = null,
    val workshopInfo: WorkshopIdInfoPR? = null,
    val postalCode: String? = null,
    val address: String? = null,
    val mainPlaque: Int? = null,
    val subPlaque: Int? = null,
    val block: Long? = null,
    val propertyConstruction: Int? = null,
    val apartment: Int? = null,
    val trade: Int? = null,
    val partPlaque: Int? = null,
    val sumOfComplications: Long? = null,
    val debitNumber: String? = null,
    val totalPayment: Long? = null,
    val meterage: Int? = null,
    val debitStatusCode: String? = null,
    val protrusion: Long? = null,
    val applicationFees: Long? = null,
    val residentialServiceInfrastructureFees: Long? = null,
    val excessDensitySurchargeFees: Long? = null,
    val increasePropertyValue: Long? = null,
    val issuanceFencingWallConstructionFees: Long? = null,
    val coveredClause3Fees: Long? = null,
    val article100: Long? = null,
    val paymentDeadLine: String? = null,
) {
    fun getRequestInfo(): List<KeyValueModel> = listOf(
        KeyValueModel(keyResId = Res.string.file_number, value = (fileNumber ?: 0).toString(), textColor = EnumTextColor.AMBER),
        KeyValueModel(keyResId = Res.string.label_request_number, value = (requestNumber ?: 0).toString()),
        KeyValueModel(
            keyResId = Res.string.label_registration_date,
            value = workshopInfo?.workshopRegisterDate?.toFormattedDate().orDash(),
            textColor = EnumTextColor.GREEN,
        ),
        KeyValueModel(keyResId = Res.string.workshop_number, value = workshopInfo?.workshopId ?: "-"),
        KeyValueModel(keyResId = Res.string.original_registration_plate, value = (mainPlaque ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.part_plaque, value = (partPlaque ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.sub_registration_plate, value = (subPlaque ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.block, value = (block ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.property_construction, value = (propertyConstruction ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.apartment, value = (apartment ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.trade, value = (trade ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.postalCode, value = postalCode ?: "-"),
        KeyValueModel(keyResId = Res.string.label_address, value = address ?: "-", numeric = false),
    )

    fun getComputingInfo(): List<KeyValueModel> = listOf(
        KeyValueModel(
            keyResId = Res.string.label_calculated_amount,
            value = (totalPayment ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            textColor = EnumTextColor.AMBER,
        ),
        KeyValueModel(
            keyResId = Res.string.label_payment_dead_line,
            value = paymentDeadLine?.toFormattedDate().orDash(),
            textColor = EnumTextColor.RED,
        ),
        KeyValueModel(keyResId = Res.string.protrusion_fee, value = (protrusion ?: 0L).toPriceFormat(), unit = RIAL_UNIT, textColor = EnumTextColor.BLUE),
        KeyValueModel(keyResId = Res.string.application_fee, value = (applicationFees ?: 0L).toPriceFormat(), unit = RIAL_UNIT, textColor = EnumTextColor.BLUE),
        KeyValueModel(
            keyResId = Res.string.residential_service_infrastructure_fees,
            value = (residentialServiceInfrastructureFees ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            textColor = EnumTextColor.BLUE,
        ),
        KeyValueModel(
            keyResId = Res.string.excess_density_surcharge,
            value = (excessDensitySurchargeFees ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            textColor = EnumTextColor.BLUE,
        ),
        KeyValueModel(
            keyResId = Res.string.increase_property_value,
            value = (increasePropertyValue ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            textColor = EnumTextColor.BLUE,
        ),
        KeyValueModel(
            keyResId = Res.string.issuance_fencing_wall_construction_fees,
            value = (issuanceFencingWallConstructionFees ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            textColor = EnumTextColor.BLUE,
        ),
        KeyValueModel(
            keyResId = Res.string.covered_clause_3_fees,
            value = (coveredClause3Fees ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            textColor = EnumTextColor.BLUE,
        ),
        KeyValueModel(keyResId = Res.string.article_100, value = (article100 ?: 0L).toPriceFormat(), unit = RIAL_UNIT, textColor = EnumTextColor.BLUE),
        KeyValueModel(keyResId = Res.string.meterage, value = (meterage ?: 0).toString()),
        KeyValueModel(
            keyResId = Res.string.label_debit_number,
            value = debitNumber ?: "",
            valueResId = if (debitNumber == null) Res.string.debit_number_allocating else null,
            numeric = debitNumber != null,
        ),
    )

    fun getDetailConstructionFile(): List<KeyValueModel> = listOf(
        KeyValueModel(keyResId = Res.string.file_number, value = (fileNumber ?: 0).toString(), textColor = EnumTextColor.GREEN),
        KeyValueModel(keyResId = Res.string.label_request_number, value = (requestNumber ?: 0).toString()),
        KeyValueModel(
            keyResId = Res.string.label_registration_date,
            value = workshopInfo?.workshopRegisterDate?.toFormattedDate().orDash(),
        ),
        KeyValueModel(keyResId = Res.string.workshop_number, value = workshopInfo?.workshopId ?: "-"),
        KeyValueModel(keyResId = Res.string.original_registration_plate, value = (mainPlaque ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.part_plaque, value = (partPlaque ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.meterage, value = (meterage ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.label_debit_number, value = debitNumber ?: "-"),
        KeyValueModel(
            keyResId = Res.string.label_payment_type,
            value = "",
            valueResId = if (debitStatusCode == "51") Res.string.installment_type else Res.string.cash_type,
            textColor = EnumTextColor.BLUE,
            numeric = false,
        )
    )

    fun getRequestInfoIssuancePaymentSheet(): List<KeyValueModel> = listOf(
        KeyValueModel(keyResId = Res.string.label_calculated_amount, value = (totalPayment ?: 0).toString(), textColor = EnumTextColor.AMBER),
        KeyValueModel(keyResId = Res.string.label_payment_dead_line, value = paymentDeadLine ?: "-", textColor = EnumTextColor.RED),
        KeyValueModel(keyResId = Res.string.file_number, value = (fileNumber ?: 0).toString(), textColor = EnumTextColor.GREEN),
        KeyValueModel(keyResId = Res.string.label_request_number, value = (requestNumber ?: 0).toString()),
        KeyValueModel(keyResId = Res.string.label_registration_date, value = workshopInfo?.workshopRegisterDate ?: "-"),
        KeyValueModel(keyResId = Res.string.workshop_number, value = workshopInfo?.workshopId ?: "-"),
        KeyValueModel(keyResId = Res.string.branch, value = workshopInfo?.brhCode ?: "-"),
        KeyValueModel(keyResId = Res.string.label_debit_number, value = debitNumber ?: "0")
    )
}
