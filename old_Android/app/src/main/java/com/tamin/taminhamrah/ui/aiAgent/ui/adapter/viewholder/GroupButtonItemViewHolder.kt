package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.AgentGroupButtonModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.databinding.ItemChatBotGroupButtonBinding
import com.tamin.taminhamrah.ui.aiAgent.ui.strategy.PromptActionStrategyFactory
import com.tamin.taminhamrah.utils.Utility.buildKeyValueText
import com.tamin.taminhamrah.utils.Utility.typeText
import com.tamin.taminhamrah.utils.extentions.gone
import com.tamin.taminhamrah.utils.extentions.visible
import kotlinx.coroutines.Job

class GroupButtonItemViewHolder(
    private val binding: ItemChatBotGroupButtonBinding,
    private val listener: ChatActionListener
) : BaseChatViewHolder<AgentGroupButtonModel>(binding.root), TypingStateListener {

    private var boundMessageId: String? = null
    private var currentTypingItem: TypingAnimatable? = null
    private var typingWriterJob: Job? = null

    override fun bind(message: AgentGroupButtonModel) {
        cleanup()
        typingWriterJob?.cancel()
        currentTypingItem = message
        boundMessageId = message.id
        binding.chatAction.root.visibility = View.GONE
//        binding.bubbleContainer.isVisible = !message.title.isNullOrEmpty()
        when {
            message.skipTyping -> {
                showCompletedState(message)
                binding.chatAction.root.visibility = View.VISIBLE
                listener.onTypingComplete(message)
            }

            message.isTypingComplete -> {
                showCompletedState(message)
                binding.chatAction.root.visibility = View.VISIBLE
            }

            message.shouldStartTyping -> {
                startTyping(message)
            }

            else -> {
                showCompletedState(message)
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
        if (message !is AgentGroupButtonModel) return
        if (boundMessageId != message.id) return
        binding.chatAction.root.visibility = View.GONE
        typingWriterJob?.cancel()
        binding.apply {
            root.alpha = 0.7f
            chipGroupInside.gone()
            chipGroupOutside.gone()
            txtName.visible()
            if (message.title != null) {
                typingWriterJob = txtName.typeText(
                    message.title!!,
                    onProgress = {
                        listener.onTypingProgress()
                    },
                    onComplete = {
                        if (boundMessageId == message.id) {
                            finishTyping(message)
                        }
                    }
                )
            } else {
                finishTyping(message)
            }
        }
    }

    private fun finishTyping(message: AgentGroupButtonModel) {
        showCompletedState(message)
        listener.onTypingComplete(message.apply {
            isTypingComplete = true
            shouldStartTyping = false
        })
        binding.chatAction.root.visible()
    }

    override fun completeTyping(message: TypingAnimatable) {
        if (message is AgentGroupButtonModel) {
            typingWriterJob?.cancel()
            showCompletedState(message)
            binding.chatAction.root.visible()

        }
    }

    private fun showCompletedState(message: AgentGroupButtonModel) {
        binding.apply {
            val isDeepLinkAction = message.actionContent is AgentActionContent.DeepLink || message.actionContent is AgentActionContent.LocalDeepLink || message.actionContent is AgentActionContent.DisplayReport
            bubbleContainer.isVisible = !message.title.isNullOrEmpty() || isDeepLinkAction || message.content != null
            txtName.text = message.title
            txtName.isVisible = !message.title.isNullOrEmpty()

            if (message.content != null) {
                textMessage.text = buildKeyValueText(message.content)
                textMessage.visible()
            } else {
                textMessage.gone()
            }

            val targetChipGroup = if (isDeepLinkAction || message.content != null) {
                chipGroupOutside.gone()
                chipGroupInside.visible()
                chipGroupInside
            } else {
                chipGroupInside.gone()
                chipGroupOutside.visible()
                chipGroupOutside
            }
            targetChipGroup.removeAllViews()

            message.prompts.forEach { prompt ->
                val chip = Chip(root.context).apply {
                    text = prompt.prompt
                    isClickable = true
                    isCheckable = false
                    typeface = ResourcesCompat.getFont(
                        itemView.context,
                        R.font.iran_sans_mobile_fa_num
                    )
                    message.color?.let { colorString ->
                            val color = colorString.toColorInt()
                            chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
                    }

                    setOnClickListener {
                        val (strategy, action) = PromptActionStrategyFactory.getStrategyAndAction(
                            prompt,
                            message.actionContent
                        )
                        strategy.execute(action, listener)
                    }
                }
                chip.setEnsureMinTouchTargetSize(false)
                targetChipGroup.addView(chip)
            }
            root.alpha = 1.0f
        }
    }


    override fun cleanup() {
        val itemToFinish = currentTypingItem as? AgentGroupButtonModel
        typingWriterJob?.cancel()
        itemToFinish?.apply {
            showCompletedState(this)
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
        val item = currentTypingItem as? AgentGroupButtonModel
        item?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
            showCompletedState(it)
        }
        binding.chatAction.root.visible()
        currentTypingItem = null
    }
}