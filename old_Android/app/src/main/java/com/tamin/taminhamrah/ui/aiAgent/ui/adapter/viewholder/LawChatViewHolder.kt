package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.view.View
import com.tamin.taminhamrah.data.repository.ai.model.AgentLawChatModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.databinding.ItemChatLawBinding
import com.tamin.taminhamrah.ui.aiAgent.ui.strategy.PromptActionStrategyFactory
import com.tamin.taminhamrah.utils.Utility.typeText
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.toPercentageString
import com.tamin.taminhamrah.utils.extentions.visible
import kotlinx.coroutines.Job

class LawChatViewHolder(
    private val binding: ItemChatLawBinding,
    private val listener: ChatActionListener
) : BaseChatViewHolder<AgentLawChatModel>(binding.root), TypingStateListener {

    private var boundMessageId: String? = null
    private var currentTypingItem: TypingAnimatable? = null
    private var typingWriterJob: Job? = null

    override fun bind(message: AgentLawChatModel) {
        cleanup()
        typingWriterJob?.cancel()
        currentTypingItem = message
        boundMessageId = message.id
        binding.chatAction.root.visibility = View.GONE
        when {
            message.skipTyping -> {
                showCompletedLawData(message)
                binding.chatAction.root.visibility = View.VISIBLE
                listener.onTypingComplete(message)
            }
            message.isTypingComplete -> {
                showCompletedLawData(message)
                binding.chatAction.root.visibility = View.VISIBLE
            }
            message.shouldStartTyping -> {
                startTyping(message)
            }
            else -> {
                binding.textMessage.text = ""
            }
        }
        setupChatAction(message, binding.chatAction.btnLike, binding.chatAction.btnDislike, binding.chatAction.textTime,binding.chatAction.btnCopy,binding.chatAction.btnShare)
    }

    override fun startTyping(message: TypingAnimatable) {
        if (message !is AgentLawChatModel) return
        if (boundMessageId != message.id) return
        binding.chatAction.root.visibility = View.GONE
        typingWriterJob?.cancel()
        binding.root.alpha = 0.7f
        binding.textShowButton.gone()
        binding.textScore.gone()
        binding.txtName.visible()
        binding.txtName.text = message.reference
        val formattedText = buildLawText(message)

        typingWriterJob = binding.textMessage.typeText(
            formattedText,
            onProgress = {
                listener.onTypingProgress()
            },
            onComplete = {
                if (boundMessageId != message.id) return@typeText
                showCompletedLawData(message)
                listener.onTypingComplete(message.apply {
                    isTypingComplete = true
                    shouldStartTyping = false
                })
                binding.chatAction.root.visible()
            }
        )
    }

    override fun completeTyping(message: TypingAnimatable) {
        if (message is AgentLawChatModel) {
            typingWriterJob?.cancel()
            showCompletedLawData(message)
            binding.chatAction.root.visible()
        }
    }

    private fun showCompletedLawData(message: AgentLawChatModel) {
        val formattedText = buildLawText(message)
        binding.apply {
            txtName.visible()
            textShowButton.visible()
            textScore.visible()
            txtName.text = message.reference
            textMessage.text = formattedText
            textScore.text = message.score?.toPercentageString() ?: "--"

            textShowButton.setOnClickListener {
                PromptActionStrategyFactory.getStrategy(message.actionContent)
                    .execute(message.actionContent, listener)
            }
            root.alpha = 1.0f
        }
    }

    private fun buildLawText(message: AgentLawChatModel): String {
        return buildString {
            message.content?.let {
                append(it)
            }
        }
    }

    override fun cleanup() {
        val itemToFinish = currentTypingItem as? AgentLawChatModel
        itemToFinish?.let { showCompletedLawData(it) }
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
        val item = currentTypingItem as? AgentLawChatModel
        item?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
            showCompletedLawData(it)
        }
        binding.chatAction.root.visible()
        currentTypingItem = null
    }
}