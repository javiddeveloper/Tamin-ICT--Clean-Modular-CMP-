package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import androidx.recyclerview.widget.DiffUtil
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentGroupButtonModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentLawChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AiErrorModel
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.AiHeaderModel
import com.tamin.taminhamrah.data.repository.ai.model.AiKeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel

class AiChatDiffCallback : DiffUtil.ItemCallback<AiChatModel>() {
    override fun areItemsTheSame(oldItem: AiChatModel, newItem: AiChatModel): Boolean {
        return when {
            oldItem is TypingAnimatable && newItem is TypingAnimatable ->
                oldItem.id == newItem.id
            oldItem is VoiceModel  && newItem is VoiceModel->
                oldItem.path == newItem.path
            else -> oldItem === newItem
        }
    }

    override fun areContentsTheSame(oldItem: AiChatModel, newItem: AiChatModel): Boolean {
        return when {
            oldItem is AiTextModel && newItem is AiTextModel ->
                oldItem == newItem
            oldItem is AiKeyValueModel && newItem is AiKeyValueModel ->
                oldItem == newItem
            oldItem is AiClickableModel && newItem is AiClickableModel ->
                oldItem == newItem
            oldItem is AgentClickableModel && newItem is AgentClickableModel ->
                oldItem == newItem
            oldItem is AgentGroupButtonModel && newItem is AgentGroupButtonModel ->
                oldItem == newItem
            oldItem is AgentLawChatModel && newItem is AgentLawChatModel ->
                oldItem == newItem
            oldItem is AiGenerativeModel && newItem is AiGenerativeModel ->
                oldItem == newItem
            oldItem is AiHeaderModel && newItem is AiHeaderModel ->
                oldItem == newItem
            oldItem is VoiceModel && newItem is VoiceModel ->
                oldItem == newItem
            oldItem is AiErrorModel && newItem is AiErrorModel ->
                oldItem == newItem
            else -> false
        }
    }

    override fun getChangePayload(oldItem: AiChatModel, newItem: AiChatModel): Any? {
        return when {
            oldItem is TypingAnimatable && newItem is TypingAnimatable -> {
                when {
                    oldItem.shouldStartTyping != newItem.shouldStartTyping -> Constants.PAYLOAD_START_TYPING
                    oldItem.isTypingComplete != newItem.isTypingComplete -> Constants.PAYLOAD_TYPING_COMPLETE
                    else -> null
                }
            }
            else -> null
        }
    }
}