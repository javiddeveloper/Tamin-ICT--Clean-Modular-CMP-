package com.tamin.taminhamrah.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.databinding.WidgetRecyclerviewWithEmptyMessageBinding
import com.tamin.taminhamrah.utils.UiUtils

class RecyclerViewWithEmptyMessage(mContext: Context, attrs: AttributeSet?) :
    BaseWidget2(mContext, attrs) {

    interface OnLoadMore {
        fun loadData(currentPage:Int, pageSize:Int, startIndex:Int)
    }

    private var mListener: OnLoadMore? = null

    fun setLoadMoreListener(listener: OnLoadMore) {
        mListener = listener
    }

    val queryPageSize = 10
    var currentPage = 0

    var btnRetryIsVisible = false

    var isLoading = true
    var totalPages = 100

    private fun isLastPage(): Boolean {
        return currentPage == totalPages
    }

    private lateinit var viewBinding: WidgetRecyclerviewWithEmptyMessageBinding
    override fun initLayout(context: Context?, attrs: AttributeSet?) {

        context?.let {
            viewBinding = WidgetRecyclerviewWithEmptyMessageBinding.inflate(
                LayoutInflater.from(it),
                this,
                true
            )

            viewBinding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        val totalVisibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val isNotLoadingAndNotLastPage = !isLoading && !isLastPage()
                        val isAtLastItem =
                            firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
                        val isNotAtBeginning = firstVisibleItemPosition >= 0
                        val isTotalMoreThanVisible = totalItemCount >= queryPageSize
                        val shouldPaginate =
                            isNotLoadingAndNotLastPage &&
                                    isAtLastItem &&
                                    isNotAtBeginning &&
                                    isTotalMoreThanVisible &&
                                    !recyclerView.canScrollVertically(1) &&
                                  /*  !btnRetryIsVisible &&*/
                                    !isLastPage()

                        if (shouldPaginate) {
                            currentPage++
                            mListener?.loadData(currentPage, queryPageSize, getStartIndex())
                        }
                    }
                }
            })

            attrs?.let { setAttribute(context, attrs) }
        }
    }

    private fun setAttribute(context: Context, attrs: AttributeSet) {

        viewBinding.apply {

            val ta = context.obtainStyledAttributes(attrs, R.styleable.SMRecycler, 0, 0)
            val index = ta.getInteger(R.styleable.SMRecycler_decoration_type, 0)
            val type: ItemDecorationType =
                ItemDecorationType.values()[index]
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
        }
    }

    fun getRecycler(listener: OnLoadMore? = null): RecyclerView {
        if (mListener == null)
            mListener = listener
        return viewBinding.recycler
    }

    private fun getStartIndex(): Int {
        return if (currentPage > 0) currentPage * queryPageSize else currentPage
    }

    fun setupResponse(
        showMessage: Boolean?,
        message: String = "",
        isLoading: Boolean,
        totalItems: Int? = 0,
        showRetryButton: Boolean
    ) {
        this.isLoading = isLoading
        totalPages = (totalItems?.div(queryPageSize) ?: 0) + 1
//        btnRetryIsVisible = showRetryButton

        if (btnRetryIsVisible) currentPage--

        if (showMessage == true) {
            viewBinding.recycler.visibility = View.GONE
            viewBinding.tvMessage.visibility = View.VISIBLE
            if (message.isNotBlank()) viewBinding.tvMessage.text = message
        } else {
            viewBinding.recycler.visibility = View.VISIBLE
            viewBinding.tvMessage.visibility = View.GONE
        }
    }

    fun showMessage(showMessage: Boolean) {

        if (showMessage) {
            viewBinding.recycler.visibility = View.GONE
            viewBinding.tvMessage.visibility = View.VISIBLE
        } else {
            viewBinding.recycler.visibility = View.VISIBLE
            viewBinding.tvMessage.visibility = View.GONE
        }
    }





    private enum class ItemDecorationType {
        VERTICAL,
        HORIZONTAL,
        LINE,
        TAMIN_BG,
        GRID_MULTI_COL,
        MENU
    }
}