/*
*
* @author: Javid Sattar 
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    context: Context,
    private val spaceDp: Int = 8
) : RecyclerView.ItemDecoration() {
    private val spacePx = (spaceDp * context.resources.displayMetrics.density).toInt()

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            left = spacePx
            right = spacePx
            bottom = spacePx
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spacePx
            }
        }
    }
}