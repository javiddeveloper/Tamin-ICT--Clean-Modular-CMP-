package com.tamin.taminhamrah.ui.home.services.employer.registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.employer.InsuredDocsResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredSummaryResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserInfoResponse
import com.tamin.taminhamrah.data.remote.models.employer.NewInsuredUserStatusResponse
import com.tamin.taminhamrah.data.remote.models.employer.asDomainModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.WorkshopNewMember
import com.tamin.taminhamrah.databinding.FragmentInsuredRegistrationBinding
import com.tamin.taminhamrah.databinding.InsuredRegistrationStepConfirmBinding
import com.tamin.taminhamrah.databinding.InsuredRegistrationStepUploadDocsBinding
import com.tamin.taminhamrah.databinding.InsuredRegistrationStepUserInfoBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface.OnItemClickListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.HelperDate
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import com.tamin.taminhamrah.widget.DatePickerWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.util.Date
import kotlin.math.abs

@AndroidEntryPoint
class InsuredRegistrationFragment :
    BaseFragment<FragmentInsuredRegistrationBinding, InsuredRegistrationViewModel>(),
    OnItemClickListener<UploadedImageModel>, StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    companion object {
        const val ARG_WORKSHOP_ID = "ARG_WORKSHOP_ID"
        const val ARG_PERSONAL_REQUEST_ID = "ARG_PERSONAL_REQUEST_ID"
        const val ARG_ORGANIZATION_ID = "ARG_ORGANIZATION_ID"
    }
////////////////////////////////////////////////////////////////////////////////// Class Variables ////////////////////////////////////

    override val mViewModel: InsuredRegistrationViewModel by viewModels()

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Constants.REQUEST_LUNCHER) {
                    //                     val imageUri = Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))
                    var imageUri: Uri? = null

                    if (result.data != null) {
                        imageUri =
                            Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))
                    }
                    provideImageForUpload(Constants.REQUEST_DEFAULT_IMAGE_TYPE, imageUri)
                }

            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.image_upload_error)
                )
            }
        }

    val imageListAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(this)
    }

