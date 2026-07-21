package com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.mafasahesab

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.AppBarLayout
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedFileModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.contract.ContractInfo
import com.tamin.taminhamrah.databinding.FragmentRegisterMafasahesabBinding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractConditionBinding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractInfoBinding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractTerm01Binding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractTerm02Binding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractTerm03Binding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractTerm04050607Binding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractTerm11Binding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractTerm13Binding
import com.tamin.taminhamrah.databinding.MafasahesabStepContractTerm29Binding
import com.tamin.taminhamrah.databinding.MafasahesabStepLetterInfoBinding
import com.tamin.taminhamrah.databinding.MafasahesabStepUploadDocBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.ViewerImageActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.PermissionMessageDialog
import com.tamin.taminhamrah.ui.dialog.StoragePermissionGetImageTextProvider
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.ImagePreviewAdapter
import com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.EnumMafasaHesabStep
import com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.EnumMafasaHesabStep.STEP_CONTRACT_INFO
import com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.EnumMafasaHesabStep.STEP_CONTRACT_STATUS_BY_SUBJECT
import com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.EnumMafasaHesabStep.STEP_LETTER_INFO
import com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.EnumMafasaHesabStep.STEP_UPLOAD_IMAGE
import com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract.EnumMafasaHesabStep.values
import com.tamin.taminhamrah.ui.home.services.employer.contract.model.DocumentInfo
import com.tamin.taminhamrah.ui.imagePicker.MultiCustomGalleryUI
import com.tamin.taminhamrah.utils.ImageUtils
import com.tamin.taminhamrah.utils.NumberTextWatcherForThousand
import com.tamin.taminhamrah.utils.NumberTextWatcherForThousand.trimCommaOfString
import com.tamin.taminhamrah.utils.PickImageUtils
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.compression.utils.FileUriUtils
import com.tamin.taminhamrah.utils.extentions.getFileName
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import com.tamin.taminhamrah.utils.extentions.visible
import com.tamin.taminhamrah.utils.stepperView.StepperLayout
import com.tamin.taminhamrah.utils.stepperView.VerticalStepperItemView
import com.tamin.taminhamrah.widget.DatePickerWidget
import com.tamin.taminhamrah.widget.edittext.SelectableItemView
import com.tamin.taminhamrah.widget.edittext.number.EditTextNumber
import com.tamin.taminhamrah.widget.edittext.string.EditTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.util.Date
import kotlin.math.abs

