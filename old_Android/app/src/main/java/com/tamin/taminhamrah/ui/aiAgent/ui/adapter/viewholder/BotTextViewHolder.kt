package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.R.id.message
import android.view.View
import androidx.core.view.isGone
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.databinding.ItemChatBotBinding
import com.tamin.taminhamrah.utils.Utility.typeText
import com.tamin.taminhamrah.utils.extentions.visible
import kotlinx.coroutines.Job

class BotTextViewHolder(
    private val binding: ItemChatBotBinding,
    private val listener: ChatActionListener
) : BaseChatViewHolder<AiTextModel>(binding.root), TypingStateListener {

    private var boundMessageId: String? = null
    private var currentTypingItem: TypingAnimatable? = null
    private var typingWriterJob: Job? = null

    override fun bind(message: AiTextModel) {
        cleanup()
        typingWriterJob?.cancel()
        currentTypingItem = message
        boundMessageId = message.id
        binding.chatAction.root.visibility = View.GONE
        binding.txtTitle.apply {
            isGone = message.title.isNullOrEmpty()
            binding.txtTitle.text = message.title
        }

        when {
            message.skipTyping -> {
                binding.txtTitle.text = message.title
                listener.onTypingComplete(message)
            }
            message.isUserMessage -> {
                binding.textMessage.text = message.message
            }

            message.isTypingComplete -> {
                binding.textMessage.text = message.message
                binding.chatAction.root.visibility = View.VISIBLE
            }

            message.shouldStartTyping -> {
                startTyping(message)
            }

            else -> {
                binding.textMessage.text = ""
            }
        }
        setupChatAction(
            message,
            binding.chatAction.btnLike,
            binding.chatAction.btnDislike,
            binding.chatAction.textTime,
            binding.chatAction.btnCopy,
            binding.chatAction.btnShare
        )
    }

    override fun startTyping(message: TypingAnimatable) {
        if (message !is AiTextModel) return
        if (boundMessageId != message.id) return
//        if (message.message.isBlank()) return
        binding.chatAction.root.visibility = View.GONE
        typingWriterJob?.cancel()
        typingWriterJob = binding.textMessage.typeText(
            message.message,
            onProgress = {
                listener.onTypingProgress()
            },
            onComplete = {
                if (boundMessageId != message.id) return@typeText
                listener.onTypingComplete(message.apply {
                    isTypingComplete = true
                    shouldStartTyping = false
                })
                binding.chatAction.root.visible()
            }
        )
    }

    override fun completeTyping(message: TypingAnimatable) {
        if (message is AiTextModel) {
            typingWriterJob?.cancel()
            binding.textMessage.text = message.message
            binding.chatAction.root.visible()
        }
    }

    override fun cleanup() {
        val itemToFinish = currentTypingItem
        typingWriterJob?.cancel()
        if (itemToFinish != null && !itemToFinish.isTypingComplete) {
            listener.onTypingComplete(itemToFinish.apply {
                isTypingComplete = true
                shouldStartTyping = false
            })
        }
        boundMessageId = null
        currentTypingItem = null
    }

    override fun forceStopTyping() {
        typingWriterJob?.cancel()
        currentTypingItem?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
        }
        currentTypingItem = null
        binding.chatAction.root.visible()
    }

    override fun skipTypingAnimation() {
        typingWriterJob?.cancel()
        val item = currentTypingItem as? AiTextModel
        item?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
            binding.textMessage.text = it.message
        }
        binding.chatAction.root.visible()
        currentTypingItem = null
    }
}