package com.tamin.taminhamrah.utils.extentions
import android.widget.Button
import androidx.core.content.ContextCompat
import com.tamin.taminhamrah.R


fun Button.enableButton() {
    isEnabled = true
    setBackgroundColor(ContextCompat.getColor(context,
        R.color.colorPrimary))
}

fun Button.disableButton() {
    isEnabled = false
    setBackgroundColor(ContextCompat.getColor(context,
        R.color.gray))
}