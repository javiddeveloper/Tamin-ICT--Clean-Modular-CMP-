package com.tamin.taminhamrah.utils.extentions

import android.view.View
import android.view.View.LAYER_TYPE_HARDWARE
import android.view.View.LAYER_TYPE_NONE
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import com.tamin.taminhamrah.R

fun View.scaleY(state: Boolean, animation: Boolean = true) {
    if (!animation)
        scaleY = if (state) 1f else -1f
    else
        animate().scaleY(if (state) 1f else -1f)
            .setDuration(500)
            .start()
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.isVisible() = this.visibility == View.VISIBLE


fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.slideBottomIn() {
    if (visibility == View.VISIBLE) {
        return
    }

    val animate = context.makeAnimation(R.anim.slide_in_bottom).apply {


        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

                layerType(false)

            }

            override fun onAnimationStart(animation: Animation?) {
                layerType()
                visibility = View.VISIBLE
            }

        })
    }
    this.startAnimation(animate)
}

fun View.slideBottomOut() {
    if (visibility == View.GONE)
        return
    val animate = context.makeAnimation(R.anim.slide_out_bottom)
    animate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            layerType(false)
            visibility = View.GONE


        }

        override fun onAnimationStart(animation: Animation?) {
            layerType()
        }

    })

    this.startAnimation(animate)
}

fun View.slideUpIn() {
    if (visibility == View.VISIBLE)
        return
    val animate = context.makeAnimation(R.anim.slide_in_top)
    animate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            layerType(false)
        }

        override fun onAnimationStart(animation: Animation?) {
            layerType()
            visibility = View.VISIBLE
        }
    })
    this.startAnimation(animate)
}

fun View.layerType(is_hardwarw: Boolean = true) {
    if (is_hardwarw)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    else
        setLayerType(LAYER_TYPE_NONE, null)
}


fun View.slideUpOut() {

    if (visibility == View.GONE)
        return
    val animate = context.makeAnimation(R.anim.slide_out_top)
    animate.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            layerType(false)
            gone()
        }

        override fun onAnimationStart(animation: Animation?) {
            layerType()
        }
    })
    this.startAnimation(animate)
}

fun View.bubbleAnim() {
    var scale = ScaleAnimation(
        0f,
        1f,
        0f,
        1f,
        ScaleAnimation.RELATIVE_TO_SELF,
        .5f,
        ScaleAnimation.RELATIVE_TO_SELF,
        .5f
    )
    scale.duration = 1000
    scale.interpolator = OvershootInterpolator()
    this.startAnimation(scale)

}


