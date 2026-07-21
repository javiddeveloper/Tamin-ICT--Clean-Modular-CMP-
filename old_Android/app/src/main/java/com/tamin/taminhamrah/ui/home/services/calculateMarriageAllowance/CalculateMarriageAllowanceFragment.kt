package com.tamin.taminhamrah.ui.home.services.calculateMarriageAllowance

import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.responses.CalculateMarriageResponse
import com.tamin.taminhamrah.databinding.FragmentCalculateMarriageAllowanceBinding
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.home.services.wedingpresent.WeddingPresentViewModel
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.widget.DatePickerWidget
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date


@AndroidEntryPoint
class CalculateMarriageAllowanceFragment :
    BaseFragment<FragmentCalculateMarriageAllowanceBinding, WeddingPresentViewModel>() {

    override val mViewModel: WeddingPresentViewModel by viewModels()
    private var selectedDateString: String? = null

    val transition: Transition = Slide(Gravity.BOTTOM)

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_calculate_marriage_allowance
    }

    override fun setupObserver() {
        mViewModel.mldCalculateMarriageAllowance.observe(this, ::showResult)
    }


    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            moreViews = null,
        )
    }

    override fun getData() {

    }

    override fun onClick() {
        viewDataBinding?.apply {

            widgetDatePickerMarriage.setOnClickListener {
                rootLayoutResult.visibility = View.GONE
            }

            widgetDatePickerMarriage.setListener(object : DatePickerWidget.DateSelectOrListener {
                override fun onDateSelect(
                    jalaliDate: String,
                    gregorianDate: Date,
                    timeStamp: Long,
                    serverFormattedDate: String,
                    serverFormattedDateWithDayOffset: String
                ) {
                    selectedDateString = timeStamp.toString()
                }
            })


            btnCal.setOnClickListener {
                if (selectedDateString.isNullOrEmpty())
                    widgetDatePickerMarriage.setError(getString(R.string.error_select_date))
                selectedDateString?.let {
                    mViewModel.calculateMarriageAllowance(it)
                }
            }
        }
    }

    private fun setTransaction() {
        transition.duration = 600
        transition.addTarget(viewDataBinding?.rootLayoutResult)
        TransitionManager.beginDelayedTransition(viewDataBinding?.parent, transition)
    }

    private fun showResult(result: CalculateMarriageResponse) {
//        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result.isSuccess) {

                result.data?.let {
                    if (it[1].isNullOrEmpty()) {
                        var errStr: String? = null
                        if (it[0]?.trim() != "")
                            errStr = it[0]
                        showAlertDialog(
                            MessageOfRequestDialogFragment.MessageType.ERROR,
                            errStr ?: getString(R.string.unable_to_request)
                        )

                    } else {
                        viewDataBinding?.apply {
                            amountPayable = Utility.getNumberWithSeparatorForStringValue(it[1])
                            totalSalary = Utility.getNumberWithSeparatorForStringValue(it[0])
                            setTransaction()
                            rootLayoutResult.visibility = View.VISIBLE
                            //    }
                        }
                    }
            }
        }
    }



}