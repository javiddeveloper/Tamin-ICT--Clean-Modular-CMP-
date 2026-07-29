package com.tamin.taminhamrah.data.entity

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.tamin.taminhamrah.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class KeyValueModel(
    var _key: String = "",
    var _value: String = "",
    var _isKeyBold:Boolean=false,
    var _textColor:EnumTextColor=EnumTextColor.NORMAL,
    var _isHeader:Boolean=false,
    var _type: String = "",
    var _isValueBold:Boolean=false,
    var _hasBackground:Boolean=false,
    @StringRes
    var _keyStringResId: Int = 0,
    @StringRes
    var _valueStringResId: Int = 0,
    ):Parcelable

enum class EnumTextColor(@ColorRes val colorRes:Int){
    NORMAL(R.color.textColorTitle),
    LIGHT_GREEN(R.color.bg_dialog_green_light),
    GREEN(R.color.green),
    RED(R.color.textColorTitle),
    BLUE(R.color.colorPrimaryLight),
    BLUE_GREEN(R.color.green_blue),
    AMBER(R.color.text_color_dialog_orange)
}

