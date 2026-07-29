package com.tamin.taminhamrah.ui.mytamin.inbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.databinding.DialogInboxSearchBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.annotations.NotNull

@AndroidEntryPoint
class InboxSearchDialogFragment :
    BaseBottomSheetDialogFragment<DialogInboxSearchBinding, InboxViewModel>() {

    override val mViewModelDialog: InboxViewModel by activityViewModels()
    override fun getLayoutId() = R.layout.dialog_inbox_search

    private var dateTimestampFrom: Long? = 0L
    private var dateTimestampTo: Long? = 0L

    fun setupObserver() {
        mViewModelDialog.mldSystemList.observe(this, ::showSystemResult)
        mViewModelDialog.mldSubjectList.observe(this, ::showSubjectResult)
    }

    private var onListener: MenuInterface.OnResult? = null

    fun setListener(listener: MenuInterface.OnResult) {
        onListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClick()
        setupObserver()
    }

    private fun init() {

    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {
        viewBinding?.apply {
            widgetDatePickerStart.inputDate.setOnClickListener {
                val datePicker = getDatePicker()
                datePicker?.setListener(object : MyPersianPickerListener {

                    override fun onDateSelected(@NotNull persianPickerDate: MyPersianPickerDate) {

                        dateTimestampFrom = persianPickerDate.timestamp
                        widgetDatePickerStart.inputDate.setText("${persianPickerDate.persianYear}/${persianPickerDate.persianMonth}/${persianPickerDate.persianDay}")
                    }

                    override fun onDismissed() {}
                })
                datePicker?.show()

            }

            widgetDatePickerEnd.inputDate.setOnClickListener {
                val datePicker = getDatePicker()
                datePicker?.setListener(object : MyPersianPickerListener {
                    override fun onDateSelected(@NotNull persianPickerDate: MyPersianPickerDate) {

                        dateTimestampTo = persianPickerDate.timestamp
                        widgetDatePickerEnd.inputDate.setText("${persianPickerDate.persianYear}/${persianPickerDate.persianMonth}/${persianPickerDate.persianDay}")
                    }

                    override fun onDismissed() {}
                })
                datePicker?.show()
            }

            /* inputSystemType.selectableInput?.setOnClickListener {
                 if (mViewModel.mldSystemList.value == null) {
                     mViewModel.getSystemList()
                 } else {
                     openMenuDialog(
                         mViewModel.mldSystemList.value?.data,
                         getString(R.string.label_system)
                     )
                 }
             }

             inputSubjectType.selectableInput?.setOnClickListener {

                 if (mViewModel.mldSubjectList.value == null) {
                     mViewModel.getSubjectList()
                 } else {
                     openMenuDialog(
                         mViewModel.mldSubjectList.value?.data,
                         getString(R.string.label_subject)
                     )
                 }
             }*/
        }

    }

    private fun showSystemResult(result: Resource<List<MenuModel>?>?) {

        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result?.status == Resource.Status.SUCCESS) {
            openMenuDialog(result.data, getString(R.string.label_system))
        }
    }

    private fun showSubjectResult(result: Resource<List<MenuModel>?>?) {

        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result?.status == Resource.Status.SUCCESS) {
            openMenuDialog(result.data, getString(R.string.label_subject))
        }
    }

    private fun openMenuDialog(list: List<MenuModel>?, tag: String) {

        val dialog = MenuDialogFragment.newInstance()
        dialog.setMenuListener(object : MenuInterface.OnFetchData {

            override fun onFetch() {
                list?.let {
                    this@InboxSearchDialogFragment.lifecycleScope.launchWhenCreated {
                        val pager = Pager(
                            config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                            pagingSourceFactory = { LocalPagingSource(list) }
                        )
                        pager.flow.cachedIn(lifecycleScope).collectLatest {
                            dialog.updateData(it)
                        }
                    }
                }
            }

        }, object : MenuInterface.OnResult {

            override fun onResult(itemResult: MenuModel) {
//                selectedPensionerId = item.id!!
//                item.title?.let { it1 -> inputPensionNumber.input.setText(it1) }
            }
        })
        dialog.show(childFragmentManager, tag)

        /* val bundle = Bundle()
         bundle.putParcelableArrayList(
             MenuDialogFragment.ARG_MENU_ITEMS,
             list as? ArrayList<MenuModel>
         )

         val dialog = MenuDialogFragment()..............
         dialog.arguments = bundle
         dialog.setListener(object : MenuInterface.OnResult {
             override fun onResult(item: MenuModel) {
                 when (tag) {
                     getString(R.string.label_system) -> {
 //                        item.title?.let { it1 -> viewBinding?.inputSystemType?.selectableInput?.setText(it1) }
                     }
                     getString(R.string.label_subject) -> {
 //                        item.title?.let { it1 -> viewBinding?.inputSubjectType?.selectableInput?.setText(it1) }
                     }
                 }

             }
         })

         dialog.show(childFragmentManager, tag)*/
    }


}