package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.employer.Letetsubjectcode
import com.tamin.taminhamrah.data.remote.models.employer.LetterInfo
import com.tamin.taminhamrah.data.remote.models.employer.RegisterLetterResponse
import com.tamin.taminhamrah.databinding.CorrespondenceStepDescriptionBinding
import com.tamin.taminhamrah.databinding.CorrespondenceStepDetailBinding
import com.tamin.taminhamrah.databinding.CorrespondenceStepUploadImageBinding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle01Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle02Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle03Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle04Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle05Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle06Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle07Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle08Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle09Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle10Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle11Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle12Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle13Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle14Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle15Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle16Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle17Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle19Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle20Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle23Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle24Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle25Binding
import com.tamin.taminhamrah.databinding.DistantCorrespondenceDetailTitle26Binding
import com.tamin.taminhamrah.databinding.FragmentRegisterDistantCorrespondenceBinding
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence.EnumDistantCorrespondenceStep.STEP_CORRESPONDENCE_DETAILS
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence.EnumDistantCorrespondenceStep.STEP_DESCRIPTION
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence.EnumDistantCorrespondenceStep.STEP_UPLOAD_IMAGE
import com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence.EnumDistantCorrespondenceStep.values
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.getFileName
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import com.tamin.taminhamrah.widget.DatePickerWidget
import com.tamin.taminhamrah.widget.edittext.SelectableItemView
import com.tamin.taminhamrah.widget.edittext.number.EditTextNumber
import com.tamin.taminhamrah.widget.edittext.string.EditTextString
import com.tamin.taminhamrah.widget.edittext.string.MultiLineEditTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import saman.zamani.persiandate.PersianDate
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class DistantCorrespondenceFragment :
    BaseFragment<FragmentRegisterDistantCorrespondenceBinding, DistantCorrespondenceInfoViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    companion object {
        const val ARG_BRANCH_CODE = "ARG_BRANCH_CODE"
        const val ARG_WORKSHOP_CODE = "ARG_WORKSHOP_CODE"
        const val ARG_LETTER_INFO = "ARG_LETTER_INFO"
        const val ARG_VIEW_ONLY = "ARG_VIEW_ONLY"
    }

    override val mViewModel: DistantCorrespondenceInfoViewModel by viewModels()
    private val requestList = ArrayList<KeyValueModel>()

    /*  private val letterAdapter: CorrespondenceAdapter by lazy {
          CorrespondenceAdapter(object :
              AdapterInterface.OnDeleteClickListener<KeyValueModel> {
              override fun onDelete(item: KeyValueModel) {
                  requestList.remove(item)
                  letterAdapter.deleteItem(item)
              }
          }, viewOnly)
      }*/
    private val letterAdapter = KeyValueAdapter()

    private val imageAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        val bundle = Bundle()
                        bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                        bundle.putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                        handlePageDestination(
                            R.id.action_correspondence_to_image_preview,
                            bundle
                        )
                    }

                    Constants.DELETE_IMAGE_TAG -> {
                        imageAdapter.removeItem(item)
                        mViewModel.dataModel.leterImage = null
                    }
                }
            }

        }, viewOnly)
    }

    private val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Timber.tag("resultImageLaunch").i("resultCode= ${result.resultCode}")
            try {
                provideImageForUpload(
                    Constants.REQUEST_DEFAULT_IMAGE_TYPE,
                    if (result.data != null) Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI)) else null,
                    result.resultCode
                )
            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.image_upload_error)
                )
            }
        }

    private val branchCode by lazy { arguments?.getString(ARG_BRANCH_CODE) }
    private val workshopCode by lazy { arguments?.getString(ARG_WORKSHOP_CODE) }
    private val letterItem by lazy { arguments?.getParcelable(ARG_LETTER_INFO) as? LetterInfo }
    private val viewOnly by lazy { arguments?.getBoolean(ARG_VIEW_ONLY) }

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_register_distant_correspondence

    override fun setupObserver() {
        mViewModel.mldUploadImage.observe(this, ::onUploadImage)
        mViewModel.mldDownloadImage.observe(this, ::onDownloadImage)
        mViewModel.mldRegisterRequest.observe(this, ::onRegister)
    }

    private fun onRegister(result: RegisterLetterResponse?) {
        if (result?.isSuccess==true)
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.CONFIRM,
                getString(R.string.message_success_send_info),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
    }

    override fun initView() {
        viewDataBinding?.apply {
            appBar.toolbar.imageBack.setOnClickListener { backButtonPress() }
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground)
        }
        initStepper()
    }

    override fun getData() {}

    override fun onClick() {}

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int
    ) {
        Timber.tag("uploadImageTag").i("requestCode=$requestCode")
        val fileName = getFileName(imageUri)
        mViewModel.uploadImage(
            body,
            requestCode = requestCode,
            imageUri = imageUri,
            fileName = fileName
        )
    }

    private fun onUploadImage(result: UploadedImageModel) {
        mViewModel.dataModel.leterImage = result.guid ?: ""
        imageAdapter.setItems(listOf(result))
    }

    private fun onDownloadImage(result: UploadedImageModel) {
        mViewModel.dataModel.leterImage = result.guid ?: ""
        imageAdapter.setItems(listOf(result))
    }

    private fun initStepper(initialStep: Int = 1) {
        val stepLayout = ArrayList<ViewBinding>()
        viewDataBinding?.apply {
            values().forEach {
                stepLayout.add(inflateView(it))
            }
            stepper.initial(stepLayout, initialStep)
            stepper.onNextStepClickListener = this@DistantCorrespondenceFragment
            stepper.onPreviousStepClickListener = this@DistantCorrespondenceFragment
        }
    }

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.apply {
            when (stepIndex) {
                STEP_CORRESPONDENCE_DETAILS.step -> {
                    nextStep()
                }
                STEP_DESCRIPTION.step -> {
                    val binding = getStepLayoutBindingByStep(STEP_DESCRIPTION.step) as? CorrespondenceStepDescriptionBinding
                    if (binding?.inputDescription?.getValue()?.isNotBlank() == true) {
                        mViewModel.dataModel.descriptions = binding.inputDescription.getValue(false)
                        nextStep()
                    }

                }
                STEP_UPLOAD_IMAGE.step -> {
                    mViewModel.sendDistantCorrespondenceRequest(workshopCode, branchCode)
                }
            }
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.previousStep()
    }

    override fun chooseImage(requestCode: Int) {
        resultImageLaunch.launch(Intent(requireActivity(), MultiCustomGalleryUI::class.java).apply {
            putExtra(Constants.TEMPID, Constants.REQUEST_DEFAULT_IMAGE_TYPE)
            putExtra(Constants.REQUEST_CODE_TAG, requestCode)
        })
    }

    fun createToolbarBundle(item: MenuModel)= Bundle().apply {
        putString(Constants.TOOLBAR_TITLE, item.title)
        putString(Constants.TOOLBAR_SUBTITLE, item.description)
        putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
    }

    private fun inflateView(it: EnumDistantCorrespondenceStep): ViewBinding {
        return when (it) {
            STEP_CORRESPONDENCE_DETAILS -> {
                CorrespondenceStepDetailBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    var viewId: String?
                    correspondenceStepCondition.apply {
                        index = it.step
                        title = it.title
                    }
                    selectLetterTitle.getIt().setOnClickListener {

                        val dialog = MenuDialogFragment.newInstance(
                            true,
                            getString(R.string.label_request_title)
                        )
                        dialog.setMenuListener(object : MenuInterface.OnFetchData {
                            override fun onFetch() {
                                this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                    mViewModel.getLetterTitleList()
                                        .collectLatest { pagingData ->
                                            val result = pagingData.map {
                                                MenuModel(
                                                    id = it.code?.toString(),
                                                    title = it.subjectDesc
                                                )
                                            }
                                            dialog.updateData(result)
                                        }
                                }
                            }
                        }, object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {
                                selectLetterTitle.setValue(itemResult.title ?: "")
                                mViewModel.dataModel.letetsubjectcode =
                                    Letetsubjectcode().apply { code = itemResult.id?.toLong() }

                                val emptyListsType = listOf("18", "21", "22", "27", "28")

                                if (!emptyListsType.contains(itemResult.id)) {
                                    btnAddItem.isVisible = true
                                    correspondenceStepCondition.nextStepEnable = true
                                } else {
                                    btnAddItem.isVisible = false
                                    correspondenceStepCondition.nextStepEnable = true
                                }

                                viewId = itemResult.id

                                setDetailsView(viewId, this@apply)

                                // letterAdapter.clearData()
                                mViewModel.dataModel.letterRequestDetailCollection.clear()
                            }
                        }/*, object : MenuInterface.OnSearchFromServer {
                            override fun onSearch(str: String) {

                            }
                        }*/
                        )
                        dialog.show(childFragmentManager, "jgkutgiutuytyu")
                    }
                    recyclerList.apply {
                        adapter = letterAdapter
                        if (itemDecorationCount == 0)
                            addItemDecoration(UiUtils.createDivider(context))
                    }
                    btnAddItem.setOnClickListener {
                        letterAdapter.setItems(emptyList())
                        requestList.clear()
                        if (checkValidInputsDetail(containerDetail)) {
                            mViewModel.dataModel.letterRequestDetailCollection.add(mViewModel.tempDetailCollection.clone())
                            letterAdapter.setItems(requestList)
                            btnClearList.isVisible = true
                        }
                        if (mViewModel.dataModel.letterRequestDetailCollection.isNotEmpty()) {
                            correspondenceStepCondition.nextStepEnable = true
                        }
                    }
                    btnClearList.setOnClickListener {
                        letterAdapter.setItems(emptyList())
                        btnClearList.isVisible = false
                        correspondenceStepCondition.nextStepEnable = false
                    }
                    if (viewOnly == true) {
                        selectLetterTitle.enableView(false)
                    }
                }
            }
            STEP_DESCRIPTION -> {
                CorrespondenceStepDescriptionBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    correspondenceStepDescription.apply {
                        index = it.step
                        title = it.title
                    }
                    inputDescription.getInput().doOnTextChanged { text, _, _, _ ->
                        when (text?.length) {
                            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 -> {
                                inputDescription.enableError(
                                    getString(R.string.error_fill_fields),
                                    true
                                )

                                correspondenceStepDescription.nextStepEnable = false
                            }
                            else -> {
                                inputDescription.enableError("", false)
                                correspondenceStepDescription.nextStepEnable = true
                            }
                        }
                    }
                    letterItem?.apply {
                        inputDescription.getInput().setText(descriptions)
                    }
                    if (viewOnly == true) {
                        inputDescription.enableView(false)
                    }
                }
            }
            STEP_UPLOAD_IMAGE -> {
                CorrespondenceStepUploadImageBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    CorrespondenceStepperItemUploadImage.apply {
                        index = it.step
                        title = it.title
                    }
                    btnAddDocument.setOnClickListener {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                        //if (fileListUploaded.size == 0) {
                        chooseImage()
                        /*   } else {
                               showAlertDialog(
                                   MessageOfRequestDialogFragment.MessageType.WARNING,
                                   getString(R.string.error_upload_personal_image)
                               )
                           }*/
                    }
                    recycler.apply {
                        this.adapter = imageAdapter
                        if (itemDecorationCount == 0) {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                    }
                    letterItem?.leterImage?.let { it1 -> mViewModel.getLetterAttachedImage(it1) }
                    if (viewOnly == true) {
                        btnAddDocument.gone()
                        CorrespondenceStepperItemUploadImage.nextStepEnable = false
                        recycler.isEnabled = false
                    }
                }
            }
        }
    }

    private fun checkValidInputsDetail(containerDetail: FrameLayout): Boolean {
        val rootView = containerDetail.getChildAt(0) as? ViewGroup
        var isValidInput = true
        if (rootView != null) {
            for (i in 0 until rootView.childCount) {
                when (val childView = rootView.getChildAt(i)) {
                    is EditTextNumber -> { if ((childView.getInput().text ?: "").length < childView.getMinLength()) {
                            childView.setError(getString(R.string.label_error_enter_correct_data))
                            isValidInput = false
                        } else {
                            checkAndAddToList(childView.getHint(), childView.getValue(false),childView.tag as? String?)
                        } }
                    is EditTextString -> {
                        if ((childView.getInput().text ?: "").length < childView.getMinLength()
                        ) {
                            childView.setError(getString(R.string.label_error_enter_correct_data))
                            isValidInput = false
                        } else {
                            checkAndAddToList(childView.getHint(), childView.getValue(false),childView.tag as? String?)
                        }
                    }
                    is SelectableItemView -> {
                        if (childView.getValue(false).isBlank()) {
                            childView.setError(getString(R.string.label_error_enter_correct_data))
                            isValidInput = false
                        } else {
                            checkAndAddToList(childView.getHint(), childView.getValue(false), childView.tag as? String?)
                        }
                    }
                    is MultiLineEditTextString -> {
                        if ((childView.getInput().text ?: "").length < childView.getMinLength()) {
                            childView.setError(getString(R.string.label_error_enter_correct_data))
                            isValidInput = false
                        } else {
                            checkAndAddToList(childView.getHint(), childView.getValue(false),childView.tag as? String?)
                        }
                    }
                    is DatePickerWidget -> {
                        if (childView.getDateString().isBlank()) {
                            childView.setError(getString(R.string.label_error_enter_correct_data))
                            isValidInput = false
                        } else {
                            checkAndAddToList(childView.getHint(), childView.getDateString(),childView.tag as? String?)
                        }
                    }
                }
            }
        }
        return isValidInput
    }
    private fun setDetailsView(itemId: String?, viewBinding: CorrespondenceStepDetailBinding) {
        viewBinding.apply {
            groupDetails.visible()
            containerDetail.removeAllViews()
            containerDetail.tag = itemId
            requestList.clear()
            letterAdapter.setItems(emptyList())
            btnClearList.isVisible = false
            when (itemId) {
                "1" -> {
                    DistantCorrespondenceDetailTitle01Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        selectMonth.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getMonthsFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectMonth.setValue(itemResult.title ?: "")
                                        selectMonth.disableError()
                                        mViewModel.tempDetailCollection.month = itemResult.id
                                    }
                                })
                            }.show(childFragmentManager, "selectMonth")
                        }
                        if (mViewModel.dataModel.letterRequestDetailCollection.isNotEmpty()) {
                            correspondenceStepCondition.nextStepEnable = true
                        }
                        inputYear.getInput().doOnTextChanged { text, _, _, _ ->
                            var str = text.toString()
                            val cYear = PersianDate().shYear
                            if (str.isNotBlank() && str.toInt() > cYear) {
                                str = cYear.toString()
                                inputYear.getInput().setText(str)
                            }
                        }
                    }
                }
                "2" -> {
                    DistantCorrespondenceDetailTitle02Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    )
                }
                "3" -> {
                    DistantCorrespondenceDetailTitle03Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetIntroLetterDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.leterDate = serverTime
                                widgetIntroLetterDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
                "4" -> {
                    DistantCorrespondenceDetailTitle04Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetTechSkillDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.leterDate = serverTime
                                widgetTechSkillDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetStartDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {

                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetEndDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetEndDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
                "5" -> {
                    DistantCorrespondenceDetailTitle05Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        selectMaintenanceCenter.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getMaintenanceCenterFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectMaintenanceCenter.setValue(itemResult.title ?: "")
                                        mViewModel.tempDetailCollection.requertType = itemResult.id
                                    }
                                })
                            }.show(childFragmentManager, "selectMaintenanceCenter")
                        }
                    }
                }
                "6" -> {
                    DistantCorrespondenceDetailTitle06Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    )
                }
                "7" -> {
                    DistantCorrespondenceDetailTitle07Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        selectMonth.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getMonthsFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectMonth.setValue(itemResult.title ?: "")
                                        mViewModel.tempDetailCollection.month = itemResult.id
                                    }
                                })
                            }.show(childFragmentManager, "selectMonth")
                        }
                        inputYear.getInput().doOnTextChanged { text, _, _, _ ->
                            var str = text.toString()
                            val cYear = PersianDate().shYear
                            if (str.isNotBlank() && str.toInt() > cYear) {
                                str = cYear.toString()
                                inputYear.getInput().setText(str)
                            }
                        }
                    }
                }
                "8" -> {
                    DistantCorrespondenceDetailTitle08Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        selectCertificateType.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getCertificateTypeFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectCertificateType.setValue(itemResult.title ?: "")
                                        mViewModel.tempDetailCollection.requertType = itemResult.id
                                    }
                                })
                            }.show(childFragmentManager, "selectCertificateType")
                        }
                        widgetEndDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.leterDate = serverTime
                                widgetEndDate.setLocalDate(newTimeStamp)
                            }
                        })

                    }
                }
                "9" -> {
                    DistantCorrespondenceDetailTitle09Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetStartDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetEndDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetEndDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
                "10" -> {
                    DistantCorrespondenceDetailTitle10Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        selectShutdownType.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getShutDownTypeFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectShutdownType.setValue(itemResult.title ?: "")
                                        mViewModel.tempDetailCollection.requertType = itemResult.title
                                    }
                                })
                            }.show(childFragmentManager, "selectShutdownType")
                        }
                        widgetShutDownStartDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetShutDownStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetShutDownEndDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetShutDownEndDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
                "11" -> {
                    DistantCorrespondenceDetailTitle11Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetStartDate.setListener(object : DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetEndDate.setListener(object : DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetEndDate.setLocalDate(newTimeStamp)
                            }
                        })
                        inputWorkingDaysInMonth.getInput().doOnTextChanged { text, _, _, _ ->
                            var str = text.toString()
                            if (str.isNotBlank() && str.toInt() > 31) {
                                str = "31"
                                inputWorkingDaysInMonth.getInput().setText(str)
                            }
                        }
                    }
                }
                "12" -> {
                    DistantCorrespondenceDetailTitle12Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetStartDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(timeStamp, serverFormattedDateWithDayOffset)
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetEndDate.setListener(object : DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset)
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetEndDate.setLocalDate(newTimeStamp)
                            }
                        })

                        inputWorkingDaysInMonth.getInput().doOnTextChanged { text, _, _, _ ->
                            var str = text.toString()
                            if (str.isNotBlank() && str.toInt() > 31) {
                                str = "31"
                                inputWorkingDaysInMonth.getInput().setText(str)
                            }
                        }
                    }
                }
                "13" -> {
                    DistantCorrespondenceDetailTitle13Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetStartDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetEndDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetEndDate.setLocalDate(newTimeStamp)
                            }
                        })
                        selectIncludingType.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getIncludingTypeFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectIncludingType.setValue(itemResult.title ?: "")
                                        mViewModel.tempDetailCollection.requertType = itemResult.title
                                    }
                                })
                            }.show(childFragmentManager, "tyuytuytu")
                        }
                    }
                }
                "14" -> {
                    DistantCorrespondenceDetailTitle14Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetExemptionStartDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetExemptionStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetExemptionEndDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetExemptionEndDate.setLocalDate(newTimeStamp)
                            }
                        })

                        selectExemptionType.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getExemptionTypeFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectExemptionType.setValue(itemResult.title ?: "")
                                        mViewModel.tempDetailCollection.requertType = itemResult.title
                                    }
                                })
                            }.show(childFragmentManager, "tyuytuytu")
                        }
                    }
                }
                "15" -> {
                    DistantCorrespondenceDetailTitle15Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetLeaveWorkDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetLeaveWorkDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
                "16" -> {
                    DistantCorrespondenceDetailTitle16Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    )
                }
                "17" -> {
                    DistantCorrespondenceDetailTitle17Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetFinishDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetFinishDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
                "18", "21", "22", "27", "28" -> {}
                "19" -> {
                    DistantCorrespondenceDetailTitle19Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        selectPaymentType.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@DistantCorrespondenceFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getPaymentTypeFlow()
                                                .collectLatest { pagingData -> updateData(pagingData) }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        selectPaymentType.setValue(itemResult.title ?: "")
                                        mViewModel.tempDetailCollection.requertType = itemResult.title
                                    }
                                })
                            }.show(childFragmentManager, "selectPaymentType")
                        }
                    }
                }
                "20" -> {
                    DistantCorrespondenceDetailTitle20Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    )
                }
                "23" -> {
                    DistantCorrespondenceDetailTitle23Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    )
                }
                "24" -> {
                    DistantCorrespondenceDetailTitle24Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    )
                }
                "25" -> {
                    DistantCorrespondenceDetailTitle25Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetChequeDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetChequeDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
                "26" -> {
                    DistantCorrespondenceDetailTitle26Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerDetail,
                        true
                    ).apply {
                        widgetActivityStartDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.sdate = serverTime
                                widgetActivityStartDate.setLocalDate(newTimeStamp)
                            }
                        })
                        widgetActivityEndDate.setListener(object :
                            DatePickerWidget.DateSelectOrListener {
                            override fun onDateSelect(
                                jalaliDate: String,
                                gregorianDate: Date,
                                timeStamp: Long,
                                serverFormattedDate: String,
                                serverFormattedDateWithDayOffset: String
                            ) {
                                val (newTimeStamp, serverTime) = checkSelectedDate(
                                    timeStamp,
                                    serverFormattedDateWithDayOffset
                                )
                                mViewModel.tempDetailCollection.edate = serverTime
                                widgetActivityEndDate.setLocalDate(newTimeStamp)
                            }
                        })
                    }
                }
            }
        }
    }

    private fun checkSelectedDate(timeStamp: Long, serverDate: String): Pair<Long, String> {
        val currentTime = System.currentTimeMillis()
        val currentDate = Calendar.getInstance().time
        val currentDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val currentDateStr = currentDateFormatter.format(currentDate) + "T19:30:00.000Z"
        var date = ""
        var time = timeStamp
        if (currentTime < timeStamp) {
            date = currentDateStr
            time = currentTime
        } else {
            date = serverDate
        }
        return Pair(time, date)
    }

    private fun checkAndAddToList(title: String, value: String?, tag: String?) {
        requestList.forEach { info ->
            if (info._key == title) {
                requestList.remove(info)
            }
        }
        requestList.add(KeyValueModel(_key = title, _value = value ?: "_"))
        when (tag) {
            "year" -> {
                mViewModel.tempDetailCollection.year = value
            }
            "dbtno" -> {
                mViewModel.tempDetailCollection.dbtno = value
            }
            "ordno" -> {
                mViewModel.tempDetailCollection.ordno = value
            }
            "risuid" -> {
                mViewModel.tempDetailCollection.risuid = value
            }
            "letterNo" -> {
                mViewModel.tempDetailCollection.leterNo = value
            }
            "listno" -> {
                mViewModel.tempDetailCollection.listno = value
            }
            "destinationRcntrow" -> {
                mViewModel.tempDetailCollection.destinationRcntrow = value
            }
            "amount" -> {
                mViewModel.tempDetailCollection.amount = value?.replace(",","")?.toLong()
            }
            "day" -> {
                mViewModel.tempDetailCollection.day = value
            }
            "address" -> {
                mViewModel.tempDetailCollection.address = value
            }
            "f1" -> {
                mViewModel.tempDetailCollection.f1 = value
            }
        }
    }
}
