package com.tamin.taminhamrah.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.tamin.taminhamrah.R

class TypeWriterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    init {
        if (!isInEditMode) {
            val typeface = ResourcesCompat.getFont(context, R.font.iran_sans_mobile_fa_num)
            this.typeface = typeface
        }
    }

    private var mText: String = ""
    private var mIndex = 0
    private var mDelay: Long = 40L
    private var isAnimationRunning = false
    private var mAnimationChangeListener: OnAnimationChangeListener? = null
    private var animationId: Long = 0L

    private val mHandler = Handler(Looper.getMainLooper())

    fun animateText(text: String) {
        Log.d("ChatDebug", "TypeWriterView animateText called with text length: ${text.length}")
        stopAnimation()
        animationId = System.currentTimeMillis()
        Log.d("ChatDebug", "New animation started with ID: $animationId")

        mText = text
        mIndex = 0
        setText("")
        isAnimationRunning = true
        scheduleNextCharacter()
    }

    private fun scheduleNextCharacter() {
        val currentId = animationId // capture current ID

        mHandler.postDelayed({
            if (currentId != animationId || !isAnimationRunning) {
                Log.d("ChatDebug", "Animation ID changed or stopped, returning")
                return@postDelayed
            }

            if (mIndex <= mText.length) {
                text = mText.take(mIndex++)

                if (mIndex <= mText.length) {
                    scheduleNextCharacter()
                } else {
                    Log.d("ChatDebug", "Animation completed for ID: $currentId")
                    isAnimationRunning = false

                    if (currentId == animationId) {
                        Log.d("ChatDebug", "Calling animation callback")
                        mAnimationChangeListener?.onAnimationEnd()
                    }
                }
            }
        }, mDelay)
    }

    fun stopAnimation() {
        if (isAnimationRunning) {
            isAnimationRunning = false
            animationId = System.currentTimeMillis() // invalidate current animation
            mHandler.removeCallbacksAndMessages(null)
            text = mText
            Log.d("ChatDebug", "Animation stopped, new invalidation ID: $animationId")
        }
    }

    fun forceStopAnimation() {
        isAnimationRunning = false
        animationId = System.currentTimeMillis() // invalidate current animation
        mHandler.removeCallbacksAndMessages(null)
        if (mText.isNotEmpty()) {
            text = mText
        }
        Log.d("ChatDebug", "Animation force stopped and text set to full")
    }

    fun isAnimationRunning() = isAnimationRunning

    fun setCharacterDelay(millis: Long) {
        mDelay = millis
    }

    fun setOnAnimationChangeListener(listener: OnAnimationChangeListener?) {
        mAnimationChangeListener = listener
    }

    fun clearListener() {
        mAnimationChangeListener = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        forceStopAnimation()
        clearListener()
    }

    interface OnAnimationChangeListener {
        fun onAnimationEnd()
    }
}