package com.tamin.taminhamrah.ui.home.services.orotezProtez

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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.local.models.ToolBarStepperModel
import com.tamin.taminhamrah.data.remote.models.services.DependantsModel
import com.tamin.taminhamrah.data.remote.models.services.RequestFile
import com.tamin.taminhamrah.data.remote.models.services.RequestOrthosis
import com.tamin.taminhamrah.data.remote.models.services.ShortTermOrthosisResponse
import com.tamin.taminhamrah.data.remote.models.services.ShorttermRequest
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.data.remote.models.services.orthosisInfoResponse.InsuredOrthosisInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.orthosisInfoResponse.asDomainModel
import com.tamin.taminhamrah.databinding.FragmentOrotezProtezBinding
import com.tamin.taminhamrah.databinding.OrotezStep1Binding
import com.tamin.taminhamrah.databinding.OrotezStep2Binding
import com.tamin.taminhamrah.databinding.OrotezStep3Binding
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
import com.tamin.taminhamrah.utils.extentions.gone
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
class OrotezProtezFragment :
    BaseFragment<FragmentOrotezProtezBinding, OrotezProtezViewModel>(),
    DialogClickInterface.onClickListener, AdapterInterface.OnItemClickListener<UploadedImageModel>,
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    override val mViewModel: OrotezProtezViewModel by viewModels()

    private val listAdapter by lazy { ImagePreviewAdapter(this) }

    val resultImageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result.resultCode == Constants.REQUEST_LUNCHER) {
                    var imageUri: Uri? = null

                    if (result.data != null)
                        imageUri = Uri.parse(result.data?.extras?.getString(Constants.IMAGE_URI))

                    if (!isDuplicateImage(imageUri, mViewModel.fileListUploaded))
                        provideImageForUpload(mViewModel.tempImageType, imageUri)
                    else
                        showAlertDialog(
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
        return R.layout.fragment_orotez_protez
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreate(savedInstanceState)
    }

    override fun getData() {
        mViewModel.getInsuredOrthosisInfo()
    }


    override fun initView() {
        setupToolbarStepper(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null,
        )
    }

    override fun onClick() {
    }

    override fun setupObserver() {
        mViewModel.mldUserInfo.observe(this, ::onUserInfo)
        mViewModel.mldUploadImage.observe(this, ::onUploadImage)
        mViewModel.mldSendOrthosisRequest.observe(this, ::onSendRequest)
    }

    private fun onUserInfo(result: InsuredOrthosisInfoResponse) {
        if (!result.isSuccess) return
        result.data?.apply {
            viewDataBinding?.appBar?.userInfo = ToolBarStepperModel(
                userName = "$insuranceFirstName $insuranceLastName",
                nationalID = nationalCode ?: "-",
                accountNum = bankAccount ?: "-"
            )

            mViewModel.dataModel.shorttermRequest = ShorttermRequest(
                risuid = risuid,
                branchCode = branchCode,
                branchName = branchName,
                insuranceFirstName = insuranceFirstName,
                insuranceLastName = insuranceLastName,
                mobilNumber = mobilNumber,
                nationalCode = nationalCode,
                requestHelpType = "04",
                serviceDateTimeStamp = 0
            )
        }
        initStepper()
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
            if (size >= 4)
                ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(3)) as? OrotezStep3Binding)?.itemAddDoc?.root?.gone()

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
        dialog.show(childFragmentManager, OrotezProtezFragment().javaClass.simpleName)
    }

    override fun chooseImage(requestCode: Int) {
        this@OrotezProtezFragment.lifecycleScope.launchWhenCreated {

            val pager = Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                pagingSourceFactory = { LocalPagingSource(mViewModel.getImageTitleList()) })

            pager.flow.cachedIn(lifecycleScope).collectLatest { pagingData ->
                openImageTypeMenu(pagingData, onResultCallBack = object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        mViewModel.fileListUploaded.apply {
                            for (i in 0 until size) {
                                if (get(i).imageType == itemResult.id) {
                                    removeAt(i)
                                    break
                                }
                            }

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
        requireActivity().onBackPressed()
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
                    R.id.action_orotezProtezFragment_to_ImageViewerActivity,
                    bundle
                )
            }
            Constants.DELETE_IMAGE_TAG -> {
                mViewModel.fileListUploaded.apply {
                    remove(item)
                    listAdapter.removeItem(item)
                    ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(3)) as? OrotezStep3Binding)?.apply {
                        if (size < 4 && !itemAddDoc.root.isVisible)
                            itemAddDoc.root.visible()
                    }
                }
            }
        }
    }

    override fun uploadImage(body: MultipartBody.Part, orgPath: Uri?, imageUri: Uri,requestCode:Int) {
        mViewModel.apply {
            uploadImage(body)
            tempImageOriginalUri = orgPath
            tempImageUri = imageUri
        }
    }


    //step -> select user
    private fun initialStep1() = OrotezStep1Binding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
    ).apply {
        mViewModel.mldUserInfo.value?.data?.asDomainModel()?.get(0)?.apply {
            selectLastBranch.setValue(title ?: "")
        }

        //   selectLastBranch.hideDrawable()
        selectLastBranch.getIt().setOnClickListener {
            showDialog(OrotezDialogType.BRANCH_LIST, getString(R.string.last_branch),
                object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        mViewModel.dataModel.shorttermRequest?.apply {
                            branchCode = itemResult.id
                            branchName = itemResult.title
                        }
                        selectLastBranch.setValue(itemResult.title ?: "")
                    }
                })
        }

        selectInsuranceNum.getIt().setOnClickListener {
            showDialog(OrotezDialogType.INSURED_LIST, getString(R.string.insurance_num),
                object : MenuInterface.OnResult {
                    override fun onResult(itemResult: MenuModel) {
                        if (widgetDatePicker.tilDate.editText?.text?.isNotEmpty() == true)
                            stepperItem.nextStepEnable = true

                        mViewModel.dataModel.shorttermRequest?.risuid =
                            mViewModel.mldUserInfo.value?.data?.risuid

                        selectInsuranceNum.getLayout().isErrorEnabled = false
                        selectInsuranceNum.setValue(itemResult.title ?: "")

                        (itemResult.baseModel as? DependantsModel)?.apply {
                            if (relationShip == itemResult.description) {
                                ((viewDataBinding?.stepper?.getStepLayoutBindingByStep(2))
                                        as? OrotezStep2Binding)?.itemDependant = this

                                mViewModel.dataModel.apply {
                                    useNationalid = nationCode
                                    useRel = relationShipCode
                                    useRelationShip = relationShip
                                    useRfName = risuFname
                                    useRlName = risuLName
                                    useRisuId = risuId
                                }
                            }

                        }
                    }
                })
        }
        widgetDatePicker.inputDate.setOnClickListener {
            val datePickerStart = getDatePicker()
            datePickerStart?.setListener(object : MyPersianPickerListener {
                override fun onDateSelected(@NotNull myPersianPickerDate: MyPersianPickerDate) {
                    if (selectInsuranceNum.getValue().isNotEmpty())
                        stepperItem.nextStepEnable = true
                    widgetDatePicker.tilDate.isErrorEnabled = false
                    val persianMonth = if (myPersianPickerDate.persianMonth in 1..9) {
                        "0${myPersianPickerDate.persianMonth}"
                    } else {
                        "${myPersianPickerDate.persianMonth}"
                    }
                    widgetDatePicker.inputDate.setText("${myPersianPickerDate.persianYear}/$persianMonth/${myPersianPickerDate.persianDay}")
                    mViewModel.dataModel.useTajTimeStamp = myPersianPickerDate.timestamp
                }

                override fun onDismissed() {}
            })
            datePickerStart?.show()
        }
    }

    //step -> info user
    private fun initialStep2() = OrotezStep2Binding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
    )

    //step -> upload document and send request
    private fun initialStep3() = OrotezStep3Binding.inflate(
        LayoutInflater.from(requireContext()), viewDataBinding?.stepper, true
    ).apply {
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
            val stepLayout = listOf(initialStep1(), initialStep2(), initialStep3())
            stepper.initial(stepLayout)
            stepper.onNextStepClickListener = this@OrotezProtezFragment
            stepper.onPreviousStepClickListener = this@OrotezProtezFragment
        }
    }

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.apply {

            when (stepIndex) {
                3 -> {
                    //    (getStepLayoutBindingByStep(3) as? OrotezStep3Binding).apply {
                    mViewModel.fileListUploaded.apply {
                        if (isEmpty() || size == 1) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                getString(R.string.error_select_image)
                            )
                        } else {

                            if ((filter { it.imageType == Constants.DOCTOR_OROTEZ_IMAGE_TYPE }).isNotEmpty()
                                && (filter { it.imageType == Constants.PURCHASE_INVOICE_OROTEZ_IMAGE_TYPE }).isNotEmpty()
                            ) {
                                val list: MutableList<RequestFile> = mutableListOf()
                                forEach {
                                    list.add(
                                        RequestFile(
                                            documentFile = it.guid,
                                            documentType = it.imageType
                                        )
                                    )
                                }
                                mViewModel.dataModel.shorttermRequest?.apply {
                                    requestFileList = list
                                    request = RequestOrthosis()
                                }
                                mViewModel.saveShortTermOrthosis(mViewModel.dataModel)

                            } else {
                                showAlertDialog(
                                    MessageOfRequestDialogFragment.MessageType.ERROR,
                                    getString(R.string.error_add_all_pic_orotez)
                                )
                            }
                        }
                    }
                    //    }
                }
                else -> {
                    nextStep()
                }
            }
        }
    }

    private fun showDialog(
        type: OrotezDialogType,
        title: String,
        resultCallBack: MenuInterface.OnResult
    ) {
        val dialog = MenuDialogFragment.newInstance(true, title).apply {
            setMenuListener(object : MenuInterface.OnFetchData {
                override fun onFetch() {
                    this@OrotezProtezFragment.lifecycleScope.launchWhenCreated {
                        when (type) {
                            OrotezDialogType.BRANCH_LIST -> {
                                mViewModel.mldUserInfo.value?.data?.asDomainModel()
                                    ?.apply {
                                        if (size > 1) {
                                            val pager = Pager(
                                                config = PagingConfig(
                                                    Constants.QUERY_PAGE_SIZE_10,
                                                    2
                                                ),
                                                pagingSourceFactory = { LocalPagingSource(this) })
                                            pager.flow.cachedIn(lifecycleScope).collectLatest {
                                                updateData(it)
                                            }
                                        } else {
                                            dismiss()
                                        }
                                    }
                            }

                            OrotezDialogType.INSURED_LIST -> {
                                mViewModel.getDependantsResponse.collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            id = it.relationShipCode,
                                            title = it.relationShip + "-" + it.risuId,
                                            description = it.relationShip,
                                            baseModel = it
                                        )
                                    }
                                    updateData(result)
                                }
                            }
                        }
                    }
                }

            }, resultCallBack)
        }
        dialog.show(childFragmentManager, "MenuDialogFragment")

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
}