@AndroidEntryPoint
class MafasaHesabRegisterFragment :
    BaseFragment<FragmentRegisterMafasahesabBinding, MafasaHesabInfoViewModel>(),
    StepperLayout.NextStepClickListener,
    StepperLayout.PreviousStepClickListener {

    private var requestCode = 0
    override val mViewModel: MafasaHesabInfoViewModel by viewModels()
/*
    class ResultCallback : ActivityResultContract<String, String>() {

        override fun createIntent(context: Context, input: String): Intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                putExtra(Constants.REQUEST_CODE_TAG, input)
            }

        override fun parseResult(resultCode: Int, intent: Intent?): String = when {
            resultCode != Activity.RESULT_OK -> ""
            else -> {
                intent?.getStringExtra("data") ?: ""
            }
        }
    }*/

    val resultImageLaunch =
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

    var pdfType = 0

    private val resultPDFLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Timber.tag("resultPDFLaunch").i("resultCode= ${result.resultCode}")
            try {
                val uri = result.data?.dataString?.toUri()
                if (uri == null) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.file_upload_error)
                    )
                    return@registerForActivityResult
                }
                val fileName = getFileName(uri)
                val filePath = FileUriUtils.getRealPath(requireContext(), uri)
                if (filePath.isNullOrBlank()) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.file_upload_error)
                    )
                    return@registerForActivityResult
                }
                val pdfFile = File(filePath)

                val requestFile: RequestBody =
                    pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "file",
                    pdfFile.name,
                    requestFile
                )

                mViewModel.uploadPdfFile(body, pdfType, uri, fileName)

            } catch (e: Exception) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.file_upload_error)
                )
                e.localizedMessage?.let {
                    Timber.tag("handleImageRequest: ").e("Error%s", it.toString())
                }
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            try {
                val permissionDialog = PermissionMessageDialog()
                permissions.entries.forEach { perm ->
                    when (perm.key) {
                        "android.permission.READ_EXTERNAL_STORAGE", "android.permission.READ_MEDIA_IMAGES" -> {
                            if (!perm.value) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    permissionDialog.showPermissionDialog(
                                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                            perm.key
                                        ),
                                        permissionTextProvider = StoragePermissionGetImageTextProvider(),
                                        onCancelClicked = {},
                                        onOkClicked = {},
                                    )
                                    permissionDialog.createDialog()?.show(childFragmentManager,"permissionDialog")
                                } else {
                                    requireActivity().onBackPressed()
                                }
                            } else {
                                resultPDFLaunch.launch(getPdfFromFile(requestCode))
                            }
                        }
                        Manifest.permission.WRITE_EXTERNAL_STORAGE ->{

                        }
                    }
                }
            } catch (e: Exception) {
                Timber.tag("permissionLauncher").v("permissionLauncher exception = " + e)
            }
        }



    private val imageListAdapter: ImagePreviewAdapter by lazy {
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
                            R.id.action_registerMafasa_to_image_preview,
                            bundle
                        )
                    }

                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.imageFileList.remove(item)
                        imageListAdapter.setItems(mViewModel.imageFileList)
                    }
                }
            }

        })
    }
    private val pdfListAdapter: PDFPreviewAdapter by lazy {
        PDFPreviewAdapter(object : AdapterInterface.OnItemClickListener<UploadedFileModel> {
            override fun onItemClick(
                item: UploadedFileModel,
                transitionView: View?,
                tag: String?
            ) {
                when (tag) {
                    Constants.PDF_PREVIEW_TAG -> {

                        val bundle = Bundle()
                        bundle.putString(
                            PdfViewerActivity.ARG_TITLE,
                            Utility.getToolbarTitle(arguments)
                        )
                        bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, item.fileUri?.path)
                        handlePageDestination(R.id.action_registerMafasa_to_pdf_preview, bundle)
                    }

                    Constants.PDF_DELETE_TAG -> {
                        mViewModel.pdfFileList.remove(item)
                        pdfListAdapter.setItems(mViewModel.pdfFileList)
                    }
                }
            }

        })
    }


    private val imageListTerm1Adapter: ImagePreviewAdapter by lazy {
        ImagePreviewAdapter(object :
            AdapterInterface.OnItemClickListener<UploadedImageModel> {
            override fun onItemClick(
                item: UploadedImageModel,
                transitionView: View?,
                tag: String?
            ) {
                when (tag) {
                    Constants.IMAGE_PREVIEW_TAG -> {
                        val bundle = Bundle()
                        bundle.putString(
                            ViewerImageActivity.TITLE_IMAGE,
                            item.imageName
                        )
                        bundle.putString(
                            ViewerImageActivity.URI_IMAGE,
                            item.imageUri.toString()
                        )
                        handlePageDestination(
                            R.id.action_registerMafasa_to_image_preview,
                            bundle
                        )
                    }

                    Constants.DELETE_IMAGE_TAG -> {
                        mViewModel.imageFileList.remove(item)
                        imageListAdapter.setItems(mViewModel.imageFileList)
                    }
                }
            }

        })
    }

    val contract by lazy { arguments?.getParcelable(mViewModel.ARG_CONTRACT) as? ContractInfo }

    override fun getBindingVariable() = Pair(BR.viewModel, mViewModel)

    override fun getLayoutId() = R.layout.fragment_register_mafasahesab

    override fun setupObserver() {
        mViewModel.mldUploadImage.observe(this, ::onUploadImage)
        mViewModel.mldUploadPdfFile.observe(this, ::onUploadPdf)
        mViewModel.mldRegister.observe(this, ::onRegisterMafasaHesab)
    }

    private fun onRegisterMafasaHesab(result: GeneralRes?) {
        if (result?.isSuccess == true)
            showAlertDialog(
                MessageOfRequestDialogFragment.MessageType.CONFIRM,
                getString(R.string.message_success_send_info),
                dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
            )
    }

    override fun initView() {
        viewDataBinding?.appBar?.toolbar?.imageBack?.setOnClickListener { backButtonPress() }
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
        initStepper()
    }

    override fun getData() {


    }

    override fun onClick() {

    }

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

    private fun onUploadPdf(result: UploadedFileModel) {
        mViewModel.pdfFileList.add(result)
        pdfListAdapter.setItems(mViewModel.pdfFileList)
    }

    private fun onUploadImage(result: UploadedImageModel) {

        if (result.imageType == mViewModel.image_request_code_term_1.toString()) {
            mViewModel.dataModel.subjectimageId = result.guid ?: ""
            imageListTerm1Adapter.setItems(listOf(result))

        } else {
            mViewModel.imageFileList.add(result)
            imageListAdapter.setItems(mViewModel.imageFileList)
        }
    }

    private fun initStepper(initialStep: Int = 1) {

        val stepLayout = ArrayList<ViewBinding>()
        viewDataBinding?.apply {

            values().forEach {
                stepLayout.add(inflateView(it))

            }
            stepper.initial(stepLayout, initialStep)
            stepper.onNextStepClickListener = this@MafasaHesabRegisterFragment
            stepper.onPreviousStepClickListener = this@MafasaHesabRegisterFragment
        }
    }

    override fun onNextStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.apply {
            when (stepIndex) {
                STEP_CONTRACT_INFO.step -> {
                    nextStep()
                }

                STEP_LETTER_INFO.step -> {
                    if (stepLetterInfoHasValidData(getStep(stepIndex) as? MafasahesabStepLetterInfoBinding))
                        nextStep()
                }

                STEP_UPLOAD_IMAGE.step -> {
                    if (updateDocsAndCheckSize())
                        nextStep()
                    else
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            getString(R.string.error_select_image_2)
                        )
                }

                STEP_CONTRACT_STATUS_BY_SUBJECT.step -> {
                    if (lastStepIsValid(((getStep(stepIndex) as? MafasahesabStepContractConditionBinding)))) {
                        mViewModel.dataModel.contractorWorkshopId =
                            contract?.workshop?.workshopId ?: ""
                        mViewModel.sendRegisterRequest(
                            contract?.workshop?.workshopId,
                            contract?.contractRow,
                            contract?.branch?.code,
                            contract?.contractSequence
                        )
                    }
                }
            }
        }
    }

    private fun lastStepIsValid(vb: MafasahesabStepContractConditionBinding?): Boolean {

        var isValid = true

        if (mViewModel.dataModel.contractsubjectcode.isBlank()) {
            isValid = false
            vb?.selectCondition?.setError(getString(R.string.error_fill_fields))
        } else
            vb?.containerConditions?.apply {

                when (tag) {
                    "01" -> {
                        if (mViewModel.dataModel.subjectOwner.isBlank()) {
                            findViewById<SelectableItemView>(R.id.selectContractByPrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                        if (mViewModel.dataModel.subjecttext1.isBlank()) {
                            findViewById<EditTextString>(R.id.inputPlanCredit)
                                .setError(getString(R.string.error_fill_fields))

                            isValid = false
                        }
                        if (mViewModel.dataModel.subjecttext2.isNullOrBlank()) {
                            findViewById<EditTextString>(R.id.inputBudgetRow)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                        if (mViewModel.dataModel.subjectamount1.isNullOrBlank()) {
                            findViewById<EditTextNumber>(R.id.inputInsurancePrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                    }

                    "02" -> {

                        if (mViewModel.dataModel.subjectOwner.isBlank()) {
                            findViewById<SelectableItemView>(R.id.selectSupplyResponsibility)
                                .setError(getString(R.string.error_fill_fields))
                        } else if (mViewModel.dataModel.subjectOwner == "3" &&
                            mViewModel.dataModel.subjectamount1.isNullOrBlank()
                        ) {
                            findViewById<EditTextNumber>(R.id.inputPrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                    }

                    "03" -> {

                        if (mViewModel.dataModel.subjecttext1.isBlank()) {
                            findViewById<EditTextNumber>(R.id.inputMechanicalPercent)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        } else {
                            if (mViewModel.dataModel.subjecttext2.isBlank()) {
                                findViewById<AppCompatTextView>(R.id.tvPercent).error =
                                    getString(R.string.error_fill_fields)
                                isValid = false
                            }
                        }

                    }

                    "04", "05", "06", "07" -> {

                        if (mViewModel.dataModel.subjectamount1.isNullOrBlank()) {
                            findViewById<EditTextNumber>(R.id.inputConstructorDriverPrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        } else {
                            if (mViewModel.dataModel.subjectamount2 == null) {
                                findViewById<AppCompatTextView>(R.id.tvDriverPrice).error =
                                    getString(R.string.error_fill_fields)
                                isValid = false
                            }
                        }
                    }

                    //8, 9, 10, 12 ,13, 14, 15, 16,17, 18,19,20,21,22,23,24,25,26,27,28,30--->nothing
                    "11" -> {
                        if (mViewModel.dataModel.subjectamount1 == null) {
                            findViewById<EditTextNumber>(R.id.inputMadePrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                        if (mViewModel.dataModel.subjectamount2 == null) {
                            findViewById<EditTextNumber>(R.id.inputTransportPrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                        if (mViewModel.dataModel.subjectamount3 == null) {
                            findViewById<EditTextNumber>(R.id.inputInstallationPrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                        if (mViewModel.dataModel.subjectamount4 == null) {
                            findViewById<EditTextNumber>(R.id.inputPerformancePrice)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                    }

                    "29" -> {

                        if (mViewModel.dataModel.subjectamount1 == null) {
                            findViewById<EditTextNumber>(R.id.inputCurrencyAmount)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                        if (mViewModel.dataModel.subjectamount2 == null) {
                            findViewById<EditTextNumber>(R.id.inputAverage)
                                .setError(getString(R.string.error_fill_fields))
                            isValid = false
                        }
                    }
                }
            }

        return isValid
    }

    private fun updateDocsAndCheckSize(): Boolean {
        mViewModel.dataModel.documentList.clear()

        for (image in mViewModel.imageFileList) {
            mViewModel.dataModel.documentList.add(
                DocumentInfo(
                    image.guid ?: "",
                    image.imageType ?: "",
                    "1"
                )
            )
        }

        for (pdf in mViewModel.pdfFileList) {
            mViewModel.dataModel.documentList.add(
                DocumentInfo(
                    pdf.guid ?: "",
                    pdf.fileType ?: "",
                    "2"
                )
            )
        }

        mViewModel.dataModel.hasLetImage = mViewModel.imageFileList.isNotEmpty()
        return mViewModel.dataModel.documentList.isNotEmpty()
    }

    private fun stepLetterInfoHasValidData(viewBinding: MafasahesabStepLetterInfoBinding?): Boolean {
        var isValid = true
        viewBinding?.apply {

            if (inputLetterNumber.getValue().isEmpty()) {
                isValid = false
            }


            if (widgetLetterDate.getDateString().isBlank()) {
                widgetLetterDate.setError(getString(R.string.error_fill_fields))
                isValid = false
            }

            if (widgetContractStartDate.getDateString().isBlank()) {
                widgetContractStartDate.setError(getString(R.string.error_fill_fields))
                isValid = false
            }
            if (widgetContractEndDate.getDateString().isBlank()) {
                widgetContractEndDate.setError(getString(R.string.error_fill_fields))
                isValid = false
            }

            if (mViewModel.dataModel.endDate < mViewModel.dataModel.startDate) {
                isValid = false
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_date_prority)
                )
            }

            if (inputAmount.getValue().isEmpty())
                isValid = false

            if (inputAmount.getValue(false) == "0") {
                isValid = false

                inputAmount.setError(getString(R.string.label_error_enter_correct_data))
            }


            /*   if (inputExchangeAmount.getValue().isBlank()) {
                   isValid = false
               }

               if (inputRialAmount.getValue().isBlank()) {
                   isValid = false
               }*/

            mViewModel.dataModel.totalAmount =
                getTotalValue(inputAmount.getValue(false), inputRialAmount.getValue(false))

            if (getLongValueOf(mViewModel.dataModel.currencyAmount) > 0 && getLongValueOf(mViewModel.dataModel.currencyAmountToRial) == 0L) {
                isValid = false
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_valid_currency_to_rial)
                )
            }

        }
        return isValid
    }

    private fun getLongValueOf(value: String): Long {
        return try {
            Utility.removeNumberSeparator(value).toLong()
        } catch (ex: Exception) {
            ex.printStackTrace()
            0L
        }
    }

    override fun onPreviousStepClickListener(stepIndex: Int, step: VerticalStepperItemView) {
        viewDataBinding?.stepper?.previousStep()
    }

    override fun chooseImage(requestCode: Int) {
        val intent = Intent(requireActivity(), MultiCustomGalleryUI::class.java)
        intent.putExtra(Constants.TEMPID, Constants.REQUEST_DEFAULT_IMAGE_TYPE)
        intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)
        resultImageLaunch.launch(intent)
    }

    private fun choosePDF(requestCode: Int) {
        if (PickImageUtils.hasPermissionsOfList(
                requireActivity(),
                getStoragePermissions()
            )
        ) {
            resultPDFLaunch.launch(getPdfFromFile(requestCode))
        } else {
            permissionLauncher.launch(getStoragePermissions())
        }
    }

    private fun getPdfFromFile(requestCode: Int): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.putExtra(Constants.REQUEST_CODE_TAG, requestCode)
        return intent
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
                var toolbarTitle = ""
                arguments?.let { args ->
                    ImageUtils.loadImage(imgIcon, Utility.getToolbarIconImage(args))
                    toolbarTitle = Utility.getToolbarTitle(args)
                    Timber.tag("WomenContractTitle").e("Women Called : title=$toolbarTitle")

                }
                toolbar.imgInfo.visibility = View.GONE
                tvTitle.text = toolbarTitle
                appBarView.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
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
                })
            }
        }
    }

    private fun inflateView(it: EnumMafasaHesabStep): ViewBinding {
        return when (it) {
            STEP_CONTRACT_INFO -> {
                MafasahesabStepContractInfoBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItemMafasaHesabContractInfo.apply {
                        index = it.step
                        title = it.title
                    }
                    contract?.apply {

                        tvContractNumber.text = contract?.contractNumber ?: "-"
                        tvContractDate.text = Utility.getDateSeparator(contract?.contractDate)
                        tvContractTitle.text = contract?.contractSubject ?: "-"
                        tvContractorWorkshopId.text = contract?.employer?.workshopId ?: "-"
                        tvContractorName.text = contract?.employer?.workshopName ?: "-"
                        tvContractRow.text = contract?.contractRow ?: "-"

                    }
                }
            }

            STEP_LETTER_INFO -> {
                MafasahesabStepLetterInfoBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {

                    stepperItemMafasaHesabInfo.apply {
                        index = it.step
                        title = it.title
                    }
                    itemDescForeignExchange.descTxt.text =
                        HtmlCompat.fromHtml(
                            getString(R.string.label_foreign_exchange),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )

                    inputLetterNumber.apply {

                        getInput().doOnTextChanged { text, start, before, count ->

                            if (text.toString().isEmpty())
                                setError(getString(R.string.error_fill_fields))
                            else if (text.toString().length < 5)
                                setError(getString(R.string.label_error_enter_correct_data))
                            else {
                                mViewModel.dataModel.letterNumber = text.toString()
                                getLayout().isErrorEnabled = false
                            }
                        }
                    }

                    widgetLetterDate.setListener(object : DatePickerWidget.DateSelectOrListener {
                        override fun onDateSelect(
                            jalaliDate: String,
                            gregorianDate: Date,
                            timeStamp: Long,
                            serverFormattedDate: String,
                            serverFormattedDateWithDayOffset: String
                        ) {
                            mViewModel.dataModel.letterDate = serverFormattedDateWithDayOffset
                        }
                    })

                    widgetContractStartDate.setListener(object :
                        DatePickerWidget.DateSelectOrListener {
                        override fun onDateSelect(
                            jalaliDate: String,
                            gregorianDate: Date,
                            timeStamp: Long,
                            serverFormattedDate: String,
                            serverFormattedDateWithDayOffset: String
                        ) {
                            mViewModel.dataModel.startDate = serverFormattedDateWithDayOffset
                        }
                    })

                    widgetContractEndDate.setMaxYear(100)
                    widgetContractEndDate.setListener(object :
                        DatePickerWidget.DateSelectOrListener {
                        override fun onDateSelect(
                            jalaliDate: String,
                            gregorianDate: Date,
                            timeStamp: Long,
                            serverFormattedDate: String,
                            serverFormattedDateWithDayOffset: String
                        ) {
                            mViewModel.dataModel.endDate = serverFormattedDateWithDayOffset
                        }
                    })

                    inputAmount.getInput().apply {
                        addTextChangedListener(NumberTextWatcherForThousand(inputAmount.getInput()))
                        doOnTextChanged { _, _, _, _ ->
                            calculateTotalValue()

                            mViewModel.dataModel.amountCount =
                                Utility.removeNumberSeparator(inputAmount.getValue(false))
                        }
                    }

                    inputExchangeAmount.getInput().apply {
                        addTextChangedListener(NumberTextWatcherForThousand(inputExchangeAmount.getInput()))
                        doOnTextChanged { _, _, _, _ ->
                            mViewModel.dataModel.currencyAmount =
                                Utility.removeNumberSeparator(inputExchangeAmount.getValue(false))
                        }
                    }

                    inputRialAmount.getInput().apply {
                        addTextChangedListener(NumberTextWatcherForThousand(inputRialAmount.getInput()))
                        doOnTextChanged { _, _, _, _ ->
                            calculateTotalValue()
                            mViewModel.dataModel.currencyAmountToRial =
                                Utility.removeNumberSeparator(inputRialAmount.getValue(false))
                        }
                    }

                    rgSubContractor.setOnCheckedChangeListener { group, checkedId ->
                        if (checkedId == R.id.rbYes) mViewModel.dataModel.SubContractor = "1"
                        if (checkedId == R.id.rbNo) mViewModel.dataModel.SubContractor = "0"
                    }
                }
            }

            STEP_UPLOAD_IMAGE -> {
                MafasahesabStepUploadDocBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {
                    stepperItemMafasahesabDocs.apply {
                        index = it.step
                        title = it.title
                    }

                    btnAddImage.setOnClickListener {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

                        this@MafasaHesabRegisterFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.getImageTitleFlow()
                                .collectLatest { pagingData ->
                                    openImageTypeMenu(
                                        pagingData,
                                        onResultCallBack = object : MenuInterface.OnResult {
                                            override fun onResult(itemResult: MenuModel) {
                                                //tag used as REQUEST_CODE
                                                chooseImage(itemResult.tag)

                                            }
                                        })
                                }
                        }
                    }

                    btnAddPDF.setOnClickListener {
                        view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }

                        this@MafasaHesabRegisterFragment.lifecycleScope.launchWhenCreated {
                            mViewModel.getImageTitleFlow()
                                .collectLatest { pagingData ->
                                    openImageTypeMenu(
                                        pagingData,
                                        onResultCallBack = object : MenuInterface.OnResult {
                                            override fun onResult(itemResult: MenuModel) {
                                                //tag used as REQUEST_CODE

                                                requestCode = itemResult.tag
                                                pdfType = itemResult.tag
                                                choosePDF(itemResult.tag)
                                            }
                                        })
                                }
                        }
                    }

                    recyclerImage.apply {
                        this.adapter = imageListAdapter
                        if (itemDecorationCount == 0) {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                    }
                    recyclerPDf.apply {
                        this.adapter = pdfListAdapter
                        if (itemDecorationCount == 0) {
                            addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                        }
                    }


                }
            }

            STEP_CONTRACT_STATUS_BY_SUBJECT -> {
                MafasahesabStepContractConditionBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    viewDataBinding?.stepper,
                    true
                ).apply {

                    stepperItemMafasahesabCondition.apply {
                        index = it.step
                        title = it.title
                        nextButtonTitle = getString(R.string.register_mafasa_hesab)
                    }
                    selectCondition.getIt().setOnClickListener {

                        val dialog = MenuDialogFragment.newInstance(
                            true,
                            getString(R.string.label_contract_according_to_title)
                        )
                        dialog.setMenuListener(object : MenuInterface.OnFetchData {
                            override fun onFetch() {
                                this@MafasaHesabRegisterFragment.lifecycleScope.launchWhenCreated {
                                    mViewModel.getMafasaHesabContractSubjects()
                                        .collectLatest { pagingData ->
                                            val result = pagingData.map {
                                                MenuModel(
                                                    id = it.code,
                                                    title = it.description
                                                )
                                            }
                                            dialog.updateData(result)
                                        }
                                }
                            }
                        }, object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {
                                selectCondition.setValue(itemResult.title ?: "")
                                mViewModel.dataModel.contractsubjectTitle = itemResult.title ?: ""
                                mViewModel.dataModel.contractsubjectcode = itemResult.id ?: ""

                                setUiVisibilityCordingContractSubject(
                                    mViewModel.dataModel.contractsubjectcode,
                                    this@apply
                                )
                            }
                        }, object : MenuInterface.OnSearch {
                            override fun onSearch(str: String) {
                                this@MafasaHesabRegisterFragment.lifecycleScope.launchWhenCreated {
                                    mViewModel.getMafasaHesabContractSubjects()
                                        .collectLatest { pagingData ->
                                            val result = pagingData.map {
                                                MenuModel(
                                                    id = it.code,
                                                    title = it.description
                                                )
                                            }
                                            dialog.updateData(result)
                                        }
                                }
                            }
                        })
                        dialog.show(childFragmentManager, "jgkutgiutuytyu")
                    }
                    if (mViewModel.dataModel.contractsubjectcode.isNotBlank()) {
                        selectCondition.setValue(mViewModel.dataModel.contractsubjectTitle)
                        setUiVisibilityCordingContractSubject(
                            mViewModel.dataModel.contractsubjectcode,
                            this@apply
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUiVisibilityCordingContractSubject(
        itemId: String,
        viewBinding: MafasahesabStepContractConditionBinding
    ) {
        viewBinding.apply {
            imageListTerm1Adapter.clearItems()
            mViewModel.dataModel.clearValues()
            containerConditions.removeAllViews()
            containerConditions.tag = itemId
            when (itemId) {
                "01" -> {
                    MafasahesabStepContractTerm01Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {
                        selectContractByPrice.getIt().setOnClickListener {
                            MenuDialogFragment.newInstance(true).apply {
                                setMenuListener(object : MenuInterface.OnFetchData {
                                    override fun onFetch() {
                                        this@MafasaHesabRegisterFragment.lifecycleScope.launchWhenCreated {
                                            mViewModel.getContractTermStep1Flow()
                                                .collectLatest { pagingData ->
                                                    updateData(pagingData)
                                                }
                                        }
                                    }
                                }, object : MenuInterface.OnResult {
                                    override fun onResult(itemResult: MenuModel) {
                                        mViewModel.dataModel.subjectOwner = itemResult.id ?: ""
                                        selectContractByPrice.setValue(itemResult.title ?: "")
                                    }
                                })
                            }.show(childFragmentManager, "tyuytuytu")
                        }

                        layoutUploadImage.recycler.apply {
                            this.adapter = imageListTerm1Adapter
                            if (itemDecorationCount == 0) {
                                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                            }
                        }

                        layoutUploadImage.btnAddDocument.setOnClickListener {
                            view?.windowToken?.let {
                                Utility.hideKeyboard(
                                    requireContext(),
                                    it
                                )
                            }
                            chooseImage(mViewModel.image_request_code_term_1)
                        }

                        inputPlanCredit.getInput().doOnTextChanged { text, start, before, count ->
                            mViewModel.dataModel.subjecttext1 = text.toString()
                            inputPlanCredit.enableError(
                                getString(R.string.error_fill_fields),
                                text.toString().isEmpty()
                            )
                        }

                        inputBudgetRow.getInput().doOnTextChanged { text, start, before, count ->
                            mViewModel.dataModel.subjecttext2 = text.toString()
                        }

                        inputInsurancePrice.getInput().apply {
                            addTextChangedListener(NumberTextWatcherForThousand(this))
                            doOnTextChanged { text, start, before, count ->
                                mViewModel.dataModel.subjectamount1 =
                                    trimCommaOfString(text.toString())
                            }
                        }
                    }
                }

                "02" -> {
                    MafasahesabStepContractTerm02Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {
                        selectSupplyResponsibility.getIt().setOnClickListener {

                            val dialog = MenuDialogFragment.newInstance(true)
                            dialog.setMenuListener(object : MenuInterface.OnFetchData {
                                override fun onFetch() {
                                    this@MafasaHesabRegisterFragment.lifecycleScope.launchWhenCreated {
                                        mViewModel.getContractTemStep2Flow()
                                            .collectLatest { pagingData ->
                                                dialog.updateData(pagingData)
                                            }
                                    }
                                }
                            }, object : MenuInterface.OnResult {
                                override fun onResult(itemResult: MenuModel) {
                                    selectSupplyResponsibility.setValue(itemResult.title ?: "")
                                    mViewModel.dataModel.subjectOwner = itemResult.id ?: ""

                                    if (itemResult.id == "3") {
                                        inputPrice.visible()
                                        inputPrice.getInput().addTextChangedListener(
                                            NumberTextWatcherForThousand(inputPrice.getInput())
                                        )
                                        inputPrice.getInput()
                                            .doOnTextChanged { text, start, before, count ->
                                                mViewModel.dataModel.subjectamount1 =
                                                    trimCommaOfString(text.toString())
                                            }
                                    } else {
                                        inputPrice.setValueOfText("")
                                        inputPrice.gone()
                                    }
                                }
                            })
                            dialog.show(childFragmentManager, "tyuytuytu")
                        }
                    }
                }

                "03" -> {
                    MafasahesabStepContractTerm03Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {

                        inputMechanicalPercent.getInput().doOnTextChanged { text, _, _, count ->
                            if (text.isNullOrBlank()) {
                                mViewModel.dataModel.subjecttext1 = ""
                                mViewModel.dataModel.subjecttext2 = ""
                            } else {
                                var input = getLongValueOf(text.toString())
                                if (input > 100) {
                                    input = 100
                                    inputMechanicalPercent.setTextWidget("100")
                                }
                                val result = 100 - input
                                if (inputMechanicalPercent.getValue(false).isNotBlank())
                                    tvPercent.text = "درصد دستی : $result"

                                mViewModel.dataModel.subjecttext1 = text.toString()
                                mViewModel.dataModel.subjecttext2 = result.toString()
                            }
                        }
                    }
                }

                "04", "05", "06" -> {
                    MafasahesabStepContractTerm04050607Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {

                        inputConstructorDriverPrice.getInput().apply {
                            addTextChangedListener(NumberTextWatcherForThousand(this))
                            doOnTextChanged { text, _, _, _ ->
                                calculateValueInStep04to07(
                                    text.toString(), tvDriverPrice, false
                                )

                            }
                        }
                    }
                }

                "07" -> {
                    MafasahesabStepContractTerm04050607Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {

                        inputConstructorDriverPrice.getInput().hint =
                            "هزینه خرید تجهیزات غیر از مواد مصرف"
                        tvDriverPrice.text = "هزینه خدمات، اجرا و مصالح مصرفی :"

                        inputConstructorDriverPrice.getInput().doOnTextChanged { _, _, _, _ ->
                            calculateValueInStep04to07(
                                inputConstructorDriverPrice.getValue(false),
                                tvDriverPrice,
                                true
                            )
                        }
                    }
                }
                //8, 9, 10, 12 , 14, 15, 16,17, 18,19,20,21,22,23,24,25,26,27,28,30--->nothing
                "11" -> {
                    MafasahesabStepContractTerm11Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {

                        tvWorkshopAddress.text = contract?.employer?.address ?: "-"
                        tvNationalCode.text = contract?.employer?.nationalId ?: "-"

                        inputMadePrice.getInput().apply {
                            addTextChangedListener(NumberTextWatcherForThousand(inputMadePrice.getInput()))
                            doOnTextChanged { text, start, before, count ->
                                mViewModel.dataModel.subjectamount1 =
                                    Utility.removeNumberSeparator(text.toString())
                            }
                        }

                        inputTransportPrice.getInput().apply {
                            addTextChangedListener(NumberTextWatcherForThousand(inputTransportPrice.getInput()))
                            doOnTextChanged { text, start, before, count ->
                                mViewModel.dataModel.subjectamount2 =
                                    Utility.removeNumberSeparator(text.toString())
                            }
                        }

                        inputInstallationPrice.getInput().apply {
                            addTextChangedListener(
                                NumberTextWatcherForThousand(
                                    inputInstallationPrice.getInput()
                                )
                            )
                            doOnTextChanged { text, start, before, count ->
                                mViewModel.dataModel.subjectamount3 =
                                    Utility.removeNumberSeparator(text.toString())
                            }
                        }

                        inputPerformancePrice.getInput().apply {
                            addTextChangedListener(
                                NumberTextWatcherForThousand(
                                    inputPerformancePrice.getInput()
                                )
                            )
                            doOnTextChanged { text, start, before, count ->
                                mViewModel.dataModel.subjectamount4 =
                                    Utility.removeNumberSeparator(text.toString())
                            }
                        }
                    }
                }

                "13" -> {
                    MafasahesabStepContractTerm13Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {

                    }
                }

                "29" -> {
                    MafasahesabStepContractTerm29Binding.inflate(
                        LayoutInflater.from(requireContext()),
                        containerConditions,
                        true
                    ).apply {
                        inputCurrencyAmount.getInput().apply {
                            addTextChangedListener(NumberTextWatcherForThousand(inputCurrencyAmount.getInput()))
                            doOnTextChanged { text, start, before, count ->
                                mViewModel.dataModel.subjectamount1 = text.toString()
                            }
                        }

                        inputAverage.getInput().apply {
                            addTextChangedListener(NumberTextWatcherForThousand(inputAverage.getInput()))
                            doOnTextChanged { text, start, before, count ->
                                mViewModel.dataModel.subjectamount2 = text.toString()
                            }
                        }

                        layoutUploadImage.recycler.apply {
                            this.adapter = imageListTerm1Adapter
                            if (itemDecorationCount == 0) {
                                addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                            }
                        }

                        layoutUploadImage.btnAddDocument.setOnClickListener {
                            view?.windowToken?.let {
                                Utility.hideKeyboard(
                                    requireContext(),
                                    it
                                )
                            }
                            chooseImage(mViewModel.image_request_code_term_1)
                        }
                    }
                }

            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotalValue() {

        (getStep(STEP_LETTER_INFO.step) as? MafasahesabStepLetterInfoBinding)?.apply {

            val total = getTotalValue(inputAmount.getValue(false), inputRialAmount.getValue(false))

            tvTotalValue.text =
                " مجموع کل ناخالص کارکرد: ${Utility.getThousandSeparated(total)} ریال"
        }


    }

    private fun getTotalValue(amountStr: String, currencyAmountToRialStr: String): Double {
        return try {
            val amount = Utility.removeNumberSeparator(amountStr)
            val currencyAmountToRial =
                Utility.removeNumberSeparator(currencyAmountToRialStr)
            amount.toDouble() + currencyAmountToRial.toDouble()
        } catch (ex: Exception) {
            ex.printStackTrace()
            0.0
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateValueInStep04to07(
        userInput: String, tvResult: AppCompatTextView,
        isTermNumber07: Boolean
    ) {

        val constructorDriverPrice = Utility.removeNumberSeparator(userInput)
        val cntamount = getLongValueOf(mViewModel.dataModel.amountCount)
        val cntamountcurrencyToR = getLongValueOf(mViewModel.dataModel.currencyAmountToRial)

        mViewModel.dataModel.subjectamount1 = constructorDriverPrice
        if (constructorDriverPrice.isNotBlank()) {

            val modValue = cntamount + cntamountcurrencyToR - getLongValueOf(constructorDriverPrice)
            if (modValue <= 0) {

                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    if (isTermNumber07) getString(R.string.label_error_mafasahesab_step_3_term_7)
                    else getString(R.string.label_error_mafasahesab_step_3_term_456)
                )
            } else {
                val resultWithSeparator = Utility.getRialWithSeparator(modValue)
                tvResult.text =
                    "مبلغ کارکرد انجام شده توسط رانندگان خود مالک  : $resultWithSeparator"
                mViewModel.dataModel.subjectamount2 = modValue

            }
        }
    }

    private fun getStep(step: Int): ViewBinding? {
        return viewDataBinding?.stepper?.getStepLayoutBindingByStep(step)
    }
    private fun getStoragePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

}
