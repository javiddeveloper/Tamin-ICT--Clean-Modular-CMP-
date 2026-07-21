package com.tamin.taminhamrah.ui.home.services.contracts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.viewbinding.ViewBinding
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.CityNameListResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.ProvinceResponse
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.BranchesInfoListResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.CheckAgeAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.ConcludingStudentInsuranceContractResponse
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.Personal
import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.UpdateAddressInfoRequest
import com.tamin.taminhamrah.databinding.ContractStepBranchBinding
import com.tamin.taminhamrah.databinding.ContractStepUserInfoBinding
import com.tamin.taminhamrah.databinding.FragmentInsuranceContractBinding
import com.tamin.taminhamrah.databinding.GuardianshipContractStepBinding
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumFreelanceStep
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumInsuranceType
import com.tamin.taminhamrah.ui.home.services.contracts.model.EnumOptionalContractStep
import com.tamin.taminhamrah.ui.home.services.studentContract.model.GuardianType
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.Utility.checkPhoneNumber
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import timber.log.Timber
import kotlin.math.abs

abstract class ContractBaseFragment<T> :
    BaseFragment<FragmentInsuranceContractBinding, ContractViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    abstract override val mViewModel: ContractViewModel

    protected val contractType by lazy {
        val arg = arguments?.get(ARG_CONTACT_TYPE)
        (arg as? EnumInsuranceType) ?: (arg as? String)?.let { EnumInsuranceType.valueOf(it) }
            ?: throw Exception("set contract type")
    }

    companion object {
        const val REQUEST_CODE_IMAGE_GUARDIANSHIP = 1001
        const val REQUEST_CODE_IMAGE_DOC = 1002
        const val ARG_CONTACT_TYPE = "ARG_CONTACT_TYPE"
    }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Timber.tag("resultImageLaunch").i("resultCode= ${result.resultCode}")
            try {
                provideImageForUpload(
                    Constants.REQUEST_DEFAULT_IMAGE_TYPE,
                    if (result.data != null) result.data?.extras?.getString(Constants.IMAGE_URI)
                        ?.toUri() else null,
                    result.resultCode
                )

            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.image_upload_error)
                )
            }
        }

    val contractImageListAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(onContractImageClickListener)
    }
    val guardianshipImageListAdapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(onGuardianshipImageClickListener)
    }
    val onContractImageClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
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
                            R.id.action_contract_to_ImageViewerActivity,
                            bundle
                        )
                    }

                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.imageFile = null
                        contractImageListAdapter.clearItems()
                    }
                }
            }

        }
    }
    val onGuardianshipImageClickListener by lazy {
        object : AdapterInterface.OnItemClickListener<UploadedImageModel> {
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
                        handlePageDestination(R.id.action_contract_to_ImageViewerActivity, bundle)
                    }

                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.dataModel.guardianshipImage = null
                        guardianshipImageListAdapter.clearItems()
                    }

                }
            }
        }
    }
    private var restartedFragment = false

    override fun getLayoutId() = R.layout.fragment_insurance_contract

    override fun onStart() {
        super.onStart()
        if (restartedFragment) {
            initStepper(viewDataBinding?.stepper?.currentStepIndex ?: 1)
            restartedFragment = false
        }

    }

    var loadTempPersonalInfo = false

    override fun onStop() {
        restartedFragment = true


        val guardianStep =
            if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_CONTRACT_APPLICANT.stepIndex else EnumFreelanceStep.STEP_CONTRACT_APPLICANT.stepIndex
        (getStep(guardianStep) as? GuardianshipContractStepBinding)?.apply {

        }

        val userInfoStep =
            if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_USER_INFO.stepIndex else EnumFreelanceStep.STEP_USER_INFO.stepIndex

        (getStep(userInfoStep) as? ContractStepUserInfoBinding)?.apply {
            mViewModel.dataModel.tempAddress = inputAddress.getValue(false)
            mViewModel.dataModel.tempZipCode = inputZipCode.getValue(false)
            mViewModel.dataModel.tempPhoneNumber = inputPhoneNumber.getValue(false)
            loadTempPersonalInfo = true
        }
        super.onStop()

    }

    abstract fun getStepsView(): List<ViewBinding>

    open fun initStepper(initialStep: Int = 1) {
        viewDataBinding?.apply {
            stepper.initial(getStepsView(), initialStep)
            stepper.onNextStepClickListener = this@ContractBaseFragment
            stepper.onPreviousStepClickListener = this@ContractBaseFragment
        }
    }

    fun setupToolbarContract() {
        viewDataBinding?.let {
            it.appBar.apply {
                var toolbarTitle = ""
                arguments?.let { args ->
                    ImageUtils.loadImage(imgIcon, Utility.getToolbarIconImage(args))
                    toolbarTitle = Utility.getToolbarTitle(args)
                }
                
                if (toolbarTitle.isBlank()) {
                    toolbarTitle = when (contractType) {
                        EnumInsuranceType.TYPE_STUDENT -> getString(R.string.title_student_contract_fragment)
                        EnumInsuranceType.TYPE_WOMAN -> getString(R.string.title_women_contract_fragment)
                        EnumInsuranceType.TYPE_OPTIONAL -> getString(R.string.title_optional_contract_fragment)
                        EnumInsuranceType.TYPE_FREELANCE -> getString(R.string.title_freelance_contract_fragment)
                        else -> contractType.insuranceName
                    }
                }
                toolbar.imgInfo.visibility = View.GONE
                tvTitle.text = toolbarTitle
                appBarView.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                    val maxScroll = appBarLayout.totalScrollRange
                    val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

                    handleAlphaOnTitle(percentage, containerAppbarTitle)
                    handleToolbarTitleVisibility(
                        percentage,
                        toolbar.tvToolbarTitle,
                        toolbarTitle,
                        it.appbarBackgroundImage.imageBackground,
                        null
                    )
                }
            }
        }
    }

    override fun onClick() {
        viewDataBinding?.btnRegistration?.setOnClickListener {
            requireActivity().onBackPressed()
            val bundle = Bundle()
            bundle.putString(
                Constants.TOOLBAR_TITLE,
                getString(R.string.label_insurance_registration)
            )
            bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, R.drawable.ic_reg)
            handlePageDestination(R.id.action_contractList_to_registration, bundle)

        }
    }

    override fun getData() {
        mViewModel.getRegistrationInfo(contractType == EnumInsuranceType.TYPE_OPTIONAL)
    }

    override fun initView() {
        viewDataBinding?.appBar?.toolbar?.imageBack?.setOnClickListener { backButtonPress() }
        setupToolbarContract()
        mViewModel.dataModel.cntFreeJobCode = when (contractType) {
            EnumInsuranceType.TYPE_WOMAN -> {
                Constants.WOMEN_CONTRACT_CODE
            }

            EnumInsuranceType.TYPE_STUDENT -> {
                Constants.STUDENT_CONTRACT_CODE
            }

            else -> ""
        }
    }

    override fun uploadImage(
        body: MultipartBody.Part,
        orgPath: Uri?,
        imageUri: Uri,
        requestCode: Int
    ) {
        Timber.tag("uploadImageTag").i("requestCode=$requestCode")
        mViewModel.uploadImage(body, requestCode)
        if (requestCode == REQUEST_CODE_IMAGE_DOC)
            mViewModel.dataModel.tempImageUri = imageUri
        else
            mViewModel.dataModel.guardianImageUri = imageUri
    }


    override fun chooseImage(requestCode: Int) {
        val intent = Intent(activity, MultiCustomGalleryUI::class.java)
        intent.putExtra(Constants.TEMPID, Constants.REQUEST_DEFAULT_IMAGE_TYPE)
        intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)
        resultImageLaunch.launch(intent)
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.previousStep()
    }

    var handelNextClick = false

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        handelNextClick = false
        when {
            (contractType == EnumInsuranceType.TYPE_OPTIONAL && stepIndex == EnumOptionalContractStep.STEP_USER_INFO.stepIndex) || (contractType != EnumInsuranceType.TYPE_OPTIONAL && stepIndex == EnumFreelanceStep.STEP_USER_INFO.stepIndex) -> {
                handelNextClick = true
                if (checkValidInput()) {
                    if (changedAddressInfo()) {
                        (getStep(stepIndex) as? ContractStepUserInfoBinding)?.apply {
                            mViewModel.updateUserAddressInfo(
                                updateAddressInfoRequest = UpdateAddressInfoRequest(
                                    cityId = selectCityName.tag?.toString(),
                                    address = inputAddress.getValue(false),
                                    zipCode = inputZipCode.getValue(false),
                                    mobile = inputMobile.getValue(false),
                                    phoneNumber = inputPhoneNumber.getValue(false),
                                    personal = Personal(ssn = mViewModel.mldRegistrationInfo.value?.data?.personalInfo?.ssn)
                                )
                            )

                        }
                    } else {
                        viewDataBinding?.stepper?.nextStep()
                    }
                }
            }

            (contractType == EnumInsuranceType.TYPE_OPTIONAL && stepIndex == EnumOptionalContractStep.STEP_CONTRACT_APPLICANT.stepIndex) || (contractType != EnumInsuranceType.TYPE_OPTIONAL && stepIndex == EnumFreelanceStep.STEP_CONTRACT_APPLICANT.stepIndex) -> {
                handelNextClick = true

                if (mViewModel.dataModel.guardianShip == GuardianType.FOR_ITSELF ||
                    (mViewModel.dataModel.guardianShip == GuardianType.FOR_GUARDIAN && checkGuardianshipInput())
                ) {

                    //fetch data for branch step
                    if (!mViewModel.dataModel.receivedBranchInfo)
                        mViewModel.getAsyncBranchInfo(
                            mViewModel.dataModel.provinceCode,
                            mViewModel.dataModel.cityCode
                        )
                    else
                        viewDataBinding?.stepper?.nextStep()
                }
            }


        }
    }

    protected fun showActiveContractError() {
        showAlertDialog(
            MessageOfRequestDialogFragment.MessageType.INFO,
            getString(R.string.you_have_enabled_contract),
            dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
        )
    }

    protected fun getStep(step: Int): ViewBinding? {
        return viewDataBinding?.stepper?.getStepLayoutBindingByStep(step)
    }

    protected fun showDialog(
        type: ContractDialogType,
        title: String,
        resultCallback: MenuInterface.OnResult
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title).apply {

            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@ContractBaseFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            ContractDialogType.BRANCH_LIST ->
                                mViewModel.getBranchesInfoList(cityCode = mViewModel.dataModel.cityCode)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.code,
                                                title = it.name + "-" + if (it.branchAddress.isNullOrBlank()) "" else it.branchAddress,
                                                description = it.branchAddress
                                            )
                                        }
                                        updateData(result)
                                    }

                            ContractDialogType.CITY_LIST_EMPTY_PROVINCE -> {
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
                            }

                            ContractDialogType.CITY_LIST ->
                                mViewModel.getCitiesList(provinceCode = if (type == ContractDialogType.CITY_LIST) mViewModel.dataModel.provinceCode else null)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.cityCode,
                                                title = it.cityName
                                            )
                                        }
                                        updateData(result)
                                    }

                            ContractDialogType.PROVINCES_LIST ->
                                mViewModel.getProvincesList().collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.provinceCode,
                                            title = it.provinceName
                                        )
                                    }
                                    updateData(result)
                                }

                            ContractDialogType.JOB_TITLES ->
                                mViewModel.getFreelancerJobTitles(jobTitle = "")
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.jobCode,
                                                title = it.jobTitle
                                            )
                                        }
                                        updateData(result)
                                    }
                        }

                    }
                }
            }, resultCallback, object : MenuInterface.OnSearch {
                override fun onSearch(str: String) {
                    this@ContractBaseFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            ContractDialogType.BRANCH_LIST ->
                                mViewModel.getBranchesInfoList(
                                    cityCode = mViewModel.dataModel.cityCode,
                                    branchName = str
                                ).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.code,
                                            title = it.name + "-" + it.branchAddress,
                                        )
                                    }
                                    updateData(result)
                                }

                            ContractDialogType.CITY_LIST_EMPTY_PROVINCE -> {
                                mViewModel.getCityListFlow(str)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                id = it.cityCode,
                                                title = it.cityName
                                            )
                                        }
                                        updateData(result)
                                    }
                            }

                            ContractDialogType.CITY_LIST ->
                                mViewModel.getCitiesList(
                                    provinceCode = if (type == ContractDialogType.CITY_LIST) mViewModel.dataModel.provinceCode else null,
                                    cityName = str
                                ).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.cityCode,
                                            title = it.cityName
                                        )
                                    }
                                    updateData(result)
                                }

                            ContractDialogType.PROVINCES_LIST ->
                                mViewModel.getProvincesList(str).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.provinceCode,
                                            title = it.provinceName
                                        )
                                    }
                                    updateData(result)
                                }

                            ContractDialogType.JOB_TITLES ->
                                mViewModel.getFreelancerJobTitles(
                                    jobTitle = str,
                                ).collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.jobCode,
                                            title = it.jobTitle,
                                        )
                                    }
                                    updateData(result)
                                }
                        }
                    }
                }
            })
        }
        dialog.show(childFragmentManager, (MenuDialogFragment::class.java).simpleName)
    }

    protected fun checkGuardianshipInput(): Boolean {

        var resultOk = false
        val guardianStep =
            if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_CONTRACT_APPLICANT.stepIndex else EnumFreelanceStep.STEP_CONTRACT_APPLICANT.stepIndex
        (getStep(guardianStep) as? GuardianshipContractStepBinding)?.apply {

            when {
                etNationalCode.getValueNationalCode().isBlank() ||
                        etName.getValue().isBlank() ||
                        etGuardianShipNumber.getValue().isBlank() -> {

                    //do nothing--- error shown in getValue method..
                }

                guardianshipDate.inputDate.text.isNullOrBlank() -> {
                    guardianshipDate.tilDate.error =
                        getString(R.string.error_select_guardianship_date)
                }

                etGuardianShipNumber.getValue(false).length < 10 -> {
                    etGuardianShipNumber.getLayout().error =
                        getString(R.string.error_guardianship_number)
                }

                guardianshipDate.inputDate.text?.length!! < 7 -> {
                    guardianshipDate.tilDate.error =
                        getString(R.string.error_select_guardianship_date)
                }

                else -> resultOk = true
            }

            if (mViewModel.dataModel.guardianshipImage == null) {
                resultOk = false
                addGuardianshipDoc.uploadErrorGroup.visibility = View.VISIBLE
            } else {
                addGuardianshipDoc.uploadErrorGroup.visibility = View.GONE
            }

            if (resultOk) {
                mViewModel.dataModel.guardianName = etName.getValue(false)
                mViewModel.dataModel.guardianDate = guardianshipDate.inputDate.text.toString()
                mViewModel.dataModel.guardianNationalId = etNationalCode.getValueNationalCode(false)
                Timber.tag("afterTextChangeDebug")
                    .e(" 2 : nationalId=${mViewModel.dataModel.guardianNationalId}")

                mViewModel.dataModel.guardianNumber = etGuardianShipNumber.getValue(false)
                etGuardianShipNumber.getLayout().isErrorEnabled = false
            }

        }
        return resultOk

    }

    protected fun checkValidInput(): Boolean {
        var message = ""
        val userInfoStep =
            if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_USER_INFO.stepIndex else EnumFreelanceStep.STEP_USER_INFO.stepIndex

        (getStep(userInfoStep) as? ContractStepUserInfoBinding)?.apply {
            if (inputAddress.getValue().isBlank()) {
                message = getString(R.string.error_enter_address)
                inputAddress.setError(message)
                return false
            }
            if (inputZipCode.getValue().length < 10) {
                message = getString(R.string.error_input_length_zip_code)
                inputZipCode.setError(message)
                return false
            }
            if (selectCityName.getValue().isEmpty()) {
                message = getString(R.string.error_input_city_name)
                selectCityName.setError(message)
                return false
            }

            val validationPhoneResult = checkPhoneNumber(inputPhoneNumber.getValue())
            if (validationPhoneResult != 0) {
                inputPhoneNumber.setError(getString(validationPhoneResult))
                return false
            }
            return true
//            when {
//                inputZipCode.getValue().length < 10 -> {
//                    message = getString(R.string.error_input_length_zip_code)
//                    inputZipCode.setError(message)
//                }
////                inputMobile.getValue().length < 11 -> {
////                    message = getString(R.string.error_input_length_mobile)
////                    inputMobile.setError(message)
////                }
//
//
//                inputPhoneNumber.getValue().length < 11 -> {
//                    message = getString(R.string.error_input_length_phone)
//                    inputPhoneNumber.setError(message)
//                }
//
//                !(Pattern.compile("^0\\d[1-9]{2}\\d{7}\$").matcher(inputPhoneNumber.getValue())
//                    .matches()) -> {
//                    message = getString(R.string.error_input_phone)
//                    inputPhoneNumber.setError(message)
//                }
//            }
//            return message == ""
        }
        return false
    }

    protected fun changedAddressInfo(): Boolean {
        val userInfoStep =
            if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_USER_INFO.stepIndex else EnumFreelanceStep.STEP_USER_INFO.stepIndex

        (getStep(userInfoStep) as? ContractStepUserInfoBinding)?.apply {
            return !(mViewModel.dataModel.usersCity == selectCityName.getValue(false) &&
                    mViewModel.dataModel.usersAddress.trim() == inputAddress.getValue(false)
                .trim() &&
                    mViewModel.dataModel.usersZipCode == inputZipCode.getValue(false) &&
//                    mViewModel.dataModel.usersMobile == inputMobile.getValue(false) &&
                    mViewModel.dataModel.usersPhoneNumber == inputPhoneNumber.getValue(false))
        }
        return true
    }

    protected fun createToolbarBundle(item: MenuModel): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.TOOLBAR_TITLE, item.title)
        bundle.putString(Constants.TOOLBAR_SUBTITLE, item.description)
        bundle.putInt(Constants.TOOLBAR_ICON_IMAGE, item.iconRes)
        return bundle
    }

    protected fun onCheckContractCondition(result: CheckAgeAndHistoryResponse) {

        if (result.isSuccess) {
            viewDataBinding?.let { binding ->
                if (result.data == null) {
                    binding.rootLayoutErrorRegister.visibility =
                        View.VISIBLE
                } else {
                    result.data.apply {
                        val overlapMessage = getOverlapError(chkRelolap, chkRelolapMessage)
                        mViewModel.dataModel.provinceCode = provinceCode
                        mViewModel.dataModel.cityCode = city
                        mViewModel.dataModel.brchCodeNew = contract?.branchCode
                        mViewModel.dataModel.eligibilityStatus = eligibilityStatus ?: -1
                        when {
                            insuranceIdState == null ->
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.error_recive_data),
                                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                                )

                            overlapMessage != null ->
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    overlapMessage,
                                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                                )

                            !insuranceIdState -> binding.rootLayoutErrorRegister.visibility =
                                View.VISIBLE

                            contract?.contractStatusObject?.selfIsuContStatCode == 1 -> showActiveContractError()

                            (if (newAge == null || newAge.length < 3) 0 else newAge.take(2)
                                .toInt()) < 18 -> showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.INFO,
                                getString(R.string.you_are_under_18_years_old_its_not_possible_for_provide_offline_services),
                                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                            )

                            hasOtherContract(otherContract) ->
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.you_have_enabled_contract),
                                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                                )

                            checkContractStatus == 2 -> showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                getString(R.string.cancellation_contract_expiration_of_more_than_20_days_from_the_date_of_contract),
                                MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                            )

                            checkContractStatus == 4 || checkContractStatus == 5 ->
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.cancellation_contract_expiration_of_more_than_three_months),
                                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                                )

                            contractType == EnumInsuranceType.TYPE_WOMAN && mViewModel.dataModel.isMan() ->
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.women_contract_error),
                                    MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                                )

                            else -> {
                                initStepper()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun hasOtherContract(raw: Any?): Boolean = raw == null

    private fun getOverlapError(overlapStatus: String?, rawMessage: String?): String? {
        val status = overlapStatus?.trim().orEmpty()
        val isOkStatus = status.isBlank() || status == "1"
        if (isOkStatus) return null
        return rawMessage
    }

    protected fun onRegistrationInfoResult(result: ConcludingStudentInsuranceContractResponse) {
        if (result.isSuccess) {
            result.data?.apply {
                viewDataBinding?.userInfo = this
                viewDataBinding?.appBar?.line2?.visible()
                "${getString(R.string.label_national_code)} : ${personalInfo?.nationalId}".also { viewDataBinding?.appBar?.tvNationalCode?.text = it }
                "${getString(R.string.birthdate)} : ${getPersianDate(personalInfo?.dateOfBirth)}".also { viewDataBinding?.appBar?.tvBirthDate?.text = it }
                "${personalInfo?.firstName ?: ""} ${personalInfo?.lastName ?: ""}".also { viewDataBinding?.appBar?.tvSubTitle?.text = it }

                mViewModel.dataModel.genderCode =
                    result.data?.personalInfo?.gender?.genderCode ?: ""
                mViewModel.dataModel.genderDesc =
                    result.data?.personalInfo?.gender?.genderDesc ?: ""

                mViewModel.dataModel.insuranceId = result.data?.insuranceId ?: ""
                mViewModel.dataModel.firstName = personalInfo?.firstName ?: ""
                mViewModel.dataModel.lastName = personalInfo?.lastName ?: ""
                mViewModel.dataModel.ssn = personalInfo?.ssn ?: ""
                mViewModel.dataModel.nationalId = personalInfo?.nationalId ?: ""


                Timber.tag("onSetDataAgainTag").i("CAlled")
                lastContact?.apply {
                    mViewModel.dataModel.usersAddress = address ?: ""
                    mViewModel.dataModel.usersZipCode = zipCode ?: ""
                    mViewModel.dataModel.usersMobile = mobile ?: ""
                    mViewModel.dataModel.mobileExist = mViewModel.dataModel.usersMobile.isNotBlank()
                    mViewModel.dataModel.usersPhoneNumber = phoneNumber ?: ""
                }
            }
            //   mViewModel.checkContractCondition()
        }
    }

    protected fun onGetBranchInfoResponse(result: BranchesInfoListResponse) {
        val branchList = result.data?.list
        if (result.isSuccess) {
            if (!branchList.isNullOrEmpty()) {
                val step =
                    if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_SELECT_BRANCH.stepIndex else EnumFreelanceStep.STEP_SELECT_BRANCH.stepIndex

                (getStep(step) as? ContractStepBranchBinding)?.apply {
                    mViewModel.dataModel.branchName = branchList[0].name ?: "-"
                    mViewModel.dataModel.branchAddress = branchList[0].branchAddress ?: "-"
                    mViewModel.dataModel.brchCodeNew = branchList[0].code ?: "-"
                    stepperItem.nextStepEnable = true

                    locationSelector.setBranch(
                        mViewModel.dataModel.branchName,
                        mViewModel.dataModel.brchCodeNew
                    )
                }
                mViewModel.dataModel.receivedBranchInfo = true
                viewDataBinding?.stepper?.nextStep()
            }
        }
    }

    protected fun onGetProvinceNameResponse(result: ProvinceResponse) {
        val provinceList = result.data?.list
        if (result.isSuccess && !provinceList.isNullOrEmpty()) {
            val step =
                if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_SELECT_BRANCH.stepIndex else EnumFreelanceStep.STEP_SELECT_BRANCH.stepIndex
            mViewModel.dataModel.provinceName = provinceList[0].provinceName ?: ""
            mViewModel.dataModel.provinceCode = provinceList[0].provinceCode

            (getStep(step) as? ContractStepBranchBinding)?.locationSelector?.setProvince(
                mViewModel.dataModel.provinceName, mViewModel.dataModel.provinceCode
            )

        }
    }

    protected fun onGetCityNameResponse(result: CityNameListResponse) {
        val cityList = result.data?.list
        if (result.isSuccess && !cityList.isNullOrEmpty()) {
            mViewModel.dataModel.cityNameOfBranch = cityList[0].cityName
            mViewModel.dataModel.cityCode = cityList[0].cityCode
            val step =
                if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_SELECT_BRANCH.stepIndex else EnumFreelanceStep.STEP_SELECT_BRANCH.stepIndex

            (getStep(step) as? ContractStepBranchBinding)?.locationSelector?.setCity(
                mViewModel.dataModel.cityNameOfBranch,
                mViewModel.dataModel.cityCode
            )
        }
    }

    protected fun onUpdateUserAddress(result: GeneralRes) {
        if (result.isSuccess) {
            val stepIndex =
                if (contractType == EnumInsuranceType.TYPE_OPTIONAL) EnumOptionalContractStep.STEP_USER_INFO.stepIndex else EnumFreelanceStep.STEP_USER_INFO.stepIndex
            (getStep(stepIndex) as? ContractStepUserInfoBinding)?.apply {
                mViewModel.dataModel.usersCity = selectCityName.getValue(false)
                mViewModel.dataModel.usersAddress = inputAddress.getValue(false)
                mViewModel.dataModel.usersZipCode = inputZipCode.getValue(false)
                mViewModel.dataModel.usersMobile = inputMobile.getValue(false)
                mViewModel.dataModel.usersPhoneNumber = inputPhoneNumber.getValue(false)
            }
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_verify_new_address_info)
            )
            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    val root =
                        (getStep(stepIndex) as? ContractStepUserInfoBinding)?.root
                    val btn = root?.findViewById<AppCompatButton>(R.id.btnNextStepRules)
                    btn?.callOnClick()
                }

                override fun onCancelClick() {
                }

            })

            dialog.show(childFragmentManager, "FilterDialogFragment")
        }
    }

    protected fun onUploadContractImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.imageFile =
                UploadedImageModel(
                    guid = result.guid,
                    imageUri = mViewModel.dataModel.tempImageUri,
                    imageName = mViewModel.dataModel.tempImageName,
                    orgUri = mViewModel.dataModel.tempImageUri
                )
            contractImageListAdapter.setItems(listOf(mViewModel.dataModel.imageFile!!))
        }
    }

    protected fun onUploadGuardianshipImageResponse(result: UploadImageResponse) {
        if (result.isSuccess) {
            mViewModel.dataModel.guardianshipImage =
                UploadedImageModel(
                    guid = result.guid,
                    imageUri = mViewModel.dataModel.guardianImageUri,
                    imageName = mViewModel.dataModel.guardianImageName,
                    orgUri = mViewModel.dataModel.guardianImageUri
                )
            guardianshipImageListAdapter.setItems(listOf(mViewModel.dataModel.guardianshipImage!!))
        }
    }

    override fun setupObserver() {

        mViewModel.mldCheckContractCondition.observe(this, ::onCheckContractCondition)
        mViewModel.mldRegistrationInfo.observe(this, ::onRegistrationInfoResult)



        mViewModel.mldUploadImageContractDoc.observe(this, ::onUploadContractImageResponse)
        mViewModel.mldUploadImageGuardianshipDoc.observe(this, ::onUploadGuardianshipImageResponse)
        mViewModel.mldGetCityWhitOutPaging.observe(this, ::onGetCityNameResponse)
        mViewModel.mldGetProvinceWhitOutPaging.observe(this, ::onGetProvinceNameResponse)
        mViewModel.mldGetInfoOfBranch.observe(this, ::onGetBranchInfoResponse)
        mViewModel.mldSaveUsersAddressInfo.observe(this, ::onUpdateUserAddress)

    }

    abstract fun inflateStepView(step: T): ViewBinding
}
