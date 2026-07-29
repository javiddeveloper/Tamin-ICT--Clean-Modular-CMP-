package com.tamin.taminhamrah.ui.home.dashboard

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.DialogFilterBinding
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import com.tamin.taminhamrah.utils.UiUtils
import dagger.hilt.android.AndroidEntryPoint

@Deprecated("We removed it")
@AndroidEntryPoint
class FilterDialogFragment :
    BaseBottomSheetDialogFragment<DialogFilterBinding,BaseViewModel>(),
    AdapterInterface.OnItemClickListener<MenuModel>,
    CompoundButton.OnCheckedChangeListener {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_filter

    companion object {
        const val ARG_FILTER_LIST = "ARG_FILTER_LIST"
        const val ARG_FILTER_TITLE = "ARG_FILTER_TITLE"
    }

//    val mViewModel: ServicesViewModel by activityViewModels()

    private var onListener: MenuInterface.OnResultListItem? = null
    fun setListener(listener: MenuInterface.OnResultListItem) {
        onListener = listener
    }

    private lateinit var listAdapter: FilterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
    }

    private fun getList(): List<MenuModel>? {
        return arguments?.getParcelableArrayList<MenuModel>(ARG_FILTER_LIST)
    }

    private fun init() {
        viewBinding?.tvTitle?.text = arguments?.getString(ARG_FILTER_TITLE)
        listAdapter = FilterAdapter(this)
        getList()?.let { listAdapter.setItems(it) }
//        mViewModel.mldMainServiceList.value?.let { listAdapter.setItems(it) }
        viewBinding?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    UiUtils.BackgroundItemDecoration(
                        ContextCompat.getColor(requireContext(), R.color.lineColor),
                        ContextCompat.getColor(requireContext(), android.R.color.white)
                    )
                )
            }
        }
        val result = getList()?.filter { it.isSelected }

        if (result.isNullOrEmpty()) {
            viewBinding?.cbSelectAll?.isChecked = false
        } else {
            viewBinding?.cbSelectAll?.isChecked = result.size == getList()?.size
        }

    }

    private fun onClick() {
        viewBinding?.cbSelectAll?.setOnCheckedChangeListener(this)

        viewBinding?.btnConfirm?.setOnClickListener {
            // mViewModel.filterByTitle()

            getList()?.let { it1 -> onListener?.onResult(it1) }
            dismiss()
        }
    }

    override fun onItemClick(item: MenuModel, transitionView: View?, tag: String?) {
        viewBinding?.cbSelectAll?.apply {
            setOnCheckedChangeListener(null)
            isChecked = false
            setOnCheckedChangeListener(this@FilterDialogFragment)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        getList()?.apply {
            if (isChecked) {
                forEach {
                    it.isSelected = true
                }
            } else {
                forEach {
                    it.isSelected = false
                }
            }
            listAdapter.notifyItemRangeChanged(0, size)
        }
    }
}