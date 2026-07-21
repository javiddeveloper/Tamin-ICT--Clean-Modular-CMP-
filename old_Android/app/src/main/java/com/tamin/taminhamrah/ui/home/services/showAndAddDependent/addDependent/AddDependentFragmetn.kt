package com.tamin.taminhamrah.ui.home.services.showAndAddDependent.addDependent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.size
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.BranchListResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.InquiryEducationCodeResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.InquiryRegistryResponse
import com.tamin.taminhamrah.databinding.FragmentAddDependentBinding
import com.tamin.taminhamrah.databinding.StepCommitmentDaughterDependentBinding
import com.tamin.taminhamrah.databinding.StepDependentInfoBinding
import com.tamin.taminhamrah.databinding.StepEducationCodeDependentBinding
import com.tamin.taminhamrah.databinding.StepInquiryInfoDependentBinding
import com.tamin.taminhamrah.databinding.StepUploadDocumentDependentBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.DependentsViewModel
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.enums.EnumFamilyRelationShip
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.enums.EnumGenderMode
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.enums.EnumStepperMode
import com.tamin.taminhamrah.ui.home.services.showAndAddDependent.model.DialogTypeAddDependent
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.isDuplicateImage
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody

@AndroidEntryPoint
class AddDependentFragment() :
    BaseFragment<FragmentAddDependentBinding, DependentsViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener, Parcelable {

    //Class Variables
    override val mViewModel: DependentsViewModel by viewModels()
    val onItemDocumentClick by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?,
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        handlePageDestination(
                            R.id.action_AddDependentFragment_to_ImageViewerActivity,
                            Bundle().apply {
                                putString(ViewerImageActivity.TITLE_IMAGE, item.imageName)
                                putString(ViewerImageActivity.URI_IMAGE, item.imageUri.toString())
                            }
                        )
                    }
                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.documentList.remove(item)
                        documentListAdapter.setItems(mViewModel.dataModel.documentList)
                    }
                }
            }
        }
    }
    val documentListAdapter: ImagePreviewAdapter by lazy { ImagePreviewAdapter(onItemDocumentClick) }
    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Constants.REQUEST_LUNCHER) {
                val imageUri = if (result.data != null)
                    Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))
                else
                    null
                //when capture image from camera imageUri is null
                if (imageUri == null || !isDuplicateImage(imageUri,
                        mViewModel.dataModel.documentList)
                )
                    provideImageForUpload(mViewModel.dataModel.tempImageType, imageUri)
                else
                    showAlertDialog(MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_select_repeat_pic))
            }
        }

    constructor(parcel: Parcel) : this() {
    }

    //Base Methods
    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)
    override fun getLayoutId() = R.layout.fragment_add_dependent

    override fun setupObserver() {
        mViewModel.mldActiveBranch.observe(this, ::onActiveBranchResponse)
        mViewModel.mldInquiryRegistry.observe(this, ::onInquiryRegistryResponse)
        mViewModel.mldUploadImage.observe(this, ::onUploadImageResponse)
        mViewModel.mldInquiryEducationCode.observe(this, ::onInquiryEducationCodeResponse)
        mViewModel.mldAddDependent.observe(this, ::onAddDependentResponse)
    }

    override fun getData() {
        mViewModel.getActiveBranch()
    }

    override fun initView() {
        viewDataBinding?.apply {
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
                moreViews = null)
            tvDesc.descTxt.text = getText(R.string.add_dependent_desc)
        }
    }

    override fun onClick() {
    }

    override fun chooseImage(requestCode: Int) {
        showDialog(DialogTypeAddDependent.IMAGE_LIST, getString(R.string.document),
            object : MenuInterface.OnResult {
                override fun onResult(itemResult: MenuModel) {
                    mViewModel.dataModel.tempImageType = itemResult.id.toString()
                    mViewModel.dataModel.tempImageName = itemResult.title ?: ""
                    Intent(activity, MultiCustomGalleryUI::class.java).apply {
                        putExtra(Constants.TEMPID, mViewModel.dataModel.tempImageType)
                        resultImageLaunch.launch(this)
                    }
                }
            })
    }


    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int,
    ) {
        mViewModel.uploadImage(body)
        mViewModel.dataModel.tempImageUri = imageUri
        mViewModel.dataModel.tempImageOriginalUri = orgPath
    }

    //Listeners
    private fun onActiveBranchResponse(result: BranchListResponse) {
        if (result.isSuccess) {
            if (result.data.isNullOrEmpty()) {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
            } else {
                viewDataBinding?.tvTitleInfo?.visibility = View.VISIBLE
                viewDataBinding?.tvDesc?.root?.visibility = View.VISIBLE
                initStepper()
                mViewModel.dataModel.activeBranchList.clear()
                mViewModel.dataModel.activeBranchList.addAll(result.getBranchList())
            }
        }
    }

    private fun onInquiryRegistryResponse(result: InquiryRegistryResponse) {
        if (result.isSuccess) {
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
            mViewModel.dataModel.loadUserInfo(result.data)

            mViewModel.dataModel.stepperMode = getStepperMode(result.data.age, result.data.gender)
            initStepper(mViewModel.dataModel.stepperMode)

            mViewModel.dataModel.needCallInquiryRequest = false
            setUiInquiryInfoStepper((viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? StepInquiryInfoDependentBinding))
            checkDependentType(result.data.registryConfirmState)
        }
    }

    private fun onInquiryEducationCodeResponse(result: InquiryEducationCodeResponse) {
        if (result.isSuccess) {
            if (result.data.isNullOrBlank()) {
                showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data))
            } else {
                mViewModel.dataModel.needCallInquiryEducationCode = false
                setUiInquiryEducationStep(result.data)
            }
        }
    }

    private fun onUploadImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.loadImageInfo(result.guid)
            documentListAdapter.setItems(mViewModel.dataModel.documentList)
        }
    }

    private fun onAddDependentResponse(result: GeneralRes) {
        if (result.isSuccess)
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_add_dependent),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS)
    }

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.apply {
            when {
                stepIndex == 1 -> {
                    if (mViewModel.dataModel.needCallInquiryRequest) {
                        (getStepLayoutBindingByStep(1) as? StepInquiryInfoDependentBinding)?.apply {
                            mViewModel.dataModel.dependentNationalId = edNationalId.getValueNationalCode()
                            when {
                                edNationalId.getLayout().isErrorEnabled -> {
                                    edNationalId.getLayout().error =
                                        getString(R.string.error_not_valid_national_id)
                                }
                                datePickerBirthDate.inputDate.text.toString().isBlank() -> {
                                    datePickerBirthDate.tilDate.error =
                                        getString(R.string.error_select_birthdate)
                                }
                                selectDependent.getValue(false).isBlank() -> {
                                    selectDependent.getLayout().error =
                                        getString(R.string.error_select_dependency)
                                }
                                else -> {
                                    mViewModel.inquiryRegistryInfo(mViewModel.dataModel.dependentNationalId,
                                        mViewModel.dataModel.birthDateTimeStamp,
                                        mViewModel.dataModel.selectedDependent?.description/*dependencyCode*/
                                            ?: "")
                                }
                            }
                        }
                    } else nextStep()
                }
                stepIndex == 2 && mViewModel.dataModel.stepperMode == EnumStepperMode.SON_MODE
                        && mViewModel.dataModel.needCallInquiryEducationCode -> {
                    (getStepLayoutBindingByStep(2) as? StepEducationCodeDependentBinding)?.apply {
                        if (edEducationCode.getValue().isBlank())
                            edEducationCode.getLayout().error =
                                getString(R.string.error_enter_education_code)
                        else if (edEducationCode.getLayout().isErrorEnabled)
                            showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR,
                                getString(R.string.error_inquiry_study_code))
                        else {
                            mViewModel.dataModel.educationCode = edEducationCode.getValue()
                            mViewModel.inquiryEducationCode(mViewModel.dataModel.dependentNationalId,
                                mViewModel.dataModel.educationCode)
                        }
                    }
                }
                stepIndex == size - 1 -> {
                    (getStepLayoutBindingByStep(size - 1) as? StepDependentInfoBinding)?.apply {
                        when {
                            selectCityBirth.getValue(false).isBlank() -> {
                                selectCityBirth.getLayout().error =
                                    getString(R.string.error_select_city_name)
                            }
                            selectCityIssuance.getValue(false).isBlank() -> {
                                selectCityIssuance.getLayout().error =
                                    getString(R.string.error_select_city_name)
                            }
                            mViewModel.dataModel.selectedBranch.isBlank() -> {
                                selectLastBranch.getLayout().error =
                                    getString(R.string.error_select_branch_name)
                            }
                            else -> {
                                nextStep()
                            }
                        }
                    }
                }
                stepIndex == size -> { //last step
                    if (mViewModel.dataModel.getImageTitleList().size > mViewModel.dataModel.documentList.size) {
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.INFO,
                            getString(R.string.error_need_upload_document)
                        )
                    } else {
                        mViewModel.addNewDependent(mViewModel.dataModel.getRequestModel())
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

    //Utils
    private fun checkDependentType(registryConfirmState: String) {
        when ((mViewModel.dataModel.selectedDependent?.description)) {
            EnumFamilyRelationShip.SPOUSE.code -> {
                mViewModel.dataModel.isDisabledIdCard = false
                mViewModel.dataModel.isDisabledMarriageContract = false
            }
            EnumFamilyRelationShip.SON.code, EnumFamilyRelationShip.DAUGHTER.code -> {
                //registryConfirmState=1 => the civil registry has not confirmed the user's information
                mViewModel.dataModel.isDisabledIdCard = registryConfirmState != "1"
                mViewModel.dataModel.isDisabledMarriageContract = true
            }
        }
    }

    private fun initStepper(enumStepperMode: EnumStepperMode = EnumStepperMode.DEFAULT_MODE) {
        viewDataBinding?.stepper?.apply {
            val steps = ArrayList<ViewBinding>().apply {
                add(initInquiryInfoStepper())
                if (enumStepperMode == EnumStepperMode.SON_MODE) {
                    add(initEducationCodeStepper())
                } else if (enumStepperMode == EnumStepperMode.DAUGHTER_MODE) {
                    add(initCommitmentGirlStep())
                }
                add(initDependentInfoStepper())
                add(initUploadDocumentStepper())
            }
            initial(steps)
            onNextStepClickListener = this@AddDependentFragment
            onPreviousStepClickListener = this@AddDependentFragment
        }
    }

    private fun setUiInquiryInfoStepper(stepBinding: StepInquiryInfoDependentBinding?) {
        stepBinding?.apply {
            tvValueName.text = getString(R.string.space,
                mViewModel.dataModel.dependentUserName,
                mViewModel.dataModel.dependentUserFamily)
            tvValueFatherName.text = mViewModel.dataModel.dependentFatherName
            groupDependentInfo.visibility = View.VISIBLE
            if (!mViewModel.dataModel.needCallInquiryRequest)
                stepperInquiryInfo.nextButtonTitle = getString(R.string.ok_and_continue)
        }
    }

    private fun setUiInquiryEducationStep(data: String) {
        (viewDataBinding?.stepper?.getStepLayoutBindingByStep(2) as? StepEducationCodeDependentBinding)?.apply {
            tvUniversity.visibility = View.VISIBLE
            tvValueUniversity.visibility = View.VISIBLE
            mViewModel.dataModel.universityName = data
            tvValueUniversity.text = mViewModel.dataModel.universityName
            stepperInquiryEducation.nextButtonTitle = getString(R.string.next_step)
        }
    }

    private fun resetUiInquiryInfo(stepperBinding: StepInquiryInfoDependentBinding) {
        stepperBinding.stepperInquiryInfo.nextButtonTitle = getString(R.string.inquiry_info)
        stepperBinding.groupDependentInfo.visibility = View.GONE
        mViewModel.dataModel.resetDependentInfo()
        documentListAdapter.clearItems()
    }

    private fun initInquiryInfoStepper() =
        StepInquiryInfoDependentBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepper, true).apply {
            //initial stepper =>when the stepper view is reinitialize, the child's need to receive the stored values
            datePickerBirthDate.input = mViewModel.dataModel.selectedBirthDateFormat
            selectDependent.setValue(mViewModel.dataModel.selectedDependent?.title ?: "")
            edNationalId.setTextWidget(mViewModel.dataModel.dependentNationalId)
            if (!mViewModel.dataModel.needCallInquiryRequest)
                setUiInquiryInfoStepper(this)

            datePickerBirthDate.inputDate.setOnClickListener {
                val dataPicker = getDatePicker()
                dataPicker?.setListener(object : MyPersianPickerListener {
                    override fun onDateSelected(myPersianPickerDate: MyPersianPickerDate) {

                        if (mViewModel.dataModel.selectedBirthDateFormat.isNotBlank() && !mViewModel.dataModel.needCallInquiryRequest)
                            resetUiInquiryInfo(this@apply)


                        datePickerBirthDate.tilDate.isErrorEnabled = false

                        val list = 1..9
                        val day = if (myPersianPickerDate.gregorianDay in list) {
                            "0${myPersianPickerDate.gregorianDay}"
                        } else {
                            "${myPersianPickerDate.gregorianDay}"
                        }
                        val month = if (myPersianPickerDate.gregorianMonth in list) {
                            "0${myPersianPickerDate.gregorianMonth}"
                        } else {
                            "${myPersianPickerDate.gregorianMonth}"
                        }

                        mViewModel.dataModel.birthDateTimeStamp =
                            myPersianPickerDate.timestamp.toString()
                        mViewModel.dataModel.selectedBirthDateGregorian =
                            "${myPersianPickerDate.gregorianYear}-${month}-${day}T19:30:00.000Z"
                        mViewModel.dataModel.selectedBirthDateFormat =
                            "${myPersianPickerDate.persianYear}/${myPersianPickerDate.persianMonth}/${myPersianPickerDate.persianDay}"

                        datePickerBirthDate.inputDate.setText(mViewModel.dataModel.selectedBirthDateFormat)
                    }

                    override fun onDismissed() {
                    }
                })
                dataPicker?.show()
            }
            selectDependent.getIt().setOnClickListener {
                showDialog(DialogTypeAddDependent.DEPENDENT_LIST,
                    getString(R.string.relative),
                    object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            if (mViewModel.dataModel.selectedDependent != null && !mViewModel.dataModel.needCallInquiryRequest)
                                resetUiInquiryInfo(this@apply)

                            selectDependent.getLayout().isErrorEnabled = false
                            selectDependent.setValue(itemResult.title ?: "")
                            mViewModel.dataModel.selectedDependent = itemResult
                        }

                    })
            }
            edNationalId.getInput().doAfterTextChanged {
                if (mViewModel.dataModel.dependentNationalId.isNotBlank() && !mViewModel.dataModel.needCallInquiryRequest)
                    resetUiInquiryInfo(this@apply)
                edNationalId.getLayout().isErrorEnabled = false
            }
        }

    private fun initDependentInfoStepper() =
        StepDependentInfoBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepper, true).apply {
            if (mViewModel.dataModel.stepperMode != EnumStepperMode.DEFAULT_MODE)
                stepperDependentInfo.index = 3

            selectCityBirth.getIt().setOnClickListener {
                showDialog(DialogTypeAddDependent.CITY_LIST, getString(R.string.birth_city),
                    object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectCityBirth.getLayout().isErrorEnabled = false
                            selectCityBirth.setValue(itemResult.title ?: "")
                            mViewModel.dataModel.selectedCityBirthCode = itemResult.id ?: ""
                        }
                    })
            }
            selectCityIssuance.getIt().setOnClickListener {
                showDialog(DialogTypeAddDependent.CITY_LIST,
                    getString(R.string.issue_city),
                    object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectCityIssuance.getLayout().isErrorEnabled = false
                            selectCityIssuance.setValue(itemResult.title ?: "")
                            mViewModel.dataModel.selectedCityIssuance = itemResult.id ?: ""
                        }
                    })
            }
            selectLastBranch.getIt().setOnClickListener {
                showDialog(DialogTypeAddDependent.BRANCH_LIST,
                    getString(R.string.insurance_branch),
                    object : MenuInterface.OnResult {
                        override fun onResult(itemResult: MenuModel) {
                            selectLastBranch.getLayout().isErrorEnabled = false
                            selectLastBranch.setValue(itemResult.title ?: "")
                            mViewModel.dataModel.selectedBranch = itemResult.id ?: ""
                        }
                    })
            }
            if (mViewModel.dataModel.activeBranchList.size == 1) {
                selectLastBranch.getIt().setOnClickListener(null)
                selectLastBranch.setValue(mViewModel.dataModel.activeBranchList[0].title ?: "")
                mViewModel.dataModel.selectedBranch =
                    mViewModel.dataModel.activeBranchList[0].id ?: ""
            }
        }

    private fun initEducationCodeStepper() = StepEducationCodeDependentBinding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true)

    private fun initCommitmentGirlStep() =
        StepCommitmentDaughterDependentBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepper,
            true).apply {
            cbDescDaughterConfirm.setOnCheckedChangeListener { _, isChecked ->
                stepperDaughterConfirm.nextStepEnable = isChecked
            }
        }

    private fun initUploadDocumentStepper() =
        StepUploadDocumentDependentBinding.inflate(LayoutInflater.from(requireContext()),
            viewDataBinding?.stepper, true).apply {

            recycler.adapter = documentListAdapter
            if (recycler.itemDecorationCount == 0)
                recycler.addItemDecoration(UiUtils.VerticalItemSetMarginDecoration(topMargin = 10,
                    bottomMargin = 10))

            if (mViewModel.dataModel.stepperMode != EnumStepperMode.DEFAULT_MODE)
                stepperUploadDoc.index = 4

            itemAddDoc.root.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                if (mViewModel.dataModel.getImageTitleList().size == 0) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.INFO,
                        getString(R.string.do_not_need_upload_document))
                } else {
                    chooseImage()
                }
            }
        }

    fun showDialog(
        type: DialogTypeAddDependent,
        title: String,
        callBackResult: MenuInterface.OnResult,
    ) {
        MenuDialogFragment.newInstance(true,title).apply {
//            val bundle = Bundle()
//            bundle.putString(MenuDialogFragment.ARG_MENU_TITLE, title)
//            arguments = bundle
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@AddDependentFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            DialogTypeAddDependent.DEPENDENT_LIST -> {
                                if (mViewModel.dataModel.dependentList.isEmpty())
                                    mViewModel.getFamilyRelationShips().collectLatest { pagingData ->
                                        updateData(pagingData.map {
                                            val item = MenuModel(
                                                id = it.id.toString(),
                                                title = it.relationDesc,
                                                description = it.relationCode,
                                                description2 = it.bailCode)
                                            mViewModel.dataModel.dependentList.add(item)
                                            item
                                        })
                                    }
                                else
                                    Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10,
                                        2),
                                        pagingSourceFactory = { LocalPagingSource(mViewModel.dataModel.dependentList) })
                                        .flow.cachedIn(lifecycleScope)
                                        .collectLatest { pagingData -> updateData(pagingData) }
                            }
                            DialogTypeAddDependent.CITY_LIST -> {
                                mViewModel.getCityList().collectLatest { pagingData ->
                                    updateData(pagingData.map {
                                        MenuModel(id = it.cityCode, title = it.cityName)
                                    })
                                }
                            }
                            DialogTypeAddDependent.BRANCH_LIST -> {
                                Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                    pagingSourceFactory = { LocalPagingSource(mViewModel.dataModel.activeBranchList) }).flow.cachedIn(
                                    lifecycleScope).collectLatest { pagingData ->
                                    updateData(pagingData)
                                }
                            }
                            DialogTypeAddDependent.IMAGE_LIST -> {
                                Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                                    pagingSourceFactory = { LocalPagingSource(mViewModel.dataModel.getImageTitleList()) }).flow.cachedIn(
                                    lifecycleScope).collectLatest { pagingData ->
                                    updateData(pagingData)
                                }
                            }
                        }
                    }
                }
            }, callBackResult, object : MenuInterface.OnSearch {
                override fun onSearch(str: String) {
                    this@AddDependentFragment.lifecycleScope.launchWhenCreated {
                        if (type == DialogTypeAddDependent.CITY_LIST) {
                            mViewModel.getCityList(str).collectLatest { pagingData ->
                                updateData(pagingData.map {
                                    MenuModel(id = it.cityCode, title = it.cityName)
                                })
                            }
                        }
                    }
                }
            })
        }.show(childFragmentManager, "AddDependentFragment")
    }

    private fun getStepperMode(age: Int, gender: String) = when {
        age >= 18 && gender == EnumGenderMode.WOMAN.code
                && mViewModel.dataModel.selectedDependent?.description == EnumFamilyRelationShip.DAUGHTER.code -> {
            EnumStepperMode.DAUGHTER_MODE
        }
        age >= 19 && gender == EnumGenderMode.MAN.code &&
                mViewModel.dataModel.selectedDependent?.description == EnumFamilyRelationShip.SON.code -> {
            EnumStepperMode.SON_MODE
        }
        else -> {
            EnumStepperMode.DEFAULT_MODE
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddDependentFragment> {
        override fun createFromParcel(parcel: Parcel): AddDependentFragment {
            return AddDependentFragment(parcel)
        }

        override fun newArray(size: Int): Array<AddDependentFragment?> {
            return arrayOfNulls(size)
        }
    }


}