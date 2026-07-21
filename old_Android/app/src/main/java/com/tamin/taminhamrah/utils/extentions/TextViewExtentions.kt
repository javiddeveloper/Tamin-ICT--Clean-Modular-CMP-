package com.tamin.taminhamrah.utils.extentions

import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.ui.guide.GuideFragment
import com.tamin.taminhamrah.utils.GuideSpannable

const val maxLineDefault:Int = 7
fun TextView.addClickablePartTextViewResizable(
    strSpanned: String,
    maxLine: Int,
    spannableText: String,
    viewMore: Boolean,
    callBack:GuideFragment.ShowMoreClickListener
): SpannableStringBuilder {
    val tv = this
    val str = strSpanned.toString()
    val ssb = SpannableStringBuilder(strSpanned)
    if (str.contains(spannableText)) {
        ssb.setSpan(object : GuideSpannable(false) {
            override fun onClick(p0: View) {
                tv.apply {
                    if (viewMore) {
                        layoutParams = layoutParams
                        setText(tag.toString(), TextView.BufferType.SPANNABLE)
                        invalidate()
                        callBack.onShowMoreClick()

                        makeTextViewResizable(-1, context.getString(R.string.less_desc), false,callBack)

                    } else {
                        layoutParams = layoutParams
                        setText(tag.toString(), TextView.BufferType.SPANNABLE)
                        invalidate()
                        makeTextViewResizable(maxLineDefault, context.getString(R.string.more_desc), true,callBack)
                        callBack.onShowLessClick()


                    }
                }
            }
        }, str.indexOf(spannableText), str.indexOf(spannableText) + spannableText.length, 0)
    }
    return ssb
}

fun TextView.makeTextViewResizable(maxLine: Int = maxLineDefault, expandText: String, viewMore: Boolean = true,callBack:GuideFragment.ShowMoreClickListener) {
    this.apply {
        if (tag == null)
            tag = text

        viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                when (maxLine) {
                    0 -> {
                        val lineEndIndex = layout.getLineEnd(0)
                        val text: String =
                            text.subSequence(0, lineEndIndex - expandText.length + 1)
                                .toString() + " " + expandText
                        setText(text)
                        movementMethod = LinkMovementMethod.getInstance()
                        setText(
                            addClickablePartTextViewResizable(
                                text,
                                maxLine,
                                expandText,
                                viewMore,callBack
                            ), TextView.BufferType.SPANNABLE
                        )
                    }
                    in 1..lineCount -> {
                        val lineEndIndex = layout.getLineEnd(maxLine - 1)
                        val text: String =
                            text.subSequence(0, lineEndIndex - expandText.length + 1)
                                .toString() + " " + expandText
                        setText(text)
                        movementMethod = LinkMovementMethod.getInstance()
                        setText(
                            addClickablePartTextViewResizable(
                                text, maxLine, expandText,
                                viewMore,callBack
                            ), TextView.BufferType.SPANNABLE
                        )
                    }
                    else -> {
                        val lineEndIndex =
                            layout.getLineEnd(layout.lineCount - 1)
                        val text: String =
                            text.subSequence(0, lineEndIndex).toString() + " " + expandText
                        setText(text)
                        movementMethod = LinkMovementMethod.getInstance()
                        setText(
                            addClickablePartTextViewResizable(
                                text, lineEndIndex, expandText,
                                viewMore,callBack
                            ), TextView.BufferType.SPANNABLE
                        )
                    }
                }
            }
        })
    }
}

fun TextView.setTextViewDrawableColor(color:Int) {
    val img: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_circle_orange);
    img?.setTint(ContextCompat.getColor(context,color))
    setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
}