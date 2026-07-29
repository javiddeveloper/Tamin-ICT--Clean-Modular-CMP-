package com.tamin.taminhamrah.ui.home.services.employer.registration

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.DialogWorkshopRecentlyAddedMemberSearchBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class WRecentlyAddedMemberSDialogFragment :
    BaseBottomSheetDialogFragment<DialogWorkshopRecentlyAddedMemberSearchBinding,InsuredRegistrationViewModel >() {

    override val mViewModelDialog: InsuredRegistrationViewModel by viewModels()
    override fun getLayoutId()= R.layout.dialog_workshop_recently_added_member_search
    var mListener: OnResultListener<Map<String, String>>? = null

    fun setListener(listener: OnResultListener<Map<String, String>>) {
        mListener = listener
    }

    fun setupObserver() {
    }

    private var onListener: MenuInterface.OnResult? = null

    fun setListener(listener: MenuInterface.OnResult) {
        onListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        initView()
        setupObserver()
    }

    var selectedStatusId: String? = null
    private fun initView() {

        viewBinding?.inputRequestStatus?.getIt()?.setOnClickListener {

            val dialog = MenuDialogFragment.newInstance(menuTitle = "")
            dialog.setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@WRecentlyAddedMemberSDialogFragment.lifecycleScope.launchWhenCreated {
                        val pager = Pager(
                            config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                            pagingSourceFactory = { LocalPagingSource(mViewModelDialog.getStatusList()) })
                        pager.flow.cachedIn(lifecycleScope).collectLatest { pagingData ->
                            dialog.updateData(pagingData)
                        }
                    }
                }
            }, object : MenuInterface.OnResult {
                override fun onResult(itemResult: MenuModel) {
                    viewBinding?.inputRequestStatus?.getIt()?.setText(itemResult.title)
                    selectedStatusId = itemResult.id
                }
            })
            dialog.show(childFragmentManager, "hkjhkj")

        }

    }

    @SuppressLint("SetTextI18n")
    private fun onClick() {
        viewBinding?.apply {
            btnSearch.setOnClickListener {
                val map = HashMap<String, String>()
                map[mViewModelDialog.ARG_NATIONAL_CODE] = inputNationalCode.getNullableValue()
                if (!selectedStatusId.isNullOrBlank())
                    map[mViewModelDialog.ARG_REQUEST_STATUS] = selectedStatusId!!
                mListener?.onDialogResult(map)

                dismiss()
            }
        }
    }

    private fun setSearchTitle(s: CharSequence?, btnSearch: AppCompatButton) {
        if (s.isNullOrBlank()) {
            btnSearch.text = getString(R.string.label_show_all_workshops)
        } else {
            btnSearch.text = getString(R.string.label_search_workshop)
        }
    }
}