package com.tamin.taminhamrah.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.tamin.taminhamrah.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object UiUtils {

    fun createDivider(context: Context): DividerItemDecoration {
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(
            ContextCompat.getDrawable(
                context,
                R.color.lineColor2
            )!!
        )
        return decoration
    }

    class HorizontalItemMarginDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {

            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    right = margin
                }
                bottom = margin / 2
                top = margin / 2
                left = margin / 2
            }
        }
    }


    class VerticalItemMarginDecoration(
        private val margin: Int,
        private val setTopMargin: Boolean? = true
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                if (setTopMargin == true && parent.getChildAdapterPosition(view) == 0) {
                    top = margin
                }
                right = margin
                left = margin

                bottom = margin

            }
        }
    }
    class VerticalItemSetMarginDecoration(
        private val topMargin: Int =0,
        private val bottomMargin: Int =0,
        private val endMargin: Int =0,
        private val startMargin: Int =0,
        private val setTopMargin: Boolean? = true
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                if (setTopMargin == true && parent.getChildAdapterPosition(view) == 0) {
                    top = topMargin
                }
                right = endMargin
                left = startMargin
                bottom = bottomMargin

            }
        }
    }

    class RTLGridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int
    ) :
        RecyclerView.ItemDecoration() {
        private val includeEdge = true
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item phases_position
            val column = position % spanCount // item column
            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =spacing - (column) * spacing / spanCount + spacing
                //  (spacing - column * spacing / spanCount)*column // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }


    /* set spacing for grid view */
    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int
    ) :
        RecyclerView.ItemDecoration() {
        private val includeEdge = true
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item phases_position
            val column = position % spanCount // item column
            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
              //  (spacing - column * spacing / spanCount)*column // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }

    class SpacesItemDecoration(private val space: Int, private val colSize: Int) :
        ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.top = space
            outRect.left = space
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) / colSize > 0) {
                outRect.right = space
            } else {
                outRect.right = 0
            }
            outRect.bottom = 0
        }
    }

    class BackgroundItemDecoration(val old: Int, val new: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) % 2 == 0) {
                    view.setBackgroundColor(old)
                } else
                    view.setBackgroundColor(new)


            }
        }
    }

    class BackgroundItemDecorationDrowable(val context: Context) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            with(outRect) {
                val index = parent.getChildAdapterPosition(view)
                view.background = when {
                    index == 0 -> {
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.bg_left_corner_rounded_white
                        )
                    }
                    parent.getChildAdapterPosition(view) % 2 == 0 -> {
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.bg_left_corner_rounded_white_with_grey_adge
                        )
                    }
                    else -> AppCompatResources.getDrawable(
                        context,
                        R.drawable.bg_left_corner_rounded_grey_with_white_adge
                    )
                }
            }
        }
    }

    val VORDIPLOM_COLORS = intArrayOf(
        Color.rgb(103, 230, 147), Color.rgb(103, 230, 147), Color.rgb(103, 230, 147),
        Color.rgb(103, 230, 147), Color.rgb(103, 230, 147)
    )

    fun getColorList(
        @ColorInt selectedColor: Int,
        @ColorInt unselectedColor: Int
    ): ColorStateList? {
        val states = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
        val colors = intArrayOf(
            selectedColor,
            unselectedColor
        )
        return ColorStateList(states, colors)
    }

    fun getHtmlString(textStr: String?): Spanned? {
        if (textStr.isNullOrBlank()) return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (Html.fromHtml(textStr, Html.FROM_HTML_MODE_LEGACY))
        } else {
            (Html.fromHtml(textStr))
        }
    }

     fun createCustomTextColorForRestDay(diffDayStartAndEndRest: Long): String {
        return "<font color=" + "#878787" + ">" + "تعداد روز استراحت " + "</font>" +
                "<font color=" + "#67e693" + ">" + "<b>" + diffDayStartAndEndRest + "</b>" + "</font>" +
                "<font color=" + "#878787" + ">" + " روز " + "</font>"
    }

     fun createTextColorOrangeAndBold(addedValue: String?): String{
        return "<font color=#ff8f00><b>$addedValue</b></font>"
    }

    fun createTextColorGreenAndBold(addedValue: String?): String{
        return "<font color=#3acc6c><b>$addedValue</b></font>"
    }

    fun createTextColorBlueAndBold(addedValue: String?): String{
        return "<font color=#6c63ff><b>$addedValue</b></font>"
    }

    // Function to get the Bitmap from the file path
    fun getQrCodeBitmap(qrCodeFilePath:String?): Bitmap? {
        return if (qrCodeFilePath != null) {
            BitmapFactory.decodeFile(qrCodeFilePath)
        } else {
            null
        }
    }

    // Function to compress and save the Bitmap to a file
    fun setQrCodeBitmap(bitmap: Bitmap?,context: Context): String? {
        return if (bitmap != null) {
            val file =
                createTempImageFile(context) // Create a temporary file to store the compressed image
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Compress the Bitmap
            outputStream.close()

            file.absolutePath // Store the file path in the data model
        } else {
            null // Reset the file path if the Bitmap is null
        }
    }

    // Function to create a temporary file to store the compressed image
    fun createTempImageFile(context: Context): File {
        val storageDir = context.cacheDir // Use the cache directory specific to your app

        try {
            val imageFile = File.createTempFile(
                "compressed_image", // Prefix for the temporary file name
                ".jpg", // Suffix or file extension
                storageDir // Directory to store the temporary file
            )

            return imageFile
        } catch (e: IOException) {
            // Handle the exception if the temporary file cannot be created
            throw e
        }
    }

    fun getAttributeColor(context: Context, attrId: Int): Int {
        TypedValue().let {
            context.theme.resolveAttribute(attrId, it, true)
            return ContextCompat.getColor(context, it.resourceId)
        }
    }


}