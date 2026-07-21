package com.tamin.taminhamrah.ui.dialog.menuDialogMultiSelect

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.DialogMenuMultiSelectBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuDialogMultiSelectFragment :  BaseBottomSheetDialogFragment<DialogMenuMultiSelectBinding,BaseViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel>{

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_menu_multi_select

    companion object {
        const val ARG_MENU_ITEMS = "ARG_MENU_ITEMS"
    }

    private var onListener: MenuInterface.OnResultListItem? = null

    private var onStopDialogListener: AdapterInterface.OnStopDialogListener? = null

    private var defaultList: ArrayList<MenuModel>? = null
    private var selectedList = arrayListOf<MenuModel>()

    fun setListener(listener: MenuInterface.OnResultListItem) {
        onListener = listener
    }

    fun setStopListenr(listener: AdapterInterface.OnStopDialogListener) {
        onStopDialogListener = listener
    }

    private lateinit var listAdapter: MenuMultiSelectAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
    }

    private fun getExtraValue(): ArrayList<MenuModel>? {
        return arguments?.getParcelableArrayList(ARG_MENU_ITEMS)
    }


    private fun init() {
        defaultList = getExtraValue()
        listAdapter = MenuMultiSelectAdapter()
        viewBinding?.recycler?.apply {
            layoutManager = layoutManager
            adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(
                        UiUtils.createDivider(requireContext())
                )
            }
        }
        defaultList?.let {
            listAdapter.setItems(it.toList(),this)
        }

    }

    private fun onClick() {
        viewBinding?.let {
            it.btnConfirm.setOnClickListener {
                onListener?.onResult(selectedList)
                dismiss()
            }
        }

    }


    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        if (selectedList.contains(item))
            selectedList.remove(item)
        else
        selectedList.add(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        onStopDialogListener?.onStop()
    }

}