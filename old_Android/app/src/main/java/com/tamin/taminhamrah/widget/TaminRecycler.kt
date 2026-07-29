    package com.tamin.taminhamrah.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.WidgetRecyclerviewWithEmptyMessageBinding
import com.tamin.taminhamrah.utils.UiUtils

abstract class TaminRecycler(mContext: Context, attrs: AttributeSet?) :
    BaseWidget2(mContext, attrs) {

    interface OnLoadMore {
        fun loadData()
    }

    private var mListener: OnLoadMore? = null

    fun setLoadMoreListener(listener: OnLoadMore) {
        mListener = listener
    }

    val queryTotalPage = 10
    var currentPage = 0

    var isScrolling = false
    var btnRetryIsVisible = false

        var isLoading = true
    var totalPages = 100

    private lateinit var viewBinding: WidgetRecyclerviewWithEmptyMessageBinding

    override fun initLayout(context: Context?, attrs: AttributeSet?) {
        context?.let {
            viewBinding = WidgetRecyclerviewWithEmptyMessageBinding.inflate(
                LayoutInflater.from(it),
                this,
                true
            )

            viewBinding.recycler.addOnScrollListener(scrollListener)

            attrs?.let { setAttribute(context, attrs) }
        }
    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {

        viewBinding.apply {


            val ta = context.obtainStyledAttributes(attrs, R.styleable.SMRecycler)

            val type: ItemDecorationType =
                ItemDecorationType.values()[ta.getInt(R.styleable.SMRecycler_decoration_type, 0)]
            recycler.apply {
                if (itemDecorationCount == 0) {
                    when (type) {
                        ItemDecorationType.VERTICAL -> {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }

                        ItemDecorationType.TAMIN_BG -> {
                            addItemDecoration(UiUtils.BackgroundItemDecorationDrowable(context))
                        }

                        ItemDecorationType.GRID_MULTI_COL -> {
                            addItemDecoration(UiUtils.GridSpacingItemDecoration(5, 20))
                        }

                        ItemDecorationType.HORIZONTAL -> {

                            addItemDecoration(UiUtils.HorizontalItemMarginDecoration(40))
                        }

                        ItemDecorationType.MENU -> {
                            addItemDecoration(
                                UiUtils.BackgroundItemDecoration(
                                    ContextCompat.getColor(context, R.color.lineColor),
                                    ContextCompat.getColor(context, android.R.color.white)
                                )
                            )
                        }

                        ItemDecorationType.LINE -> {
                            addItemDecoration(UiUtils.createDivider(this.context))
                        }
                    }
                }
            }

            ta.recycle()
        }

    }

    fun getRecycler(): RecyclerView {
        return viewBinding.recycler
    }

    fun showMessage(show: Boolean) {
        if (show) {
            viewBinding.recycler.visibility = View.GONE
            viewBinding.tvMessage.visibility = View.VISIBLE
        } else {
            viewBinding.recycler.visibility = View.VISIBLE
            viewBinding.tvMessage.visibility = View.GONE
        }

    }

    private fun isLastPage(): Boolean {
        return currentPage == totalPages
    }

    fun setTotalPage(totalItems: Int) {
        totalPages = totalItems / queryTotalPage + 1
    }



    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //State is scrolling
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val totalVisibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage()
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE_10
            val shouldPaginate =
                isNotLoadingAndNotLastPage &&
                        isAtLastItem &&
                        isNotAtBeginning &&
                        isTotalMoreThanVisible &&
                        isScrolling &&
                        !recyclerView.canScrollVertically(1) &&
                        !btnRetryIsVisible &&
                        !isLastPage()

            if (shouldPaginate) {
                mListener?.loadData()
                isScrolling = false
            }
        }
    }

    enum class ItemDecorationType {
        HORIZONTAL,
        VERTICAL,
        LINE,
        TAMIN_BG,
        GRID_MULTI_COL,
        MENU
    }
}