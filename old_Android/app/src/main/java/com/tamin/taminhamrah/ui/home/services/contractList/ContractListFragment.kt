package com.tamin.taminhamrah.ui.home.services.contractList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.user.ContractItem
import com.tamin.taminhamrah.databinding.FragmentContractListBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.contractList.searchContactList.SearchContractListBottomFragment
import com.tamin.taminhamrah.ui.home.services.contracts.ContractBaseFragment.Companion.ARG_CONTACT_TYPE
import com.tamin.taminhamrah.ui.home.services.contracts.EditContractFragment
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.studentContract.payment.insurance.InsurancePaymentFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class ContractListFragment : BaseFragment<FragmentContractListBinding, ContractListViewModel>(),
    AdapterInterface.OnItemClickListener<ContractItem>,
    DialogResultInterface.OnResultListener<Map<String, String>> {
    lateinit var listAdapter: ContractListAdapter
    override val mViewModel: ContractListViewModel by viewModels()

    var contractNumber = "0"

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_contract_list

    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::onDownloadPdfFileResponse)
    }

    override fun initView() {
        initAdapter()
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_search
        )
    }

    private fun initAdapter() {
        listAdapter = ContractListAdapter()
        listAdapter.onClickListener = this@ContractListFragment
        setupRecycler(viewDataBinding?.recycler, listAdapter)
    }

    override fun getData() {
        this@ContractListFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getContractInsuranceList().collectLatest { pagingData ->
                var btnVisibility = true
                val result = pagingData.filter {
                    if (it.contractStatusObject?.selfIsuContStatCode == 1 && it.premiumTypeCode != "38") {
                        //   viewDataBinding?.btnAddContract?.visibility = View.GONE
                        btnVisibility = false

                    } else {
//                        viewDataBinding?.btnAddContract?.visibility = View.VISIBLE
                    }
                    true
                }
                listAdapter.submitData(this@ContractListFragment.lifecycle, result)
                listAdapter.addLoadStateListener { loadState ->
                    if (loadState.source.append is LoadState.NotLoading) {
                        viewDataBinding?.btnAddContract?.visibility =
                            if (btnVisibility) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    val dialog by lazy {
        MenuDialogFragment.newInstance(true, getString(R.string.label_contract_types))
            .apply {
                setMenuListener(object : MenuInterface.OnFetchData {
                    override fun onFetch() {
                        this@ContractListFragment.lifecycleScope.launchWhenCreated {
                            val pager = Pager(
                                config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                pagingSourceFactory = { LocalPagingSource(Utility.getContractTypes()) })

                            pager.flow.cachedIn(lifecycleScope).collectLatest {
                                updateData(it)
                            }
                        }
                    }
                }, object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        var title = ""
                        when (itemResult.id) {
                            "0" -> {
                                goToContractPage(
                                    getString(R.string.title_student_contract_fragment),
                                    EnumInsuranceType.TYPE_STUDENT
                                )
                            }
                            "1" -> {
                                goToContractPage(
                                    getString(R.string.title_women_contract_fragment),
                                    EnumInsuranceType.TYPE_WOMAN
                                )
                            }
                            "2" -> {
                                goToContractPage(
                                    getString(R.string.title_freelance_contract_fragment),
                                    EnumInsuranceType.TYPE_FREELANCE
                                )
                            }
                            "3" -> {
                                //optional insurance
                                goToContractPage(
                                    getString(R.string.title_optional_contract_fragment),
                                    EnumInsuranceType.TYPE_OPTIONAL
                                )
                            }
                        }
                    }
                })


            }
    }

    private fun goToContractPage(title: String, insuranceType: EnumInsuranceType) {
        val bundle = Bundle()
        bundle.putSerializable(ARG_CONTACT_TYPE, insuranceType)
        bundle.putString(Constants.TOOLBAR_TITLE, title)
        handlePageDestination(
            if (insuranceType == EnumInsuranceType.TYPE_OPTIONAL) R.id.action_to_optionalContractFragment else R.id.action_contractList_to_newContract,
            bundle
        )
    }

    override fun onClick() {
        viewDataBinding?.appBar?.toolbar?.imgAction?.setOnClickListener {
            val dialog = SearchContractListBottomFragment()
            dialog.mListener = this@ContractListFragment
            dialog.show(childFragmentManager, ContractListFragment().javaClass.simpleName)
        }

        viewDataBinding?.btnAddContract?.setOnClickListener {

            dialog.show(childFragmentManager, MenuDialogFragment::javaClass.name)

        }
    }

    override fun onResume() {
        super.onResume()
        if (listAdapter.itemCount != 0) {
            listAdapter.refresh()
            getData()
        }
    }

    override fun onDialogResult(item: Map<String, String>) {
        viewDataBinding?.apply {
            this@ContractListFragment.lifecycleScope.launchWhenCreated {
                val contractNumberSearchItem = item[mViewModel.ARG_CONTRACT_NUMBER]
                val insuranceTypeSearchItem = item[mViewModel.ARG_INSURANCE_TYPE]
                mViewModel.getContractInsuranceList(
                    contractNumber = contractNumberSearchItem,
                    premiumTypeCode = insuranceTypeSearchItem
                ).collectLatest { pagingData ->
                    listAdapter.submitData(requireActivity().lifecycle, pagingData.map { data ->
                        contractNumber = data.contractNumber.toString()
                        data
                    })

                }
            }
        }
    }

    override fun onItemClick(item: ContractItem, transitionView: View?, tag: String?) {

        val dialog = MenuDialogFragment.newInstance()
        dialog.setMenuListener(object : MenuInterface.OnFetchData {

            override fun onFetch() {
                this@ContractListFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getActionListFlow(item)
                        .collectLatest { pagingData ->
                            dialog.updateData(pagingData)
                        }

                }
            }

        }, object : MenuInterface.OnResult {

            override fun onResult(menu: MenuModel) {
                when (menu.title) {
                    "پرداخت حق بیمه" -> {//payment
                        val bundle = Bundle()

                        bundle.putInt(
                            Constants.TOOLBAR_ICON_IMAGE,
                            R.drawable.ic_mobile_payment
                        )

                        when (item.premiumType?.insuranceTypeCode) {
                            "01" -> {

                                if (item.freeJob?.jobCode == "099796" && item.freeJob.discrioption == "دانشجو") {
                                    bundle.putSerializable(
                                        InsurancePaymentFragment.INSURANCE_TYPE,
                                        EnumInsuranceType.TYPE_STUDENT
                                    )
                                    bundle.putString(
                                        Constants.TOOLBAR_TITLE,
                                        "${getString(R.string.label_insurance_payment)} ${EnumInsuranceType.TYPE_STUDENT.insuranceName}"
                                    )
                                } else {
                                    bundle.putSerializable(
                                        InsurancePaymentFragment.INSURANCE_TYPE,
                                        EnumInsuranceType.TYPE_FREELANCE
                                    )
                                    bundle.putString(
                                        Constants.TOOLBAR_TITLE,
                                        "${getString(R.string.label_insurance_payment)} ${EnumInsuranceType.TYPE_FREELANCE.insuranceName}"
                                    )
                                }
                            }
                            "02" -> {
                                bundle.putSerializable(
                                    InsurancePaymentFragment.INSURANCE_TYPE,
                                    EnumInsuranceType.TYPE_OPTIONAL
                                )
                                bundle.putString(
                                    Constants.TOOLBAR_TITLE,
                                    "${getString(R.string.label_insurance_payment)} ${EnumInsuranceType.TYPE_OPTIONAL.insuranceName}"
                                )
                            }
                            "38" -> {
                                bundle.putSerializable(
                                    InsurancePaymentFragment.INSURANCE_TYPE,
                                    EnumInsuranceType.TYPE_FRACTION
                                )
                                bundle.putString(
                                    Constants.TOOLBAR_TITLE,
                                    "${getString(R.string.label_insurance_payment2)} ${EnumInsuranceType.TYPE_FRACTION.insuranceName}"
                                )
                            }
                        }

                        handlePageDestination(
                            R.id.action_contractList_to_insurancePayment,
                            bundle
                        )
                    }
                    "مشاهده پرداخت ها" -> {//payment report
                        val bundleService = Bundle()
                        bundleService.putString(
                            Constants.TOOLBAR_TITLE,
                            getString(R.string.label_history_payment)
                        )
                        bundleService.putInt(
                            Constants.TOOLBAR_ICON_IMAGE,
                            R.drawable.ic_contract_list
                        )
                        bundleService.putString(
                            Constants.CONTRACT_NUMBER_KEY,
                            item.contractNumber.toString()
                        )

                        when (item.premiumType?.insuranceTypeCode) {
                            "01" -> {
                                bundleService.putString(
                                    Constants.CONTRACT_SYSTEM_TYPE,
                                    EnumInsuranceType.TYPE_FREELANCE.systemType
                                )
                            }
                            "02" -> {
                                bundleService.putString(
                                    Constants.CONTRACT_SYSTEM_TYPE,
                                    EnumInsuranceType.TYPE_OPTIONAL.systemType
                                )
                            }
                        }

                        handlePageDestination(
                            R.id.action_contractListFragment_to_paymentListFragment,
                            bundle = bundleService
                        )
                    }
                    "مشاهده قرارداد" -> {//view
                        if (item.premiumTypeCode == "38")
                            mViewModel.downloadFractionContractPdf()
                        else
                            mViewModel.downloadContractPdf(item.premiumType?.insuranceTypeCode == "02")
                    }
                    "ویرایش قرارداد" -> {//edit

                        when (item.premiumType?.insuranceTypeCode) {
                            "01" -> {
                                val bundle = Bundle()
                                bundle.putString(
                                    Constants.TOOLBAR_TITLE,
                                    getString(R.string.label_edit_contract)
                                )
                                bundle.putSerializable(
                                    EditContractFragment.ARG_CONTRACT_TYPE,
                                    when (item.cntFreeJobCode) {
                                        Constants.STUDENT_CONTRACT_CODE -> EnumInsuranceType.TYPE_STUDENT
                                        Constants.WOMEN_CONTRACT_CODE -> EnumInsuranceType.TYPE_WOMAN
                                        else -> EnumInsuranceType.TYPE_FREELANCE
                                    }
                                )
                                handlePageDestination(
                                    R.id.action_contractListFragment_to_editContractFragment,
                                    bundle
                                )
                                Timber.tag("job_code").e("${item.freeJob}")

                            }
                            "02" -> {
                                val bundle = Bundle()
                                bundle.putSerializable(
                                    ARG_CONTACT_TYPE,
                                    EnumInsuranceType.TYPE_OPTIONAL
                                )
                                bundle.putString(
                                    Constants.TOOLBAR_TITLE,
                                    getString(R.string.label_edit_optional_contract)
                                )
                                bundle.putBoolean(Constants.EDIT_CONTRACT_MODE, true)
                                handlePageDestination(
                                    R.id.action_contractListFragment_to_editOptionalContractFragment,
                                    bundle
                                )
                            }
                        }
                    }

                    "غیرفعال کردن قرارداد" -> {//cancellation
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.TOOLBAR_TITLE,
                            getString(R.string.label_cancel_contract)
                        )
                        bundle.putInt(Constants.SERVICE_ID, 0)
                        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_contract_list)
                        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_contract_list)
                        when (item.premiumType?.insuranceTypeCode) {
                            "01" -> {
                                bundle.putString(
                                    Constants.CONTRACT_SYSTEM_TYPE,
                                    EnumInsuranceType.TYPE_FREELANCE.insuranceType
                                )
                            }
                            "02" -> {
                                bundle.putString(
                                    Constants.CONTRACT_SYSTEM_TYPE,
                                    EnumInsuranceType.TYPE_OPTIONAL.insuranceType
                                )
                            }
                        }
                        handlePageDestination(
                            R.id.action_contractListFragment_to_cancelContractFragment,
                            bundle
                        )
                    }
                }
            }
        })
        dialog.show(childFragmentManager, "contractListMenu")

    }

    // download pdf contract response
    private fun onDownloadPdfFileResponse(result: PdfDownloadResponse) {
        if (!result.isSuccess)
            return
        val file = Utility.writeByteStreamToDisk(
            Utility.getToolbarTitle(arguments),
            requireContext(),
            result.pdf
        )
        if (file == null) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_file)
            )
            return
        }
        val bundle = Bundle()
        bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
        bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
        handlePageDestination(R.id.action_ContractFragment_to_Activity_pdf_view, bundle)
    }
}