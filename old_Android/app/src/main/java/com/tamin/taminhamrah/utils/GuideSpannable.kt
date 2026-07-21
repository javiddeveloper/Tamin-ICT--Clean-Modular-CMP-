package com.tamin.taminhamrah.utils

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View


open class GuideSpannable(var isUnderline: Boolean) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor("#1b76d3")
    }

  open  override fun onClick(p0: View) {
    }

}