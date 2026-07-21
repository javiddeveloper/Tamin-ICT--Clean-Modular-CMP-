package com.tamin.taminhamrah.data.remote.models.services.workshop.definitiveDebtArticle16
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class WorkShopListDefinitiveArticle16Response : ListDataModel<WorkShopListDefinitiveArticle16Model>()

data class WorkShopListDefinitiveArticle16Model(
    @SerializedName("nationalcode")
    val nationalCode: String? = null,
    @SerializedName("startdate")
    val startDate: String? = null,
    val workshop: Workshop? = null
){
    fun getDetailWorkShop() = listOf(
        KeyValueModel(_keyStringResId = R.string.employer_type , _value = workshop?.character?.characterDesc?: "-", _textColor = EnumTextColor.GREEN),
        KeyValueModel(_keyStringResId = R.string.start_activity_date , _value = Utility.getDateSeparator(startDate), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.label_type_activity , _value = workshop?.workshopType?.workshopTypeDesc?: "-"),
        KeyValueModel(_keyStringResId = R.string.label_branch_code , _value = workshop?.branchCode ?: "-"),
        KeyValueModel(_keyStringResId = R.string.label_branch_name , _value = workshop?.branchTitle ?: "-"),
        KeyValueModel(_keyStringResId = R.string.register_workshop_date , _value =  Utility.getDateSeparator(workshop?.workshopRegisterDate)),
        KeyValueModel(_keyStringResId = R.string.approve_date_workshop , _value =  Utility.getDateSeparator(workshop?.workshopApproveDate))
    )
}

data class Workshop(
val workshopId: String? = null,
val workshopName: String? = null,
val workshopStatus: WorkshopStatus? = null,
val workshopType: WorkshopType? = null,
val branchCode: String? = null,
val branchTitle: String? = null,
val character: Character? = null,
val workshopRegisterDate: String? = null,
val workshopApproveDate: String? = null,
val activityName: Any? = null,
)

data class WorkshopType(
@SerializedName("workshoptypeDesc")
val workshopTypeDesc: String? = null
)

data class WorkshopStatus(
val workshopStatusCode: String? = null,
val workshopStatusDesc: String? = null
)

data class Character(
val characterCode: String? = null,
val characterDesc: String? = null,
)

