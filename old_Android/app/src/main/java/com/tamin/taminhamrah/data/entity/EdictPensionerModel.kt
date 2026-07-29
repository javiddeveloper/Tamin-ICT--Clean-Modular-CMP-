package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class EdictPensionerModel(
    var itemListHistoryDate: @RawValue List<KeyValueModel>? = null,
    var itemListEdictInfo:  @RawValue List<KeyValueModel>? = null,
    var itemListWageInfoBeforeIncrease:  @RawValue List<KeyValueModel>? = null,
    var itemListWageInfoAfterIncrease: @RawValue  List<KeyValueModel>? = null,
    var itemListToolbarInfo: @RawValue  List<KeyValueModel>? = null,
    var selectedPensionerId: @RawValue  String? = "",
    var selectedDateEdict: @RawValue  String? = ""
):Parcelable



@Parcelize
class EdictPensionerModels: ArrayList<EdictPensionerModel>(), Parcelable


