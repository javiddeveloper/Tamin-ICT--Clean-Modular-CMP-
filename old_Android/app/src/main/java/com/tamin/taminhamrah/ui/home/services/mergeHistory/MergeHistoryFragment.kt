package com.tamin.taminhamrah.ui.home.services.mergeHistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.tamin.taminhamrah.BR
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordResponse
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.data.remote.models.services.SendInsuranceHistoryToInstitutionModel
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModels
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryResponse
import com.tamin.taminhamrah.databinding.FragmentMergeCombinedRecordBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.ActionAppBarInterface
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.MenuInterface
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.MenuDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.ui.home.services.historyinsurance.AllHistoryInsuranceFragment
import com.tamin.taminhamrah.ui.home.services.wageandhistory.WageAndHistoryDetailFragment
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MergeHistoryFragment :
    BaseFragment<FragmentMergeCombinedRecordBinding, MergeHistoryViewModel>(),
    AdapterInterface.OnItemClickListener<String>,
    ActionAppBarInterface.OnActionClickListener {

    val listAdapter: CombinedHistoryAdapter by lazy {
        CombinedHistoryAdapter(this)
    }

    var pdfTitle = ""
    override val mViewModel: MergeHistoryViewModel by viewModels()
    var combinedHistoryList = arrayListOf<CombinedRecordModel>()
    var wageAndHistoryList = arrayListOf<WageAndHistoryModel>()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_merge_combined_record
    }

    override fun setupObserver() {
        mViewModel.mldDownloadPdf.observe(viewLifecycleOwner, ::showPDFResult)
        mViewModel.mldPensionCheck.observe(viewLifecycleOwner, ::onPensionCheck)
        mViewModel.mldWageAndHistory.observe(viewLifecycleOwner, ::onWageAndHistory)
        mViewModel.mldCombinedHistory.observe(viewLifecycleOwner, ::onCombinedHistory)
        mViewModel.mldSendHistoryCertificate.observe(viewLifecycleOwner, ::onSendCertificate)

    }

    private fun onWageAndHistory(result: WageAndHistoryResponse) {
        if (result.isSuccess) {
            wageAndHistoryList.addAll(result.data?.list ?: emptyList())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onCombinedHistory(result: CombinedRecordResponse) {
        if (result.isSuccess) {
            combinedHistoryList.clear()
            mViewModel.originalYearList.addAll(result.data?.list ?: emptyList())

            val mergeResult = result.data?.list?.map { item ->
                sumDayItems(item)
                item
            }?.filter {
                !it.isDuplicate
            }
            val mergeList = mergeResult ?: emptyList()
            if (combinedHistoryList.isEmpty() && mergeList.isEmpty()) {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_history_exists),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            } else {
                listAdapter.setItems(mergeList)

                if (combinedHistoryList.size <= 10) {
                    createVerticalChart()
                } else {
                    createHorizontalChart()
                }
                if (combinedHistoryList.isNotEmpty())
                    viewDataBinding?.apply {
                        val normalizedDuration = Utility.normalizeHistoryDuration(
                            combinedHistoryList[0].historyYears,
                            combinedHistoryList[0].historyMonths,
                            combinedHistoryList[0].historyDays
                        )
                        btnYears.text =
                            "${normalizedDuration.years}\n${getString(R.string.label_year)}"
                        btnMonths.text =
                            "${normalizedDuration.months}\n${getString(R.string.label_month)}"
                        btnDays.text =
                            "${normalizedDuration.days}\n${getString(R.string.label_day)}"
                        tvTotalDays.text = combinedHistoryList[0].sumHistoryYears ?: "0"
                    }
            }
        }
    }

    override fun initView() {
        viewDataBinding?.apply {
            recycler.getRecycler().apply {
                adapter = listAdapter
                if (itemDecorationCount == 0) {
                    addItemDecoration(UiUtils.VerticalItemMarginDecoration(40))
                }
            }
            setupToolbar(
                viewDataBinding?.appBar,
                viewDataBinding?.appbarBackgroundImage?.imageBackground,
                viewDataBinding?.containerButtons,
                R.drawable.ic_download,
                onActionAppBarClickListener = this@MergeHistoryFragment,
                R.drawable.ic_correspondence,
                {
                    mViewModel.sendAllInsuranceHistoryToInstitution()
                }
            )
        }
    }

    override fun getData() {
        mViewModel.pensionCheck()
    }

    override fun onClick() {
        viewDataBinding?.apply {
            btnZoomOut.setOnClickListener { (layoutChart.getChildAt(0) as? BarChart)?.zoomOut() }
            btnZoomIn.setOnClickListener { (layoutChart.getChildAt(0) as? BarChart)?.zoomIn() }
            btnChangeOrientation.setOnClickListener {
                when (btnChangeOrientation.tag) {
                    getString(R.string.tag_vertical_chart) -> createHorizontalChart()
                    getString(R.string.tag_horizontal_chart) -> createVerticalChart()
                }
            }
        }
    }

    private fun createVerticalChart() {
        val chartViewBinding: ViewDataBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.bar_chart,
            viewDataBinding?.parent,
            false
        )
        chartViewBinding.lifecycleOwner = this@MergeHistoryFragment
        chartViewBinding.executePendingBindings()

        viewDataBinding?.apply {
            layoutChart.removeAllViews()
            btnChangeOrientation.tag = getString(R.string.tag_vertical_chart)
            layoutChart.addView(chartViewBinding.root)
            (layoutChart.getChildAt(0) as? BarChart)?.let { setChartData(combinedHistoryList, it) }
        }
    }
    private fun onSendCertificate(data: SendInsuranceHistoryToInstitutionModel) {
        val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
        dialog.arguments = createBundle(
            MessageOfRequestDialogFragment.MessageType.SUCCESS,
            data.text ?: getString(R.string.send_insurance_history_to_institution)
        )
        dialog.setDialogClickListener(object : DialogClickInterface.onClickListener {
            override fun onConfirmClick() {

            }

            override fun onCancelClick() {
            }
        })
        dialog.show(childFragmentManager, AllHistoryInsuranceFragment::class.simpleName)
    }
    private fun createHorizontalChart() {

        val chartViewBinding: ViewDataBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.horizontal_chart,
            viewDataBinding?.parent,
            false
        )

        chartViewBinding.lifecycleOwner = this@MergeHistoryFragment
        chartViewBinding.executePendingBindings()

        viewDataBinding?.apply {
            layoutChart.removeAllViews()
            btnChangeOrientation.tag = getString(R.string.tag_horizontal_chart)
            layoutChart.addView(chartViewBinding.root)
            (layoutChart.getChildAt(0) as? BarChart)?.let{
                setChartData(combinedHistoryList,it)
            }
        }
    }

    private fun onPensionCheck(result: CheckInsuredInfoResponse) {
        if (!result.isSuccess) return
        when (result.data?.typeUser) {
            EnumTypeUser.ANONYMOUS.title -> {
                showAlertDialog(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    getString(R.string.error_recive_data),
                    dismissType = MessageOfRequestDialogFragment.DismissType.BACK_TO_PREVIOUS
                )
            }
            EnumTypeUser.PENSIONER.title -> {
                val dialog = DialogManagerMessageOfRequest.getInstanceOfDialog()
                dialog.arguments = createBundle(
                    MessageOfRequestDialogFragment.MessageType.ERROR,
                    result.data?.list?.get(1)
                        ?: getString(R.string.error_active_relation_user_is_pensioner)
                )
                dialog.setDialogClickListener(object :
                    DialogClickInterface.onClickListener {
                    override fun onConfirmClick() {
                        requireActivity().onBackPressed()
                    }

                    override fun onCancelClick() {
                    }
                })
                dialog.show(childFragmentManager, "Alert Dialog MessageOfRequest")
            }
            EnumTypeUser.INSURED.title -> {
                this@MergeHistoryFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.getHistoryInfo()
                }
            }
        }
    }

    private fun sumDayItems(newItem: CombinedRecordModel) {
        combinedHistoryList.forEach { oldItem ->
            oldItem.apply {
                if (hisYear == newItem.hisYear) {
                    hisMonth1 = sumMonth(hisMonth1, newItem.hisMonth1)
                    hisMonth2 = sumMonth(hisMonth2, newItem.hisMonth2)
                    hisMonth3 = sumMonth(hisMonth3, newItem.hisMonth3)
                    hisMonth4 = sumMonth(hisMonth4, newItem.hisMonth4)
                    hisMonth5 = sumMonth(hisMonth5, newItem.hisMonth5)
                    hisMonth6 = sumMonth(hisMonth6, newItem.hisMonth6)
                    hisMonth7 = sumMonth(hisMonth7, newItem.hisMonth7)
                    hisMonth8 = sumMonth(hisMonth8, newItem.hisMonth8)
                    hisMonth9 = sumMonth(hisMonth9, newItem.hisMonth9)
                    hisMonth10 = sumMonth(hisMonth10, newItem.hisMonth10)
                    hisMonth11 = sumMonth(hisMonth11, newItem.hisMonth11)
                    hisMonth12 = sumMonth(hisMonth12, newItem.hisMonth12)
                    newItem.isDuplicate = true
                    return@forEach
                }
            }
        }
        combinedHistoryList.add(newItem)
    }

    private fun sumMonth(monthOld: String?, monthNew: String?): String {
        var a = monthOld?.toInt() ?: 0
        val b = monthNew?.toInt() ?: 0
        a += b
        return a.toString()
    }

    private fun setChartData(list: ArrayList<CombinedRecordModel>, chart: HorizontalBarChart) {
        initChart(list, chart)
        val values = ArrayList<BarEntry>()
        for (i in list.indices) {
            values.add(BarEntry(i.toFloat(), list[i].sumYear?.toFloat()!!))
        }
        val set1 = BarDataSet(values, "Data Set")
        set1.setColors(*UiUtils.VORDIPLOM_COLORS)
        set1.setDrawValues(true)
        set1.valueTextColor =
            ContextCompat.getColor(chart.context, R.color.colorPrimaryLight)
        set1.valueTypeface = myTypeface

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)

        val data = BarData(dataSets)

        chart.apply {
            this.data = data
            setFitBars(false)
        }
    }

    private fun initChart(list: List<CombinedRecordModel?>, chart: HorizontalBarChart) {

        chart.apply {
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
                position = XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f // only intervals of 1 day
                labelCount = list.size
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = axisTextColorInt
                valueFormatter = IndexAxisValueFormatter(getChartIndexes(list))
                isVerticalFadingEdgeEnabled = true

            }

            axisLeft?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = axisTextColorInt
                isVerticalFadingEdgeEnabled = true
            }

            axisRight?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = android.R.color.transparent
                isVerticalFadingEdgeEnabled = true

            }

            axisLeft.axisLineColor =
                ContextCompat.getColor(context, R.color.red)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val bundle = Bundle()
                    e?.x?.let {
                        bundle.putParcelable(
                            MergeDetailHistoryFragment.ARG_SELECTED_ITEM,
                            mViewModel.originalYearList[it.toInt()]
                        )

                        val dialog = MergeDetailHistoryFragment()
                        dialog.arguments = bundle
                        dialog.show(
                            childFragmentManager,
                            MergeDetailHistoryFragment().javaClass.simpleName
                        )
                    }
                }

                override fun onNothingSelected() {
                }
            })
        }
    }

    private fun setChartData(list: ArrayList<CombinedRecordModel>, chart: BarChart) {
        initChart(list, chart)
        val values = ArrayList<BarEntry>()
        for (i in list.indices) {
            values.add(BarEntry(i.toFloat(), list[i].sumYear?.toFloat()!!))
        }
        val set1 = BarDataSet(values, "Data Set")
        set1.setColors(*UiUtils.VORDIPLOM_COLORS)
        set1.setDrawValues(true)
        set1.valueTextColor =
            ContextCompat.getColor(chart.context, R.color.colorPrimaryLight)
        set1.valueTypeface = myTypeface

        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)

        val data = BarData(dataSets)
        chart.apply {
            this.data = data
            setFitBars(false)
        }
    }

    private fun initChart(list: List<CombinedRecordModel?>, chart: BarChart) {

        chart.apply {
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
                position = XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f // only intervals of 1 day
                labelCount = list.size
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = axisTextColorInt
                setGridBackgroundColor(com.google.android.material.R.attr.colorTertiary)
                valueFormatter = IndexAxisValueFormatter(getChartIndexes(list))
                isVerticalFadingEdgeEnabled = true

            }

            axisLeft?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = axisTextColorInt
                isVerticalFadingEdgeEnabled = true
            }

            axisRight?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = axisLineColorInt
                textColor = android.R.color.transparent
                isVerticalFadingEdgeEnabled = true

            }

            axisLeft.axisLineColor =
                ContextCompat.getColor(context, R.color.red)
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val bundle = Bundle()
                    e?.x?.let {
                        bundle.putParcelable(
                            MergeDetailHistoryFragment.ARG_SELECTED_ITEM,
                            mViewModel.originalYearList[it.toInt()]
                        )
                        val dialog = MergeDetailHistoryFragment()
                        dialog.arguments = bundle
                        dialog.show(
                            childFragmentManager,
                            MergeDetailHistoryFragment().javaClass.simpleName
                        )
                    }
                }

                override fun onNothingSelected() {
                }
            })
        }
    }

    private fun getChartIndexes(list: List<CombinedRecordModel?>): ArrayList<String> {
        val label: ArrayList<String> = ArrayList()
        list.forEach {
            label.add("${it?.hisYear}")
        }
        return label
    }

    override fun onActionClick() {
        val dialog =
            MenuDialogFragment.newInstance(menuTitle = getString(R.string.label_download_file))
        dialog.setMenuListener(object : MenuInterface.OnFetchData {

            override fun onFetch() {
                this@MergeHistoryFragment.lifecycleScope.launchWhenCreated {
                    mViewModel.mldDownloadHistoryList.collectLatest { pagingData ->
                        dialog.updateData(pagingData)
                    }
                }
            }

        }, object : MenuInterface.OnResult {
            override fun onResult(itemResult: MenuModel) {
                when (itemResult.id) {
                    "0" -> {
                        mViewModel.downloadAllHistoryPdf()
                        pdfTitle = "کلیه سوابق"
                    }
                    "1" -> {
                        mViewModel.downloadWageAndHistoryPdf()
                        pdfTitle = "سوابق و ریز دستمزدها"
                    }
                    "2" -> {
                        mViewModel.downloadCombinedRecordPdf()
                        pdfTitle = "سوابق تلفیقی"
                    }
                }
            }
        })
        dialog.show(childFragmentManager, "")
    }

    private fun showPDFResult(result: PdfDownloadResponse) {
        if (result.isSuccess && result.pdf != null) {

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
            }




            val bundle = Bundle()
            bundle.putString(PdfViewerActivity.ARG_TITLE, pdfTitle)
            bundle.putString(PdfViewerActivity.ARG_PDF_FILE_PATH, file?.path)
            handlePageDestination(R.id.action_merge_history_to_pdf_viewer, bundle)


        }

    }

    override fun onItemClick(item: String, transitionView: View?, tag: String?) {
        val bundleHistory = Bundle()
        val allHistorySameYear = WageAndHistoryModels()
        allHistorySameYear.addAll(wageAndHistoryList.filter { it.hisyear == item })
        bundleHistory.putParcelableArrayList(Constants.ARRAYLIST, allHistorySameYear)
        val dialog = WageAndHistoryDetailFragment()
        dialog.arguments = bundleHistory
        dialog.show(childFragmentManager, MergeDetailHistoryFragment().javaClass.simpleName)
    }

    override fun onDestroyView() {
        viewDataBinding?.layoutChart?.removeAllViews()
        super.onDestroyView()
    }
}