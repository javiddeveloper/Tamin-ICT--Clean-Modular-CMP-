package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent

interface FormHost {
    val formContainer: ViewGroup
    val hostContext: Context
    val fragmentManager: FragmentManager
    val currentData: Map<String?, String?>?
    val messageId: String?

    fun getAdapterPosition(): Int
    fun dispatchFormAction(actionId: String, data: Map<String, Any?>)
    fun dispatchActionClick(action: AgentActionContent)
    fun requestDocumentUpload(fieldId: String)
    fun showFallback(text: String)
    fun dp(value: Int): Int
    fun updateFormData(data: Map<String, Any?>)
}
