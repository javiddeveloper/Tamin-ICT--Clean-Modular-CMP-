package com.tamin.taminhamrah.ui.home.services.wageandhistory

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModels
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.databinding.FragmentWageAndHistoryDetailBinding
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.FormHost
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.SchemaBusinessGenerator
import com.tamin.taminhamrah.ui.appinterface.AdapterInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WageAndHistoryDetailFullFragment :
    Fragment(R.layout.fragment_wage_and_history_detail),
    AdapterInterface.OnShowMoreClickListener<Int> {

    private var viewBinding: FragmentWageAndHistoryDetailBinding? = null
    private lateinit var listAdapter: WageAndHistoryDetailAdapter
    private val schemaGenerator = SchemaBusinessGenerator()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentWageAndHistoryDetailBinding.bind(view)
        initView()
        initData()
    }

    private fun initView() {
        listAdapter = WageAndHistoryDetailAdapter()
        viewBinding?.recycler?.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initData() {
        val cachedKey = arguments?.getString(ARG_PAYLOAD_CACHE_KEY)
        val wagePayload = if (!cachedKey.isNullOrEmpty()) {
            WageAndHistoryDetailFragment.Companion.PayloadCache.take(cachedKey)
        } else {
            arguments?.getParcelable(Constants.ARRAYLIST) as? WageAndHistoryModels
        }
        val schemaJson = arguments?.getString(ARG_SCHEMA_JSON)

        if (wagePayload != null) {
            viewBinding?.formContainer?.visibility = View.GONE
            viewBinding?.recycler?.visibility = View.VISIBLE
            listAdapter.setItems(wagePayload, this)
            return
        }

        if (!schemaJson.isNullOrBlank()) {
            viewBinding?.recycler?.visibility = View.GONE
            viewBinding?.formContainer?.visibility = View.VISIBLE
            bindSchema(schemaJson)
            return
        }
    }

    override fun onShowMoreClick(item: Int, transitionView: View?, tag: String?) {
        viewBinding?.recycler?.layoutManager?.scrollToPosition(item)
    }

    override fun onDestroyView() {
        schemaGenerator.cleanup()
        viewBinding?.formContainer?.removeAllViews()
        viewBinding = null
        super.onDestroyView()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun bindSchema(schemaJson: String) {
        val schema = Gson().fromJson(schemaJson, FormSchema::class.java)
        val dataJson = arguments?.getString(ARG_DATA_JSON)
        val data = if (dataJson.isNullOrBlank()) {
            null
        } else {
            val type = object : TypeToken<Map<String?, String?>>() {}.type
            Gson().fromJson<Map<String?, String?>>(dataJson, type)
        }

        val container = viewBinding?.formContainer ?: return
        val host = object : FormHost {
            override val formContainer = container
            override val hostContext = requireContext()
            override val fragmentManager = childFragmentManager
            override val currentData = data
            override val messageId: String? = null

            override fun getAdapterPosition(): Int = 0

            override fun dispatchFormAction(actionId: String, data: Map<String, Any?>) {
                parentFragmentManager.setFragmentResult(
                    WageAndHistoryDetailFragment.REQUEST_KEY_FORM_ACTION,
                    Bundle().apply {
                        putString(WageAndHistoryDetailFragment.BUNDLE_KEY_ACTION_ID, actionId)
                        putString(WageAndHistoryDetailFragment.BUNDLE_KEY_PAYLOAD_JSON, Gson().toJson(data))
                    }
                )
            }

            override fun dispatchActionClick(action: AgentActionContent) {
                parentFragmentManager.setFragmentResult(
                    WageAndHistoryDetailFragment.REQUEST_KEY_ACTION_CONTENT,
                    Bundle().apply {
                        putString(
                            WageAndHistoryDetailFragment.BUNDLE_KEY_ACTION_CONTENT_JSON,
                            Gson().toJson(action)
                        )
                    }
                )
            }

            override fun showFallback(text: String) {
                parentFragmentManager.setFragmentResult(
                    WageAndHistoryDetailFragment.REQUEST_KEY_FALLBACK,
                    Bundle().apply { putString(WageAndHistoryDetailFragment.BUNDLE_KEY_FALLBACK_TEXT, text) }
                )
            }

            override fun requestDocumentUpload(fieldId: String) {
                parentFragmentManager.setFragmentResult(
                    WageAndHistoryDetailFragment.REQUEST_KEY_DOCUMENT_REQUEST,
                    Bundle().apply { putString(WageAndHistoryDetailFragment.BUNDLE_KEY_FIELD_ID, fieldId) }
                )
            }

            override fun dp(value: Int): Int {
                return (value * resources.displayMetrics.density).toInt()
            }

            override fun updateFormData(data: Map<String, Any?>) {
                // no-op in details fragment
            }
        }
        schemaGenerator.bind(host, schema, AiGenerativeModel(schema = schema, data = data))
    }

    companion object {
        private const val ARG_SCHEMA_JSON = "arg_schema_json"
        private const val ARG_DATA_JSON = "arg_data_json"
        private const val ARG_PAYLOAD_CACHE_KEY = "arg_payload_cache_key"
    }
}
