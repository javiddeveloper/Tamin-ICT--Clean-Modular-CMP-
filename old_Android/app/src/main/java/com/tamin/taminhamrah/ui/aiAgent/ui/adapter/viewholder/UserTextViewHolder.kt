package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.databinding.ItemChatUserBinding
import com.tamin.taminhamrah.utils.Utility.formatChatTime

class UserTextViewHolder(
    private val binding: ItemChatUserBinding
) : BaseChatViewHolder<AiTextModel>(binding.root) {

    override fun bind(message: AiTextModel) {
            binding.textMessage.text = message.message
            binding.chatAction.textTime.text = formatChatTime(message.createdAt)
            setupChatAction(message, btnCopy = binding.chatAction.btnCopy)
    }

    override fun cleanup() {}
}