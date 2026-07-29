package com.tamin.taminhamrah.ui.home.services.viewEdictPensioner

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
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
import com.tamin.taminhamrah.data.remote.models.services.PensionerIdResponse
import com.tamin.taminhamrah.data.remote.models.services.edict.EdictPensionerResModel
import com.tamin.taminhamrah.data.remote.models.services.edict.EdictPensionerResponse
import com.tamin.taminhamrah.databinding.FragmentEdictPensionerBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.adapter.EdictDetailInfoAdapter
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.Utility.getNumberWithSeparatorForStringValue
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.isNumericString
import com.tamin.taminhamrah.utils.extentions.scaleY
import com.tamin.taminhamrah.utils.extentions.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import saman.zamani.persiandate.PersianDate
import kotlin.math.abs

@AndroidEntryPoint
class EdictPensionerFragment :
    BaseFragment<FragmentEdictPensionerBinding, EdictPensionerViewModel>() {

    ///////////Class Variables
    override val mViewModel: EdictPensionerViewModel by viewModels()

    private val edictDetailInfoAdapter by lazy { EdictDetailInfoAdapter() }

    private var edictYear: String = ""
    private var edictMonth: String = ""

    private var selectedMonth: String = ""
    var totalPensionBeforeIncrease = ""

    ///////////Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_edict_pensioner

    override fun initView() {
        initInfoText()
        initialToolbarEdict()
        initRecyclerViews()
    }

    private fun initInfoText() {
        viewDataBinding?.cardViewMessageHelp?.tvHelp?.text = getString(R.string.message_help)
    }

    override fun getData() {
        mViewModel.getPensionerIdList()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            selectMonth.getIt().setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                MenuDialogFragment().apply {
                    val bundle = Bundle()
                    bundle.putString(
                        MenuDialogFragment.ARG_MENU_TITLE,
                        this@EdictPensionerFragment.getString(R.string.label_month)
                    )
                    arguments = bundle
                    setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            this@EdictPensionerFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getPaymentMonthFlow(
                                    when (inputYear.getValue()) {
                                        "1399" -> true
                                        else -> false
                                    }
                                ).collectLatest { pagingData ->
                                    updateData(pagingData)
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectMonth.getLayout().isErrorEnabled = false
                            selectedMonth = "${itemResult.id ?: "01"}01"
                            itemResult.title?.let { it1 -> selectMonth.getIt().setText(it1) }
                        }
                    })
                }.show(childFragmentManager, EdictPensionerFragment().javaClass.simpleName)
            }

            inputYear.getInput().doOnTextChanged { text, _, _, _ ->
                val str = text.toString()
                val cYear = PersianDate().shYear
                if (str.isNumericString()) {
                    val number = str.toInt()
                    if (number > cYear)
                        inputYear.getInput().setText(cYear.toString())
                }
                inputYear.getLayout().isErrorEnabled = false
                if (str.length == 4) {
                    autoSelectMonth(str)
                }
            }

            selectPensionId.getIt().setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                MenuDialogFragment.newInstance(true, getString(R.string.pension_number)).apply {
                    setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            this@EdictPensionerFragment.lifecycleScope.launchWhenCreated {
                                Pager(
                                    config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                    pagingSourceFactory = {
                                        LocalPagingSource(mViewModel.pensionIdModelList)
                                    }
                                ).flow.cachedIn(lifecycleScope).collectLatest { paginData ->
                                    this@apply.updateData(paginData.map {
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

            btnSearch.setOnClickListener {
                when {
                    selectPensionId.getValue(false).isBlank() -> {
                        selectPensionId.getLayout().error =
                            getString(R.string.error_message_select_pensioner_id)
                    }

                    inputYear.getValue(false).isEmpty() -> {
                        inputYear.getLayout().error = getString(R.string.please_enter_valid_value)
                    }

                    inputYear.getValue(false).length < 4 -> {
                        inputYear.getLayout().error =
                            getString(R.string.error_fill_year_with_4_char)
                    }

                    selectedMonth.isBlank() -> {
                        selectMonth.getLayout().error =
                            getString(R.string.error_message_select_month)
                    }

                    else -> {
                        edictYear = inputYear.getValue(false)
                        edictMonth = selectMonth.getValue(false)
                        viewModel?.mldEdictInfo?.postValue(EdictPensionerResponse())
                        mViewModel.getEdictPensioner(
                            selectPensionId.getValue(false),
                            "${inputYear.getValue(false)}$selectedMonth"
                        )
                    }
                }
            }
            tvTitlePage.setOnClickListener {
                groupSearch.visible()
                btnSearchExpand.visibility = View.INVISIBLE
                tvDescSurvivor.gone()
                btnSendToInbox.gone()
            }

            btnSendToInbox.setOnClickListener {
                mViewModel.sendRequestInquirePensionCertificate(
                    "${viewDataBinding?.inputYear?.getValue()}$selectedMonth",
                    selectPensionId.getValue()
                )
            }
            appBarInfoEdict.toolbar.imgAction.setOnClickListener {
                mViewModel.downloadEdictPdf(
                    selectPensionId.getValue(),
                    "${viewDataBinding?.inputYear?.getValue()}$selectedMonth"
                )
            }
        }
    }
    private fun autoSelectMonth(yearStr: String) {
        val is1399 = yearStr == "1399"
        val singleMonth = mViewModel.getSingleMonthIfOnly(is1399)
        if (singleMonth != null) {
            selectedMonth = "${singleMonth.id ?: "01"}01"
            viewDataBinding?.selectMonth?.getIt()?.setText(singleMonth.title)
            viewDataBinding?.selectMonth?.getLayout()?.isErrorEnabled = false
        } else {
            selectedMonth = ""
            viewDataBinding?.selectMonth?.getIt()?.setText("")
        }
    }

    override fun setupObserver() {
        mViewModel.mldEdictInfo.observe(this, ::onShowEdictInfoResponse)
        mViewModel.mldPensionIds.observe(this, ::onPensionIdsResponse)
        mViewModel.mldPdf.observe(this, ::onPdfDownloadResponse)
        mViewModel.mldSendCertificateToInbox.observe(this, ::onSendCertificateResponse)
    }

    //region Listeners
    private fun onPensionIdsResponse(result: PensionerIdResponse) {
        if (result.isSuccess) {
            mViewModel.pensionIdModelList.apply {
                clear()
                addAll(result.data?.list ?: emptyList())
                if (isNotEmpty()) {
                    viewDataBinding?.selectPensionId?.setValue(get(0).pensionerId ?: "")
                } else {
                    DialogManagerMessageOfRequest.getInstanceOfDialog().apply {
                        arguments = createBundle(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            this@EdictPensionerFragment.getString(R.string.error_active_relation_user_is_insured)
                        )
                        setDialogClickListener(object :
                            DialogClickInterface.onClickListener {
                            override fun onConfirmClick() {
                                requireActivity().onBackPressed()
                            }

                            override fun onCancelClick() {
                            }

                        })
                    }.show(childFragmentManager, "EdictPensionerFragment")
                }
            }
        }
    }

    private fun onShowEdictInfoResponse(result: EdictPensionerResponse) {
        if (result.isSuccess) {
            result.data?.let { info ->
                info.edictYear = edictYear
                info.edictMonth = edictMonth
                if (info.edictInfo == null && info.survivorInfo == null) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.dont_have_edict_info)
                    )
                    return
                }
                setVisibilityUi()
                resetStateExpandButton()
                mViewModel.isPensioner = info.edictInfo?.id != null
                totalPensionBeforeIncrease = getNumberWithSeparatorForStringValue(
                    info.edictInfo?.totalPensionBeforeIncrease
                )
                val detailInfo: MutableMap<String, List<KeyValueModel>> = mutableMapOf()
                loadToolbarInfo(info.getToolbarInfoList())
                detailInfo.putAll(loadEdictInfo(info))
                detailInfo.putAll(info.getDescriptionsEdict())
                detailInfo.putAll(info.getEdictTypeInfo())
                edictDetailInfoAdapter.setItems(detailInfo)
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

            } ?: showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                getString(R.string.error_recive_data)
            )
        }
    }

    private fun onSendCertificateResponse(result: GeneralRes) {
        if (result.isSuccess) {
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_send_to_inbox_edict_certificate)
            )
        }
    }

    private fun onPdfDownloadResponse(result: PdfDownloadResponse) {
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
            handlePageDestination(
                R.id.action_edictPensionerFragment_to_pdfViewerActivity,
                Bundle().apply {
                    putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
                    putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
                })
        }
    }
    //endregion

    //////////Utils
    private fun initRecyclerViews() {
        viewDataBinding?.rcvInfoEdict?.apply {
            adapter = edictDetailInfoAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.createDivider(requireContext()))
            }
        }
    }

    private fun resetStateExpandButton() {
        viewDataBinding?.btnSearchExpand?.scaleY(true, animation = false)
    }

    private fun setVisibilityUi() {
        viewDataBinding?.apply {
            btnSearchExpand.visible()
            btnSendToInbox.visible()
            groupSearch.gone()
        }
    }

    private fun loadEdictInfo(info: EdictPensionerResModel?): MutableMap<String, ArrayList<KeyValueModel>> {
        val listEdictInfo = info?.getEdictInfoList() ?: ArrayList()
        if (mViewModel.isPensioner) {
            listEdictInfo.forEach { item ->
                val listDate = item._value.split("-")
                if (listDate.size >= 3) {
                    item._value = getString(
                        R.string.history_format_edict_service,
                        UiUtils.createTextColorBlueAndBold(listDate[0]),
                        UiUtils.createTextColorGreenAndBold(listDate[1]),
                        UiUtils.createTextColorOrangeAndBold(listDate[2])
                    )
                }
            }
        } else {
            viewDataBinding?.tvDescSurvivor?.apply {
                isVisible = true
                val name = UiUtils.createTextColorBlueAndBold(info?.edictInfo?.fatherName ?: "_")
                val pensionId =
                    UiUtils.createTextColorBlueAndBold(info?.edictInfo?.pensionerId ?: "_")
                val pensionBeforeIncrease = UiUtils.createTextColorOrangeAndBold(
                    getNumberWithSeparatorForStringValue(info?.edictInfo?.pensionBeforeIncrease)
                        ?: "_"
                )
                val pensionAfterIncrease = UiUtils.createTextColorGreenAndBold(
                    getNumberWithSeparatorForStringValue(info?.edictInfo?.pensionAfterIncrease)
                        ?: "_"
                )
                text = HtmlCompat.fromHtml(
                    getString(
                        R.string.survivor_edict_desc,
                        name,
                        pensionId,
                        pensionBeforeIncrease,
                        pensionAfterIncrease,
                        "****"
                    ), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
        return mutableMapOf(getString(R.string.edict_info) to listEdictInfo)
    }

    private fun initialToolbarEdict() {
        viewDataBinding?.appBarInfoEdict?.apply {
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

    private fun loadToolbarInfo(list: List<KeyValueModel>? = null) {
        viewDataBinding?.appBarInfoEdict?.apply {
            if (list.isNullOrEmpty() || list.size < 4) return
            groupInfo.visible()
            tvTitle.text = getString(list[0]._valueStringResId)
            tvSubTitle.text = list[1]._value
            tvSubEdictDateValue.text = list[2]._value
            tvAmountPayTitle.text =
                if (mViewModel.isPensioner) getString(R.string.amount_payable_monthly) else getString(
                    R.string.label_gross_amount_pay
                )
            tvAmountPayValue.text = getString(R.string.rial_with_value, list[3]._value)
            toolbar.imgAction.visible()
            toolbar.imgAction.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_download
                )
            )
        }
    }
}