////////////////////////////////////////////////////////////////////////////////// Class  Variables////////////////////////////////////

    override fun getBindingVariable(): Pair<Int, Any?> = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_insured_registration

    override fun setupObserver() {
        mViewModel.apply {
            mldInsuredUserInfo.observe(this@InsuredRegistrationFragment, ::onUserInfo)
            mldUserIsNew.observe(this@InsuredRegistrationFragment, ::isNewUser)
            mldUploadImage.observe(this@InsuredRegistrationFragment, ::onUploadImageResponse)
            mldSummary.observe(this@InsuredRegistrationFragment, ::onSummaryResponse)
            mldRecentlyAddedMemberInfo.observe(
                this@InsuredRegistrationFragment,
                ::onGetRecentlyAddedInfo
            )
            mldInsuredDocs.observe(this@InsuredRegistrationFragment, ::onGetInsuredDoc)
            mldIPutInsuredDocs.observe(this@InsuredRegistrationFragment, ::onPutInsuredDoc)
            mldPDfDownload.observe(this@InsuredRegistrationFragment, ::onDownloadForm)
        }

    }

    private fun onDownloadForm(result: PdfDownloadResponse?) {
        if (result?.isSuccess == true) {

            val file = Utility.writeByteStreamToDisk(
                "فرم اظهارنامه نامنویسی",
                requireContext(),
                result.pdf
            )
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            } else {
                Utility.copyFileToDownloads(file, requireContext(), "فرم اظهارنامه نامنویسی")
            }

            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.CONFIRM,
                getString(R.string.message_success_download_employer_agreement_form)
            )
        }
    }

    private fun onPutInsuredDoc(result: GeneralRes?) {
        if (result?.isSuccess == true)
            if (mViewModel.mldRecentlyAddedMemberInfo.value == null) {
                // new user
                mViewModel.getSummary(mViewModel.mldInsuredUserInfo.value?.data?.request?.id)
            } else {
                //edit
                mViewModel.getSummary(mViewModel.mldRecentlyAddedMemberInfo.value?.personal?.request?.id)
            }
    }

    private fun onGetInsuredDoc(result: InsuredDocsResponse?) {
        if (result?.isSuccess == true) {
            if (mViewModel.tempUserInfoModel.imageFileList == null)
                mViewModel.tempUserInfoModel.imageFileList = ArrayList()
            result.data?.list?.asDomainModel()?.forEach {
                mViewModel.tempUserInfoModel.imageFileList?.add(it)
            }
            imageListAdapter.clearItems()
            imageListAdapter.setItems(mViewModel.tempUserInfoModel.imageFileList!!)
        }
    }

    private fun onGetRecentlyAddedInfo(result: WorkshopNewMember?) {
        initStepper()
    }

    private fun onSummaryResponse(result: NewInsuredSummaryResponse?) {
        if (result?.isSuccess == true) {
            (viewDataBinding?.stepper?.getStepLayoutBindingByStep(3) as? InsuredRegistrationStepConfirmBinding)?.apply {
                tvRelationShip.text = result.data?.relationDescription ?: "-"
                tvRelationShipStartDate.text =
                    if (result.data?.relationWithTamin?.dateOfStart != null)
                        ConvertDate.convertTimestampToPersianDate(result.data?.relationWithTamin?.dateOfStart!!)
                    else "-"
                tvBranchName.text = result.data?.relationWithTamin?.organizationId ?: "-"
            }
            viewDataBinding?.stepper?.nextStep()
        }
    }

    private fun onUploadImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            if (mViewModel.tempUserInfoModel.imageFileList == null)
                mViewModel.tempUserInfoModel.imageFileList = ArrayList()
            mViewModel.tempUserInfoModel.imageFileList?.add(
                UploadedImageModel(
                    guid = result.guid,
                    imageUri = mViewModel.tempUserInfoModel.tempImageUri,
                    imageName = mViewModel.tempUserInfoModel.tempImageName,
                    orgUri = mViewModel.tempUserInfoModel.tempImageUri,
                    imageType = mViewModel.tempUserInfoModel.tempImageType
                )
            )
            mViewModel.tempUserInfoModel.tempImageName = null
            mViewModel.tempUserInfoModel.tempImageUri = null
            mViewModel.tempUserInfoModel.tempImageType = null
            mViewModel.tempUserInfoModel.imageFileList?.let { imageListAdapter.setItems(it) }
        }
    }

    private fun isNewUser(result: NewInsuredUserStatusResponse?) {
        if (result?.isSuccess == true) {
            viewDataBinding?.stepper?.nextStep()
        }
    }

    private fun onUserInfo(result: NewInsuredUserInfoResponse?) {
        if (result?.isSuccess == true) {
            mViewModel.tempUserInfoModel.personal.id = result.data?.id
            mViewModel.checkUserIsNew(mViewModel.tempUserInfoModel.personal.nationalId)
        }
    }

    override fun initView() {
        viewDataBinding?.appBar?.toolbar?.imageBack?.setOnClickListener { backButtonPress() }
        setupToolbarContract()

        /* parentFragmentManager.addOnBackStackChangedListener {
             if (parentFragmentManager.backStackEntryCount == 0) {

                 val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                 dialog.arguments = createBundle(
                     MessageOfRequestDialogFragment.MessageType.WARNING,
                     getString(R.string.label_are_you_sure_to_exit_from_this_page), btnCancel = true
                 )
                 dialog.setDialogClickListener(object :
                     DialogClickInterface.onClickListener {
                     override fun onConfirmClick() {
                         requireActivity().onBackPressed()
                     }

                     override fun onCancelClick() {
                         dialog.dismiss()
                     }
                 }
                 )
                 dialog.show(childFragmentManager, "ExitFromThisPage")
             }
         }*/
    }

    override fun getData() {

        if (arguments?.getLong(ARG_PERSONAL_REQUEST_ID) != null &&
            arguments?.getLong(ARG_PERSONAL_REQUEST_ID)!! > 0
        ) {
            mViewModel.getRecentlyAddedUser(arguments?.getLong(ARG_PERSONAL_REQUEST_ID))

        } else {
            initStepper()
        }
    }

    override fun onClick() {
        viewDataBinding?.btnRegistrationForm?.setOnClickListener {
            mViewModel.getRegistrationDeclarationForm()
        }
    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int
    ) {
        mViewModel.uploadImage(body)
        mViewModel.tempUserInfoModel.tempImageUri = imageUri
    }

    private val organizationId by lazy { arguments?.getString(ARG_ORGANIZATION_ID) }
    private val workshopId by lazy { arguments?.getString(ARG_WORKSHOP_ID) }

    private fun initialStepUserInfo() = InsuredRegistrationStepUserInfoBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper,
        true
    ).apply {

        inputNationalId.getInput().doOnTextChanged { text, _, _, _ ->
            mViewModel.tempUserInfoModel.personal.nationalId = text.toString()
        }
        inputName.getInput().doOnTextChanged { text, _, _, _ ->
            mViewModel.tempUserInfoModel.personal.firstName = text.toString()
        }
        inputLastName.getInput().doOnTextChanged { text, _, _, _ ->
            mViewModel.tempUserInfoModel.personal.lastName = text.toString()
        }

        widgetBirthDate.setListener(object : DatePickerWidget.DateSelectOrListener {
            override fun onDateSelect(
                jalaliDate: String,
                gregorianDate: Date,
                timeStamp: Long,
                serverFormattedDate: String,
                serverFormattedDateWithDayOffset: String
            ) {
                mViewModel.tempUserInfoModel.personal.dateOfBirth = serverFormattedDateWithDayOffset
            }
        })

        widgetJobStartDate.setListener(object : DatePickerWidget.DateSelectOrListener {
            override fun onDateSelect(
                jalaliDate: String,
                gregorianDate: Date,
                timeStamp: Long,
                serverFormattedDate: String,
                serverFormattedDateWithDayOffset: String
            ) {
                mViewModel.tempUserInfoModel.relationWithTamin.dateOfStart =
                    serverFormattedDateWithDayOffset
            }
        })
        selectCityOfBirth.getIt().setOnClickListener {
            showDialog(InsuredRegistrationDialogType.CITY_LIST,
                getString(R.string.birth_city),
                object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        mViewModel.tempUserInfoModel.personal.cityOfBirthId =
                            itemResult.id ?: ""
                        mViewModel.tempUserInfoModel.selectedCityOfBirth = itemResult.title
                        selectCityOfBirth.setValue(itemResult.title ?: "")
                    }
                })
        }

        selectCityOfIssue.getIt().setOnClickListener {
            showDialog(InsuredRegistrationDialogType.CITY_LIST,
                getString(R.string.issue_city),
                object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        mViewModel.tempUserInfoModel.personal.cityOfIssueId =
                            itemResult.id ?: ""
                        mViewModel.tempUserInfoModel.selectedCityOfIssue = itemResult.title
                        selectCityOfIssue.setValue(itemResult.title ?: "")
                    }
                })
        }

        selectJob.getIt().setOnClickListener {
            showDialog(InsuredRegistrationDialogType.JOB_TITLES,
                getString(R.string.label_job),
                object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        mViewModel.tempUserInfoModel.relationWithTamin.job =
                            itemResult.id ?: ""
                        mViewModel.tempUserInfoModel.selectedjob = itemResult.title
                        selectJob.setValue(itemResult.title ?: "")
                    }
                })
        }

        mViewModel.tempUserInfoModel.apply {
            inputNationalId.getInput().setText(personal.nationalId ?: "")
            inputName.getInput().setText(personal.firstName ?: "")
            inputLastName.getInput().setText(personal.lastName)
            selectCityOfBirth.getIt().setText(selectedCityOfBirth)
            selectCityOfIssue.getIt().setText(selectedCityOfIssue)
            selectJob.getIt().setText(selectedjob)
            try {
                widgetBirthDate.setLocalDate(personal.dateOfBirth)
                widgetJobStartDate.setLocalDate(mViewModel.tempUserInfoModel.relationWithTamin.dateOfStart)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    private fun initialStepUploadDocs() = InsuredRegistrationStepUploadDocsBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper,
        true
    ).apply {
        layoutUploadImage.recycler.apply {
            this.adapter = imageListAdapter
            if (itemDecorationCount == 0) {
                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
            }
        }

//        mViewModel.tempUserInfoModel.imageFileList?.let { imageListAdapter.setItems(it) }

        layoutUploadImage.btnAddDocument.setOnClickListener {
            view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

            chooseImage()
        }
    }

    private fun initialStepConfirm() = InsuredRegistrationStepConfirmBinding.inflate(
        LayoutInflater.from(requireContext()),
        viewDataBinding?.stepper,
        true
    ).apply {
        stepperReview.nextButtonTitle = getString(R.string.label_confirm_return)
    }

    private fun initStepper(initialStep: Int = 1) {
        Timber.tag("debugReturnPage").i("initStepper: ")
        viewDataBinding?.apply {
            val stepLayouts = listOf(
                initialStepUserInfo(),
                initialStepUploadDocs(),
                initialStepConfirm()
            )
            // mViewModel.mldAddedUser.value?.needToCalculate = true
            stepper.initial(stepLayouts, initialStep)
            stepper.onNextStepClickListener = this@InsuredRegistrationFragment
            stepper.onPreviousStepClickListener = this@InsuredRegistrationFragment
        }
    }

    //////////////////////////////////////////////////////////////////////////////////Call Backs ////////////////////////////////////
    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.apply {
            when (stepIndex) {
                1 -> {
                    if (hasValidData()) {
                        if (mViewModel.mldRecentlyAddedMemberInfo.value == null) {
                            //new user
                            if (mViewModel.mldInsuredUserInfo.value == null || mViewModel.mldInsuredUserInfo.value?.isSuccess == false) {
                                mViewModel.tempUserInfoModel.relationWithTamin.apply {
                                    organizationId = this@InsuredRegistrationFragment.organizationId
                                    workshopId = this@InsuredRegistrationFragment.workshopId
                                    mViewModel.postNewInsuredInfo()
                                }
                            } else if (userInfoIsChanged()) {
                                mViewModel.updateNewInsuredInfo(mViewModel.mldInsuredUserInfo.value?.data?.request?.id)
                            } else {
                                nextStep()
                            }
                        } else {
                            //edit
                            if (userInfoIsChanged()) {
                                mViewModel.updateNewInsuredInfo(mViewModel.mldRecentlyAddedMemberInfo.value?.personal?.id)
                            } else {
                                nextStep()
                            }
                        }
                    }

                }
                2 -> {
                    //if (imageListIsChanged()) {
                    mViewModel.putInsuredRegistrationDocList(
                        mViewModel.tempUserInfoModel.personal.id,
                        mViewModel.tempUserInfoModel.imageFileList
                    )
                    //   }
                }
                3 -> requireActivity().onBackPressed()
                else -> {
                    nextStep()
                }
            }
        }
    }

    private fun hasValidData(): Boolean {
        var isValidStep = true
        (viewDataBinding?.stepper?.getStepLayoutBindingByStep(1) as? InsuredRegistrationStepUserInfoBinding)?.apply {
            if (mViewModel.tempUserInfoModel.personal.firstName.isNullOrBlank()) {
                isValidStep = false
                inputName.setError(getString(R.string.error_fill_fields))
            } else
                if (mViewModel.tempUserInfoModel.personal.lastName.isNullOrBlank()) {
                    isValidStep = false
                    inputLastName.setError(getString(R.string.error_fill_fields))
                } else
                    if (mViewModel.tempUserInfoModel.personal.nationalId.isNullOrBlank()) {
                        isValidStep = false
                        inputNationalId.setError(getString(R.string.error_fill_fields))
                    } else
                        if (mViewModel.tempUserInfoModel.personal.dateOfBirth.isNullOrBlank()) {
                            isValidStep = false
                            widgetBirthDate.setError(getString(R.string.error_fill_fields))
                        } else
                            if (mViewModel.tempUserInfoModel.personal.cityOfBirthId.isNullOrBlank()) {
                                isValidStep = false
                                selectCityOfBirth.setError(getString(R.string.error_fill_fields))
                            } else
                                if (mViewModel.tempUserInfoModel.personal.cityOfIssueId.isNullOrBlank()) {
                                    isValidStep = false
                                    selectCityOfIssue.setError(getString(R.string.error_fill_fields))
                                } else
                                    if (mViewModel.tempUserInfoModel.selectedjob.isNullOrBlank()) {
                                        isValidStep = false
                                        selectJob.setError(getString(R.string.error_fill_fields))
                                    } else
                                        if (mViewModel.tempUserInfoModel.relationWithTamin.dateOfStart.isNullOrBlank()) {
                                            isValidStep = false
                                            widgetJobStartDate.setError(getString(R.string.error_fill_fields))
                                        }

        }
        return isValidStep
    }

    /*   private fun imageListIsChanged(): Boolean {
           if (mViewModel.tempUserInfoModel.imageFileList?.size != mViewModel.mldInsuredDocs.value?.data?.list?.size)
               return true
           else if (mViewModel.tempUserInfoModel.imageFileList?.size == 0) return true
           else
               mViewModel.tempUserInfoModel.imageFileList?.forEach {tempImage->
                   mViewModel.mldInsuredDocs.value?.data?.list?.forEach {uploadedImage->
                       if (tempImage.guid == uploadedImage. )
                   }

               }



       }*/

    private fun userInfoIsChanged(): Boolean {
        //can not edit user info before registration

        mViewModel.mldInsuredUserInfo.value?.data?.let { orgData ->
            mViewModel.tempUserInfoModel.let { tempData ->
                return !(tempData.personal.firstName == orgData.firstName &&
                        tempData.personal.lastName == orgData.lastName &&
                        tempData.personal.cityOfBirthId == orgData.cityOfBirthId &&
                        tempData.personal.cityOfIssueId == orgData.cityOfIssueId &&
                        HelperDate.convertStringToDate(
                            tempData.personal.dateOfBirth ?: "", "yyyy-MM-dd'T'HH:mm:ss.sss"
                        )?.time == orgData.dateOfBirth)
            }
        }

        return false
    }

    override fun onPreviousStepClickListener(
        stepIndex: Int,
        step: VerticalStepperItemView
    ) {
        viewDataBinding?.stepper?.previousStep()
    }

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
                    R.id.action_insured_registration_to_ImageViewerActivity,
                    bundle
                )
            }
            Constants.DELETE_IMAGE_TAG -> {
                val deletedItem =
                    mViewModel.tempUserInfoModel.imageFileList?.filter { it.guid == item.guid }

                deletedItem?.forEach {
                    mViewModel.tempUserInfoModel.imageFileList?.remove(it)
                }


                mViewModel.tempUserInfoModel.imageFileList?.let {
                    imageListAdapter.clearItems()
                    imageListAdapter.setItems(it)
                }

            }
        }
    }
