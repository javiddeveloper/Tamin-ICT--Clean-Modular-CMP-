package com.tamin.taminhamrah.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import androidx.annotation.Nullable
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.Constants.QUERY_PAGE_SIZE_15
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.BaseListResponse
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.databinding.DialogPagingListBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.dialog.MenuAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.ValidationUtil

abstract class BasePagingListDialogFragment<T> :
    BaseBottomSheetDialogFragment<DialogPagingListBinding,BaseViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    var oldList = arrayListOf<MenuModel>()
    var oldListSet = mutableSetOf<MenuModel>()
    var isLastPage = false
    var isScrolling = false
    var btnRetryIsVisible = false
    var isLoading = true
    var totalPages = 100

    private lateinit var listAdapter: MenuAdapter

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    abstract fun getList(str: String = "")
    abstract fun resetPage()
    abstract fun getTotalPage(): Int
    abstract fun hasRequestItem(): Boolean
    abstract fun getMenuModelList(list: List<T>): List<MenuModel>
    abstract fun getPage(): Int
    abstract fun setupObserver()
    abstract fun getTitle(): String

    var onResultListener: DialogResultInterface.OnResultListener<MenuModel>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
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
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + totalVisibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE_15
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                getList()
                isScrolling = false
            }
        }
    }

    fun setListener(listener: DialogResultInterface.OnResultListener<MenuModel>) {
        onResultListener = listener
    }

    private fun initData() {
        getList()
    }

    private fun initView() {
        listAdapter = MenuAdapter()
        viewBinding?.recycler?.getRecycler()?.apply {
            adapter = listAdapter
            val layoutManager = LinearLayoutManager(requireContext())
            this.layoutManager = layoutManager
            setFocusableInTouchMode(true)
            setNestedScrollingEnabled(true)
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    UiUtils.BackgroundItemDecoration(
                        ContextCompat.getColor(requireContext(), R.color.lineColor),
                        ContextCompat.getColor(requireContext(), android.R.color.white)
                    )
                )
            }
            addOnScrollListener(this@BasePagingListDialogFragment.scrollListener)
            setupObserver()
        }
        viewBinding?.labelTitle?.text = getTitle()
        onClick()
    }

    private fun onClick() {
        viewBinding?.let {
            it.btnRetry.setOnClickListener {
                getList()
            }

            it.searchView.apply {
                setOnClickListener {
                    isIconified = false
                    getList()
                }
                it.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(s: String?): Boolean {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                        s?.let {
                            resetPage()
                            getList(ValidationUtil.persianToEnglish(s))
                            oldList = arrayListOf()
                            oldListSet = mutableSetOf()
                        }
                        return true
                    }

                    override fun onQueryTextChange(s: String?): Boolean {

                        return true
                    }
                })
                //Collapse the search widget
                setOnCloseListener {
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    resetPage()
                    getList()
                    false
                }

            }

        }
    }

    fun handleResponsePaging(result: Resource<BaseListResponse<T>?>?) {
        viewBinding?.loadingView?.let {
         //   handleResponse(result,it )
        }

        when (result?.status) {
            Resource.Status.SUCCESS -> {
                if (hasRequestItem()) {
                    viewBinding?.let {
                        it.labelWarning.visibility = View.GONE
                        it.recycler.showMessage(false)
                        it.loadingView.parent.visibility = View.GONE
                        if (it.btnRetry.visibility == View.VISIBLE) {
                            it.btnRetry.visibility = View.GONE
                            btnRetryIsVisible = false
                        }
                    }
                    getTotalPage().let {
                        totalPages = it / QUERY_PAGE_SIZE_15 + 1
                    }
                    isLoading = false
                    result.data?.let { Response ->
                        Response.list?.let {
                            oldList.addAll(getMenuModelList(it))
                            oldList.forEach {
                                oldListSet.add(it)
                            }
                            listAdapter.setItems(oldListSet.toList(), this)
                        }
                        isLastPage = getPage() == totalPages
                    }
                } else {
                    viewBinding?.let {
                        it.loadingView.parent.visibility = View.GONE
                        it.labelWarning.visibility = View.VISIBLE
                        if (getPage() <= 1)
                            it.recycler.showMessage(true)
                    }
                }
            }
            Resource.Status.LOADING -> {
                viewBinding?.let {
                    it.loadingView.parent.visibility = View.VISIBLE
                    it.labelWarning.visibility = View.GONE
                }

            }
            Resource.Status.ERROR -> {
                viewBinding?.let {
                    it.loadingView.parent.visibility = View.GONE
                    it.labelWarning.visibility = View.VISIBLE
                    it.labelWarning.text = result.message?.message
                    it.btnRetry.visibility = View.VISIBLE
                }
            }

            else -> {}
        }
    }


}
