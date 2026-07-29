package com.tamin.taminhamrah.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.tamin.taminhamrah.R
import javax.inject.Inject


class LoadingView @Inject constructor() {

    private var view:View?=null

    fun showLoading(context: Context, parent: ViewGroup) {
        try {
            view = LayoutInflater.from(context).inflate(
                R.layout.dialog_loading,
                null
            )
            view?.apply {
                id=View.generateViewId()
                translationZ=90F

                layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                parent.addView(view)
            }

        }catch (e: Exception){
            e.printStackTrace()
        }catch (e:Error){
            e.printStackTrace()
        }

    }

    fun hideLoading(){ view.let {view?.visibility=View.GONE } }
}