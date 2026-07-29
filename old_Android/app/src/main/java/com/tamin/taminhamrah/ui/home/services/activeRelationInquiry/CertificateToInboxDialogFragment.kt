package com.tamin.taminhamrah.ui.home.services.activeRelationInquiry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.databinding.CertificateToInboxBinding
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class CertificateToInboxDialogFragment : BaseBottomSheetDialogFragment<CertificateToInboxBinding, ActiveRelationInquiryViewModel>() {

    override val mViewModelDialog: ActiveRelationInquiryViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: CertificateToInboxBinding? = null
    private val binding get() = _binding!!
    private var selectedBranch: String = ""
    private var selectedBranchCode: String = ""

    override fun getLayoutId() = R.layout.certificate_to_inbox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = CertificateToInboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickUI()
    }

    private fun clickUI() {
        val selectedItem = mViewModelDialog.selectedItem

        binding.selectBranch.getIt().setOnClickListener {
            binding.selectBranch.getLayout().isErrorEnabled = false
            val dialog = MenuDialogFragment.newInstance(true, getString(R.string.error_select_bank))
            dialog.setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    lifecycleScope.launchWhenCreated {
                        mViewModelDialog.getBranchRequests()
                            .collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.recipientCode,
                                        title = it.recipientName
                                    )
                                }
                                dialog.updateData(result)
                            }
                    }
                }
            }, object : MenuInterface.OnResult {
                override fun onResult(itemResult: MenuModel) {
                    itemResult.title?.let {
                        binding.selectBranch.setValue(it)
                        selectedBranch = it
                    }
                    itemResult.id?.let {
                        selectedBranchCode = it
                    }
                }
            }, object : MenuInterface.OnSearch {
                override fun onSearch(str: String) {
                    lifecycleScope.launchWhenCreated {
                        mViewModelDialog.getBranchRequests(branchName = str)
                            .collectLatest { pagingData ->
                                val result = pagingData.map {
                                    MenuModel(
                                        id = it.recipientCode,
                                        title = it.recipientName
                                    )
                                }
                                dialog.updateData(result)
                            }
                    }
                }
            })

            dialog.show(childFragmentManager, "SelectBranchDialog")
        }

        binding.btnSenCertificateToInbox.setOnClickListener {

            var selectedBranchName =
                binding.interBranchName.getInput().text.toString()

            if (selectedBranch.isBlank()) {
                binding.selectBranch.getLayout().error =
                    getString(R.string.error_select_branch_name)
                return@setOnClickListener
            }

            if (selectedBranchName.isNotBlank() && !selectedBranchName.contains("شعبه")) {
                selectedBranchName = " شعبه $selectedBranchName"
            }

            Timber.tag("BottomSheet").i(selectedItem.toString())

            mViewModelDialog.sendCertificateRequest(
                branchCode = selectedItem?.organizationId,
                branchName = selectedBranchName,
                recipientId = selectedBranchCode,
                statusCode = selectedItem?.relationWithTamin
                    ?.baseAudienceType?.audienceTypeCode ?: "02",
                insuranceNumber = selectedItem?.insuranceId,
                endDate = selectedItem?.endDate
            )
            dismiss()
        }
    }

    companion object {
        fun newInstance(): CertificateToInboxDialogFragment {
            val fragment = CertificateToInboxDialogFragment()
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}