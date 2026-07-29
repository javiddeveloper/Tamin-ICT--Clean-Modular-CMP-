package com.tamin.taminhamrah.ui.home.services.inquirePensionStatus

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
import com.tamin.taminhamrah.ui.home.services.activeRelationInquiry.ActiveRelationInquiryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class CertificateToInboxInquireDialogFragment : BaseBottomSheetDialogFragment<CertificateToInboxBinding, InquirePensionStatusViewModel>() {

    override val mViewModelDialog: InquirePensionStatusViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: CertificateToInboxBinding? = null
    private val binding get() = _binding!!

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

        binding.selectBranch.getIt().setOnClickListener {
            binding.selectBranch.getLayout().isErrorEnabled = false
                val dialog = MenuDialogFragment.newInstance(
                    true,
                    getString(R.string.label_select_branch_name)
                )
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
                        itemResult.title?.let { title ->
                            binding.selectBranch.setValue(title)
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
                dialog.show(childFragmentManager, "trtyutyt")

            }

        binding.btnSenCertificateToInbox.setOnClickListener {
                if (binding.selectBranch.getValue(false).isBlank()) {
                    binding.selectBranch.getLayout().error = getString(R.string.error_select_branch_name)
                } else {
                    val branchName = StringBuilder()
                    branchName.append(binding.selectBranch.getIt().text.toString())

                    if (binding.interBranchName.getValue(false).isNotBlank()) {
                        if (!binding.interBranchName.getValue(false).contains("شعبه"))
                            branchName.append(" شعبه ")

                        branchName.append(" ")
                        branchName.append(binding.interBranchName.getValue(false))
                    }
                    mViewModelDialog.sendRequestInquirePensionCertificate(branchName.toString())
                    dismiss()
                }
            }

    }

    companion object {
        fun newInstance(): CertificateToInboxInquireDialogFragment {
            val fragment = CertificateToInboxInquireDialogFragment()
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}