package com.tamin.taminhamrah.widget

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView


/**
 * Class [StatefulRecyclerView] extends [RecyclerView] and adds position management on configuration changes.
 *
 * @author FrantisekGazo
 * @version 2016-03-15
 */
class StatefulRecyclerView : RecyclerView {
    private var mLayoutManagerSavedState: Parcelable? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    )

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState())
        bundle.putParcelable(
            SAVED_LAYOUT_MANAGER, this.layoutManager!!
                .onSaveInstanceState()
        )
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        var state: Parcelable? = state
        if (state is Bundle) {
            val bundle = state
            mLayoutManagerSavedState = bundle.getParcelable(SAVED_LAYOUT_MANAGER)
            state = bundle.getParcelable(SAVED_SUPER_STATE)
        }
        super.onRestoreInstanceState(state)
    }

    /**
     * Restores scroll position after configuration change.
     *
     *
     * **NOTE:** Must be called after adapter has been set.
     */
    private fun restorePosition() {
        if (mLayoutManagerSavedState != null) {
            this.layoutManager!!.onRestoreInstanceState(mLayoutManagerSavedState)
            mLayoutManagerSavedState = null
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        restorePosition()
    }

    companion object {
        private const val SAVED_SUPER_STATE = "super-state"
        private const val SAVED_LAYOUT_MANAGER = "layout-manager-state"
    }
}