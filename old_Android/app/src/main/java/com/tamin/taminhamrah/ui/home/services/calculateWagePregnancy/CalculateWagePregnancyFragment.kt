package com.tamin.taminhamrah.ui.home.services.calculateWagePregnancy

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.widget.ScrollView
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.databinding.FragmentCalculateWagePregnancyBinding
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.NotNull

@AndroidEntryPoint
class CalculateWagePregnancyFragment :
    BaseFragment<FragmentCalculateWagePregnancyBinding, CalculateWagePregnancyViewModel>(),
    DialogClickInterface.onClickListener {

    override val mViewModel: CalculateWagePregnancyViewModel by viewModels()
    val transition: Transition = Slide(Gravity.BOTTOM)
    private var selectedStartDateString: String? = null
    private var selectedEndDateString: String? = null
    private var isMan: Boolean = false

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_calculate_wage_pregnancy
    }

    override fun setupObserver() {
        mViewModel.mldCalculateWagePregnancy.observe(this, ::showResult)
        mViewModel.mldCheckGender.observe(this, ::checkGender)
    }


    private fun checkGender(result: String) {
        if (result != "" && (result.trim() == "m" || result.trim() == "01")) {
            val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
            dialog.arguments = createBundle(
                MessageOfRequestDialogFragment.MessageType.ERROR,
                requireContext().getString(R.string.is_not_possible_apply_pregnancy_allowance_for_men)
            )
            isMan = true
            dialog.setDialogClickListener(this)
            dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
        }
    }


    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null,
        )
    }

    override fun getData() {
        mViewModel.getGender()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            widgetDatePickerStart.inputDate.setOnClickListener {
                widgetDatePickerStart.tilDate.isErrorEnabled = false
                rootLayoutResult.visibility = View.GONE
                val datePickerStart = getDatePicker()
                datePickerStart?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else "${MyPersianPickerDate.persianMonth}"
                        selectedStartDateString = MyPersianPickerDate.timestamp.toString()
                        widgetDatePickerStart.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")

                        val diffDay = Utility.differenceBetweenTimestamps(
                            selectedStartDateString?.toLong(),
                            selectedEndDateString?.toLong()
                        )
                        if (diffDay > 0) {
                            groupShowillDays.visibility = View.VISIBLE
                            val strDate = createCustomTextColor(diffDay)
                            valueDiffrentIllDay.apply {
                                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(
                                        strDate,
                                        Html.FROM_HTML_MODE_LEGACY
                                    )
                                } else {
                                    Html.fromHtml(strDate)
                                }
                            }

                        } else {
                            groupShowillDays.visibility = View.GONE
                        }


                    }

                    override fun onDismissed() {}
                })
                datePickerStart?.show()
            }

            widgetDatePickerEnd.inputDate.setOnClickListener {
                widgetDatePickerEnd.tilDate.isErrorEnabled = false
                rootLayoutResult.visibility = View.GONE
                val datePickerEnd = getDatePicker()
                datePickerEnd?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSelected(@NotNull MyPersianPickerDate: MyPersianPickerDate) {
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else "${MyPersianPickerDate.persianMonth}"
                        selectedEndDateString = MyPersianPickerDate.timestamp.toString()
                        widgetDatePickerEnd.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                        val diffDay = Utility.differenceBetweenTimestamps(
                            selectedStartDateString?.toLong(),
                            selectedEndDateString?.toLong()
                        )
                        if (diffDay > 0) {
                            groupShowillDays.visibility = View.VISIBLE
                            val strDate = createCustomTextColor(diffDay)
                            valueDiffrentIllDay.apply {
                                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Html.fromHtml(
                                        strDate,
                                        Html.FROM_HTML_MODE_LEGACY
                                    )
                                } else {
                                    Html.fromHtml(strDate)
                                }
                            }

                        } else {
                            groupShowillDays.visibility = View.GONE
                        }

                    }

                    override fun onDismissed() {}
                })
                datePickerEnd?.show()
            }

            btnCal.setOnClickListener {
                if (selectedStartDateString.isNullOrEmpty())
                    widgetDatePickerStart.tilDate.error =
                        getString(R.string.error_select_start_rest_date)
                else if (selectedEndDateString.isNullOrEmpty())
                    widgetDatePickerEnd.tilDate.error =
                        getString(R.string.error_select_end_rest_date)
                selectedStartDateString?.let { start ->
                    selectedEndDateString?.let { end ->
                        if (start.toLong() > end.toLong()) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.INFO,
                                requireContext().getString(R.string.error_start_date_is_larger)
                            )
                        } else {
                            mViewModel.calculateWagePregnancyDays(start, end)
                        }
                    }
                }
            }

        }
    }


    private fun showResult(result: Resource<List<String>?>) {
        (requireActivity() as? MainActivity)?.handleResponse(result)
        when (result.status) {
            Resource.Status.SUCCESS -> {
                hideLoading()

                if (!result.data.isNullOrEmpty()){
                    if (result.data.size==2){
                        if(result.data[1].isNullOrBlank()) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.ERROR,
                                result.data[0]
                            )
                        }else{
                            viewDataBinding?.apply {
                                amountPayable = Utility.getNumberWithSeparatorForStringValue(result.data[1])
                                totalSalary = Utility.getNumberWithSeparatorForStringValue(result.data[0])
                                setTransaction()
                                rootLayoutResult.visibility = View.VISIBLE
                                this.appBar.appBarView.setExpanded(false, true)
                                this.nestedScrollView.apply {
                                    post {
                                        fullScroll(ScrollView.FOCUS_DOWN)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun setTransaction() {
        transition.duration = 600
        transition.addTarget(viewDataBinding?.rootLayoutResult)
        TransitionManager.beginDelayedTransition(viewDataBinding?.parent, transition)
    }

    override fun onConfirmClick() {
        if (isMan)
        //  handlePageDestination(R.id.action_calculateWagePregnancyFragment_to_servicesFragment)
            requireActivity().onBackPressed()

    }

    override fun onCancelClick() {
    }

    private fun createCustomTextColor(diffDay: Long): String {
        return "<font color=" + "#878787" + ">" + "تعداد روز بارداری " + "</font>" +
                "<font color=" + "#67e693" + ">" + "<b>" + diffDay + "</b>" + "</font>" +
                "<font color=" + "#878787" + ">" + " روز " + "</font>"
    }
}

