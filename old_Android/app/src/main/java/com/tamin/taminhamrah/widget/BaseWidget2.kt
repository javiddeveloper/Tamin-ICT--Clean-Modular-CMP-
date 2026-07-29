package com.tamin.taminhamrah.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout

abstract class BaseWidget2(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var inflater: LayoutInflater
    private var view: View? = null

    init {

        inflater = LayoutInflater.from(context)
        initLayout(context, attrs)
    }

    abstract fun initLayout(context: Context?, attrs: AttributeSet?)

     fun inflateLayout(context: Context?, @LayoutRes layoutRes: Int) {
        view = inflater.inflate(layoutRes, this)
    }


}
