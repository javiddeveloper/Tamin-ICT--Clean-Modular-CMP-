package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import androidx.core.view.isGone
import com.tamin.taminhamrah.data.repository.ai.model.AiHeaderModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.databinding.ItemHeaderBotBinding
import com.tamin.taminhamrah.utils.Utility.typeText
import kotlinx.coroutines.Job

class BotHeaderViewHolder(
    private val binding: ItemHeaderBotBinding,
    private val listener: ChatActionListener
) : BaseChatViewHolder<AiHeaderModel>(binding.root), TypingStateListener {

    private var boundMessageId: String? = null
    private var currentTypingItem: TypingAnimatable? = null
    private var typingWriterJob: Job? = null

    override fun bind(message: AiHeaderModel) {
        cleanup()
        typingWriterJob?.cancel()
        currentTypingItem = message
        boundMessageId = message.id
        binding.txtTitle.apply {
            isGone = message.title.isNullOrEmpty()
            binding.txtTitle.text = message.title
        }

        when {
            message.skipTyping -> {
                binding.txtTitle.text = message.title
                listener.onTypingComplete(message)
            }
            message.isTypingComplete -> {
                binding.txtTitle.text = message.title
            }

            message.shouldStartTyping -> {
                startTyping(message)
            }

            else -> {
                binding.txtTitle.text = ""
            }
        }
    }

    override fun startTyping(message: TypingAnimatable) {
        if (message !is AiHeaderModel) return
        if (boundMessageId != message.id) return
        typingWriterJob?.cancel()

        typingWriterJob = binding.txtTitle.typeText(
            message.title?:"",
            onProgress = {
                listener.onTypingProgress()
            },
            onComplete = {
                if (boundMessageId != message.id) return@typeText
                listener.onTypingComplete(message.apply {
                    isTypingComplete = true
                    shouldStartTyping = false
                })
            }
        )
    }

    override fun completeTyping(message: TypingAnimatable) {
        if (message is AiHeaderModel) {
            typingWriterJob?.cancel()
            binding.txtTitle.text = message.title
        }
    }

    override fun cleanup() {
        val itemToFinish = currentTypingItem as? AiHeaderModel ?: return
        binding.txtTitle.text = itemToFinish.title
        typingWriterJob?.cancel()
        if (!itemToFinish.isTypingComplete) {
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
    }

    override fun skipTypingAnimation() {
        typingWriterJob?.cancel()
        val item = currentTypingItem as? AiHeaderModel
        item?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
            binding.txtTitle.text = it.title
        }
        currentTypingItem = null
    }
}