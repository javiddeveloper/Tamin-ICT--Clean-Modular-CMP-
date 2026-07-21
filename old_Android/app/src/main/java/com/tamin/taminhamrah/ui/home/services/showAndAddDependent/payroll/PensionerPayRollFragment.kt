package com.tamin.taminhamrah.ui.home.services.showAndAddDependent.payroll

import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.android.material.appbar.AppBarLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.PayRollResponse
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.remote.models.services.getDeductionList
import com.tamin.taminhamrah.data.remote.models.services.getLoanList
import com.tamin.taminhamrah.data.remote.models.services.getPaymentList
import com.tamin.taminhamrah.databinding.FragmentPensionerPayRollBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.EdictPensionerFragment
import com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.adapter.EdictDetailInfoAdapter
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.Utility.getYearAndMonthWithDate
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import saman.zamani.persiandate.PersianDate
import kotlin.math.abs

@AndroidEntryPoint
class PensionerPayRollFragment :
    BaseFragment<FragmentPensionerPayRollBinding, PensionerPayRollViewModel>() {

    //Variables
    private var selectedMonth: String = ""
    private var selectedPaymentType: String = ""
    private var selectedPaymentTypeName: String = ""
    var mLastClickTime: Long = 0
    val payRollDetailInfoAdapter by lazy { EdictDetailInfoAdapter() }
    override val mViewModel: PensionerPayRollViewModel by viewModels()
    var requestTypeId = ""

    //Base methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_pensioner_pay_roll
    override fun setupObserver() {
        mViewModel.mldPayRoll.observe(this, ::onPayRollInfoResponse)
        mViewModel.mldSendToInboxResult.observe(this, ::onSendInboxResponse)
        mViewModel.mldPdf.observe(this, ::onDownloadPdfResponse)
        mViewModel.mldPensionerIdList.observe(this, ::onPensionerIdResponse)
    }

    private fun onPensionerIdResponse(result: PensionerIdResponse) {
        if (result.isSuccess) {
            mViewModel.pensionIdModelList.apply {
                clear()
                addAll(result.data?.list ?: emptyList())
                if (isNotEmpty()) {
                    viewDataBinding?.selectPensionId?.setValue(get(0).pensionerId ?: "")
                }
            }
        }
    }

    override fun initView() {
        viewDataBinding?.apply {

            initialToolbarPayRoll()

            viewDataBinding?.rcvInfoPayRoll?.apply {
                adapter = payRollDetailInfoAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }

            inputYear.getInput().doOnTextChanged { text, _, _, _ ->
                val str = text.toString()
                val cYear = PersianDate().shYear
                if (str.isNotBlank() && str.toInt() > cYear)
                    inputYear.getInput().setText(cYear.toString())
                inputYear.getLayout().isErrorEnabled = false
            }
            appBarInfoPayRoll.toolbar.imgAction.gone()
        }

        // setViewForShowGide()
    }

    override fun getData() {
        mViewModel.getPensionerIdList()
    }

    override fun onClick() {
        viewDataBinding?.apply {

            tvTitle.setOnClickListener {
                groupSearch.visible()
                btnSearchExpand.gone()
                setFocusUp()
            }

            btnSendInbox.setOnClickListener {
                mViewModel.sendPayRollToInbox(
                    selectPensionId.getValue(false),
                    "${inputYear.getValue()}${selectedMonth}",
                    selectedPaymentType
                )
            }

            selectPensionId.getIt().setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                MenuDialogFragment.newInstance(true, getString(R.string.pension_number)).apply {
                    setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            this@PensionerPayRollFragment.lifecycleScope.launchWhenCreated {
                                Pager(
                                    config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                    pagingSourceFactory = {
                                        LocalPagingSource(mViewModel.pensionIdModelList)
                                    }
                                ).flow.cachedIn(lifecycleScope).collectLatest { paginData ->
                                    updateData(paginData.map {
                                        MenuModel(id = it.pensionerId, title = it.pensionerId)
                                    })
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectPensionId.getLayout().isErrorEnabled = false
                            selectPensionId.setValue(itemResult.id ?: "")
                        }
                    })
                }.show(childFragmentManager, EdictPensionerFragment().javaClass.simpleName)
            }

            selectMonth.getIt().setOnClickListener {
                selectMonth.getLayout().isErrorEnabled = false

                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                MenuDialogFragment.newInstance(isVisibleSearch = false, menuTitle = getString(R.string.select_month)).apply {
                    setMenuListener(object : MenuInterface.OnFetchData {

                        override fun onFetch() {
                            this@PensionerPayRollFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.paymentMonthFlow.collectLatest { pagingData ->
                                    updateData(pagingData)
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            selectedMonth = itemResult.id!!
                            itemResult.title?.let { it1 -> selectMonth.getIt().setText(it1) }
                        }
                    })
                }.show(childFragmentManager, "month")
            }

            inputPaymentType.getIt().setOnClickListener {
                inputPaymentType.getLayout().isErrorEnabled = false
                when {
                    selectPensionId.getValue(false).isBlank() -> {
                        selectPensionId.getLayout().error =
                            getString(R.string.error_message_select_pensioner_id)
                        false
                    }
                    inputYear.getValue(true).isEmpty() -> {
                        inputYear.getLayout().error =
                            getString(R.string.please_enter_valid_value)
                        false
                    }
                    inputYear.getValue(true).length < 3 -> {
                        inputYear.getLayout().error =
                            getString(R.string.error_fill_year_with_4_char)
                        false
                    }
                    selectedMonth.isBlank() -> {
                        selectMonth.getLayout().error =
                            getString(R.string.error_message_select_month)
                        false
                    }

                    else -> {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                        MenuDialogFragment.newInstance(menuTitle = getString(R.string.select_payment_type))
                            .apply {
                                setMenuListener(object : MenuInterface.OnFetchData {

                                    override fun onFetch() {
                                        this@PensionerPayRollFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.paymentTypeFlow.collectLatest { pagingData ->
                                                updateData(pagingData)
                                            }
                                        }
                                    }

                                }, object : MenuInterface.OnResult {

                                    override fun onResult(itemResult: MenuModel) {
                                        selectedPaymentType = itemResult.id!!
                                        selectedPaymentTypeName = itemResult.title ?: ""
                                        itemResult.title?.let { it1 -> inputPaymentType.setValue(it1) }
                                    }
                                })
                            }.show(this@PensionerPayRollFragment.childFragmentManager, "trytyty")
                    }
                }


            }

            btnSearch.setOnClickListener {
                if (validateForm()) {
                    mViewModel.getPayRoll(
                        selectPensionId.getValue(false),
                        "${inputYear.getValue()}${selectedMonth}",
                        selectedPaymentType
                    )
                }
            }

            appBarInfoPayRoll.toolbar.apply {
                imgAction.setOnClickListener {
                    mViewModel.pensionerPayRollPDF(
                        "${inputYear.getValue()}${selectedMonth}",
                        selectPensionId.getValue(false),
                        selectedPaymentType
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //   cancelSequenceMaterialTapTarge()
    }

    private fun validateForm(): Boolean {
        viewDataBinding!!.apply {
            return when {
                selectPensionId.getValue(false).isBlank() -> {
                    selectPensionId.getLayout().error =
                        getString(R.string.error_message_select_pensioner_id)
                    false
                }

                inputYear.getValue(true).isEmpty() -> {
                    inputYear.getLayout().error = getString(R.string.please_enter_valid_value)
                    false
                }

                inputYear.getValue(true).length < 3 -> {
                    inputYear.getLayout().error =
                        getString(R.string.error_fill_year_with_4_char)
                    false
                }

                selectedMonth.isBlank() -> {
                    selectMonth.getLayout().error =
                        getString(R.string.error_message_select_month)
                    false
                }

                selectedPaymentType.isBlank() -> {
                    inputPaymentType.getLayout().error =
                        getString(R.string.error_select_payement_type)
                    false
                }

                else -> {
                    true
                }
            }
        }
    }

    //Listener
    private fun onPayRollInfoResponse(result: PayRollResponse) {
        if (result.isSuccess) {
            viewDataBinding?.apply {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                setFocusUp()
                val list = result.data?.list ?: emptyList()
                if (list.isNotEmpty()) {
                    list[0].sumPay?.let {
                        loadToolbarInfo(it)
                    }
                    btnSearchExpand.visible()
                    groupSearch.visibility = View.GONE
                    rcvInfoPayRoll.visible()
                    btnSendInbox.visible()
                    appBarInfoPayRoll.toolbar.imgAction.visibility = View.VISIBLE
                } else {
                    groupSearch.visibility = View.VISIBLE
                    rcvInfoPayRoll.gone()
                    btnSendInbox.gone()
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.INFO,
                        getString(R.string.error_empty_list)
                    )
                }
                result.data?.list?.apply {
                    val paymentList = getPaymentList(this)
                    val deductionList = getDeductionList(this)
                    val loanList = getLoanList(this)
                    val detailInfo: MutableMap<String, List<KeyValueModel>> = mutableMapOf()
                    if (paymentList.isNotEmpty())
                        detailInfo[getString(R.string.label_total_payment)] = paymentList
                    if (deductionList.isNotEmpty())
                        detailInfo[getString(R.string.withholding_payment)] = deductionList
                    if (loanList.isNotEmpty())
                        detailInfo[getString(R.string.loan_balance_deductions)] = loanList

                    payRollDetailInfoAdapter.setItems(detailInfo)

                }
            }
        }
    }

    private fun onSendInboxResponse(result: GeneralRes) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_send_payroll_to_inbox)
            )
        }
    }

    private fun onDownloadPdfResponse(result: PdfDownloadResponse) {
        if (result.isSuccess) {

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
            handlePageDestination(R.id.action_pay_roll_to_pdf_viewer, bundle)

        }
    }

    //Utils
    private fun setViewForShowGide() {
        viewDataBinding?.let {
            it.groupSearch.visibility = View.VISIBLE
            it.appBarInfoPayRoll.appBarView.setExpanded(true, true)
            it.nestedScrollView.scrollTo(0, 0)
        }
    }

    private fun setFocusUp() {
        viewDataBinding?.apply {
            appBarInfoPayRoll.appBarView.setExpanded(true, true)
            nestedScrollView.apply { post { fullScroll(ScrollView.FOCUS_UP) } }
        }
    }

    private fun initialToolbarPayRoll() {
        viewDataBinding?.appBarInfoPayRoll?.apply {
            var toolbarTitle = ""
            arguments?.let { arg ->
                ImageUtils.loadImage(imgIcon, Utility.getToolbarIconImage(arg))
                toolbarTitle = Utility.getToolbarTitle(arg)
                tvTitle.text = toolbarTitle
                toolbar.imageBack.setOnClickListener { backButtonPress() }
                toolbar.imgInfo.gone()
            }

            appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val maxScroll = appBarLayout.totalScrollRange
                val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
                handleAlphaOnTitle(percentage, containerAppbarTitle)
                handleToolbarTitleVisibility(
                    percentage,
                    toolbar.tvToolbarTitle,
                    toolbarTitle,
                    viewDataBinding?.appbarBackgroundImage?.imageBackground,
                    null
                )
            })
        }
    }

    private fun loadToolbarInfo(sumAmount: Long?) {
        viewDataBinding?.appBarInfoPayRoll?.apply {
            groupInfo.visible()
            tvSubEdictLabel.text = getString(R.string.pay_roll_pension)
            val inputYear = viewDataBinding?.inputYear?.getValue()
            val yearValue =
                if (inputYear?.isNotEmpty() == true && inputYear.toIntOrNull() != null) {
                    inputYear.toInt()
                } else {
                    0
                }
            tvSubEdictDateValue.text =
                getYearAndMonthWithDate(yearValue.toString(), selectedMonth)
            tvAmountPayTitle.text = getString(
                R.string.label_pure_payment
            )
            tvAmountPayValue.text = Utility.getRialWithSeparator(sumAmount)
            toolbar.imgAction.visible()
            toolbar.imgAction.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_download
                )
            )
        }
    }

    /* override fun getGuideDesc(): GuideFragment.GuideDataModel {
     val list: MutableList<String> = ArrayList()
     list.add("تست 1")
     list.add("تست 2")
     list.add("تست 3")
     list.add("تست 4")
     list.add("تست 5")
     list.add("تست 6")

     return GuideFragment.GuideDataModel(
         getString(R.string.label_wedding_present_gift_condition),
         getString(R.string.label_wedding_present_gift_info), list, "شرایط سرویس"
     )
 }

 override fun getGuideItemList(): MutableList<FancyShowCaseModel> {
     val items: MutableList<FancyShowCaseModel> = ArrayList()
     return items.also {
         viewDataBinding?.apply {
             it.add(
                 FancyShowCaseModel(
                     appBar.toolbar.imgInfo, R.string.title_img_info_tag_target_view,
                     R.string.detail_info_img_tag_target_view
                 )
             )
             it.add(
                 FancyShowCaseModel(
                     appBar.containerAppbarTitle,
                     R.string.title_pensioner_pay_roll_list_service_tag_target_view,
                     R.string.detail_pensioner_pay_roll_list_service_tag_target_view,
                     shape = Shape.RECT
                 )
             )
             it.add(
                 FancyShowCaseModel(
                     inputPensionNumber.input,
                     R.string.title_pensioner_pay_roll_list_inputPensionNumber_tag_target_view,
                     R.string.detail_pensioner_pay_roll_list_inputPensionNumber_tag_target_view,
                     shape = Shape.RECT
                 )
             )

             it.add(
                 FancyShowCaseModel(
                     inputPensionNumber.btnSelect,
                     R.string.title_pensioner_pay_roll_list_inputPensionNumber_btnSelect_tag_target_view,
                     R.string.detail_pensioner_pay_roll_list_inputPensionNumber_btnSelect_tag_target_view
                 )
             )
             it.add(
                 FancyShowCaseModel(
                     widgetDatePicker.inputDate,
                     R.string.title_pensioner_pay_roll_list_iwidgetDatePicker_tag_target_view,
                     R.string.detail_pensioner_pay_roll_list_widgetDatePicker_tag_target_view,
                     shape = Shape.RECT
                 )
             )
             it.add(
                 FancyShowCaseModel(
                     inputPaymentType.selectableInput,
                     R.string.title_pensioner_pay_roll_list_inputPaymentType_tag_target_view,
                     R.string.detail_pensioner_pay_roll_list_inputPaymentType_tag_target_view,
                     shape = Shape.RECT
                 )
             )
             it.add(
                 FancyShowCaseModel(
                     btnSearch,
                     R.string.title_pensioner_pay_roll_list_btn_search_tag_target_view,
                     R.string.detail_pensioner_pay_roll_list_btn_search_tag_target_view,
                     shape = Shape.RECT
                 )
             )

         }
     }
 }*/

}