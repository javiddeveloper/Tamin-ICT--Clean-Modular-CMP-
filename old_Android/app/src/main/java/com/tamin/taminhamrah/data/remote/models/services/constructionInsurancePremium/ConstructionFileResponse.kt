package com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class ConstructionFileResponse : ListDataModel<ConstructionFileModel>()
data class ConstructionFileModel(
    val fileNumber: Long?=null,
    val requestNumber: Long?=null,
    val requestDate: String?=null,
    @SerializedName("workshopId")
    val workshopInfo: WorkshopIdInfo?=null,
    val postalCode: String?=null,
    val address: String?=null,
    @SerializedName("mainPelak")
    val mainPlaque: Int?=null,
    @SerializedName("subPelak")
    val subPlaque: Int?=null,
    @SerializedName("block")
    val block: Long?=null,
    @SerializedName("estate")
    val propertyConstruction: Int?=null,
    val apartment: Int?=null,
    val trade: Int?=null,
    @SerializedName("partPelak")
    val partPlaque: Int?=null,
    val sumOfComplications: Long?=null,
    val debitNumber: String?=null,
    val totalPayment: Long?=null,
    @SerializedName("metrage")
    val meterage: Int?=null,
    val debitStatusCode: String?=null,
    @SerializedName("buildingProminence")
    val protrusion: Long?=null,
    @SerializedName("postulate")
    val applicationFees: Long?=null,
    @SerializedName("fondation")
    val residentialServiceInfrastructureFees: Long?=null,
    @SerializedName("densityFinance")
    val excessDensitySurchargeFees: Long?=null,
    @SerializedName("optimalCharges")
    val increasePropertyValue: Long?=null,
    @SerializedName("fencesCharges")
    val issuanceFencingWallConstructionFees: Long?=null,
    @SerializedName("thirdItemCharges")
    val coveredClause3Fees: Long?=null,
    val article100: Long?=null,
    val paymentDeadLine: String?=null,

){
    fun getRequestInfo() = listOf(
        KeyValueModel(_keyStringResId = R.string.file_number , _value = (fileNumber?: 0).toString(), _textColor = EnumTextColor.AMBER),
        KeyValueModel(_keyStringResId = R.string.label_request_number , _value = (requestNumber?:0).toString()),
        KeyValueModel(_keyStringResId = R.string.label_registration_date , _value =  Utility.getDateSeparator(workshopInfo?.workshopRegisterDate), _textColor = EnumTextColor.GREEN),
        KeyValueModel(_keyStringResId = R.string.workshop_number , _value = workshopInfo?.workshopId?: "-"),
        KeyValueModel(_keyStringResId = R.string.original_registration_plate, _value = (mainPlaque ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.part_plaque , _value = (partPlaque ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.sub_registration_plate, _value = (subPlaque ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.block, _value = (block ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.property_construction, _value = (propertyConstruction ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.apartment, _value = (apartment ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.trade, _value = (trade ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.postalCode, _value =postalCode?:"_"),
        KeyValueModel(_keyStringResId = R.string.label_address, _value =address?:"_"))

    fun getComputingInfo() = listOf(
        KeyValueModel(_keyStringResId = R.string.label_calculated_amount, _value = (Utility.getRialWithSeparator(totalPayment)), _textColor = EnumTextColor.AMBER),
        KeyValueModel(_keyStringResId = R.string.label_payment_dead_line, _value =Utility.getDateSeparator(paymentDeadLine), _textColor = EnumTextColor.RED),
        KeyValueModel(_keyStringResId = R.string.protrusion_fee , _value = (Utility.getRialWithSeparator(protrusion)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.application_fee , _value = (Utility.getRialWithSeparator(applicationFees)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.residential_service_infrastructure_fees , _value = (Utility.getRialWithSeparator(residentialServiceInfrastructureFees)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.excess_density_surcharge , _value = (Utility.getRialWithSeparator(excessDensitySurchargeFees)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.increase_property_value, _value = (Utility.getRialWithSeparator(increasePropertyValue)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.issuance_fencing_wall_construction_fees , _value = (Utility.getRialWithSeparator(issuanceFencingWallConstructionFees)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.covered_clause_3_fees, _value = (Utility.getRialWithSeparator(coveredClause3Fees)), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.article_100, _value = (article100 ?: 0).toString(), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.meterage, _value = (meterage ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.label_debit_number, _value = (debitNumber ?: "در حال تخصیص شماره بدهی").toString()),
    )

    fun getDetailConstructionFile() = listOf(
        KeyValueModel(_keyStringResId = R.string.file_number , _value = (fileNumber?: 0).toString(), _textColor = EnumTextColor.GREEN),
        KeyValueModel(_keyStringResId = R.string.label_request_number , _value = (requestNumber?:0).toString()),
        KeyValueModel(_keyStringResId = R.string.label_registration_date , _value =  Utility.getDateSeparator(workshopInfo?.workshopRegisterDate)),
        KeyValueModel(_keyStringResId = R.string.workshop_number , _value = workshopInfo?.workshopId?: "-"),
        KeyValueModel(_keyStringResId = R.string.main_plaque , _value = (mainPlaque ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.part_plaque , _value = (partPlaque ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.meterage , _value = (meterage?:0).toString()),
        KeyValueModel(_keyStringResId = R.string.label_debit_number , _value =  debitNumber?:"-"),
        KeyValueModel(_keyStringResId = R.string.label_payment_type , _valueStringResId =  if (debitStatusCode=="51")
            R.string.installment_type
        else
            R.string.cash_type, _textColor = EnumTextColor.BLUE
        )
    )

    fun getRequestInfoIssuancePaymentSheet() = arrayListOf(
        KeyValueModel(_keyStringResId = R.string.calculated_amount , _value =  (Utility.getRialWithSeparator(totalPayment)), _textColor = EnumTextColor.AMBER),
        KeyValueModel(_keyStringResId = R.string.label_payment_dead_line , _value =  Utility.getDateSeparator(paymentDeadLine), _textColor = EnumTextColor.RED),
        KeyValueModel(_keyStringResId = R.string.file_number , _value = (fileNumber?: 0).toString(), _textColor = EnumTextColor.GREEN),
        KeyValueModel(_keyStringResId = R.string.label_request_number , _value = (requestNumber?:0).toString()),
        KeyValueModel(_keyStringResId = R.string.label_registration_date , _value =  Utility.getDateSeparator(workshopInfo?.workshopRegisterDate)),
        KeyValueModel(_keyStringResId = R.string.workshop_number , _value = workshopInfo?.workshopId?: "-"),
        KeyValueModel(_keyStringResId = R.string.branch , _value = (workshopInfo?.brhCode ?: 0).toString()),
        KeyValueModel(_keyStringResId = R.string.label_debit_number , _value = (debitNumber ?: 0).toString())
    )
}
data class WorkshopIdInfo(
    val workshopRegisterDate: String?=null,
    val workshopId: String?=null,
    val brhCode: String?=null,
    )