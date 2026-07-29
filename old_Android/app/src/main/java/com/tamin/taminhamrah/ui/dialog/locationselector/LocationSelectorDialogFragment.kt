package com.tamin.taminhamrah.ui.dialog.locationselector

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.DialogMenuBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface.OnResult
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.ui.dialog.MenuAdapter
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.widget.LocationSelectorWidget.ListType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class
LocationSelectorDialogFragment :
    BaseBottomSheetDialogFragment<DialogMenuBinding, BaseViewModel>() {

    override val mViewModelDialog: LocationSelectorViewModel by viewModels()
    override fun getLayoutId() = R.layout.dialog_menu

    val listAdapter: MenuAdapter by lazy {
        MenuAdapter(object : AdapterInterface.OnItemClickListener<MenuModel> {
            override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
                onResultListener?.onResult(item)
                dismiss()
            }
        }).apply {
            if (getListType() == ListType.TYPE_PROVINCE)
                registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        setSearchViewVisibility()
                    }

                    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                        super.onItemRangeChanged(positionStart, itemCount)
                    }

                    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                        super.onItemRangeRemoved(positionStart, itemCount)
                        setSearchViewVisibility()
                    }

                    override fun onItemRangeChanged(
                        positionStart: Int,
                        itemCount: Int,
                        payload: Any?
                    ) {
                        super.onItemRangeChanged(positionStart, itemCount, payload)
                        setSearchViewVisibility()
                    }

                    override fun onItemRangeMoved(
                        fromPosition: Int,
                        toPosition: Int,
                        itemCount: Int
                    ) {
                        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                    }

                })
        }
    }

    companion object {
        private const val ARG_LIST_TYPE = "ARG_LIST_TYPE"
        private const val ARG_SELECTED_ITEM_CODE = "ARG_SELECTED_ITEM_CODE"

        fun newInstance(
            listType: ListType,
            selectedItemCode: String? = null
        ): LocationSelectorDialogFragment {
            val args = Bundle()
            args.putSerializable(ARG_LIST_TYPE, listType)
            args.putString(ARG_SELECTED_ITEM_CODE, selectedItemCode)
            args.putSerializable(Constants.IS_REMOTE_DATA, true)
            val fragment = LocationSelectorDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var onResultListener: OnResult? = null
    fun setMenuListener(resultListener: OnResult) {
        onResultListener = resultListener
    }

    fun setListener(resultListener: OnResult) {
        onResultListener = resultListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(getListType())
        onClick()
        getData(getListType())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun getSelectedItemCode() = arguments?.getString(ARG_SELECTED_ITEM_CODE)
    private fun getListType() = arguments?.getSerializable(ARG_LIST_TYPE) as? ListType

    private fun getData(
        listType: ListType?,
        searchTitle: String? = null
    ) {
        this@LocationSelectorDialogFragment.lifecycleScope.launchWhenCreated {
            when (listType) {
                ListType.TYPE_PROVINCE -> {

                    mViewModelDialog.getProvincesList(searchTitle).collectLatest { pagingData ->
                        val result = pagingData.map {
                            MenuModel(
                                id = it.provinceCode,
                                title = it.provinceName?.ifBlank { "نامشخص" }
                            )
                        }
                        listAdapter.submitData(result)
                    }
                }

                ListType.TYPE_CITY -> {
                    mViewModelDialog.getCitiesList(getSelectedItemCode(), searchTitle)
                        .collectLatest { pagingData ->
                            val result = pagingData.map {
                                MenuModel(
                                    id = it.cityCode,
                                    title = it.cityName.ifBlank { "نامشخص" }
                                )
                            }
                            listAdapter.submitData(result)
                        }
                }
                ListType.TYPE_BRANCH -> {
                    mViewModelDialog.getBranchList(getSelectedItemCode(), searchTitle)
                        .collectLatest { pagingData ->
                            val result = pagingData.map {
                                MenuModel(
                                    id = it.code,
                                    title = it.getTitle()
                                )
                            }
                            listAdapter.submitData(result)
                        }
                }
                else -> {
                    //todo:show anyThing
                }
            }
        }
    }

    private fun init(listType: ListType?) {
        viewBinding?.apply {
            when (listType) {
                ListType.TYPE_PROVINCE -> {
                    tvTitle.text = getString(R.string.label_province_name)
                    searchView.visibility = View.VISIBLE
                }
                ListType.TYPE_CITY -> {
                    tvTitle.text = getString(R.string.label_city_name)
                    searchView.visibility = View.GONE
                }
                ListType.TYPE_BRANCH -> {
                    tvTitle.text = getString(R.string.label_branch_name)
                    searchView.visibility = View.GONE
                }
                else -> {}
            }
            setupRecycler(recycler, listAdapter)
        }
    }

    private fun onClick() {
        viewBinding?.searchView?.apply {

            setOnClickListener { isIconified = false }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(searchStr: String?): Boolean {
                 //   if (searchStr?.isNotBlank() == true) {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                        getData(getListType(), searchStr)
                //    }
                    return true
                }

                override fun onQueryTextChange(searchStr: String?): Boolean {
                    if (searchStr.isNullOrEmpty())
                        isIconified = false
                    getData(getListType(), searchStr)
                    return true
                }
            })

            setOnCloseListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                getData(getListType(), null)
                false
            }
        }
    }

    private fun setSearchViewVisibility() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            viewBinding?.searchView?.visibility =
//                if ((listAdapter.itemCount ?: 0) > 9) View.VISIBLE else View.GONE
//        }, 100)
    }

}