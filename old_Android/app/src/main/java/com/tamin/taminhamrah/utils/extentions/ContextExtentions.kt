package com.tamin.taminhamrah.utils.extentions

import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.currentAnimationTimeMillis
import androidx.annotation.AnimRes


fun Context.makeAnimation(@AnimRes id: Int): Animation {
    val a = AnimationUtils.loadAnimation(this, id)
    a.interpolator = AccelerateInterpolator()
    a.startTime = currentAnimationTimeMillis()
    return a
}
