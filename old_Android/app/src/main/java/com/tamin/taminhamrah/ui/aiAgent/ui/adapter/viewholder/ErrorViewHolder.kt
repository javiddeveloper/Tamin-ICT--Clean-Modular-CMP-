package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import com.tamin.taminhamrah.data.repository.ai.model.AiErrorModel
import com.tamin.taminhamrah.databinding.ItemChatBotErrorBinding
import com.tamin.taminhamrah.utils.Utility.formatChatTime

class ErrorViewHolder(
    private val binding: ItemChatBotErrorBinding,
    private val listener: ChatActionListener
)  : BaseChatViewHolder<AiErrorModel>(binding.root) {

    override fun bind(message: AiErrorModel) {
        binding.textShowButton.setOnClickListener {
            listener.onRetryClick()
        }
        binding.textShowButton.text = message.message
        binding.textTime.text = formatChatTime(message.createdAt)
    }

    override fun cleanup() {}
}