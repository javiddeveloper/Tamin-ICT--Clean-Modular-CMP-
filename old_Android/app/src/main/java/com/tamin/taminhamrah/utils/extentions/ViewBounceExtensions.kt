package com.tamin.taminhamrah.utils.extentions

/**
 * @author  : Javid
 * @summary : ViewBounceExtensions
 */

import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator

private const val TAG_BOUNCE_ANIM = -1001

fun View.startInfiniteBounce(
    distance: Float = 20f,
    durationMs: Long = 800L
): ObjectAnimator? {
    if (!isLaidOut) {
        viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                startInfiniteBounce(distance, durationMs)
            }
        })
        return null
    }
    (getTag(TAG_BOUNCE_ANIM) as? ObjectAnimator)?.let {
        if (it.isRunning) return it
    }
    val anim = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, -distance).apply {
        duration = durationMs
        interpolator = AccelerateDecelerateInterpolator()
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = ObjectAnimator.INFINITE
        start()
    }
    setTag(TAG_BOUNCE_ANIM, anim)
    return anim
}

fun View.stopInfiniteBounce() {
    (getTag(TAG_BOUNCE_ANIM) as? ObjectAnimator)?.apply {
        cancel()
        setTag(TAG_BOUNCE_ANIM, null)
        translationY = 0f
    }
}
