package com.tamin.taminhamrah.widget

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children

abstract class BaseWidget(context: Context, attrs: AttributeSet?) : LinearLayoutCompat(context, attrs) {
    private var inflater: LayoutInflater
    private var view: View? = null

    init {
        this.orientation = VERTICAL
        inflater = LayoutInflater.from(context)
        initLayout(context, attrs)
    }

    abstract fun initLayout(context: Context?, attrs: AttributeSet?)

     fun inflateLayout(context: Context?, @LayoutRes layoutRes: Int) {
        view = inflater.inflate(layoutRes, this)
    }
    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).apply {
            children.forEach { child ->
                //Store this view state into the given container. //call dispatchSaveInstanceState
                if (childrenStates != null)
                    child.saveHierarchyState(childrenStates)
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                //Restore this view state from the given container. //call dispatchRestoreInstanceState
                if (state.childrenStates != null)
                    children.forEach { child -> child.restoreHierarchyState(state.childrenStates) }

            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    //Called by saveHierarchyState(android.util.SparseArray) to store the state for this view and its children.
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    // Called by restoreHierarchyState(android.util.SparseArray) to retrieve the state for this view and its children.
    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    //To save the state of the custom view in the parcelable return by savedInstanceState
    class SavedState : BaseSavedState {

        var childrenStates: SparseArray<Parcelable>? = null

        constructor(superState: Parcelable?) : super(superState)

        constructor(source: Parcel) : super(source) {
            childrenStates = source.readSparseArray(javaClass.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeSparseArray(childrenStates as? SparseArray<Any>)
        }

        /* @JvmField =if we want a particular field to be used as normal field and not
           as getter or setter then we have to tell the compiler not to generate any getter and setter
           for the same and this can be done by using the @JvmField annotation*/
        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel) = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

}
