package com.tamin.taminhamrah.ui.home.services.occurence

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.AllWorkshopsResponse
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceDocumentFile
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceDocumentType
import com.tamin.taminhamrah.data.remote.models.services.occurrence.OccurrenceImage
import com.tamin.taminhamrah.databinding.FragmentOccurrenceReportBinding
import com.tamin.taminhamrah.databinding.OccurenceReportStep1Binding
import com.tamin.taminhamrah.databinding.OccurenceReportStep2Binding
import com.tamin.taminhamrah.databinding.OccurenceReportStep3Binding
import com.tamin.taminhamrah.databinding.OccurenceReportStep4Binding
import com.tamin.taminhamrah.databinding.OrotezStep3Binding
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.viewEdictPensioner.EdictPensionerFragment
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import org.jetbrains.annotations.NotNull
import java.io.File

@AndroidEntryPoint
class OccurrenceReportFragment :
    BaseFragment<FragmentOccurrenceReportBinding, OccurrenceReportViewModel>(),
    DialogClickInterface.onClickListener, AdapterInterface.OnItemClickListener<UploadedImageModel>,
    StepperLayout.NextStepClickListener, StepperLayout.PreviousStepClickListener {

    override val mViewModel: OccurrenceReportViewModel by viewModels()

    private val listAdapter by lazy { ImagePreviewAdapter(this) }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Constants.REQUEST_LUNCHER) {
                    var imageUri: Uri? = null

                    if (result.data != null) imageUri =
                        Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))

                    if (!isDuplicateImage(
                            imageUri,
                            mViewModel.fileListUploaded
                        )
                    ) provideImageForUpload(mViewModel.tempImageType, imageUri)
                    else showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_select_repeat_pic)
                    )
                }
            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.image_upload_error)
                )
            }
        }

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_occurrence_report
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreate(savedInstanceState)
    }

    override fun getData() {
        mViewModel.getUserInfo()
    }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null,
        )
        initStepper()

        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.WARNING,
            desc = getString(R.string.label_Occurrence_warning_desc),
            titleId = R.string.label_Occurrence_warning_title,
            btnCancel = true/*,
            titleConfirm = getString(R.string.label_Occurrence_confirm_title)*/
        )
        dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
            override fun onConfirmClick() {
                dialog.dismiss()
            }

            override fun onCancelClick() {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        })
        dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")


    }

    override fun onClick() {
    }

    override fun setupObserver() {
        mViewModel.mldUserInfo.observe(this) {
            if (it?.isSuccess == true) {
                mViewModel.dataModel.pNationalCode = it.data?.nationalID
                ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(1)) as? OccurenceReportStep1Binding)?.apply {
                    userInfo = it.data
                }
            }
        }

        mViewModel.mldAllWorkshops.observe(this, ::onFetchAllWorkshopInfo)

        mViewModel.mldAllWorkshopHistory.observe(this) {
            if (it?.isSuccess == true) {
            }
        }

        mViewModel.mldWorkshopSpecification.observe(this) {
            if (it?.isSuccess == true) {
                ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(2)) as? OccurenceReportStep2Binding)?.apply {
                    tvWorkshopName.text = it.data?.workshopName
                }
                ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(3)) as? OccurenceReportStep3Binding)?.apply {
                    tvNationality.text = it.data?.nation?.nationDesc
                    mViewModel.dataModel.reporterType =
                        if (it.data?.nation?.nationCode == "01") "1" else "2"
                    mViewModel.dataModel.nationCode = 1
                }
            }
        }

        mViewModel.mldOfficePersonalInfo.observe(this) {
            if (it?.isSuccess == true) {
                ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(3)) as? OccurenceReportStep3Binding)?.apply {
                    tvFullName.text = "${it.data?.firstName} ${it.data?.lastName}"
                    tvGender.text = if (it.data?.gender == "02") "زن" else "مرد"

                    mViewModel.dataModel.apply {
                        pFirstName = it.data?.firstName
                        pLastName = it.data?.lastName
                        gender = if (it.data?.gender == "02") 2 else 1
                        insuranceID = it.data?.insuranceId

                    }
                }
            }
        }

        mViewModel.mldInsuredRelation.observe(this) {
            if (it?.isSuccess == true) {
                mViewModel.dataModel.apply {
                    isuTypecode = it.data?.isuType
                    isuTypeDesc = it.data?.isuTypeDesc
                    branchCode = it.data?.brhCode
                    branchName = it.data?.brhName
                }


                ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(3)) as? OccurenceReportStep3Binding)?.apply {
                    tvInsuranceType.text = it.data?.isuTypeDesc
                }
            }
        }

        mViewModel.mldUploadImage.observe(this, ::onUploadImage)

        mViewModel.mldResponse.observe(this) {
            if (it?.isSuccess == true) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.SUCCESS,
                    getString(R.string.message_occurrence_result, it.data?.reportRefrenceNumber),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            }
        }
    }

    override fun chooseImage(requestCode: Int) {
        this@OccurrenceReportFragment.lifecycleScope.launchWhenCreated {

            mViewModel.getDocTypeFlow.collectLatest { pagingData ->
                val result = pagingData.map {
                    MenuModel(
                        id = it.docTypeId,
                        title = it.docDesc
                    )
                }

                openImageTypeMenu(result, onResultCallBack = object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        mViewModel.fileListUploaded.apply {
                            mViewModel.tempImageType = itemResult.id.toString()
                            mViewModel.tempImageName = itemResult.title ?: ""
                            val intent = Intent(activity, MultiCustomGalleryUI::class.java)
                            intent.putExtra(Constants.TEMPID, mViewModel.tempImageType)
                            intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)
                            resultImageLaunch.launch(intent)
                        }
                    }
                })
            }
        }
    }

    override fun onConfirmClick() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onCancelClick() {
    }

    override fun onItemClick(item: UploadedImageModel, transitionView: View?, tag: String?) {
        when (tag) {
            Constants.IMAGE_PREVIEW_TAG -> {
                val bundle = Bundle()
                bundle.putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                bundle.putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                handlePageDestination(
                    R.id.action_occurrenceReportFragment_to_ImageViewerActivity, bundle
                )
            }

            Constants.DELETE_IMAGE_TAG -> {
                mViewModel.fileListUploaded.apply {
                    remove(item)
                    listAdapter.removeItem(item)
                    ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(3)) as? OrotezStep3Binding)?.apply {
                        if (size < 4 && !itemAddDoc.root.isVisible) itemAddDoc.root.visible()
                    }
                }
            }
        }
    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int
    ) {
        mViewModel.apply {
            uploadImage(body)
            tempImageOriginalUri = orgPath
            tempImageUri = imageUri
        }
    }

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.apply {

            when (stepIndex) {
                1 -> {
                    ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(1)) as? OccurenceReportStep1Binding)?.apply {
                        if (!mViewModel.dataModel.pNationalCode.isNullOrEmpty() && !widgetDatePicker.inputDate.text.isNullOrEmpty()) {
                            if (mViewModel.mldInsuredRelation.value?.data == null ||
                                mViewModel.mldAllWorkshops.value?.data == null
                            ) {
                                mViewModel.getInsuredRelation(mViewModel.dataModel.pNationalCode)
                                mViewModel.getAllWorkshops(mViewModel.dataModel.pNationalCode)

                            } else
                                nextStep()

                        } else {
                            widgetDatePicker.tilDate.error =
                                getString(R.string.error_message_select_date)
                        }
                    }

                }

                2 -> {
                    ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(2)) as? OccurenceReportStep2Binding)?.apply {
                        mViewModel.dataModel.apply {
                            selectWorkshopCode.getValue(true)
                            workshopName = tvWorkshopName.text.toString()
                            bossFullName = etEmployeeFullName.getValue(true)
                            bossMobileNumber = etEmployeePhone.getValue(true)
                            workshopAddress = etWorkshopAddress.getValue(true)
                            workshopTelephone = etWorkshopPhone.getValue(true)
                            workshopPostalCode = etWorkshopZipCode.getValue(true)

                            if (!branchCode.isNullOrEmpty() &&
                                !workshopCode.isNullOrEmpty() &&
                                !workshopName.isNullOrEmpty() &&
                                !bossFullName.isNullOrEmpty() &&
                                !bossMobileNumber.isNullOrEmpty() &&
                                !workshopAddress.isNullOrEmpty() &&
                                !workshopTelephone.isNullOrEmpty() &&
                                !workshopPostalCode.isNullOrEmpty()
                            )
                                nextStep()
                        }

                    }
                }

                3 -> {
                    ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(3)) as? OccurenceReportStep3Binding)?.apply {
                        mViewModel.dataModel.apply {
                            val marriageStatus = selectMarriageStatus.getValue(true)
                            if (employeeDate.isNullOrEmpty()) widgetDatePicker.tilDate.error =
                                getString(R.string.error_message_select_date)
                            jobDesc = etJobDescription.getValue(true)
                            reportJobLocation = etJobLocation.getValue(true)
                            vehicle = etTransportType.getValue(true)
                            rwworkstart = etWorkStartTime.getValue(true)
                            rwworkfinish = etWorkEndTime.getValue(true)
                            reportAddress = etAddress.getValue(true)
                            reportTelephone = etHomePhone.getValue(true)
                            reportPostalCode = etHomeZipCode.getValue(true)

                            if (marriageStatus.isNotEmpty() && !employeeDate.isNullOrEmpty() &&
                                !jobDesc.isNullOrEmpty() && !reportJobLocation.isNullOrEmpty() &&
                                !vehicle.isNullOrEmpty() && !rwworkstart.isNullOrEmpty() &&
                                !rwworkfinish.isNullOrEmpty() && !reportAddress.isNullOrEmpty() &&
                                !reportTelephone.isNullOrEmpty() && !reportPostalCode.isNullOrEmpty()
                            )
                                nextStep()
                        }
                    }
                }

                4 -> {

                    ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(4)) as? OccurenceReportStep4Binding)?.apply {
                        mViewModel.dataModel.apply {
                            selectResult.getValue(true)
                            if (occurrenceDate.isNullOrEmpty())
                                widgetDatePicker.tilDate.error =
                                    getString(R.string.error_message_select_date)

                            occurrenceTime = etOccurrenceTime.getValue(true)
                            occurrenceAddress = etOccurrenceLocation.getValue()
                            occurrenceDesc = etOccurrenceDescription.getValue(true)

                            if (mViewModel.fileListUploaded.isEmpty()) {
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.error_select_image)
                                )
                            } else
                                mViewModel.dataModel.occurrenceDocumentList = ArrayList()
                            for (item in mViewModel.fileListUploaded) {
                                mViewModel.dataModel.occurrenceDocumentList?.add(
                                    OccurrenceImage(
                                        OccurrenceDocumentType(item.imageType),
                                        OccurrenceDocumentFile(item.guid)
                                    )
                                )
                            }

                            if (!occurrenceDate.isNullOrEmpty() && !occurrenceTime.isNullOrEmpty() &&
                                occurrenceResult!! > 0 && !occurrenceAddress.isNullOrEmpty() &&
                                !occurrenceDesc.isNullOrEmpty() && !occurrenceDocumentList.isNullOrEmpty()
                            ) {
                                mViewModel.sendOccurrenceRequest(mViewModel.dataModel)
                            }
                        }

                    }
                }

                else -> {
                    nextStep()
                }
            }
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.previousStep()
    }

    override fun onDestroyView() {
        val folder =
            File("${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tempImage/")
        folder.deleteRecursively()
        super.onDestroyView()

    }

    private fun onFetchAllWorkshopInfo(result: AllWorkshopsResponse?) {
        if (result?.isSuccess == true) {
            ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(1)) as? OccurenceReportStep1Binding)?.apply {
                stepperItem.nextStep()
            }
            ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(2)) as? OccurenceReportStep2Binding)?.apply {

                selectWorkshopCode.getIt().setOnClickListener {
                    view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                    MenuDialogFragment.newInstance(true, getString(R.string.label_workshop_code))
                        .apply {
                            setMenuListener(object : MenuInterface.OnFetchData {
                                override fun onFetch() {
                                    lifecycleScope.launchWhenCreated {
                                        mViewModel.getAllWorkshopFlow(result?.data?.list)
                                            .collectLatest { pagingData ->
                                                updateData(pagingData)
                                            }
                                    }
                                }
                            }, object : MenuInterface.OnResult {
                                override fun onResult(itemResult: MenuModel) {
                                    selectWorkshopCode.getIt().setText(itemResult.title)
                                    selectWorkshopCode.disableError()
                                    groupWorkshopName.visible()

                                    val workshopInfo =
                                        itemResult.title?.split(mViewModel.WORKSHOP_INFO_SEPREATOR)
                                    val nationalCode =
                                        mViewModel.mldUserInfo.value?.data?.nationalID
                                    val birthDate =
                                        mViewModel.mldUserInfo.value?.data?.birthDateTimestamp
                                    val workshopCode = workshopInfo?.get(0)?.trim()
                                    val branchCode = workshopInfo?.get(1)?.trim()

                                    mViewModel.getWorkshopAndUserInfo(
                                        nationalCode,
                                        birthDate,
                                        workshopCode,
                                        branchCode
                                    )

                                    mViewModel.dataModel.workshopCode = workshopCode
                                    mViewModel.dataModel.workshopBranchCode = branchCode
                                }
                            })
                        }.show(childFragmentManager, EdictPensionerFragment().javaClass.simpleName)
                }
            }
        }
    }

    private fun onUploadImage(result: UploadImageResponse) {
        if (!result.isSuccess) return
        mViewModel.fileListUploaded.apply {
            add(
                UploadedImageModel(
                    guid = result.guid,
                    imageType = mViewModel.tempImageType,
                    imageUri = mViewModel.tempImageUri,
                    imageName = mViewModel.tempImageName,
                    orgUri = mViewModel.tempImageOriginalUri
                )
            )
//            if (size >= 4) ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(4)) as? OccurenceReportStep4Binding)?.itemAddDoc?.root?.gone()

            mViewModel.tempImageType = ""
            mViewModel.tempImageName = ""
            mViewModel.tempImageUri = null
            //show image and title in ui
            listAdapter.setItems(this)
        }
    }

    private fun onSendRequest(result: ShortTermOrthosisResponse) {
        if (!result.isSuccess) return
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.SUCCESS,
            result.data?.shorttermRequest?.resultMessage ?: ""
        )
        dialog.setDialogClickListener(this)
        dialog.show(childFragmentManager, OccurrenceReportFragment().javaClass.simpleName)
    }

    private fun initialStep1() = OccurenceReportStep1Binding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
    ).apply {
        widgetDatePicker.inputDate.setOnClickListener {
            getDatePicker()?.apply {
                setListener(object : MyPersianPickerListener {
                    override fun onDateSelected(@NotNull myPersianPickerDate: MyPersianPickerDate) {
                        widgetDatePicker.tilDate.isErrorEnabled = false
                        //todo: check other usage of DatePicker and use this method!!!!!!!!!
                        widgetDatePicker.inputDate.setText(getTimeString(myPersianPickerDate))
                        mViewModel.dataModel.birthDate = myPersianPickerDate.timestamp.toString()

                        if (!mViewModel.dataModel.birthDate.isNullOrEmpty())
                            stepperItem.nextStepEnable = true

                    }

                    override fun onDismissed() {}
                })
            }?.show()
        }

    }

    private fun initialStep2() = OccurenceReportStep2Binding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
    ).apply {
//        selectWorkshopCode click is in onFetchAllWorkshopInfo()
    }

    private fun initialStep3() = OccurenceReportStep3Binding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
    ).apply {
        selectMarriageStatus.getIt().setOnClickListener {
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            MenuDialogFragment.newInstance(true, getString(R.string.label_workshop_code))
                .apply {
                    setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            lifecycleScope.launchWhenCreated {
                                mViewModel.marriageFlow.collectLatest { pagingData ->
                                    updateData(
                                        pagingData
                                    )
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectMarriageStatus.getIt().setText(itemResult.title)
                            selectMarriageStatus.disableError()

                            mViewModel.dataModel.marriageStatusCode = itemResult.id?.toLong()

                        }
                    })
                }.show(childFragmentManager, EdictPensionerFragment().javaClass.simpleName)
        }

        widgetDatePicker.inputDate.setOnClickListener {
            getDatePicker()?.apply {
                setListener(object : MyPersianPickerListener {
                    override fun onDateSelected(@NotNull myPersianPickerDate: MyPersianPickerDate) {
                        widgetDatePicker.tilDate.isErrorEnabled = false
                        widgetDatePicker.inputDate.setText(getTimeString(myPersianPickerDate))
                        mViewModel.dataModel.employeeDate = myPersianPickerDate.timestamp.toString()
                    }

                    override fun onDismissed() {}
                })
            }?.show()
        }
    }

    private fun initialStep4() = OccurenceReportStep4Binding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
    ).apply {
        widgetDatePicker.inputDate.setOnClickListener {
            getDatePicker()?.apply {
                setListener(object : MyPersianPickerListener {
                    override fun onDateSelected(@NotNull myPersianPickerDate: MyPersianPickerDate) {
                        widgetDatePicker.tilDate.isErrorEnabled = false
                        widgetDatePicker.inputDate.setText(getTimeString(myPersianPickerDate))

                        mViewModel.dataModel.occurrenceDate =
                            myPersianPickerDate.timestamp.toString()

                        mViewModel.getAllWorkshopHistory()
                    }

                    override fun onDismissed() {}
                })
            }?.show()
        }

        selectResult.getIt().setOnClickListener {
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            MenuDialogFragment.newInstance(true, getString(R.string.label_occurrence_result))
                .apply {
                    setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            lifecycleScope.launchWhenCreated {
                                mViewModel.occurrenceResultFlow.collectLatest { pagingData ->
                                    updateData(
                                        pagingData
                                    )
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectResult.getIt().setText(itemResult.title)
                            selectResult.disableError()

                            mViewModel.dataModel.occurrenceResult = itemResult.id?.toLong()
                        }
                    })
                }.show(childFragmentManager, EdictPensionerFragment().javaClass.simpleName)
        }

        recycler.apply {
            this.adapter = listAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
            }
        }
        itemAddDoc.root.setOnClickListener {
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            chooseImage()
        }
    }

    private fun initStepper() {
        viewDataBinding?.apply {
            val stepLayout = listOf(initialStep1(), initialStep2(), initialStep3(), initialStep4())
            stepper.initial(stepLayout)
            stepper.onNextStepClickListener = this@OccurrenceReportFragment
            stepper.onPreviousStepClickListener = this@OccurrenceReportFragment
        }
    }

}
