package com.tamin.taminhamrah.ui.home.services.combinedRecord

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.filter
import androidx.paging.map
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
import com.tamin.taminhamrah.data.remote.models.pdfFile.PdfDownloadResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckInsuredInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.CombinedRecordModel
import com.tamin.taminhamrah.data.remote.models.services.EnumTypeUser
import com.tamin.taminhamrah.databinding.FragmentCombinedRecordBinding
import com.tamin.taminhamrah.ui.PdfViewerActivity
import com.tamin.taminhamrah.ui.appinterface.ActionAppBarInterface
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import com.tamin.taminhamrah.ui.appinterface.DialogClickInterface
import com.tamin.taminhamrah.ui.appinterface.EndOfPaginationListener
import com.tamin.taminhamrah.ui.base.BaseFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.MessageOfRequestDialogFragment
import com.tamin.taminhamrah.ui.dialog.messageOfReques.manger.DialogManagerMessageOfRequest
import com.tamin.taminhamrah.utils.UiUtils
import com.tamin.taminhamrah.utils.Utility
import com.tamin.taminhamrah.utils.extentions.createBundle
import com.tamin.taminhamrah.utils.extentions.setupRecycler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber


@AndroidEntryPoint
class CombinedRecordFragment :
    BaseFragment<FragmentCombinedRecordBinding, CombinedRecordViewModel>(),
    AdapterInterface.OnItemClickListener<String>,
    ActionAppBarInterface.OnActionClickListener {


    private lateinit var listAdapter: CombinedRecordAdapter
    private var listYears = arrayListOf<CombinedRecordModel>()
    override val mViewModel: CombinedRecordViewModel by viewModels()
    var listOfCombinedRecordMerge = arrayListOf<CombinedRecordModel>()

    override fun getBindingVariable(): Pair<Int, Any?> {
        return Pair(BR.viewModel, mViewModel)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_combined_record
    }


    override fun setupObserver() {
        mViewModel.mldPdf.observe(this, ::showPDFResult)
        mViewModel.mldPensionCheck.observe(this, ::onPensionCheck)
    }

    override fun initView() {
        viewDataBinding?.let {

            listAdapter = CombinedRecordAdapter().apply {
                onItemClickListener = this@CombinedRecordFragment
            }

            setupRecycler(it.recycler, listAdapter, listener = object : EndOfPaginationListener {
                @SuppressLint("SetTextI18n")
                override fun onEndOfPagination(tag: String) {
                    setChartData(listOfCombinedRecordMerge)
                    if (listOfCombinedRecordMerge.isNotEmpty())
                        viewDataBinding?.apply {
                            val normalizedDuration = Utility.normalizeHistoryDuration(
                                listOfCombinedRecordMerge[0].historyYears,
                                listOfCombinedRecordMerge[0].historyMonths,
                                listOfCombinedRecordMerge[0].historyDays
                            )
                            btnYears.text =
                                "${normalizedDuration.years}\n${getString(R.string.label_year)}"
                            btnMonths.text =
                                "${normalizedDuration.months}\n${getString(R.string.label_month)}"
                            btnDays.text =
                                "${normalizedDuration.days}\n${getString(R.string.label_day)}"
                            tvTotalDays.text = listOfCombinedRecordMerge[0].sumHistoryYears ?: "0"
                        }
                }
            })

            it.appBar.toolbar.imgInfo.setOnClickListener {
                mViewModel.saveBoolean(Constants.TapTargetCombinedRecordFragment, false)
                setViewForShowGide()
            }
        }
        setupToolbar(
            viewDataBinding?.appBar,
            viewDataBinding?.appbarBackgroundImage?.imageBackground,
            viewDataBinding?.containerButtons,
            R.drawable.ic_download,
            onActionAppBarClickListener = this
        )
        setViewForShowGide()
    }

    override fun getData() {
        mViewModel.pensionCheck()
    }

    override fun onClick() {

    }

    var requestTypeId = ""

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
                    result.data?.list?.get(1) ?: getString(R.string.error_active_relation_user_is_pensioner)
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
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    mViewModel.getCombinedRecordList.collectLatest { pagingData ->
                        val mergeResult = pagingData.map { newItem ->
                            listYears.add(newItem)
                            sumDayItems(newItem)
                            newItem
                        }.filter {
                            !it.isDuplicate
                        }
                        listAdapter.submitData(mergeResult)
                    }
                }
            }
        }
    }


    private fun sumDayItems(newItem: CombinedRecordModel) {
        listOfCombinedRecordMerge.forEach { oldItem ->
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
        listOfCombinedRecordMerge.add(newItem)
    }

    private fun sumMonth(monthOld: String?, monthNew: String?): String {
        var a = monthOld?.toInt() ?: 0
        val b = monthNew?.toInt() ?: 0
        a += b
        return a.toString()
    }

    private fun setChartData(list: ArrayList<CombinedRecordModel>) {
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
            this.data = data
            setFitBars(false)
        }
    }

    private fun initChart(list: List<CombinedRecordModel?>) {

        viewDataBinding?.chart?.apply {
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
                axisLineColor = R.color.lineColor
                textColor = R.color.textColorSubTitle
                valueFormatter = IndexAxisValueFormatter(getChartIndexes(list))
            }

            axisLeft?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = R.color.lineColor
                textColor = R.color.textColorSubTitle
            }

            axisRight?.apply {
                setLabelCount(4, false)
                //  leftAxis.setValueFormatter(custom)
                setPosition(YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f // this replaces setStartAtZero(true)
                this.typeface = myTypeface
                axisLineColor = R.color.lineColor
                textColor = android.R.color.transparent

            }
            axisLeft.axisLineColor = R.color.red
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    Timber.tag("onValueSelected: ").i(e.toString())

                    val bundle = Bundle()
                    e?.x?.let {
                        bundle.putParcelable(
                            CombinedRecordDetailFragment.ARG_SELECTED_ITEM,
                            listYears.get(it.toInt())
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

    private fun setViewForShowGide() {
        /*  try {
              if (!mViewModel.loadBoolean(Constants.TapTargetCombinedRecordFragment)) {
                  val ViewsList = arrayListOf<TapTargetModel>()
                  (viewDataBinding)?.appBar?.toolbar?.imgInfo?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_img_info_tag_target_view,R.string.detail_info_img_tag_target_view))
                  }
                  this.viewDataBinding?.appBar?.toolbar?.imgAction?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_img_action_download_history_tag_target_view,R.string.detail_action_download_history_imgaction_tag_target_view))
                  }
                  this.viewDataBinding?.containerButtons?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_container_buttons_tag_target_view,R.string.detail_container_buttons_tag_target_view,shape = Shape.RECT))
                  }
                  this.viewDataBinding?.tvTotalDays?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_all_day_hisory_tag_target_view,R.string.detail_all_day_hisory_tag_target_view,shape = Shape.RECT))
                  }
                  this.viewDataBinding?.chart?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_chart_tag_target_view,R.string.detail_chart_tag_target_view,shape = Shape.RECT))
                  }
                  this.viewDataBinding?.recycler?.let {
                      ViewsList.add(TapTargetModel(it,R.string.title_hisory_tag_target_view,R.string.detail_hisory_tag_target_view,shape = Shape.RECT))
                  }
                  ViewsList?.let {
                          showGide(it)
                  }
                  mViewModel.saveBoolean(Constants.TapTargetCombinedRecordFragment, true)
              }
          } catch (e: Exception) {
              Log.e("showGide: ", e.message.toString())
          }*/
    }

    override fun onActionClick() {
        mViewModel.downloadTalfighiPdf()
    }

    private fun showPDFResult(result: PdfDownloadResponse) {
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
            handlePageDestination(R.id.action_combined_records_to_pdf_viewer, bundle)
        }
    }


    override fun onItemClick(year: String, transitionView: View?, tag: String?) {
        val bundle = Bundle()
        listYears.forEach { item ->
            if (item.hisYear == year) {
                bundle.putParcelable(
                    CombinedRecordDetailFragment.ARG_SELECTED_ITEM,
                    item
                )
                val dialog = CombinedRecordDetailFragment()
                dialog.arguments = bundle
                dialog.show(childFragmentManager, "gkjhgkjh")
                return@forEach
            }
        }

    }
}