package com.tamin.taminhamrah.ui.home.services.wageandhistory

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import com.tamin.taminhamrah.ui.base.BaseBottomSheetDialogFragment
import com.tamin.taminhamrah.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@AndroidEntryPoint
class WageAndHistoryDetailFragment :
    BaseBottomSheetDialogFragment<FragmentWageAndHistoryDetailBinding, BaseViewModel>(),
    AdapterInterface.OnShowMoreClickListener<Int>  {

    override val mViewModelDialog: BaseViewModel by viewModels()
    override fun getLayoutId()= R.layout.fragment_wage_and_history_detail

    lateinit var listAdapter: WageAndHistoryDetailAdapter
    private val schemaGenerator = SchemaBusinessGenerator()
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initData() {
        val wagePayload = arguments?.getParcelable(Constants.ARRAYLIST) as? WageAndHistoryModels
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

    private fun initView() {
        listAdapter = WageAndHistoryDetailAdapter()
        viewBinding?.recycler?.apply {
            adapter = listAdapter
            val layoutManager = LinearLayoutManager(requireContext())
            this.layoutManager = layoutManager
        }
    }

    override fun onShowMoreClick(item: Int, transitionView: View?, tag: String?) {
        viewBinding?.recycler?.layoutManager?.apply {
            scrollToPosition(item)
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = (dialog as? BottomSheetDialog)
            ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?: return

        bottomSheet.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet.requestLayout()

        val behavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior = behavior
        behavior.isFitToContents = false
        behavior.halfExpandedRatio = 0.5f
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    override fun onDestroyView() {
        schemaGenerator.cleanup()
        viewBinding?.formContainer?.removeAllViews()
        bottomSheetBehavior = null
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
                    REQUEST_KEY_FORM_ACTION,
                    Bundle().apply {
                        putString(BUNDLE_KEY_ACTION_ID, actionId)
                        putString(BUNDLE_KEY_PAYLOAD_JSON, Gson().toJson(data))
                    }
                )
            }

            override fun dispatchActionClick(action: AgentActionContent) {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY_ACTION_CONTENT,
                    Bundle().apply {
                        putString(BUNDLE_KEY_ACTION_CONTENT_JSON, Gson().toJson(action))
                    }
                )
            }

            override fun showFallback(text: String) {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY_FALLBACK,
                    Bundle().apply { putString(BUNDLE_KEY_FALLBACK_TEXT, text) }
                )
            }

            override fun requestDocumentUpload(fieldId: String) {
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY_DOCUMENT_REQUEST,
                    Bundle().apply { putString(BUNDLE_KEY_FIELD_ID, fieldId) }
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

        internal object PayloadCache {
            private val cache = ConcurrentHashMap<String, WageAndHistoryModels>()

            fun put(payload: WageAndHistoryModels): String {
                val key = UUID.randomUUID().toString()
                cache[key] = payload
                return key
            }

            fun take(key: String): WageAndHistoryModels? {
                return cache.remove(key)
            }
        }

        const val REQUEST_KEY_FORM_ACTION = "ai_sheet_form_action"
        const val REQUEST_KEY_ACTION_CONTENT = "ai_sheet_action_content"
        const val REQUEST_KEY_FALLBACK = "ai_sheet_fallback"
        const val REQUEST_KEY_DOCUMENT_REQUEST = "ai_sheet_document_request"

        const val BUNDLE_KEY_ACTION_ID = "actionId"
        const val BUNDLE_KEY_PAYLOAD_JSON = "payloadJson"
        const val BUNDLE_KEY_ACTION_CONTENT_JSON = "actionContentJson"
        const val BUNDLE_KEY_FALLBACK_TEXT = "fallbackText"
        const val BUNDLE_KEY_FIELD_ID = "fieldId"

        fun newInstance(schema: FormSchema, data: Map<String?, String?>? = null): WageAndHistoryDetailFragment {
            return WageAndHistoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SCHEMA_JSON, Gson().toJson(schema))
                    if (data != null) putString(ARG_DATA_JSON, Gson().toJson(data))
                }
            }
        }
    }
}
