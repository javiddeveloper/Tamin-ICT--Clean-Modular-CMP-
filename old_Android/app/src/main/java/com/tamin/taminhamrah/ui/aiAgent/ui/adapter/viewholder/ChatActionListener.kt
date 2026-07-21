package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.ClickableItemModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel

interface ChatActionListener {
    fun onActionClick(action: AgentActionContent, position: Int)
    fun onItemClick(item: ClickableItemModel)
    fun onTypingComplete(item: TypingAnimatable)
    fun onPlayClick(model: VoiceModel, position: Int)
    fun onPauseClick(model: VoiceModel, position: Int)
    fun onRetryClick()
    fun onChipClicked(prompt: String)
    fun onTypingProgress()
    fun onDisplayReport(action: AgentActionContent)
    fun onFormAction(actionId: String, data: Map<String, Any?>, position: Int, messageId: String? = null)
    fun onUpdateFormData(data: Map<String, Any?>, position: Int, messageId: String? = null)
    fun onFormDocumentRequest(fieldId: String, position: Int, messageId: String? = null)
}
