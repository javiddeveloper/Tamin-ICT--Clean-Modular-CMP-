package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopCEOInfoResponse
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.DialogCompleteInfoOfLegalWorkshopBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.DialogResultInterface.OnResultListener
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.annotations.NotNull
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class CompleteInfoOfLegalWorkshopDialogFragment :
    BaseBottomSheetDialogFragment<DialogCompleteInfoOfLegalWorkshopBinding, CompleteInfoViewModel>(

    ) {

    override val mViewModelDialog: CompleteInfoViewModel by viewModels()

    override fun getLayoutId() = R.layout.dialog_complete_info_of_legal_workshop

    companion object {

        private const val ARG_WORKSHOP_ID = "workshopId"
        private const val ARG_BRANCH_CODE = "branchCode"

        fun newInstance(
            workshopId: String?,
            branchCode: String?
        ): CompleteInfoOfLegalWorkshopDialogFragment {
            val args = Bundle()
            args.putString(ARG_WORKSHOP_ID, workshopId)
            args.putString(ARG_BRANCH_CODE, branchCode)

            val fragment = CompleteInfoOfLegalWorkshopDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var timer: CountDownTimer? = null
    private var fragmentIsInBackground = false
    private var timerIsFinished=false

    private var onListener: MenuInterface.OnResult? = null
    private var selectedCompanyTypeId = "0"
    private var jalaliDate = ""

    var mListener: OnResultListener<Map<String, String>>? = null

    fun setListener(listener: OnResultListener<Map<String, String>>) {
        mListener = listener
    }

    fun setupObserver() {
        mViewModelDialog.mldLegalWorkshopInfo.observe(this, ::showLegalWorkshopInfo)
        mViewModelDialog.mldLegalWorkshopCEOInfo.observe(this, ::showLegalWorkshopCEOInfo)
        mViewModelDialog.mldTicketResult.observe(this, ::showTicketResult)
    }

    private fun showTicketResult(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            viewBinding?.apply {
                updateUiVisibility(true)
                timer?.start()
            }
        } else {
            viewBinding?.apply {
                updateUiVisibility(false)
            }
        }
    }

    private fun updateUiVisibility(showVerifyView: Boolean) {
        viewBinding?.apply {
            if (showVerifyView) {
                groupData.apply {
                    visibility = View.GONE
                    isClickable = false
                    isEnabled = false
                }

                groupVerifyTicket.apply {
                    visibility = View.VISIBLE
                    isClickable = true
                    isEnabled = true
                }

            } else {
                groupVerifyTicket.apply {
                    visibility = View.GONE
                    isClickable = false
                    isEnabled = false
                }

                groupData.apply {
                    visibility = View.VISIBLE
                    isClickable = true
                    isEnabled = true
                }
            }
        }

    }

    private fun showLegalWorkshopInfo(result: LegalWorkshopInfoResponse?) {
        if (result?.isSuccess == true) {
            viewBinding?.tvFullName?.text = result.data?.name
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showLegalWorkshopCEOInfo(result: LegalWorkshopCEOInfoResponse?) {
        if (result?.isSuccess == true) {
            result.data?.apply {
                viewBinding?.tvFullNameOfCEO?.text =
                    "${if(firstName.isNullOrEmpty()) "-" else result.data?.firstName} ${if(lastName.isNullOrEmpty()) "" else lastName}"
            }
        }
    }

    fun setListener(listener: MenuInterface.OnResult) {
        onListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onClick()
        setupObserver()
    }

    private fun initView() {

        viewBinding?.apply {

            inputNationalCode.getInput().addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    text: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (text?.length == 11)
                        mViewModelDialog.getLegalWorkshopInfo(text.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            inputNationalCodeOfCEO.getInput().addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    text: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (inputNationalCodeOfCEO.getValueNationalCode()
                            .isNotBlank() && text?.length == 10 && jalaliDate.isNotBlank()
                    ) {
                        mViewModelDialog.getLegalWorkshopCEOInfo(
                            inputNationalCodeOfCEO.getValueNationalCode(),
                            jalaliDate
                        )
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            inputCompanyType.selectableInput.apply {
                setOnClickListener {
                    val dialog =
                        MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_insurance_type))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {

                        override fun onFetch() {
                            this@CompleteInfoOfLegalWorkshopDialogFragment.lifecycleScope.launchWhenCreated {
                                mViewModelDialog.companyTypeFlow.collectLatest { pagingData ->
                                    dialog.updateData(pagingData)
                                }
                            }
                        }
                    }, object : MenuInterface.OnResult {

                        override fun onResult(itemResult: MenuModel) {
                            itemResult.title?.let { title ->
                                setText(title)
                            }
                            itemResult.id?.let {
                                selectedCompanyTypeId = it
                            }

                        }
                    })
                    dialog.show(childFragmentManager, "kjhkhkjhkj")
                }
            }


            widgetBirthDate.inputDate.setOnClickListener {
                val datePickerStart = getDatePicker()
                datePickerStart?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n", "SimpleDateFormat")
                    override fun onDateSelected(@NotNull myPersianPickerDate: MyPersianPickerDate) {
                        val persianMonth = if (myPersianPickerDate.persianMonth in 1..9) {
                            "0${myPersianPickerDate.persianMonth}"
                        } else {
                            "${myPersianPickerDate.persianMonth}"
                        }

                        jalaliDate = "${myPersianPickerDate.persianYear}$persianMonth${myPersianPickerDate.persianDay}"

                        widgetBirthDate.inputDate.setText("${myPersianPickerDate.persianYear}/$persianMonth/${myPersianPickerDate.persianDay}")

                        if (inputNationalCodeOfCEO.getValueNationalCode().isNotBlank()) {
                            mViewModelDialog.getLegalWorkshopCEOInfo(
                                inputNationalCodeOfCEO.getValueNationalCode(),
                                jalaliDate
                            )
                        }
                    }

                    override fun onDismissed() {}
                })
                datePickerStart?.show()
            }

            timer = object : CountDownTimer(5 * 60 * 1000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    timerIsFinished=false
                    var seconds = ((millisUntilFinished / 1000).toInt() % 60).toString()
                    var minutes = (millisUntilFinished / (1000 * 60) % 60).toString()

                    if (minutes.length == 1) minutes = "0$minutes"
                    if (seconds.length == 1) seconds = "0$seconds"
                    tvCountDown.text = "$minutes : $seconds"
                }

                override fun onFinish() {
                    timerIsFinished=true
                    if (!fragmentIsInBackground) showTimerFinishedAlert()
                }
            }
        }

    }

    private fun onClick() {
        viewBinding?.apply {
            btnSendRequest.setOnClickListener {
                if (inputNationalCode.getValue().isNotBlank() &&
                    inputNationalCodeOfCEO.getValueNationalCode().isNotBlank() &&
                    inputMobile.getValue().isNotBlank() &&
                    inputTel.getValue().isNotBlank() &&
                    inputEmail.getValue().isNotBlank()
                ) {
                    if (selectedCompanyTypeId.isBlank() || selectedCompanyTypeId=="0") {
                        showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR, getString(R.string.error_select_companyType))
                        return@setOnClickListener
                    }
                    if (jalaliDate.isBlank()) {
                        showAlertDialog(MessageOfRequestDialogFragment.MessageType.ERROR, getString(R.string.error_select_ceo_birthdate))
                        return@setOnClickListener
                    }

                    mViewModelDialog.getVerificationTicketForLegalWorkshopInfo(
                        inputMobile.getValue(),
                        inputEmail.getValue(),
                        inputNationalCodeOfCEO.getValueNationalCode()
                    )
                }
            }

            btnSubmit.setOnClickListener {

                if (inputVerificationCode.getValue().isNotBlank()) {
                    mViewModelDialog.sendVerifyTicketForLegalWorkshop(
                        jalaliDate,
                        getBranchCode(),
                        inputEmail.getValue(),
                        selectedCompanyTypeId,
                        inputMobile.getValue(),
                        inputNationalCodeOfCEO.getValueNationalCode(),
                        inputTel.getValue(),
                        inputVerificationCode.getValue(),
                        getWorkshopId(),
                        inputNationalCode.getValue()
                    )
                }
            }

            btnPrevStep.setOnClickListener {
                updateUiVisibility(false)
            }
        }
    }

    private fun getBirthDateByServerFormat(birthDateTimeStamp: Long): String {

        val d = Date(birthDateTimeStamp)
        val f: DateFormat = SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss")

        val date = f.format(d)

        return "$date GMT+0330"
    }


    private fun getWorkshopId(): String {
        return arguments?.getString(ARG_WORKSHOP_ID, "") ?: ""
    }

    private fun getBranchCode(): String {
        return arguments?.getString(ARG_BRANCH_CODE, "") ?: ""
    }

    override fun onStop() {
        fragmentIsInBackground = true
        super.onStop()
    }

    override fun onStart() {
        super.onStart()

        fragmentIsInBackground = false
        if (timerIsFinished) showTimerFinishedAlert()
    }

    private fun showTimerFinishedAlert() {

        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.message_send_verification_code_finished)
        )
        dialog.setDialogClickListener(object :
            DialogClickInterface.onClickListener {
            override fun onConfirmClick() {

                viewBinding?.apply {
                    updateUiVisibility(false)
                }
            }

            override fun onCancelClick() {
            }
        }
        )
        dialog.show(childFragmentManager, "returnToPrevPage")

    }

}