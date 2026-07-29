package com.tamin.taminhamrah.widget

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import com.google.android.material.color.MaterialColors

class CustomSearchView : SearchView {
        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            init()
        }

        private fun init() {
            // Set the text color for the SearchView
            val textColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimaryFixed)
            val searchEditText = this.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchEditText?.apply {
                setTextColor(textColor)
                setHintTextColor(textColor)
            }
         //   this.setBackgroundColor(backgroundColor) // Replace with your background drawable

            // Set the tint color for the search icon
            val searchIcon = this.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            searchIcon?.setColorFilter(textColor, PorterDuff.Mode.SRC_IN)

            // Set the tint color for the close icon
            val closeIcon = this.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeIcon?.setColorFilter(textColor, PorterDuff.Mode.SRC_IN)

        }
    }
