package com.tamin.taminhamrah.ui.dialog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.DialogMenuBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface.OnFetchData
import com.tamin.taminhamrah.ui.appinterface.MenuInterface.OnResult
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.gone
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class
MenuDialogFragment :
    BaseBottomSheetDialogFragment<DialogMenuBinding, BaseViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel> {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId() = com.tamin.taminhamrah.R.layout.dialog_menu
    var isVisibleSearch = true
    companion object {
        const val ARG_MENU_TITLE = "ARG_MENU_TITLE"
        const val ARG_MENU_VISIBLE_SEARCH = "ARG_MENU_VISIBLE_SEARCH"

        fun newInstance(isRemoteData: Boolean = false, menuTitle: String = "",isVisibleSearch:Boolean= true): MenuDialogFragment {
            val args = Bundle()
            args.putBoolean(Constants.IS_REMOTE_DATA, isRemoteData)
            args.putString(ARG_MENU_TITLE, menuTitle)
            args.putBoolean(ARG_MENU_VISIBLE_SEARCH,isVisibleSearch)
            val fragment = MenuDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var onResultListener: OnResult? = null
    private var onFetchListener: OnFetchData? = null
    private var onSearch: MenuInterface.OnSearch? = null
    private var onStopDialogListener: AdapterInterface.OnStopDialogListener? = null
    private var defaultList = arrayListOf<MenuModel>()
    private var searchBarWasShown = false

    fun setMenuListener(
        fetchListener: OnFetchData,
        resultListener: OnResult,
        searchListener: MenuInterface.OnSearch? = null
    ) {
        onFetchListener = fetchListener
        onResultListener = resultListener
        searchListener?.let {
            onSearch = searchListener
        }
    }

    fun setListener(resultListener: OnResult) {
        onResultListener = resultListener
    }

    fun setStopListenr(listener: AdapterInterface.OnStopDialogListener) {
        onStopDialogListener = listener
    }

    var listAdapter: MenuAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
        onFetchListener?.onFetch()
    }

    private fun init() {

        viewBinding?.apply {
            if (getTitle().isBlank()) {
                tvTitle.visibility = View.GONE
            } else {
                tvTitle.apply {
                    visibility = View.VISIBLE
                    text = getTitle()
                }
            }
            if (!getVisibilitySearchView()){
                isVisibleSearch = false
                searchView.visibility = View.GONE
            }
            val searchEditText =
                searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

            val textColor = MaterialColors.getColor(searchView.rootView,
                com.google.android.material.R.attr.colorPrimaryFixed
            )
            searchEditText?.setTextColor(textColor)
            searchEditText?.setHintTextColor(textColor)
        }
        listAdapter = MenuAdapter(this).apply {

            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    updateSearchViewVisibility(itemCount)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    super.onItemRangeRemoved(positionStart, itemCount)
                    updateSearchViewVisibility(itemCount)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    super.onItemRangeChanged(positionStart, itemCount, payload)
                    updateSearchViewVisibility(itemCount)
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                }
            })
        }

        setupRecycler(viewBinding?.recycler, listAdapter!!)
        /* lifecycleScope.launch {
             listAdapter.loadStateFlow.collectLatest { loadState: CombinedLoadStates ->
                 viewBinding?.let {
                     handleResponse(
                         loadState = loadState,
                         recycler = it.recycler,
                         loadingView = loadingView,
                         parent = it.rootLayout,
                         showError = false
                     )
                 }
             }
         }*/
    }

    private fun updateSearchViewVisibility(itemCount : Int) {
            // Only show search bar when search is enabled AND this dialog has a search listener (e.g. city list).
            // Dialogs without search (e.g. contract types) pass no searchListener, so onSearch is null.
            val showSearch = isVisibleSearch &&
                onSearch != null &&
                (itemCount > 9 || searchBarWasShown)
            if (showSearch) searchBarWasShown = true
            viewBinding?.searchView?.visibility = if (showSearch) View.VISIBLE else View.GONE
    }

    private fun getTitle(): String {
        return arguments?.getString(ARG_MENU_TITLE) ?: ""
    }
    private fun getVisibilitySearchView(): Boolean {
        return arguments?.getBoolean(ARG_MENU_VISIBLE_SEARCH) ?: true
    }

    var delay: Long = 400 // debounce: search after user stops typing (ms)

    var last_text_edit: Long = 0

    private val searchHandler = Handler(Looper.getMainLooper())

    private val input_finish_checker = Runnable {
        if (System.currentTimeMillis() >= last_text_edit + delay) {
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            onSearch?.onSearch(searchString)
        }
    }

    private var searchString = ""
    private fun onClick() {
        viewBinding?.searchView?.apply {
            setOnClickListener { isIconified = false }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(searchStr: String?): Boolean {
                    searchHandler.removeCallbacks(input_finish_checker)
                    if (searchStr?.isNotBlank() == true) {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                        onSearch?.onSearch(searchStr)
                    } else {
                        onSearch?.onSearch("")
                    }
                    return true
                }

                override fun onQueryTextChange(searchStr: String?): Boolean {
                    searchString = searchStr ?: ""
                    last_text_edit = System.currentTimeMillis()
                    searchHandler.removeCallbacks(input_finish_checker)
                    searchHandler.postDelayed(input_finish_checker, delay)
                    return true
                }

            })
            setOnCloseListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                onSearch?.onSearch( "")
                viewBinding?.labelWarning?.gone()
                false
            }
        }
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        onResultListener?.onResult(item)
        dismiss()
    }


    fun searchList(searchString: String?): ArrayList<MenuModel>? {
        return if (searchString.isNullOrBlank()) {
            defaultList

        } else {
            val searchedList = ArrayList<MenuModel>()
            defaultList.forEach {
                if (it.title?.contains(searchString) == true) {
                    searchedList.add(it)
                }
            }
            searchedList
        }
    }

    override fun onDestroyView() {
        searchHandler.removeCallbacks(input_finish_checker)
        super.onDestroyView()
        onStopDialogListener?.onStop()
    }


    suspend fun updateData(pagingData: PagingData<MenuModel>) {

        val result = pagingData.map {
            if (it.title.isNullOrBlank() && it.titleStringResId > 0)
                it.title = getString(it.titleStringResId)
            if (it.description.isNullOrBlank() && it.descStringResId>0)
                it.description = getString(it.descStringResId)
            it
        }
        listAdapter?.submitData(result)


    }


}