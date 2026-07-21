package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModels
import com.tamin.taminhamrah.ui.home.services.wageandhistory.WageAndHistoryDetailAdapter
import kotlin.reflect.KClass

class WageAndHistoryChartDetailRenderer : ChartDetailRenderer {
    override val actionKey: String = "WAGE_AND_HISTORY_DETAIL"
    override val payloadType: KClass<*> = WageAndHistoryModels::class

    override fun render(
        host: FormHost,
        chartHandle: ChartRenderer.ChartHandle,
        payload: Any
    ) {
        val typedPayload = payload as? WageAndHistoryModels ?: return
        ExpandableBottomSheetFragment.show(
            host.fragmentManager,
            onDismiss = { chartHandle.clearSelection() }
        ) { context, container ->
            val adapter = WageAndHistoryDetailAdapter()
            val recycler = RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = adapter
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            adapter.setItems(typedPayload)
            container.addView(recycler)
        }
    }
}
