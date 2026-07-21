package com.tamin.taminhamrah.ui.home.services.requestForPregnancyPay

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyStatusResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.PregnancyTypesResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.asDomainModel
import com.tamin.taminhamrah.databinding.FragmentRequestForPregnancyPayBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import org.jetbrains.annotations.NotNull
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class RequestForPregnancyPayFragment :
    BaseFragment<FragmentRequestForPregnancyPayBinding, RequestForPregnancyPayViewModel>(),
    AdapterInterface.OnItemClickListener<UploadedImageModel> {

    private var selectedBranch: String = ""
    private var pregnancyStatusList: ArrayList<MenuModel> = arrayListOf()
    private var pregnancyTypeList: ArrayList<MenuModel> = arrayListOf()
    private var selectedStartDateRestString: String? = null
    private var selectedEndDateRestString: String? = null
    private var selectedTypeRequest: String? = null
    private var selectedDateBabyBirth: String? = null
    private var fileList = mutableSetOf<RequestForPregnancyPayReq.ShorttermRequest.RequestFile>()
    private var barMonth: String = ""
    private var diffDayStartAndEndRest: Long = 0
    private var pregnancyPayObjRequest =
        RequestForPregnancyPayReq(shorttermRequest = RequestForPregnancyPayReq.ShorttermRequest())
    var tempImageType: String? = null
    var tempImageName: String? = null
    var tempImageOriginalUri: Uri? = null
    lateinit var listAdapter: ImagePreviewAdapter
    var fileListUploaded = ArrayList<UploadedImageModel>()
    override val mViewModel: RequestForPregnancyPayViewModel by viewModels()
    var tempImageUri: Uri? = null

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_request_for_pregnancy_pay
    }


    override fun initView() {
        listAdapter = ImagePreviewAdapter(this)

        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                actionIconRes = R.drawable.ic_calculate
            )
            layoutUploadImage.recycler.apply {
                this.adapter = listAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                }
            }
            widgetNationalCodeBaby.getInput().doAfterTextChanged {
                if (widgetNationalCodeBaby.getValueNationalCode(false).length == 10)
                    widgetNationalCodeBaby.getLayout().isErrorEnabled = false
            }
            widgetNationalCodeBaby2.getInput().doAfterTextChanged {
                if (widgetNationalCodeBaby2.getValueNationalCode(false).length == 10)
                    widgetNationalCodeBaby2.getLayout().isErrorEnabled = false
            }

            widgetNationalCodeBaby3.getInput().doAfterTextChanged {
                if (widgetNationalCodeBaby3.getValueNationalCode(false).length == 10)
                    widgetNationalCodeBaby3.getLayout().isErrorEnabled = false
            }
        }

    }

    override fun getData() {
        mViewModel.getGender()
    }

    override fun onClick() {
        viewDataBinding?.apply {

            layoutUploadImage.titleUpload.setOnClickListener {
                layoutUploadImage.apply {
                    if (layoutDescHolder.visibility == View.GONE) {
                        layoutDescHolder.visibility = View.VISIBLE
                    } else {
                        layoutDescHolder.visibility = View.GONE
                    }
                }
            }


            layoutUploadImage.btnAddDocument.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                chooseImage()
            }

            btnShowDetailInsured.setOnClickListener {
                apply {
                    if (groupDetails.visibility == View.GONE) {
                        groupDetails.visibility = View.VISIBLE
                        groupDetailsRequest.visibility = View.VISIBLE
                        btnShowDetailInsured.text = getString(R.string.hide_detail)
                    } else {
                        groupDetails.visibility = View.GONE
                        btnShowDetailInsured.text = getString(R.string.show_detail)
                    }
                }
            }

            selectLastBranch.getIt().setOnClickListener {
                try {
                    selectLastBranch.getLayout().isErrorEnabled = false
                    mViewModel.mldLatestInsuranceInfo.value?.data?.let { data ->
                        if (data.asDomainModel().size > 1) {
                            val dialog =
                                MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_select_branch_name))
                            dialog.setMenuListener(object : MenuInterface.OnFetchData {
                                override fun onFetch() {
                                    this@RequestForPregnancyPayFragment.lifecycleScope.launchWhenCreated {
                                        val pager = Pager(
                                            config = PagingConfig(
                                                Constants.QUERY_PAGE_SIZE_10,
                                                2
                                            ),
                                            pagingSourceFactory = { LocalPagingSource(data.asDomainModel()) })
                                        pager.flow.cachedIn(lifecycleScope)
                                            .collectLatest { pagingData ->
                                                dialog.updateData(pagingData)
                                            }
                                    }
                                }
                            },
                                object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        itemResult.id?.let {
                                            selectedBranch = it
                                            pregnancyPayObjRequest.shorttermRequest.branchCode = it
                                        }
                                        itemResult.title?.let { it1 ->
                                            pregnancyPayObjRequest.shorttermRequest.branchName = it1
                                            selectLastBranch.setValue(
                                                it1
                                            )
                                        }
                                    }
                                })
                            dialog.show(childFragmentManager, "hkjhkj")
                        }
                    }
                } catch (e: Exception) {
                    Timber.tag("ExceptionSelectBranch").e("Exception:" + e.message + " ")
                }
            }

            widgetDatePickerStartDateRest.inputDate.setOnClickListener {
                val datePickerStart = getDatePicker()
                datePickerStart?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                        widgetDatePickerStartDateRest.tilDate.isErrorEnabled = false
                        widgetDatePickerEndDateRest.tilDate.isErrorEnabled = false
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else {
                            "${MyPersianPickerDate.persianMonth}"
                        }
                        selectedStartDateRestString = MyPersianPickerDate.timestamp.toString()
                        widgetDatePickerStartDateRest.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                        viewDataBinding?.let {
                            diffDayStartAndEndRest = Utility.differenceBetweenTimestamps(
                                selectedStartDateRestString?.toLong(),
                                selectedEndDateRestString?.toLong()
                            )

                            selectedStartDateRestString?.let { start ->
                                selectedEndDateRestString?.let { end ->
                                    if (start.toLong() >= end.toLong()) {
                                        widgetDatePickerStartDateRest.tilDate.error =
                                            requireContext().getString(R.string.error_start_date_is_larger)
                                    }
                                }
                            }
                            if (diffDayStartAndEndRest > 365) {
                                widgetDatePickerEndDateRest.tilDate.error =
                                    requireContext().getString(R.string.error_diff_day_morethan_12month)
                            }
                            if (diffDayStartAndEndRest > 0) {
                                checkDiffDayWithRequestTypeSelected()
                                groupShowRestDays.visibility = View.VISIBLE
                                val strDate =
                                    UiUtils.createCustomTextColorForRestDay(
                                        diffDayStartAndEndRest
                                    )
                                it.valueDiffrentIllDay.apply {
                                    text = HtmlCompat.fromHtml(
                                        strDate,
                                        HtmlCompat.FROM_HTML_MODE_LEGACY
                                    )
                                }
                            } else {
                                groupShowRestDays.visibility = View.GONE

                            }
                        }

                    }

                    override fun onDismissed() {}
                })
                datePickerStart?.show()
            }

            widgetDatePickerEndDateRest.inputDate.setOnClickListener {
                val datePickerEnd = getDatePicker()
                datePickerEnd?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                        widgetDatePickerEndDateRest.tilDate.isErrorEnabled = false
                        widgetDatePickerStartDateRest.tilDate.isErrorEnabled = false
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else "${MyPersianPickerDate.persianMonth}"
                        selectedEndDateRestString = MyPersianPickerDate.timestamp.toString()
                        viewDataBinding?.let {
                            diffDayStartAndEndRest = Utility.differenceBetweenTimestamps(
                                selectedStartDateRestString?.toLong(),
                                selectedEndDateRestString?.toLong()
                            )

                            selectedStartDateRestString?.let { start ->
                                selectedEndDateRestString?.let { end ->
                                    if (start.toLong() >= end.toLong()) {
                                        widgetDatePickerEndDateRest.tilDate.error =
                                            requireContext().getString(R.string.error_start_date_is_larger)
                                    }
                                }
                            }
                            if (diffDayStartAndEndRest > 365) {
                                widgetDatePickerEndDateRest.tilDate.error =
                                    requireContext().getString(R.string.error_diff_day_morethan_12month)
                            }

                            if (diffDayStartAndEndRest > 0) {
                                checkDiffDayWithRequestTypeSelected()
                                groupShowRestDays.visibility = View.VISIBLE
                                val strDate =
                                    UiUtils.createCustomTextColorForRestDay(
                                        diffDayStartAndEndRest
                                    )
                                it.valueDiffrentIllDay.text =
                                    HtmlCompat.fromHtml(
                                        strDate,
                                        HtmlCompat.FROM_HTML_MODE_LEGACY
                                    )


                            } else {
                                groupShowRestDays.visibility = View.GONE
                            }
                        }
                        widgetDatePickerEndDateRest.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                    }

                    override fun onDismissed() {}
                })
                datePickerEnd?.show()
            }

            widgetDatePickerDateBabyBirth.inputDate.setOnClickListener {
                val datePickerStart = getDatePicker()
                datePickerStart?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                        widgetDatePickerDateBabyBirth.tilDate.isErrorEnabled = false
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else {
                            "${MyPersianPickerDate.persianMonth}"
                        }
                        selectedDateBabyBirth = MyPersianPickerDate.timestamp.toString()
                        widgetDatePickerDateBabyBirth.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                    }

                    override fun onDismissed() {}
                })
                datePickerStart?.show()
            }

            selectPregnancyStatus.getIt().apply {
                showSoftInputOnFocus = false
                setOnClickListener {
                    if (pregnancyStatusList.size > 1) {
                        try {
                            val dialog =
                                MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_pregnancy_status))
                            dialog.setMenuListener(object : MenuInterface.OnFetchData {
                                override fun onFetch() {
                                    this@RequestForPregnancyPayFragment.lifecycleScope.launchWhenCreated {
                                        val pager = Pager(
                                            config = PagingConfig(
                                                Constants.QUERY_PAGE_SIZE_10,
                                                2
                                            ),
                                            pagingSourceFactory = {
                                                LocalPagingSource(
                                                    pregnancyStatusList
                                                )
                                            })
                                        pager.flow.cachedIn(lifecycleScope)
                                            .collectLatest { pagingData ->
                                                dialog.updateData(pagingData)
                                            }
                                    }
                                }
                            },
                                object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectPregnancyStatus.getLayout().isErrorEnabled = false
                                        selectPregnancyStatus.getLayout().isErrorEnabled = false
                                        pregnancyPayObjRequest.barType = itemResult.id
                                        itemResult.title?.let { it1 ->
                                            selectPregnancyStatus.setValue(it1)
                                        }
                                    }
                                })
                            dialog.show(childFragmentManager, "hkjhkj")
                        } catch (e: Exception) {
                            Timber.tag("ExceptionSelectBranch").e("Exception:${e.message}")
                        }
                    }
                }
            }

            selectTypePregnancy.getIt().apply {
                showSoftInputOnFocus = false
                setOnClickListener {
                    val dialog =
                        MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_type_of_pregnancy))
                    try {
                        dialog.setMenuListener(object : MenuInterface.OnFetchData {
                            override fun onFetch() {
                                this@RequestForPregnancyPayFragment.lifecycleScope.launchWhenCreated {
                                    val pager = Pager(
                                        config = PagingConfig(
                                            Constants.QUERY_PAGE_SIZE_10,
                                            2
                                        ),
                                        pagingSourceFactory = {
                                            LocalPagingSource(
                                                pregnancyTypeList
                                            )
                                        })
                                    pager.flow.cachedIn(lifecycleScope)
                                        .collectLatest { pagingData ->
                                            dialog.updateData(pagingData)
                                        }
                                }
                            }
                        },
                            object : MenuInterface.OnResult {
                                override fun onResult(itemResult: MenuModel) {
                                    selectTypePregnancy.getLayout().apply {
                                        isErrorEnabled = false
                                        pregnancyPayObjRequest.barChild = itemResult.id

                                        if (barMonth == "3" && pregnancyPayObjRequest.barChild != "3") {
                                            error =
                                                requireContext().getString(R.string.error_select_type_pregnancy_is_mistake)
                                            nestedScrollView.scrollTo(
                                                0,
                                                top
                                            )
                                        }

                                        itemResult.title?.let {
                                            selectTypePregnancy.setValue(
                                                it
                                            )
                                        }
                                        when (itemResult.id) {
                                            "1" -> {
                                                groupBabyMoreThan1.visibility = View.GONE
                                                widgetNationalCodeBaby2.setTextWidget("")
                                                widgetNationalCodeBaby3.setTextWidget("")
                                            }
                                            "2" -> {
                                                widgetNationalCodeBaby2.visibility = View.VISIBLE
                                                widgetNationalCodeBaby3.setTextWidget("")
                                                widgetNationalCodeBaby3.visibility = View.GONE
                                            }
                                            "3" -> {
                                                groupBabyMoreThan1.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                }
                            })
                        dialog.show(childFragmentManager, "hkjhkj")
                    } catch (e: Exception) {
                        Timber.tag("ExceptionSelectBranch").e("Exception:${e.message}")
                    }
                }
            }

            selectTypeRequest.getIt().setOnClickListener {
                selectTypeRequest.getLayout().isErrorEnabled = false
                try {
                    val dialog =
                        MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_type_of_request))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            this@RequestForPregnancyPayFragment.lifecycleScope.launchWhenCreated {
                                val pager = Pager(
                                    config = PagingConfig(
                                        Constants.QUERY_PAGE_SIZE_10,
                                        2
                                    ),
                                    pagingSourceFactory = {
                                        LocalPagingSource(
                                            mViewModel.getRequestType()
                                        )
                                    })
                                pager.flow.cachedIn(lifecycleScope)
                                    .collectLatest { pagingData ->
                                        dialog.updateData(pagingData)
                                    }
                            }
                        }
                    }, object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            itemResult.id?.let {
                                barMonth = it
                                selectTypePregnancy.getLayout().apply {
                                    if (barMonth != "3") {
                                        isErrorEnabled = false
                                    }
                                    if (barMonth == "3" && pregnancyPayObjRequest.barChild != "3") {
                                        error =
                                            requireContext().getString(R.string.error_select_type_pregnancy_is_mistake)
                                        nestedScrollView.scrollTo(
                                            0,
                                            bottom
                                        )
                                    }
                                }
                                selectedTypeRequest = it
                                itemResult.title?.let { it1 ->
                                    selectTypeRequest.setValue(
                                        it1
                                    )
                                }
                                checkDiffDayWithRequestTypeSelected()
                            }
                        }
                    })
                    dialog.show(childFragmentManager, "hkjhkj")
                } catch (e: Exception) {
                    Timber.tag("ExceptionSelectBranch").e("Exception:${e.message}")
                }
            }

            btnSubmitCommitment.setOnClickListener {
                selectedStartDateRestString?.let { start ->
                    pregnancyPayObjRequest.barSDateTimeStamp = start.toLong()

                    selectedEndDateRestString?.let { end ->
                        pregnancyPayObjRequest.barEDateTimeStamp = end.toLong()
                    }
                }
                selectedDateBabyBirth?.let { BabyBirthDay ->
                    pregnancyPayObjRequest.barDemDatTimeStamp = BabyBirthDay.toLong()
                }
                if (selectedBranch.isBlank()) {
                    selectLastBranch.getLayout().error =
                        requireContext().getString(R.string.error_select_branch_name)
                    nestedScrollView.scrollTo(
                        0,
                        selectLastBranch.getLayout().bottom
                    )
                } else if (selectedStartDateRestString.isNullOrBlank()) {
                    widgetDatePickerStartDateRest.tilDate.error =
                        requireContext().getString(R.string.error_select_start_rest_date)
                    nestedScrollView.scrollTo(
                        0,
                        widgetDatePickerStartDateRest.inputDate.bottom
                    )
                } else if (selectedEndDateRestString.isNullOrBlank()) {
                    widgetDatePickerEndDateRest.tilDate.error =
                        requireContext().getString(R.string.error_select_end_rest_date)
                    nestedScrollView.scrollTo(
                        0,
                        widgetDatePickerEndDateRest.inputDate.bottom
                    )
                } else if (selectedDateBabyBirth.isNullOrBlank()) {
                    widgetDatePickerDateBabyBirth.tilDate.error =
                        requireContext().getString(R.string.error_select_date_birth_baby)
                    nestedScrollView.scrollTo(
                        0,
                        widgetDatePickerDateBabyBirth.inputDate.bottom
                    )
                } else if (selectPregnancyStatus.getIt().text.isNullOrBlank()) {
                    selectPregnancyStatus.getLayout().error =
                        requireContext().getString(R.string.error_select_status_pregnancy)
                    nestedScrollView.scrollTo(
                        0,
                        selectPregnancyStatus.top
                    )
                } else if (selectTypePregnancy.getIt().text.isNullOrBlank()) {
                    selectTypePregnancy.getLayout().error =
                        requireContext().getString(R.string.error_select_type_pregnancy)
                    nestedScrollView.scrollTo(
                        0,
                        selectTypePregnancy.top
                    )
                } else if (widgetNationalCodeBaby.getValueNationalCode().isBlank()) {
                    nestedScrollView.scrollTo(
                        0, widgetNationalCodeBaby.top
                    )
                } else if (pregnancyPayObjRequest.barChild.equals("2") && widgetNationalCodeBaby2.getValueNationalCode()
                        .isBlank()
                ) {
                    nestedScrollView.scrollTo(
                        0, widgetNationalCodeBaby2.top
                    )
                } else if (pregnancyPayObjRequest.barChild.equals("3") && widgetNationalCodeBaby2.getValueNationalCode()
                        .isBlank()
                ) {
                    nestedScrollView.scrollTo(
                        0, widgetNationalCodeBaby2.top
                    )
                } else if (pregnancyPayObjRequest.barChild.equals("3") && widgetNationalCodeBaby3.getValueNationalCode()
                        .isBlank()
                ) {
                    nestedScrollView.scrollTo(
                        0, widgetNationalCodeBaby3.top
                    )
                } else if (checkRepeatEnterNationalId()) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_repeat_national_id)
                    )
                } else if (inputDoctorName.getValue().isBlank()) {
                    inputDoctorName.getLayout().error =
                        requireContext().getString(R.string.error_enter_dr_name)

                    nestedScrollView.scrollTo(
                        0,
                        inputDoctorName.top
                    )

                } else if (inputDoctorCode.getInput().text?.toString().equals("")) {
                    inputDoctorCode.getLayout().error =
                        requireContext().getString(R.string.error_enter_dr_code)
                    nestedScrollView.scrollTo(
                        0,
                        inputDoctorCode.top
                    )

                } else if (barMonth.isBlank()) {
                    selectTypeRequest.getLayout().error =
                        requireContext().getString(R.string.error_enter_request_type)
                    nestedScrollView.scrollTo(
                        0,
                        selectTypeRequest.top
                    )
                } else if (barMonth == "3" && pregnancyPayObjRequest.barChild != "3") {
                    selectTypePregnancy.getLayout().error =
                        requireContext().getString(R.string.error_select_type_pregnancy_is_mistake)
                    nestedScrollView.scrollTo(
                        0,
                        selectPregnancyStatus.getLayout().top
                    )
                } else if (checkDiffDayWithRequestTypeSelected()) {
                    return@setOnClickListener
                } else if (Utility.differenceBetweenTimestamps(selectedStartDateRestString?.toLong(),
                        selectedDateBabyBirth?.toLong()) > 63) {
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,getString(R.string.error_diff_start_rest))
                }else if (fileList.isEmpty() || fileList.size < 3) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_at_least_3_images_must_be_uploaded)
                    )
                }  else {
                    pregnancyPayObjRequest.barDd = diffDayStartAndEndRest.toString()
                    pregnancyPayObjRequest.childNationalId =
                        widgetNationalCodeBaby.getValueNationalCode()
                    pregnancyPayObjRequest.childNationalId2 =
                        widgetNationalCodeBaby2.getValueNationalCode()
                    pregnancyPayObjRequest.childNationalId3 =
                        widgetNationalCodeBaby3.getValueNationalCode()

                    pregnancyPayObjRequest.barDrname = inputDoctorName.getValue()
                    pregnancyPayObjRequest.barDRid =
                        inputDoctorCode.getInput().text?.toString()
                    pregnancyPayObjRequest.shorttermRequest.requestFileList = fileList.toList()
                    pregnancyPayObjRequest.wrkPart = barMonth
                    Timber.tag("onClick: ").i(pregnancyPayObjRequest.toString())
                    mViewModel.sendRequestForPregnancyPay(pregnancyPayObjRequest)
                }
            }

            appBar.toolbar.imgAction.setOnClickListener {

                val bundle = Bundle()
                bundle.putString(
                    Constants.TOOLBAR_TITLE,
                    getString(R.string.label_caculate_pregnancy)
                )
                bundle.putString(
                    Constants.TOOLBAR_ICON_IMAGE,
                    Utility.getToolbarIconImage(arguments)
                )
                handlePageDestination(R.id.action_pregnancy_to_calculate, bundle)
            }
        }
    }

    private fun checkRepeatEnterNationalId(): Boolean {
        if (!pregnancyPayObjRequest.barChild.equals("1") && !pregnancyPayObjRequest.barChild.equals(
                ""
            )
        ) {
            viewDataBinding?.apply {
                if (widgetNationalCodeBaby.getValueNationalCode() == widgetNationalCodeBaby2.getValueNationalCode() && widgetNationalCodeBaby2.getValueNationalCode() == widgetNationalCodeBaby3.getValueNationalCode())
                    return true
                else if (widgetNationalCodeBaby2.getValueNationalCode() == widgetNationalCodeBaby3.getValueNationalCode())
                    return true
                else if (widgetNationalCodeBaby.getValueNationalCode() == widgetNationalCodeBaby2.getValueNationalCode())
                    return true
                else if (widgetNationalCodeBaby.getValueNationalCode() == widgetNationalCodeBaby3.getValueNationalCode())
                    return true
            }
        }
        return false
    }

    private fun checkDiffDayWithRequestTypeSelected(): Boolean {
        viewDataBinding?.apply {
            selectTypeRequest.getLayout().isErrorEnabled = false
            when (barMonth) {
                "1" -> {
                    if (diffDayStartAndEndRest > 186) {
                        selectTypeRequest.getLayout().error =
                            requireContext().getString(R.string.error_diff_day_morethan_6month)
                        return true
                    }
                }
                "2" -> {
                    if (diffDayStartAndEndRest > 276) {
                        selectTypeRequest.getLayout().error =
                            requireContext().getString(R.string.error_diff_day_morethan_9month)
                        return true
                    }
                }
                "3" -> {
                    if (diffDayStartAndEndRest > 365) {
                        selectTypeRequest.getLayout().error =
                            requireContext().getString(R.string.error_diff_day_morethan_12month)
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun setupObserver() {
        mViewModel.mldCheckGender.observe(this, ::checkGender)
        mViewModel.mldLatestInsuranceInfo.observe(this, ::showResultUserInfo)
        mViewModel.mldUploadImage.observe(this, ::showResultUploadImage)
        mViewModel.mldPregnancyStatus.observe(this, ::showResultPregnancyStatus)
        mViewModel.mldPregnancyType.observe(this, ::showResultPregnancyType)
        mViewModel.mldSendRequestForPregnancyPay.observe(
            this,
            ::showResultSendRequestForPregnancyPay
        )
    }

    private fun checkGender(result: String) {
        if (result != "" && (result.trim() == "m" || result.trim() == "01")) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                requireContext().getString(R.string.is_not_possible_apply_pregnancy_allowance_for_men),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
            dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
        } else {
            mViewModel.getLatestInsuranceInfo()
        }
    }

    private fun showResultSendRequestForPregnancyPay(result: RequestForPregnancyPayResponse) {
        if (result.isSuccess) {
            result.data?.shorttermRequest?.resultMessage?.let {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.SUCCESS, it
                )

                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().onBackPressed()
                    }

                    override fun onCancelClick() {
                    }
                })
                dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
            }
        }
    }

    private fun showResultPregnancyType(result: PregnancyTypesResponse) {
        pregnancyTypeList.clear()
        if (result.isSuccess) {
            result.data?.let {
                it.list?.forEach { pregnancyType ->
                    pregnancyTypeList.add(MenuModel(pregnancyType.name, pregnancyType.code))
                }
            }
        }
    }

    private fun showResultPregnancyStatus(result: PregnancyStatusResponse) {
        pregnancyStatusList.clear()
        if (result.isSuccess) {
            result.data?.let { pregnancyStatus ->
                pregnancyStatus.list?.forEach {
                    pregnancyStatusList.add(MenuModel(it.name, it.code))
                }
                if (pregnancyStatusList.size == 1) {
                    pregnancyStatusList[0].apply {
                        pregnancyPayObjRequest.barType = id
                        title?.let { it1 ->
                            viewDataBinding?.selectPregnancyStatus?.setValue(it1)
                        }
                    }
                }
            }

        }
    }

    private fun showResultUploadImage(result: UploadImageResponse) {
        if (result.isSuccess) {
            fileListUploaded.add(
                UploadedImageModel(
                    guid = result.guid,
                    imageType = tempImageType,
                    imageUri = tempImageUri,
                    imageName = tempImageName,
                    orgUri = tempImageOriginalUri
                )
            )
            fileList.add(
                RequestForPregnancyPayReq.ShorttermRequest.RequestFile(
                    documentFile = result.guid,
                    documentType = tempImageType,
                    "",
                    "",
                    ""
                )
            )
            tempImageType = null
            tempImageName = null
            tempImageUri = null
            //show image and title in ui
            listAdapter.setItems(fileListUploaded)
        }
    }

    private fun showResultUserInfo(result: LatestInsuranceInfoResponse) {


        if (result.isSuccess) {
            mViewModel.getPregnancyStatus()
            mViewModel.getPregnancyType()
            result.data?.let { data ->
                viewDataBinding?.let { binding ->
                    binding.apply {
                        item = data
                        groupDetailsRequest.visibility = View.VISIBLE
                        groupDetails.visibility = View.GONE

                        if (data.asDomainModel().size == 1) {
                            data.asDomainModel()[0].apply {
                                id?.let {
                                    selectedBranch = it
                                    pregnancyPayObjRequest.shorttermRequest.branchCode = it
                                }
                                title?.let { it1 ->
                                    pregnancyPayObjRequest.shorttermRequest.branchName = it1
                                    selectLastBranch.setValue(
                                        it1
                                    )
                                }
                            }
                        }
                    }
                    pregnancyPayObjRequest.shorttermRequest.risuid = data.risuid
                    pregnancyPayObjRequest.shorttermRequest.insuranceFirstName =
                        data.insuranceFirstName
                    pregnancyPayObjRequest.shorttermRequest.insuranceLastName =
                        data.insuranceLastName
                    pregnancyPayObjRequest.shorttermRequest.nationalCode =
                        data.nationalCode
                    pregnancyPayObjRequest.shorttermRequest.requestHelpType = "02"
                    pregnancyPayObjRequest.shorttermRequest.mobilNumber =
                        data.mobilNumber
                    pregnancyPayObjRequest.shorttermRequest.serviceDateTimeStamp =
                        data.serviceDateTimeStamp ?: 0
                }
            }
        }

    }
    /* override fun onConfirmClick() {
         if (isMan)
             requireActivity().onBackPressed()
     }*/


    override fun chooseImage(requestCode: Int) {
        this@RequestForPregnancyPayFragment.lifecycleScope.launchWhenCreated {
            val pager = Pager(
                config = PagingConfig(
                    Constants.QUERY_PAGE_SIZE_10,
                    2
                ),
                pagingSourceFactory = {
                    LocalPagingSource(
                        mViewModel.getImageTitleList()
                    )
                })
            pager.flow.cachedIn(lifecycleScope)
                .collectLatest { pagingData ->
                    openImageTypeMenu(
                        pagingData, onResultCallBack = object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {
                                itemResult.id?.let { id ->
                                    for (i in 0 until fileListUploaded.size)
                                        if (fileListUploaded[i].imageType == id) {
                                            fileListUploaded.removeAt(i)
                                            break
                                        }
                                    tempImageType = id
                                    itemResult.title?.let { title ->
                                        tempImageName = title

                                        val intent =
                                            Intent(activity, MultiCustomGalleryUI::class.java)
                                        intent.putExtra(Constants.TEMPID, tempImageType)
                                        resultImageLaunch.launch(intent)
                                    }
                                }
                            }
                        })
                }
        }

    }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Constants.REQUEST_LUNCHER) {

                    var imageUri: Uri? = null
                    result.data?.let {
                        imageUri = Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))
                    }
                    if (!isDuplicateImage(imageUri, fileListUploaded)) {
                        provideImageForUpload(tempImageType, imageUri)
                    } else {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.WARNING,
                            getString(R.string.error_select_repeat_pic)
                        )
                    }
                }


            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.image_upload_error)
                )
            }
        }


    override fun onItemClick(item: UploadedImageModel, transitionView: View?, tag: String?) {
        when (tag) {
            Constants.IMAGE_PREVIEW_TAG -> {
                val bundle = Bundle()
                bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                bundle.putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                handlePageDestination(
                    R.id.action_requestForPregnancyPayFragment_to_ImageViewerActivity,
                    bundle
                )
            }
            Constants.DELETE_IMAGE_TAG -> {
                fileListUploaded.remove(item)
                listAdapter.setItems(fileListUploaded)
                var tempDeletedFile = RequestForPregnancyPayReq.ShorttermRequest.RequestFile()
                fileList.forEach { itemFile ->
                    if (itemFile.documentFile == item.guid) {
                        tempDeletedFile = itemFile
                        return@forEach
                    }
                }
                fileList.remove(tempDeletedFile)
            }
        }

    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int,
    ) {
        mViewModel.uploadImage(body)
        tempImageOriginalUri = orgPath
        tempImageUri = imageUri

    }


    override fun onDestroyView() {
        super.onDestroyView()
        val folder =
            File("${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tempImage/")
        folder.deleteRecursively()
    }
}
