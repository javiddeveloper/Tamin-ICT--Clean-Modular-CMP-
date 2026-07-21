package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.R.id.message
import android.view.View
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AiKeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.databinding.ItemChatKeyValueBinding
import com.tamin.taminhamrah.utils.Utility.typeText
import com.tamin.taminhamrah.utils.extentions.visible
import kotlinx.coroutines.Job

class KeyValueViewHolder(
    private val binding: ItemChatKeyValueBinding,
    private val listener: ChatActionListener
) : BaseChatViewHolder<AiKeyValueModel>(binding.root) , TypingStateListener {

    private var boundMessageId: String? = null
    private var currentTypingItem: TypingAnimatable? = null
    private var typingWriterJob: Job? = null


    override fun bind(message: AiKeyValueModel) {
        cleanup()
        typingWriterJob?.cancel()
        currentTypingItem = message
        boundMessageId = message.id
        binding.chatAction.root.visibility = View.GONE
        when {
            message.skipTyping -> {
                showCompletedKeyValueData(message)
                binding.chatAction.root.visibility = View.VISIBLE
            listener.onTypingComplete(message)
            }
            message.isTypingComplete -> {
                showCompletedKeyValueData(message)
                binding.chatAction.root.visibility = View.VISIBLE
            }
            message.shouldStartTyping -> {
                startTyping(message)
            }
            else -> {
                binding.textMessage.text = ""
                binding.textTitle.text = message.title ?: ""
            }
        }
        setupChatAction(message, binding.chatAction.btnLike, binding.chatAction.btnDislike, binding.chatAction.textTime,binding.chatAction.btnCopy,binding.chatAction.btnShare)
    }

    override fun startTyping(message: TypingAnimatable) {
        if (message !is AiKeyValueModel) return
        if (boundMessageId != message.id) return
        binding.chatAction.root.visibility = View.GONE
        typingWriterJob?.cancel()
        val formattedText = buildKeyValueText(message.keyValueItems)
        binding.textTitle.text = message.title ?: ""
        typingWriterJob = binding.textMessage.typeText(
            formattedText,
            onProgress = {
                listener.onTypingProgress()
            },
            onComplete = {
                if (boundMessageId != message.id) return@typeText

                listener.onTypingComplete(
                    message.apply {
                        isTypingComplete = true
                        shouldStartTyping = false
                    }
                )
                binding.chatAction.root.visible()
            }
        )

    }

    override fun completeTyping(message: TypingAnimatable) {
        if (message is AiKeyValueModel) {
            typingWriterJob?.cancel()
            showCompletedKeyValueData(message)
            binding.chatAction.root.visible()
        }
    }

    private fun showCompletedKeyValueData(message: AiKeyValueModel) {
        val formattedText = buildKeyValueText(message.keyValueItems)
        binding.textTitle.text = message.title ?: ""
        binding.textMessage.text = formattedText
    }

    private fun buildKeyValueText(keyValueItems: List<KeyValueModel>): String {
        return buildString {
            keyValueItems.forEach { item ->
                if (item._value.isEmpty()) {
                    append(item._key)
                } else {
                    append("● ${item._key}: ${item._value}")
                }
                append("\n")

            }
        }.trimEnd()
    }

    override fun cleanup() {
        val itemToFinish = currentTypingItem  as? AiKeyValueModel
        itemToFinish?.let { showCompletedKeyValueData(it) }
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
        val item = currentTypingItem as? AiKeyValueModel
        item?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
            showCompletedKeyValueData(it)
        }
        binding.chatAction.root.visible()
        currentTypingItem = null
    }
}