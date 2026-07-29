package com.tamin.taminhamrah.ui.home.services.insuranceRegistration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.UploadedImageModel
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.UploadImageResponse
import com.tamin.taminhamrah.databinding.FragmentInsuranceRegistrationBinding
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
import com.tamin.taminhamrah.utils.extentions.navigateUp
import com.tamin.taminhamrah.utils.extentions.provideImageForUpload
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import java.io.File

@AndroidEntryPoint
class InsuranceRegistrationFragment :
    BaseFragment<FragmentInsuranceRegistrationBinding, InsuranceRegistrationViewModel>(),
    AdapterInterface.OnItemClickListener<UploadedImageModel> {

    override val mViewModel: InsuranceRegistrationViewModel by viewModels()

    private var selectedInsuranceId: String? = null
    private var selectedBirthCityId: String? = null
    private var selectedIssuedCityId: String? = null
    private var selectedBranchId: String? = null

    lateinit var listAdapter: ImagePreviewAdapter
    var tempImageType: String? = null
    var tempImageName: String? = null
    var tempImageOriginalUri: Uri? = null
    var fileListUploaded = ArrayList<UploadedImageModel>()
    var tempImageUri: Uri? = null


//    private var fileList = mutableSetOf<RequestForPregnancyPayReq.ShorttermRequest.RequestFile>()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId() = R.layout.fragment_insurance_registration

    override fun setupObserver() {
        mViewModel.mldUploadImage.observe(this, ::showResultUploadImage)
        mViewModel.mldRegistrationResult.observe(this, ::showRegistrationResult)
        //  mViewModel.mldRegistrationInfo.observe(this, ::showResultInfoUser)
    }

    /* private fun showResultInfoUser(result: ConcludingStudentInsuranceContractResponse) {
         if (result.isSuccess) {
             *//* viewDataBinding?.apply {
                 userInfo = result.data
                 groupDetailsRequest.visibility = View.VISIBLE
                 groupDetails.visibility = View.GONE
             }*//*

        }
    }*/

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )

        viewDataBinding?.apply {

            listAdapter = ImagePreviewAdapter(this@InsuranceRegistrationFragment)
            layoutUploadImage.recycler.apply {
                this.adapter = listAdapter

                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                }
            }
        }
    }

    override fun getData() {
        // mViewModel.getRegistrationInfo()
    }

    override fun onClick() {
        viewDataBinding?.apply {

            inputInsuranceType.selectableInput.apply {
                setOnClickListener {
                    val dialog =
                        MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_insurance_type))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {

                        override fun onFetch() {
                            inputInsuranceType.tilSelectableInput.isErrorEnabled = false
                            this@InsuranceRegistrationFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.insuranceTypeFlow.collectLatest { pagingData ->
                                    dialog.updateData(pagingData)
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            itemResult.title?.let { title ->
                                inputInsuranceType.selectableInput.setText(title)
                            }
                            itemResult.id?.let {
                                selectedInsuranceId = it
                            }

                        }
                    })
                    dialog.show(childFragmentManager, "fhghg")
                }
            }

            inputCityOfBirth.selectableInput.apply {
                setOnClickListener {
                    val dialog =
                        MenuDialogFragment.newInstance(true, getString(R.string.birth_city))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            inputCityOfBirth.tilSelectableInput.isErrorEnabled = false
                            this@InsuranceRegistrationFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getCityListFlow().collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            title = it.cityName,
                                            id = it.cityCode,
                                            description = it.provincecode
                                        )
                                    }
                                    dialog.updateData(result)
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            itemResult.title?.let { title ->
                                inputCityOfBirth.selectableInput.setText(title)
                            }
                            itemResult.id?.let {
                                selectedBirthCityId = it
                            }

                        }
                    }, object : MenuInterface.OnSearch {
                        override fun onSearch(str: String) {
                            this@InsuranceRegistrationFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getCityListFlow(str)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                title = it.cityName,
                                                id = it.cityCode,
                                                description = it.provincecode
                                            )
                                        }
                                        dialog.updateData(result)
                                    }

                            }
                        }
                    })
                    dialog.show(childFragmentManager, "fhghg")
                }
            }

            inputCityIssuance.selectableInput.apply {
                setOnClickListener {
                    val dialog =
                        MenuDialogFragment.newInstance(true, getString(R.string.issue_city))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            inputCityIssuance.tilSelectableInput.isErrorEnabled = false
                            this@InsuranceRegistrationFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getCityListFlow().collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            title = it.cityName,
                                            id = it.cityCode,
                                            description = it.provincecode
                                        )
                                    }
                                    dialog.updateData(result)
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            itemResult.title?.let { title ->
                                inputCityIssuance.selectableInput.setText(title)
                            }
                            itemResult.id?.let {
                                selectedIssuedCityId = it
                            }

                        }
                    }, object : MenuInterface.OnSearch {
                        override fun onSearch(str: String) {
                            this@InsuranceRegistrationFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getCityListFlow(str)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                title = it.cityName,
                                                id = it.cityCode,
                                                description = it.provincecode
                                            )
                                        }
                                        dialog.updateData(result)
                                    }

                            }
                        }
                    })
                    dialog.show(childFragmentManager, "fhghg")
                }
            }

            inputBranch.selectableInput.apply {
                setOnClickListener {
                    val dialog =
                        MenuDialogFragment.newInstance(true, getString(R.string.label_branch_name))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {
                        override fun onFetch() {
                            inputBranch.tilSelectableInput.isErrorEnabled = false
                            this@InsuranceRegistrationFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getBranchRequests().collectLatest { pagingData ->
                                    val result = pagingData.map {
                                        MenuModel(
                                            title = it.name,
                                            id = it.code,
                                            description = it.branchAddress
                                        )
                                    }
                                    dialog.updateData(result)
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            itemResult.title?.let { title ->
                                inputBranch.selectableInput.setText(title)
                            }
                            itemResult.id?.let {
                                selectedBranchId = it
                            }

                        }
                    }, object : MenuInterface.OnSearch {
                        override fun onSearch(str: String) {
                            this@InsuranceRegistrationFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.getBranchRequests(str)
                                    .collectLatest { pagingData ->
                                        val result = pagingData.map {
                                            MenuModel(
                                                title = it.name,
                                                id = it.code,
                                                description = it.branchAddress
                                            )
                                        }
                                        dialog.updateData(result)
                                    }

                            }
                        }
                    })
                    dialog.show(childFragmentManager, "fhghg")
                }
            }

            swHasNationalCard.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    swHasNationalCard.text = getString(R.string.label_has_national_card)
                    inputSerialNumber.visibility = View.VISIBLE
                    layoutUploadImage.parent.visibility = View.GONE
                } else {
                    swHasNationalCard.text = getString(R.string.label_has_not_national_card)
                    inputSerialNumber.visibility = View.GONE
                    layoutUploadImage.parent.visibility = View.VISIBLE
                }
            }

            layoutUploadImage.btnAddDocument.setOnClickListener {
                view?.windowToken?.let { Utility.hideKeyboard(requireContext(), it) }
                if (fileListUploaded.size == 0) {
                    chooseImage()
                } else {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.WARNING,
                        getString(R.string.error_upload_personal_image)
                    )
                }
            }


            btnOk.setOnClickListener {


                if (selectedInsuranceId.isNullOrEmpty()) {
                    inputInsuranceType.tilSelectableInput.isErrorEnabled = true
                    inputInsuranceType.tilSelectableInput.error =
                        getString(R.string.error_fill_fields)
                    return@setOnClickListener
                }
                if (selectedBirthCityId.isNullOrEmpty()) {
                    inputCityOfBirth.tilSelectableInput.isErrorEnabled = true
                    inputCityOfBirth.tilSelectableInput.error =
                        getString(R.string.error_fill_fields)
                    return@setOnClickListener
                }
                if (selectedIssuedCityId.isNullOrEmpty()) {
                    inputCityIssuance.tilSelectableInput.isErrorEnabled = true
                    inputCityIssuance.tilSelectableInput.error =
                        getString(R.string.error_fill_fields)
                    return@setOnClickListener
                }
                if (selectedBranchId.isNullOrEmpty()) {
                    inputBranch.tilSelectableInput.isErrorEnabled = true
                    inputBranch.tilSelectableInput.error = getString(R.string.error_fill_fields)
                    return@setOnClickListener
                }


                if (inputSerialNumber.getValue().isEmpty() && fileListUploaded.size == 0) {
                    val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                    dialog.arguments = createBundle(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        getString(R.string.error_sellect_image_or_fill_serial_number)
                    )
                    dialog.show(childFragmentManager, "ghghghghghgh")
                    return@setOnClickListener
                }

                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.WARNING,
                    getString(R.string.insurance_registration_warning),
                    btnCancel = true,
                )

                dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        mViewModel.postInsuranceRegistration(
                            selectedInsuranceId!!,
                            selectedBirthCityId!!,
                            selectedIssuedCityId!!,
                            selectedBranchId!!,
                            viewDataBinding?.inputSerialNumber?.getValue(),
                            fileListUploaded
                        )
                    }

                    override fun onCancelClick() {
                    }

                })
                dialog.show(childFragmentManager, "fhgfhgfh")
            }

        }
    }

    override fun chooseImage(requestCode: Int) {
        tempImageName = getString(R.string.label_image)
        tempImageType = Constants.IMAGE_TYPE_INSURANCE_REGISTRATION
        val intent = Intent(activity, MultiCustomGalleryUI::class.java)
        intent.putExtra(Constants.TEMPID, tempImageType)
        resultImageLaunch.launch(intent)
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

                tempImageType = null
                tempImageName = null
                tempImageUri = null
                //show image and title in ui
                listAdapter.setItems(fileListUploaded)
            }
        }
    }

    private fun showRegistrationResult(result: GeneralRes) {
        if (result.isSuccess) {

            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.CONFIRM,
                getString(R.string.message_success)
            )

            dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
                override fun onConfirmClick() {
                    navigateUp()

                }

                override fun onCancelClick() {
                }

            })
            dialog.show(childFragmentManager, "fgdhgfgfgfdg")
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
                    R.id.action_insurance_registration_to_ImageViewerActivity,
                    bundle
                )
            }
            Constants.DELETE_IMAGE_TAG -> {
                fileListUploaded.remove(item)
                listAdapter.setItems(fileListUploaded)
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