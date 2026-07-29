package com.tamin.taminhamrah.widget

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

private fun findNestedRecyclerView(view: View?): RecyclerView? {
    when (view) {
        is RecyclerView -> return view
        is ViewGroup -> {
            var index = 0
            do {
                val child = view.getChildAt(index)
                val recyclerView = findNestedRecyclerView(child)
                if (recyclerView == null) {
                    index += 1
                } else {
                    return recyclerView
                }
            } while (index < view.childCount)
        }
        else -> return null
    }
    return null
}