package com.tamin.taminhamrah.ui.home.services.employer.completeInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopCEOInfoResponse
import com.tamin.taminhamrah.data.remote.models.employer.legalWorkshop.LegalWorkshopInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.databinding.FragmentCompleteLegalWorkshopInfoBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
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
class CompleteInfoOfLegalWorkshopFragment :
    BaseFragment<FragmentCompleteLegalWorkshopInfoBinding, CompleteInfoViewModel>() {

    companion object {

         const val ARG_WORKSHOP_ID = "workshopId"
         const val ARG_BRANCH_CODE = "branchCode"

        fun newInstance(workshopId: String?, branchCode: String?): CompleteInfoOfLegalWorkshopFragment{
            val args = Bundle()
            args.putString(ARG_WORKSHOP_ID, workshopId)
            args.putString(ARG_BRANCH_CODE, branchCode)

            val fragment = CompleteInfoOfLegalWorkshopFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var timer: CountDownTimer? = null
    private var fragmentIsInBackground = false
    private var timerIsFinished=false

    override val mViewModel: CompleteInfoViewModel by viewModels()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_complete_legal_workshop_info
    }

    override fun setupObserver() {
        mViewModel.mldLegalWorkshopInfo.observe(viewLifecycleOwner, ::showLegalWorkshopInfo)
        mViewModel.mldLegalWorkshopCEOInfo.observe(viewLifecycleOwner, ::showLegalWorkshopCEOInfo)
        mViewModel.mldTicketResult.observe(viewLifecycleOwner, ::showTicketResult)
    }

    private fun showTicketResult(result: GeneralRes?) {
        if (result?.isSuccess == true) {
            viewDataBinding?.apply {
                groupData.visibility = View.GONE
                groupVerifyTicket.visibility = View.VISIBLE

                timer?.start()
            }
        } else {
            viewDataBinding?.apply {
                groupData.visibility = View.VISIBLE
                groupVerifyTicket.visibility = View.GONE
            }
        }
    }

    private fun showLegalWorkshopInfo(result: LegalWorkshopInfoResponse?) {
        if (result?.isSuccess == true) {
            viewDataBinding?.tvFullName?.text = result.data?.name?:"-"
        }else{
            viewDataBinding?.tvFullName?.text="-"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showLegalWorkshopCEOInfo(result: LegalWorkshopCEOInfoResponse?) {
        if (result?.isSuccess == true) {
            val fullName = "${result.data?.firstName?:""} ${result.data?.lastName?:""} "
            viewDataBinding?.tvFullNameOfCEO?.text = fullName.ifBlank { "-" }
        }
    }

    private var selectedCompanyTypeId = "0"
    private var birthDateTimeStamp = 0L
    override fun initView() {

        viewDataBinding?.apply {

            setupToolbar(
                appBar,
                appbarBackgroundImage.imageBackground,
            )
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
                        mViewModel.getLegalWorkshopInfo(text.toString())
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
                    /*if (inputNationalCodeOfCEO.getValue()
                            .isNotBlank() && text?.length == 10 && birthDateTimeStamp > 0
                    ) {
                        mViewModel.getLegalWorkshopCEOInfo(
                            inputNationalCodeOfCEO.getValue(),
                            getBirthDateByServerFormat(birthDateTimeStamp)
                        )
                    }*/
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            inputCompanyType.selectableInput.apply {
                setOnClickListener {
                    val dialog = MenuDialogFragment.newInstance(menuTitle =   getString(R.string.label_insurance_type))
                    dialog.setMenuListener(object : MenuInterface.OnFetchData {

                        override fun onFetch() {
                            this@CompleteInfoOfLegalWorkshopFragment.lifecycleScope.launchWhenCreated {
                                mViewModel.companyTypeFlow.collectLatest { pagingData ->
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
                        birthDateTimeStamp = myPersianPickerDate.timestamp

                        //  Sun Feb 22 1987 00:00:00 GMT+0330

//                        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                        isoFormat.timeZone = TimeZone.getTimeZone("BST")
//                        val date: Date = isoFormat.parse("2010-05-23T09:01:02")
//
//                        selectedDate = "Sun Feb 22 1987 00:00:00 GMT+0330"//myPersianPickerDate.gregorianDate.toString()+"(Iran Standard Time)"
                        widgetBirthDate.inputDate.setText("${myPersianPickerDate.persianYear}/$persianMonth/${myPersianPickerDate.persianDay}")

                        if (inputNationalCodeOfCEO.getValue().isNotBlank()) {
                            mViewModel.getLegalWorkshopCEOInfo(
                                inputNationalCodeOfCEO.getValue(),
                                getBirthDateByServerFormat(birthDateTimeStamp)
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

    private fun showTimerFinishedAlert() {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.ERROR,
            getString(R.string.message_send_verification_code_finished)
        )
        dialog.setDialogClickListener(object :
            DialogClickInterface.onClickListener {
            override fun onConfirmClick() {

                viewDataBinding?.apply {
                    groupData.visibility = View.VISIBLE
                    groupVerifyTicket.visibility = View.GONE
                }
            }

            override fun onCancelClick() {
            }
        }
        )
        dialog.show(childFragmentManager, "dkfflshfdh")
    }

    @SuppressLint("SimpleDateFormat")
    private fun getBirthDateByServerFormat(birthDateTimeStamp: Long): String {

        val d = Date(birthDateTimeStamp)
        val f: DateFormat = SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss")

        val date = f.format(d)

        return "$date GMT+0330"
    }

    override fun getData() {
    }

    @SuppressLint("SimpleDateFormat")
    override fun onClick() {

        viewDataBinding?.apply {
            btnSendRequest.setOnClickListener {
                if (inputNationalCodeOfCEO.getValue().isNotBlank() &&
                    inputMobile.getValue().isNotBlank() &&
                    inputEmail.getValue().isNotBlank()
                ) {
                    mViewModel.getVerificationTicketForLegalWorkshopInfo(
                        inputMobile.getValue(),
                        inputEmail.getValue(),
                        inputNationalCodeOfCEO.getValue()
                    )
                }
            }

            btnSubmit.setOnClickListener {
                //1987-02-21T20:30:00.000Z

//                    "birthDate": "1987-02-21T20:30:00.000Z",


//                2017-08-09T00:00:00.000
                val a = birthDateTimeStamp.toString()
                val date = Date(birthDateTimeStamp)
                val formatDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss").format(date)
                if (inputVerificationCode.getValue().isNotBlank()) {
                    mViewModel.sendVerifyTicketForLegalWorkshop(
                        formatDate.toString(),
                        getBranchCode(),
                        inputEmail.getValue(),
                        selectedCompanyTypeId,
                        inputMobile.getValue(),
                        inputNationalCodeOfCEO.getValue(),
                        inputTel.getValue(),
                        inputVerificationCode.getValue(),
                        getWorkshopId(),
                        inputNationalCode.getValue()
                    )
                }
            }

            btnPrevStep.setOnClickListener {
                groupVerifyTicket.visibility = View.GONE
                groupData.visibility = View.VISIBLE
            }
        }
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

}
