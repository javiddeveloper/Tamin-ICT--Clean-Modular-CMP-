package com.tamin.taminhamrah.ui.home.services.calculateWagePension

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.PersonalInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.data.remote.models.services.workshop.MultipleWorkShopResponse
import com.tamin.taminhamrah.databinding.FragmentCalculateWagePensionBinding
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.combinedRecord.CombinedRecordDetailFragment
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil

@AndroidEntryPoint
class CalculateWagePensionFragment :
    BaseFragment<FragmentCalculateWagePensionBinding, CalculateWagePensionViewModel>(),
    DialogClickInterface.onClickListener {
    override val mViewModel: CalculateWagePensionViewModel by viewModels()

    private var premiumPaymentHistoryYear = 0.0
    private var eligibleAmountPension = 0L
    private var averageSalaryLastTwoYears = 0L

    /**Because there is no API from the server to receive information in this field,
    and after checking the backend's code, I realized that this amount is hardcoded**/

    private val basicWage = 11112690
    private var branchCode: String? = null
    private var insuranceNumber: String? = null

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_calculate_wage_pension
    }

    override fun initView() {
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground
        )
        onClick()
    }

    override fun getData() {
        this@CalculateWagePensionFragment.lifecycleScope.launchWhenCreated {
            mViewModel.getCombinedRecordList()
        }
    }

    override fun onClick() {
        viewDataBinding?.let {
            it.swWorkingSeveralWorkshopsSimultaneously.setOnCheckedChangeListener { _, switchChecked ->
                if (switchChecked)
                    mViewModel.getPersonalInfo()
            }
        }
    }


    override fun setupObserver() {
        mViewModel.mldGetWageAndHistory.observe(this, ::showResultWageAndHistory)
        mViewModel.mldCombinedList.observe(this, ::showResultCombinedList)

        mViewModel.mldPersonalInfo.observe(this, ::onPersonalInfoResponse)

        mViewModel.mldIsMultipleWorkshops.observe(this, ::showResultMultipleWorkshops)
        mViewModel.mldCalculateMultipleWorkshops.observe(
            this,
            ::showResultCalculateMultipleWorkshops
        )
    }

    private fun showResultCalculateMultipleWorkshops(result: MultipleWorkShopResponse) {
        if (result.isSuccess)
            result.data?.result?.toLong()?.let {
                eligibleAmountPension = it
                viewDataBinding?.let { binding ->
                    binding.valueEligibleAmountPension.text =
                        Utility.getRialWithSeparator(eligibleAmountPension)
                }
            }

    }

    private fun showResultMultipleWorkshops(result: MultipleWorkShopResponse) {
        if (result.isSuccess) {
            if (result.data?.result == 0) {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.INFO,
                    requireContext().getString(R.string.error_multiple_workshop)
                )
                dialog.setDialogClickListener(this)
                dialog.show(childFragmentManager, "Alert Dialog Multiple Workshops")
            } else if (result.data?.result == 1) {
                branchCode?.let { branchCode ->
                    insuranceNumber?.let { insuranceNumber ->
                        mViewModel.calculateMultipleWorkshops(branchCode, insuranceNumber)
                    }
                }
            }
        }
    }

    private fun onPersonalInfoResponse(result: PersonalInfoResponse) {
        if (result.isSuccess) {
            result.data?.let {
                mViewModel.isMultipleWorkshops(
                    result.data.organizationId ?: "",
                    result.data.insuranceId ?: ""
                )
            }
        }
    }

//    private fun showResultIdentityInfo(result: UserInfoResponse) {
//        if (result.isSuccess) {
//            result.data?.let {
//                insuranceNumber = it.insuranceNumber ?: ""
//                branchCode?.let { branchCode ->
//                    insuranceNumber?.let { insuranceNumber ->
//                        mViewModel.isMultipleWorkshops(branchCode, insuranceNumber)
//                    }
//                }
//            }
//        }
//    }