////////////////////////////////////////////////////////////////////Utils /////////////////////////////////////////////////////

    override fun chooseImage(requestCode: Int) {
        this@InsuredRegistrationFragment.lifecycleScope.launchWhenCreated {
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
                                var duplicatePic = false
                                mViewModel.tempUserInfoModel.imageFileList?.forEach {
                                    if (it.imageName == itemResult.title) {
                                        duplicatePic = true
                                        showAlertDialog(
                                            MessageOfRequestDialogFragment.MessageType.ERROR,
                                            getString(R.string.error_select_repeat_pic)
                                        )
                                    }
                                }
                                if (!duplicatePic) {
                                    mViewModel.tempUserInfoModel.tempImageName = itemResult.title
                                    mViewModel.tempUserInfoModel.tempImageType = itemResult.id
                                    val intent = Intent(activity, MultiCustomGalleryUI::class.java)
                                    intent.putExtra(Constants.TEMPID, itemResult.id)
                                    intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)

                                    resultImageLaunch.launch(intent)
                                }
                            }
                        })
                }
        }
    }

    fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

    fun setupToolbarContract() {
        viewDataBinding?.let {
            it.appBar.apply {


                ImageUtils.loadImage(imgIcon, Utility.getToolbarIconImage(arguments))
                tvTitle.text = Utility.getToolbarTitle(arguments)
                tvSubTitle.text = Utility.getToolbarSubTitle(arguments)
                tvSubSubTitle.text = Utility.getToolbarSub2(arguments)
                tvSubSubTitle.visibility = View.VISIBLE

                toolbar.imgInfo.visibility = View.GONE

                appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    val maxScroll = appBarLayout.totalScrollRange
                    val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

                    handleAlphaOnTitle(percentage, containerAppbarTitle)
                    handleToolbarTitleVisibility(
                        percentage,
                        toolbar.tvToolbarTitle,
                        Utility.getToolbarTitle(arguments),
                        it.appbarBackgroundImage.imageBackground,
                        null
                    )
                })
            }
        }
    }

    private fun showDialog(
        type: InsuredRegistrationDialogType,
        title: String,
        resultCallback: MenuInterface.OnResult
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title).apply {

            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@InsuredRegistrationFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            InsuredRegistrationDialogType.CITY_LIST ->
                                mViewModel.getCityListFlow()
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.cityCode,
                                                title = it.cityName
                                            )
                                        }
                                        updateData(result)
                                    }
                            InsuredRegistrationDialogType.JOB_TITLES ->
                                mViewModel.getJob()
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.jobCode,
                                                title = it.jobDescription
                                            )
                                        }
                                        updateData(result)
                                    }
                        }

                    }
                }
            }, resultCallback, object : MenuInterface.OnSearch {
                override fun onSearch(str: String) {
                    this@InsuredRegistrationFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            InsuredRegistrationDialogType.CITY_LIST ->
                                mViewModel.getCityListFlow(str).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.cityCode,
                                            title = it.cityName,
                                        )
                                    }
                                    updateData(result)
                                }
                            InsuredRegistrationDialogType.JOB_TITLES ->
                                mViewModel.getJob(str).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.jobCode,
                                            title = it.jobDescription,
                                        )
                                    }
                                    updateData(result)
                                }
                        }
                    }
                }
            })
        }
        dialog.show(childFragmentManager, MenuDialogFragment::javaClass.name)
    }


    override fun onDestroy() {
        val folder =
            File("${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/tempImage/")
        folder.deleteRecursively()
        super.onDestroy()

    }


}
