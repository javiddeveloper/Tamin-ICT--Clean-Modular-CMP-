package com.tamin.taminhamrah.ui.home.services.calculateWageIllDays

import android.annotation.SuppressLint
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.widget.ScrollView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.Resource
import com.tamin.taminhamrah.databinding.FragmentCalculateWageIllDaysBinding
import com.tamin.taminhamrah.ui.LocalPagingSource
import com.tamin.taminhamrah.ui.MainActivity
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.openImageTypeMenu
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerDate
import com.tamin.taminhamrah.utils.myDatePicker.MyPersianPickerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.annotations.NotNull

@AndroidEntryPoint
class CalculateWageIllDaysFragment :
    BaseFragment<FragmentCalculateWageIllDaysBinding, CalculateWageIllDaysViewModel>() {

    override val mViewModel: CalculateWageIllDaysViewModel by viewModels()
    val transition: Transition = Slide(Gravity.BOTTOM)

    private var selectedStartDateString: String? = null
    private var selectedEndDateString: String? = null

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_calculate_wage_ill_days
    }

    override fun setupObserver() {
        mViewModel.mldCalculateWageIllDays.observe(this, ::showResult)
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
            widgetDatePickerStart.inputDate.setOnClickListener {
                widgetDatePickerStart.tilDate.isErrorEnabled = false
                rootLayoutResult.visibility = View.GONE
                val datePickerStart = getDatePicker()
                datePickerStart?.setListener(object : MyPersianPickerListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSelected( MyPersianPickerDate: MyPersianPickerDate) {
                        val persianMonth = if (MyPersianPickerDate.persianMonth in 1..9) {
                            "0${MyPersianPickerDate.persianMonth}"
                        } else {"${MyPersianPickerDate.persianMonth}"}
                        selectedStartDateString = MyPersianPickerDate.timestamp.toString()
                        widgetDatePickerStart.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                        viewDataBinding?.let {
                            val diffDay = Utility.differenceBetweenTimestamps(
                                selectedStartDateString?.toLong(),
                                selectedEndDateString?.toLong())
                            if (diffDay > 0) {
                                groupShowillDays.visibility = View.VISIBLE
                                val strDate = createCustomTextColor(diffDay)
                                it.valueDiffrentIllDay.apply {
                                    text =  HtmlCompat.fromHtml(strDate, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                }
                            } else {
                                groupShowillDays.visibility = View.GONE
                            }
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
                        viewDataBinding?.let {
                            val diffDay = Utility.differenceBetweenTimestamps(selectedStartDateString?.toLong(),selectedEndDateString?.toLong())
                            if (diffDay > 0) {
                                groupShowillDays.visibility = View.VISIBLE
                                val strDate = createCustomTextColor(diffDay)
                                it.valueDiffrentIllDay.apply {
                                    text =  HtmlCompat.fromHtml(strDate, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                }

                            } else {
                                groupShowillDays.visibility = View.GONE
                            }
                        }
                        widgetDatePickerEnd.inputDate.setText("${MyPersianPickerDate.persianYear}/$persianMonth/${MyPersianPickerDate.persianDay}")
                    }
                    override fun onDismissed() {}
                })
                datePickerEnd?.show()
            }
            var maritalId:String? = null
            selectMaritalStatus.getIt().showSoftInputOnFocus = false
            selectMaritalStatus.getIt().setOnClickListener {
                selectMaritalStatus.getLayout().isErrorEnabled = false
                this@CalculateWageIllDaysFragment.lifecycleScope.launchWhenCreated {
                    val pager = Pager(config = PagingConfig(Constants.QUERY_PAGE_SIZE_10, 2),
                        pagingSourceFactory = {
                            LocalPagingSource(mViewModel.getMaritalStatusList())
                        })

                    pager.flow.cachedIn(lifecycleScope).collectLatest { pagingData ->
                        openImageTypeMenu(pagingData, onResultCallBack = object : MenuInterface.OnResult {
                            override fun onResult(itemResult: MenuModel) {
                                itemResult.id?.let{
                                    maritalId = it
                                    itemResult.title?.let { it1 -> selectMaritalStatus.setValue(it1) }
                                }
                            }
                        })
                    }
                }
            }

            btnCal.setOnClickListener {
                if (selectedStartDateString.isNullOrEmpty())
                    widgetDatePickerStart.tilDate.error = getString(R.string.error_select_start_rest_date)
                else
                    if (selectedEndDateString.isNullOrEmpty())
                        widgetDatePickerEnd.tilDate.error = getString(R.string.error_select_end_rest_date)

                selectedStartDateString?.let { start ->
                    selectedEndDateString?.let { end ->
                        if (start.toLong() > end.toLong()) {
                            showAlertDialog(
                                MessageOfRequestDialogFragment.MessageType.INFO,
                                requireContext().getString(R.string.error_start_date_is_larger)
                            )
                        } else {

                            if (maritalId == null) {
                                selectMaritalStatus.getLayout().error = getString(R.string.error_select_marital_status)
                            }
                            maritalId?.let { spinner ->
                                mViewModel.calculateWageIllDays(start, end, spinner)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showResult(result: Resource<List<String?>?>) {
        (requireActivity() as? MainActivity)?.handleResponse(result)
        if (result.status == Resource.Status.SUCCESS) {

            result.data?.let {
                if (it[1].isNullOrEmpty()) {
                    showAlertDialog(
                        MessageOfRequestDialogFragment.MessageType.ERROR,
                        it[0] ?: ""
                    )
                } else {
                    viewDataBinding?.apply {
                        amountPayable = Utility.getNumberWithSeparatorForStringValue(it[1])
                        totalSalary = Utility.getNumberWithSeparatorForStringValue(it[0])
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

    private fun setTransaction() {
        transition.duration = 600
        transition.addTarget(viewDataBinding?.rootLayoutResult)
        TransitionManager.beginDelayedTransition(viewDataBinding?.parent, transition)
    }

    private fun createCustomTextColor(diffDay: Long): String{
      return  "<font color=" + "#878787" + ">" + "تعداد روز بیماری " + "</font>" +
                "<font color=" + "#67e693" + ">" + "<b>" + diffDay + "</b>" + "</font>" +
                "<font color=" + "#878787" + ">" + " روز " + "</font>"
    }

}