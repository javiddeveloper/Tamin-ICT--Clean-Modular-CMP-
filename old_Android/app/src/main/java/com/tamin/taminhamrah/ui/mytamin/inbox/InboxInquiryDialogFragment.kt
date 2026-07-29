package com.tamin.taminhamrah.ui.mytamin.inbox

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
import com.tamin.taminhamrah.databinding.DialogInboxInquiryBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class InboxInquiryDialogFragment :
    BaseBottomSheetDialogFragment<DialogInboxInquiryBinding, InboxViewModel>() {

    override val mViewModelDialog: InboxViewModel by activityViewModels()
    override fun getLayoutId() = R.layout.dialog_inbox_inquiry

    private var onResultListener: MenuInterface.OnResult? = null

    fun setListener(listener: MenuInterface.OnResult) {
        onResultListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
    }

    private fun onClick() {
        viewBinding?.apply {
            inputPeriod.getIt().setOnClickListener {
                Timber.tag("onDeleteListener").e(" label_inquiry_license ")
                //////////////////////////////////
                val dialog = MenuDialogFragment.newInstance(menuTitle = "")
                dialog.setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        inputPeriod.disableError()
                        this@InboxInquiryDialogFragment.lifecycleScope.launchWhenCreated {
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = { LocalPagingSource(mViewModelDialog.getLicensePeriodList()) })
                            pager.flow.cachedIn(lifecycleScope).collectLatest { pagingData ->
                                dialog.updateData(pagingData)
                            }
                        }
                    }
                }, object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        inputPeriod.setValue(
                            itemResult.title ?: mViewModelDialog.selectedItem?.title ?: ""
                        )
                        mViewModelDialog.selectedItem = itemResult


                    }
                })
                dialog.show(childFragmentManager, "hkjhkj")

                //////////////////////////////////////
            }

            btnSendRequest.setOnClickListener {
                if (mViewModelDialog.selectedItem == null)
                    inputPeriod.setError(getString(R.string.select_inquiry_lisence_period))
                else {
                    inputPeriod.disableError()
                    onResultListener?.onResult(mViewModelDialog.selectedItem!!)
                    dismiss()
                }
            }

        }

    }

}