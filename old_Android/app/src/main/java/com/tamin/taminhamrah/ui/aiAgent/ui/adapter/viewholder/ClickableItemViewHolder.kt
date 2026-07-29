package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.view.View
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.databinding.ItemChatBotClickableBinding
import com.tamin.taminhamrah.ui.aiAgent.ui.strategy.PromptActionStrategyFactory
import com.tamin.taminhamrah.utils.Utility.buildKeyValueText
import com.tamin.taminhamrah.utils.Utility.typeText
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import kotlinx.coroutines.Job

class ClickableItemViewHolder(
    private val binding: ItemChatBotClickableBinding,
    private val listener: ChatActionListener
) : BaseChatViewHolder<AgentClickableModel>(binding.root), TypingStateListener {

    private var boundMessageId: String? = null
    private var currentTypingItem: TypingAnimatable? = null
    private var typingWriterJob: Job? = null

    override fun bind(message: AgentClickableModel) {
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
                binding.txtName.text = message.title ?: ""
            }
        }
        binding.apply {
            message.title?.apply {
                txtName.text = this
                txtName.visible()
            }
        }
        setupChatAction(
            message,
            binding.chatAction.btnLike,
            binding.chatAction.btnDislike,
            binding.chatAction.textTime,
            binding.chatAction.btnCopy, binding.chatAction.btnShare
        )
    }

    override fun startTyping(message: TypingAnimatable) {
        if (message !is AgentClickableModel) return
        if (boundMessageId != message.id) return
        binding.chatAction.root.visibility = View.GONE
        typingWriterJob?.cancel()
        val formattedText = buildKeyValueText(message.content)
        binding.apply {
        txtName.text = message.title ?: ""
            root.alpha = 0.7f
            textShowButton.gone()
            typingWriterJob = textMessage.typeText(
                formattedText,
                onProgress = {
                    listener.onTypingProgress()
                },
                onComplete = {
                    if (boundMessageId == message.id) {
                        showCompletedStateAfterTyping(message, formattedText)
                        listener.onTypingComplete(message.apply {
                            isTypingComplete = true
                            shouldStartTyping = false
                        })
                        binding.chatAction.root.visible()
                    }
                }
            )
        }
    }

    override fun completeTyping(message: TypingAnimatable) {
        if (message is AgentClickableModel) {
            typingWriterJob?.cancel()
            showCompletedKeyValueData(message)
            binding.chatAction.root.visible()

        }
    }
    private fun showCompletedKeyValueData(message: AgentClickableModel) {
        val formattedText = buildKeyValueText(message.content)
        binding.txtName.text = message.title ?: ""
        binding.textMessage.text = formattedText
        message.actionContent.actionText?.apply {
            binding.textShowButton.text =this
        }
        binding.textShowButton.setOnClickListener {
            PromptActionStrategyFactory.getStrategy(message.actionContent)
                .execute(message.actionContent, listener)
        }
        binding.textShowButton.visible()
        binding.root.alpha = 1.0f
    }


    private fun showCompletedStateAfterTyping(message: AgentClickableModel, fullText: String) {
        binding.apply {
            textMessage.text = fullText
            message.title?.apply {
                txtName.text = this
            }
            message.actionContent.actionText?.apply {
                textShowButton.text = this

            }


            textShowButton.visible()
            textShowButton.setOnClickListener {
                PromptActionStrategyFactory.getStrategy(message.actionContent)
                    .execute(message.actionContent, listener)
            }
            root.alpha = 1.0f
        }
    }

    override fun cleanup() {
        val itemToFinish = currentTypingItem as? AgentClickableModel
        typingWriterJob?.cancel()
        itemToFinish?.apply {
            showCompletedStateAfterTyping(this,buildKeyValueText(itemToFinish.content ))
        }
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
        val item = currentTypingItem as? AgentClickableModel
        item?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
            showCompletedKeyValueData(it)
        }
        binding.chatAction.root.visible()
        currentTypingItem = null
    }
}