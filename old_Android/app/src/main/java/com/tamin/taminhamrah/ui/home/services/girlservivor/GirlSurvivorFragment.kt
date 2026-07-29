package com.tamin.taminhamrah.ui.home.services.girlservivor

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.ParentPersonalInfo
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.ActiveRelation
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.PersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.asDomainModel
import com.tamin.taminhamrah.data.remote.models.services.girlSurvivor.CheckGirlSurvivorConditionsResponse
import com.tamin.taminhamrah.databinding.FragmentGirlSurvivorBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.KeyValueAdapter
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GirlSurvivorFragment :
    BaseFragment<FragmentGirlSurvivorBinding, GirlSurvivorViewModel>(),
    AdapterInterface.OnItemClickListener<ActiveRelation> {

    var pensionerId: String? = null
    lateinit var listAdapter: KeyValueAdapter
    override val mViewModel: GirlSurvivorViewModel by viewModels()
    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_girl_survivor
    }

    override fun setupObserver() {
        mViewModel.mldIdentityInfo.observe(this, ::showResult)
        mViewModel.mldCondition.observe(this, ::showConditionResult)
        mViewModel.mldReport.observe(this, ::showReportResult)
        mViewModel.mldConfirm.observe(this, ::showConfirmResult)
    }


    override fun initView() {
        listAdapter = KeyValueAdapter()
        viewDataBinding?.apply {
            recyclerPersonalInfo.apply {
                this.adapter = listAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.createDivider(requireContext()))
                }
            }
            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
            )
            labelPensionId.backgroundTintList = getBackgroundTintList()
            inputZipCode.getInput().doAfterTextChanged {
                if (inputZipCode.getValue(false).length == 10)
                    inputZipCode.getLayout().isErrorEnabled = false
            }
            inputPhoneNumber.getInput().doAfterTextChanged {
                if (inputPhoneNumber.getValue(false).length == 11)
                    inputPhoneNumber.getLayout().isErrorEnabled = false
            }

            widgetNationalCode.getInput().doAfterTextChanged {
                if (widgetNationalCode.getValueNationalCode(false).length == 10)
                    widgetNationalCode.getLayout().isErrorEnabled = false
            }
            inputPensionId.getInput().doAfterTextChanged {
                if (inputPensionId.getValue(false).length == 10)
                    inputPensionId.getLayout().isErrorEnabled = false
            }
        }

    }

    override fun getData() {
        mViewModel.getPersonalInfo()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            swChangeInput.setOnCheckedChangeListener { _, switchChecked ->
                checkedSwitch(switchChecked)
            }
            btnCheckForm.setOnClickListener {
                if(checkValidInput())
                        mViewModel.checkGirlSurvivorConditions(
                            widgetNationalCode.getValueNationalCode(),
                            inputPensionId.getValue()
                        )
            }

            btnConfirm.setOnClickListener {
                if (rbConfirm.isChecked) {
                    mViewModel.confirmGirlSurvivor()
                }
            }
        }
    }

    private fun checkValidInput(): Boolean {
        viewDataBinding?.apply {
            val phone = inputPhoneNumber.getValue(false)
            val nationalId = widgetNationalCode.getValueNationalCode(false)
            val pensionerId = inputPensionId.getValue(false)
            val zipCode = inputZipCode.getValue(false)
            when {
                inputAddress.getValue(false).isBlank() -> {
                    inputAddress.getLayout().error = getString(R.string.error_enter_address)
                }
                zipCode.isBlank() -> {
                    inputZipCode.getLayout().error = getString(R.string.error_enter_zip_code)
                }
                zipCode.length < 10 -> {
                    inputZipCode.getLayout().error =
                        getString(R.string.error_not_valid_zip_code)
                }
                phone.isBlank() -> {
                    inputPhoneNumber.getLayout().error = getString(R.string.error_enter_phone)
                }
                !phone.startsWith("0") -> {
                    inputPhoneNumber.getLayout().error =
                        getString(R.string.error_not_valid_phone)

                }
                phone.length > 11 -> {
                    inputPhoneNumber.getLayout().error =
                        getString(R.string.error_not_valid_phone)
                }
               !widgetNationalCode.isVisible && pensionerId.length<10 ->{
                   inputPensionId.getLayout().error = getString(R.string.error_not_valid_pension_id)
               }

                nationalId.isBlank() && pensionerId.isBlank() -> {
                    if (widgetNationalCode.isVisible)
                        widgetNationalCode.getLayout().error =
                            getString(R.string.error_not_valid_national_id)
                    else if (inputPensionId.isVisible)
                        inputPensionId.getLayout().error =
                            getString(R.string.error_enter_pension_num)
                }
                else->{
                    return true
                }
            }
        }
        return false
    }

    private fun checkedSwitch(switchChecked: Boolean) {
        viewDataBinding?.labelPensionId?.isEnabled = switchChecked
        if (switchChecked) {
            manageSwitch(false, View.INVISIBLE, View.VISIBLE)
        } else {
            manageSwitch(true, View.VISIBLE, View.INVISIBLE)
        }
    }

    private fun manageSwitch(
        flag: Boolean,
        viewNationalCode: Int,
        viewForeignNationalsCode: Int,
    ) {
        viewDataBinding?.apply {
            widgetNationalCode.visibility = viewNationalCode
            inputPensionId.visibility = viewForeignNationalsCode
            if (flag) {
                widgetNationalCode.requestFocus()
            } else {
                inputPensionId.requestFocus()
            }
        }

        clearEditText()
    }

    private fun clearEditText() {
        viewDataBinding?.apply {
            widgetNationalCode.setTextWidget("")
            inputPensionId.setTextWidget("")
        }

    }


    private fun showResult(result: PersonalInfoResponse) {
        if (result.isSuccess) {
            result.data?.let {
                listAdapter.setItems(
                    it.asDomainModel().createKeyValue(result.data.asDomainModel())
                )
            }
        }
    }

    private fun showConditionResult(result: CheckGirlSurvivorConditionsResponse) {
        if (result.isSuccess) {
            result.data?.let {
                viewDataBinding?.apply {
                    mViewModel.mldIdentityInfo.value?.data?.asDomainModel()?.let { info ->
                        info.address = inputAddress.getInput().text.toString()
                        info.zipCode = inputZipCode.getValue()
                        info.phoneNumber = inputPhoneNumber.getValue()
                        info.parentInfo = ParentPersonalInfo(
                            widgetNationalCode.getValueNationalCode(),
                            inputPensionId.getValue()
                        )
                        info.apply {
                            mViewModel.getGirlSurvivorReport(
                                address ?: "",
                                phoneNumber ?: "",
                                zipCode ?: "",
                                fatherName ?: "",
                                birthDateTimeStamp ?: 0,
                                insuranceNumber ?: "",
                                parentInfo?.nationalCode ?: "",
                                parentInfo?.pensionId ?: "",
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showReportResult(result: PdfDownloadResponse) {
        if (result.isSuccess) {
            val file = Utility.writeByteStreamToDisk(
                Utility.getToolbarTitle(arguments),
                requireContext(),
                result.pdf
            )
            if (file == null) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_file)
                )
                return
            }
            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, Utility.getToolbarTitle(arguments))
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file.path)
            handlePageDestination(
                R.id.action_girlSurvivorFragment_to_pdf_viewer,
                bundle = bundle
            )
            viewDataBinding?.groupConfirm?.visibility = View.VISIBLE
        }
    }

    private fun showConfirmResult(result: GeneralRes) {
        if (result.isSuccess) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.SUCCESS,
                getString(R.string.message_success_girl_survivor)
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


    override fun onItemClick(item: ActiveRelation, transitionView: View?, tag: String?) {
        handlePageDestination(R.id.action_active_relation_to_certificate)
    }

    private fun getBackgroundTintList(): ColorStateList? {
        return UiUtils.getColorList(
            ContextCompat.getColor(requireContext(), R.color.lineColor),
            ContextCompat.getColor(requireContext(), R.color.textColorTitle)
        )
    }
}