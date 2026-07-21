package com.tamin.taminhamrah.ui.home.services.requestPaymentForillDays

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.HtmlCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.Constants.IMAGE_URI
import com.tamin.taminhamrah.Constants.REQUEST_LUNCHER
import com.tamin.taminhamrah.Constants.TEMPID
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForIllDayResponse
import com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay.RequestPaymentForillDayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.RequestForPregnancyPayReq.ShorttermRequest.RequestFile
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.asDomainModel
import com.tamin.taminhamrah.databinding.FragmentRequestPaymentForIllDaysBinding
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
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
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
class RequestPaymentForillDaysFragment :
    BaseFragment<FragmentRequestPaymentForIllDaysBinding, RequestPaymentForillDaysViewModel>(),
    AdapterInterface.OnItemClickListener<UploadedImageModel> {

    override val mViewModel: RequestPaymentForillDaysViewModel by viewModels()
    private var selectedBranch: String = ""
    private var selectedCityCode: String = ""
    private var selectedProvinceCode: String = ""
    private var selectedStartDateRestString: String? = null
    private var selectedEndDateRestString: String? = null
    private var diffDayStartAndEndRest: Long = 0
    private var fileList = mutableSetOf<RequestFile>()
    private var illDayRequest =
        RequestPaymentForillDayReq(shorttermRequest = RequestForPregnancyPayReq.ShorttermRequest())
    var tempImageType: String? = null
    var tempImageName: String? = null
    var tempImageOriginalUri: Uri? = null
    lateinit var listAdapter: ImagePreviewAdapter
    var fileListUploaded = ArrayList<UploadedImageModel>()
    var tempImageUri: Uri? = null
    var callRequestOfCovid = false
    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_request_payment_for_ill_days
    }

    override fun setupObserver() {
        mViewModel.mldLatestInsuranceInfo.observe(this, ::showBranchResult)
        mViewModel.mldUploadImage.observe(this, ::showResultUploadImage)
        mViewModel.mldCovidResult.observe(this, ::showResultCovid)
        mViewModel.mldSendRequestForIllDay.observe(this, ::showResultSendRequestForIllDay)
    }

    private fun showResultSendRequestForIllDay(result: RequestPaymentForIllDayResponse) {
        try {
            if (result.isSuccess) {
                result.data?.shorttermRequest?.resultMessage?.let {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments =
                        createBundle(MessageOfRequestDialogFragment.MessageType.SUCCESS, it)

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
        } catch (e: java.lang.Exception) {
            Timber.tag("showResultForIllDay: ").e(e.message.toString())
        }
    }

    private fun showResultCovid(result: ListDataModel<String>) {
        if (result.isSuccess) {
            val startDate =
                result.data?.list?.get(0)?.let {
                    illDayRequest.bimSdateTimeStamp = null
                    selectedStartDateRestString = null
                    ConvertDate.convertTimestampToPersianDate(it)
                }
            val endDate = result.data?.list?.get(1)?.let {
                illDayRequest.bimEdateTimeStamp = null
                selectedEndDateRestString = null
                ConvertDate.convertTimestampToPersianDate(it)
            }
            illDayRequest.bimKind = "1"
            viewDataBinding?.apply {
                groupSelectDate.visibility = View.GONE
                groupShowRestDays.visibility = View.GONE
                groupReciveDate.visibility = View.VISIBLE
                lableDateReciveFromMinistryHealth.text =
                    requireContext().getString(
                        R.string.message_confirm_covid_date_request_ill_days,
                        startDate,
                        endDate
                    )
            }
        } else {
            viewDataBinding?.apply {
                groupSelectDate.visibility = View.VISIBLE
                groupReciveDate.visibility = View.GONE
                selectAfflictedWithDiseaseCovid.isChecked = false
                callRequestOfCovid = true
                selectAfflictedWithDiseaseCovid.isEnabled = false
            }
        }
    }

    private fun showBranchResult(result: LatestInsuranceInfoResponse) {
        if (result.isSuccess) {
            result.data?.let { data ->
                viewDataBinding?.let { binding ->
                    binding.apply {
                        item = data
                        groupDetailsRequest.visibility = View.VISIBLE
                        groupDetails.visibility = View.GONE
                        data.asDomainModel().apply {
                            if (size == 1) {
                                get(0).let {
                                    it.id?.let { id ->
                                        selectedBranch = id
                                        illDayRequest.shorttermRequest?.branchCode = id
                                    }
                                    it.title?.let { title ->
                                        binding.selectLastBranch.setValue(title)
                                        illDayRequest.shorttermRequest?.branchName = title
                                    }
                                }
                            }
                        }
                    }
                    illDayRequest.shorttermRequest?.apply {
                        risuid = data.risuid
                        illDayRequest.shorttermRequest?.insuranceFirstName = data.insuranceFirstName
                        insuranceLastName = data.insuranceLastName
                        nationalCode = data.nationalCode
                        requestHelpType = "01"
                        mobilNumber = data.mobilNumber
                        serviceDateTimeStamp = data.serviceDateTimeStamp ?: 0
                    }

                }
            }
        }
    }


    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            actionIconRes = R.drawable.ic_calculate
        )
        addHints()
        listAdapter = ImagePreviewAdapter(this)
        viewDataBinding?.layoutUploadImage?.recycler?.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
            }
        }
    }


    private fun addHints() {
        //     val text= HtmlCompat.fromHtml(getString(R.string.label_image_guid)+"<font color='#6c63ff'> <u>(توضیحات بیشتر)</u></font>",HtmlCompat.FROM_HTML_MODE_LEGACY)
        val hintList = arrayOf(
            getString(R.string.label_desc_upload_title),
            getString(R.string.label_desc_upload_image_document1),
            getString(R.string.label_desc_upload_image_document2)
        )

        for (item in hintList) {
            val descViewBinding: ViewDataBinding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_desc,
                viewDataBinding?.parent,
                false
            )
            descViewBinding.apply {
                setVariable(BR.item, item)
                lifecycleOwner = this@RequestPaymentForillDaysFragment
                executePendingBindings()
            }
            viewDataBinding?.apply { layoutUploadImage.layoutDescHolder.addView(descViewBinding.root) }

        }
    }

    override fun getData() {
        mViewModel.getLatestInsuranceInfo()
    }

    override fun onClick() {
        viewDataBinding?.apply {

            btnShowDetailInsured.setOnClickListener {
                if (groupDetails.visibility == View.GONE) {
                    groupDetails.visibility = View.VISIBLE
                    groupDetailsRequest.visibility = View.VISIBLE
                    btnShowDetailInsured.text = getString(R.string.hide_detail)

                } else {
                    groupDetails.visibility = View.GONE
                    btnShowDetailInsured.text = getString(R.string.show_detail)
                }
            }

            selectLastBranch.getIt().setOnClickListener {
                selectLastBranch.getLayout().isErrorEnabled = false
                mViewModel.mldLatestInsuranceInfo.value?.data?.asDomainModel()?.apply {
                    if (size > 1) {
                        val dialog = MenuDialogFragment.newInstance(menuTitle = "")
                        dialog.setMenuListener(object : MenuInterface.OnFetchData {
                            override fun onFetch() {
                                this@RequestPaymentForillDaysFragment.lifecycleScope.launchWhenCreated {
                                    val pager = Pager(
                                        config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                        pagingSourceFactory = { LocalPagingSource(this@apply) })
                                    pager.flow.cachedIn(lifecycleScope)
                                        .collectLatest { pagingData ->
                                            val result = pagingData.map {
                                                MenuModel(
                                                    id = it.id,
                                                    title = it.title,
                                                    description = it.description,
                                                    description2 = it.description2,
                                                    showDesc = it.showDesc,
                                                    isEdited = it.isEdited
                                                )
                                            }
                                            dialog.updateData(result)
                                        }
                                }
                            }
                        }, object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {
                                itemResult.id?.let {
                                    selectedBranch = it
                                    illDayRequest.shorttermRequest?.branchCode = it
                                }
                                itemResult.title?.let { it1 ->
                                    illDayRequest.shorttermRequest?.branchName = it1
                                    selectLastBranch.setValue(
                                        it1
                                    )
                                }
                            }
                        })
                        dialog.show(childFragmentManager, "hkjhkj")

                    }

                }

            }
            selectCity.getIt().setOnClickListener {
                showCityListDialog(
                    getString(R.string.label_city_name),
                    object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            itemResult.id?.let {
                                selectedCityCode = it
                                selectedProvinceCode = itemResult.extraData
                                illDayRequest.cityCode = it
                                illDayRequest.provinceCode = itemResult.extraData
                            }
                            itemResult.title?.let { it1 ->
//                                illDayRequest.shorttermRequest?.branchName = it1
                                selectCity.setValue(
                                    it1
                                )
                            }
                        }

                    }
                )
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
                                groupShowRestDays.visibility = View.VISIBLE
                                val strDate = UiUtils.createCustomTextColorForRestDay(
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
                                groupShowRestDays.visibility = View.VISIBLE
                                val strDate = UiUtils.createCustomTextColorForRestDay(
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
                if (fileList.size <= 4) {
                    chooseImage()
                } else {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_max_upload_5_item)
                    )
                }
            }

            inputDoctorCode.getInput().doAfterTextChanged {
                inputDoctorCode.getLayout().isErrorEnabled = false
            }

            //covid select
            selectAfflictedWithDiseaseCovid.setOnClickListener {
                if (selectAfflictedWithDiseaseCovid.isChecked && !callRequestOfCovid) {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        requireContext().getString(R.string.label_desc_selected_covid_option),
                        true
                    )

                    dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                        override fun onConfirmClick() {
                            //test covid request
                            // val listOfTime = arrayListOf("1611532800000", "1612742400000")
                            //  mViewModel.mldCovidResult.postValue(Resource.success(listOfTime))
                            mViewModel.getCovidResult()

                        }

                        override fun onCancelClick() {
                            groupSelectDate.visibility = View.VISIBLE
                            groupReciveDate.visibility = View.GONE
                            selectAfflictedWithDiseaseCovid.isChecked = false
                        }
                    })
                    dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
                } else {
                    illDayRequest.bimKind = "2"
                    groupSelectDate.visibility = View.VISIBLE
                    groupReciveDate.visibility = View.GONE
                    callRequestOfCovid = true
                    selectAfflictedWithDiseaseCovid.isEnabled = false
                }
            }

            selectHasMedicalRecordRelatedBreak.setOnClickListener {
                if (selectHasMedicalRecordRelatedBreak.isChecked) {
                    illDayRequest.bimWkstatus = "1"
                } else {
                    illDayRequest.bimWkstatus = "2"
                }
            }
            //send request
            btnSubmitCommitment.setOnClickListener {
                viewDataBinding?.apply {
                    if (selectedBranch.isBlank()) {
                        selectLastBranch.getLayout().error =
                            requireContext().getString(R.string.error_select_branch_name)
                        nestedScrollView.scrollTo(
                            0,
                            selectLastBranch.getLayout().bottom
                        )
                    } else if (selectedProvinceCode.isBlank() || selectedCityCode.isBlank()) {
                        selectCity.getLayout().error =
                            requireContext().getString(R.string.error_select_city_name)
                        nestedScrollView.scrollTo(
                            0,
                            selectCity.getLayout().bottom
                        )
                    } else if (illDayRequest.bimKind.isBlank() && selectedStartDateRestString.isNullOrBlank()) {
                        widgetDatePickerStartDateRest.tilDate.error =
                            requireContext().getString(R.string.error_select_start_rest_date)
                        nestedScrollView.scrollTo(
                            0,
                            widgetDatePickerStartDateRest.inputDate.bottom
                        )
                    } else if (illDayRequest.bimKind.isBlank() && selectedEndDateRestString.isNullOrBlank()) {
                        widgetDatePickerEndDateRest.tilDate.error =
                            requireContext().getString(R.string.error_select_end_rest_date)
                        nestedScrollView.scrollTo(
                            0,
                            widgetDatePickerEndDateRest.inputDate.bottom
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
                    } else if (fileList.isEmpty() || fileList.size < 1) {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_at_least_1_images_must_be_uploaded)
                        )
                    } else {
                        illDayRequest.bimDrname = inputDoctorName.getValue()
                        illDayRequest.bimDrid = inputDoctorCode.getInput().text?.toString()
                        illDayRequest.cityCode = selectedCityCode
                        illDayRequest.provinceCode = selectedProvinceCode
                        illDayRequest.shorttermRequest?.requestFileList = fileList.toList()
                        illDayRequest.shorttermRequest?.branchName
                        selectedStartDateRestString?.let {
                            illDayRequest.bimSdateTimeStamp = it.toLong()
                        }
                        selectedEndDateRestString?.let {
                            illDayRequest.bimEdateTimeStamp = it.toLong()
                        }
                        mViewModel.sendRequestForIllDay(illDayRequest)
                    }
                }
            }

            appBar.toolbar.imgAction.setOnClickListener {

                val bundle = Bundle()
                with(bundle) {
                    putString(
                        Constants.TOOLBAR_TITLE,
                        getString(R.string.label_caculate_illness_days)
                    )
                    putString(
                        Constants.TOOLBAR_ICON_IMAGE,
                        Utility.getToolbarIconImage(arguments)
                    )
                    handlePageDestination(R.id.action_illness_to_calculate, bundle)
                }
            }
        }
    }

    private fun showCityListDialog(
        title: String,
        callBackResult: MenuInterface.OnResult,
    ) {
        MenuDialogFragment.newInstance(true, title).apply {
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@RequestPaymentForillDaysFragment.lifecycleScope.launchWhenCreated {
                        mViewModel.getCityList().collectLatest { pagingData ->
                            updateData(pagingData.map {
                                MenuModel(
                                    id = it.cityCode,
                                    title = it.cityName,
                                    extraData = it.provincecode
                                )
                            })
                        }
                    }
                }
            }, callBackResult, object : MenuInterface.OnSearch {
                override fun onSearch(str: String) {
                    this@RequestPaymentForillDaysFragment.lifecycleScope.launchWhenCreated {
                        mViewModel.getCityList(str).collectLatest { pagingData ->
                            updateData(pagingData.map {
                                MenuModel(
                                    id = it.cityCode,
                                    title = it.cityName,
                                    extraData = it.provincecode
                                )
                            })
                        }
                    }
                }
            })
        }.show(childFragmentManager, "RequestPaymentForillDaysFragment")
    }

    override fun chooseImage(requestCode: Int) {
        tempImageName = getString(R.string.ill_day_title_image)
        tempImageType = Constants.REQUEST_ILL_DAY_PIC_1_IMAGE_TYPE
        val intent = Intent(activity, MultiCustomGalleryUI::class.java)
        intent.putExtra(TEMPID, tempImageType)
        intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)

        resultImageLaunch.launch(intent)
    }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == REQUEST_LUNCHER) {

                    var imageUri: Uri? = null
                    result.data?.let {
                        imageUri = Uri.parse(result.data?.extras?.getString(IMAGE_URI))
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

    private fun showResultUploadImage(result: UploadImageResponse) {
        if (result.isSuccess) {
            result.let { guid ->
                fileListUploaded.add(
                    UploadedImageModel(
                        guid = guid.guid,
                        imageType = tempImageType,
                        imageUri = tempImageUri,
                        imageName = tempImageName,
                        orgUri = tempImageOriginalUri
                    )
                )
                fileList.add(
                    RequestFile(
                        documentFile = guid.guid,
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
    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int
    ) {
        mViewModel.uploadImage(body)
        tempImageOriginalUri = orgPath
        tempImageUri = imageUri
    }


    override fun onItemClick(item: UploadedImageModel, transitionView: View?, tag: String?) {
        when (tag) {
            Constants.IMAGE_PREVIEW_TAG -> {
                val bundle = Bundle()
                bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                bundle.putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                handlePageDestination(
                    R.id.action_requestPaymentForillDaysFragment_to_ImageViewerActivity,
                    bundle
                )
            }

            Constants.DELETE_IMAGE_TAG -> {
                fileListUploaded.remove(item)
                listAdapter.setItems(fileListUploaded)
                var tempDeletedFile = RequestFile()
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


    override fun onDestroyView() {
        super.onDestroyView()
        val folder =
            File("${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tempImage/")
        folder.deleteRecursively()
    }


}