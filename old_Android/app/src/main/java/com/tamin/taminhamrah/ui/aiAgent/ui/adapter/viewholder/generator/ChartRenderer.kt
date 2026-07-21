package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import com.github.mikephil.charting.charts.BarChart
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tamin.taminhamrah.data.remote.models.services.AiChartItem
import com.tamin.taminhamrah.databinding.ItemChatBotChartBinding
import com.tamin.taminhamrah.utils.UiUtils

object ChartRenderer {

    class ChartHandle(val view: View, val chart: BarChart) {
        fun clearSelection() {
            chart.highlightValue(null)
        }

        fun setOnBarSelected(listener: OnBarSelectedListener) {
            chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val index = e?.x?.toInt() ?: return
                    listener.onBarSelected(index)
                }

                override fun onNothingSelected() {}
            })
        }
    }

    fun interface OnBarSelectedListener {
        fun onBarSelected(index: Int)
    }

    fun create(
        context: Context,
        items: List<AiChartItem>,
        typeface: Typeface? = null,
        onBarSelected: OnBarSelectedListener? = null
    ): ChartHandle {
        val binding = ItemChatBotChartBinding.inflate(LayoutInflater.from(context))
        render(binding.chart, items, typeface)
        if (onBarSelected != null) {
            binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    val index = e?.x?.toInt() ?: return
                    onBarSelected.onBarSelected(index)
                }

                override fun onNothingSelected() {}
            })
        }
        return ChartHandle(binding.root, binding.chart)
    }

    fun render(
        chart: BarChart,
        items: List<AiChartItem>,
        typeface: Typeface?,
        animate: Boolean = true
    ) {
        if (items.isEmpty()) {
            chart.clear()
            return
        }
        configureAxes(chart, items, typeface)
        applyData(chart, items, typeface)
        if (animate) chart.animateY(1500)
        chart.invalidate()
    }

    fun parseChartItems(json: String?): List<AiChartItem> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            Gson().fromJson(json, object : TypeToken<List<AiChartItem>>() {}.type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun configureAxes(chart: BarChart, items: List<AiChartItem>, typeface: Typeface?) {
        chart.apply {
            description.isEnabled = false
            setMaxVisibleValueCount(40)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            setDrawBarShadow(false)
            setDrawGridBackground(false)
            legend.isEnabled = false

            xAxis?.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = items.size
                this.typeface = typeface
                valueFormatter = IndexAxisValueFormatter(items.map { it.label })
            }

            axisLeft?.apply {
                setLabelCount(4, false)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f
                this.typeface = typeface
                isVerticalFadingEdgeEnabled = true
            }

            axisRight?.apply {
                setLabelCount(4, false)
                setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                spaceTop = 0f
                axisMinimum = 0f
                this.typeface = typeface
                textColor = android.R.color.transparent
                isVerticalFadingEdgeEnabled = true
            }
        }
    }

    private fun applyData(chart: BarChart, items: List<AiChartItem>, typeface: Typeface?) {
        val entries = items.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.value)
        }
        val dataSet = BarDataSet(entries, "Chart").apply {
            setColors(*UiUtils.VORDIPLOM_COLORS)
            valueTypeface = typeface
            setDrawValues(true)
        }
        chart.data = BarData(listOf<IBarDataSet>(dataSet))
        chart.setFitBars(false)
    }
}