//    private fun showResultLastRelation(result: LastRelationResponse) {
//        if (result.isSuccess) {
//            branchCode = result.data?.organizationId
//            mViewModel.getIdentityInfo()
//        }
//    }

    private fun showResultCombinedList(result: CombinedRecordResponse) {
        if (result.isSuccess) {

            result.data?.list?.let { list ->
                if (list.isEmpty())
                    return

                setChartData(list)
                viewDataBinding?.let { binding ->
                    val normalizedDuration = Utility.normalizeHistoryDuration(
                        list[0].historyYears,
                        list[0].historyMonths,
                        list[0].historyDays
                    )
                    "${normalizedDuration.years}\n${getString(R.string.label_year)}".also {
                        binding.btnYears.text = it
                    }
                    "${normalizedDuration.months}\n${getString(R.string.label_month)}".also {
                        binding.btnMonths.text = it
                    }
                    "${normalizedDuration.days}\n${getString(R.string.label_day)}".also {
                        binding.btnDays.text = it
                    }
                    binding.tvTotalDays.text = list[0].sumHistoryYears ?: "0"
                }
            }

            result.data?.list?.get(0)?.sumHistoryYears?.let { year ->
                premiumPaymentHistoryYear = year.toDouble() / 365

            }
            mViewModel.getWageAndInsuranceHistory()
        }
    }

    private fun showResultWageAndHistory(result: WageAndHistoryResponse) {
        if (result.isSuccess) {
            result.data?.list?.let { data ->
                internalCalc(data)
            }
        }
    }

    private fun internalCalc(list: List<WageAndHistoryModel>) {
        val listDays = arrayListOf<String>()
        val listWages = arrayListOf<String>()
        (list.size - 1 downTo 0).forEach { i ->
            list[i].hismon12?.let { mon ->
                listDays.add(mon)
                list[i].hiswage12?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon11?.let { mon ->
                listDays.add(mon)
                list[i].hiswage11?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon10?.let { mon ->
                listDays.add(mon)
                list[i].hiswage10?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon9?.let { mon ->
                listDays.add(mon)
                list[i].hiswage9?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon8?.let { mon ->
                listDays.add(mon)
                list[i].hiswage8?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon7?.let { mon ->
                listDays.add(mon)
                list[i].hiswage7?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon6?.let { mon ->
                listDays.add(mon)
                list[i].hiswage6?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon5?.let { mon ->
                listDays.add(mon)
                list[i].hiswage5?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon4?.let { mon ->
                listDays.add(mon)
                list[i].hiswage4?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon3?.let { mon ->
                listDays.add(mon)
                list[i].hiswage3?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon2?.let { mon ->
                listDays.add(mon)
                list[i].hiswage2?.let { wage ->
                    listWages.add(wage)
                }
            }
            list[i].hismon1?.let { mon ->
                listDays.add(mon)
                list[i].hiswage1?.let { wage ->
                    listWages.add(wage)
                }
            }
        }
        var sumDays = 0
        var sumWages = 0.0


        for (i in 0 until listDays.size) {
            if (sumDays < 730) {
                sumDays += listDays[i].toInt()
                sumWages += listWages[i].toInt()
            } else {
                break
            }
        }
        val data1 =
            BigDecimal(premiumPaymentHistoryYear).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        val data2 = ceil(sumWages / 24)
        var data3 = ceil((data2 / 30) * data1)
        if (data1 >= 20 && data3 < 11112690) {
            data3 = basicWage.toDouble()
        }
        if (data1 < 20) {
            val minWage = (data1 / 30) * basicWage
            if (data3 < minWage) {
                data3 = minWage
            }
        }
        averageSalaryLastTwoYears = data2.toLong()
        eligibleAmountPension = data3.toLong()
        viewDataBinding?.let { binding ->
            binding.valueEligibleAmountPension.text =
                Utility.getRialWithSeparator(eligibleAmountPension)
            String.format("%.2f", premiumPaymentHistoryYear)
                .also { binding.valuePremiumPaymentHistoryYear.text = it }
            binding.valueAverageSalaryLastTwoYearsRials.text =
                Utility.getRialWithSeparator(averageSalaryLastTwoYears)
        }
    }


    override fun onConfirmClick() {
        viewDataBinding?.let {
            it.swWorkingSeveralWorkshopsSimultaneously.isChecked = false
        }
    }

    override fun onCancelClick() {
    }

    private fun setChartData(list: List<CombinedRecordModel>) {
        initChart(list)
        val values = ArrayList<BarEntry>()
        for (i in list.indices) {
            values.add(BarEntry(i.toFloat(), list[i].sumYear?.toFloat()!!))
        }
        val set1 = BarDataSet(values, "Data Set")
        set1.setColors(*UiUtils.VORDIPLOM_COLORS)
        set1.setDrawValues(true)
        set1.valueTextColor = R.color.colorPrimaryLight
        set1.valueTypeface = myTypeface
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        viewDataBinding?.chart?.apply {
            set1.valueTextColor =
                ContextCompat.getColor(this.context, R.color.colorPrimaryLight)
            this.data = data
            setFitBars(false)
        }
    }

    private fun initChart(list: List<CombinedRecordModel?>) {

        viewDataBinding?.chart?.apply {
            val axisLineColorInt =
                ContextCompat.getColor(context, R.color.lineColor)
            val axisTextColorInt =
                ContextCompat.getColor(context, R.color.textColorSubTitle)

            description.isEnabled = false

            // if more than 60 entries are displayed in the chart, no values will be drawn
            setMaxVisibleValueCount(40)

            // scaling can now only be done on x- and y-axis separately
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false

            setDrawBarShadow(false)
            setDrawGridBackground(false)

            // add a nice and smooth animation
            animateY(1500)

            legend.isEnabled = false

            xAxis?.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f // only intervals of 1 day
                labelCount = list.size
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = axisTextColorInt
                valueFormatter = IndexAxisValueFormatter(getChartIndexes(list))
            }

            axisLeft?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = axisTextColorInt
            }

            axisRight?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = android.R.color.transparent

            }
            axisLeft.axisLineColor = ContextCompat.getColor(context, R.color.red)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    Timber.tag("onValueSelected: ").i(e.toString())
                    val bundle = Bundle()
                    e?.x?.let {
                        bundle.putParcelable(
                            CombinedRecordDetailFragment.ARG_SELECTED_ITEM,
                            mViewModel.mldCombinedList.value?.data?.list?.get(it.toInt())
                        )
                        val dialog = CombinedRecordDetailFragment()
                        dialog.arguments = bundle
                        dialog.show(childFragmentManager, "gkjhgkjh")
                    }
                }

                override fun onNothingSelected() {
                }

            })
        }

    }

    fun getChartIndexes(list: List<CombinedRecordModel?>): ArrayList<String> {
        val label: ArrayList<String> = ArrayList()
        list.forEach {
            label.add("${it?.hisYear}")
        }
        return label
    }

}
