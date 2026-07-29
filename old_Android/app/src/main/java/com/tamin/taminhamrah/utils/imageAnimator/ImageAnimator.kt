package com.tamin.taminhamrah.utils.imageAnimator

import android.content.Context
import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.tamin.taminhamrah.R

fun setLookDirection(imageView: ImageView, context: Context, isLookingDown: Boolean) {
    val targetRes = if (isLookingDown) {
        R.drawable.look_bottom_anim
    } else {
        R.drawable.look_direct_anim
    }

    if (imageView.tag == targetRes) {
        return
    }

    val drawable = getDrawable(context, targetRes)
    imageView.setImageDrawable(drawable)

    imageView.tag = targetRes

    if (drawable is Animatable) {
        drawable.start()
    }
}