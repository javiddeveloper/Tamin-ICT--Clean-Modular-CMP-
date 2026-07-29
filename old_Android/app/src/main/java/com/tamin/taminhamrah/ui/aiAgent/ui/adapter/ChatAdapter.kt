package com.tamin.taminhamrah.ui.aiAgent.ui.adapter

import android.view.ViewGroup
import com.tamin.taminhamrah.Constants.PAYLOAD_START_TYPING
import com.tamin.taminhamrah.Constants.PAYLOAD_TYPING_COMPLETE
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.AiChatDiffCallback
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.BaseChatViewHolder
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.ChatViewHolderFactory
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.TypingStateListener

class ChatAdapter(
    private val viewHolderFactory: ChatViewHolderFactory
) : androidx.recyclerview.widget.ListAdapter<AiChatModel, BaseChatViewHolder<*>>(AiChatDiffCallback()) {
    private var activeTypingHolder: TypingStateListener? = null

    override fun getItemViewType(position: Int): Int {
        return viewHolderFactory.getItemViewType(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseChatViewHolder<*> {
        return viewHolderFactory.createViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseChatViewHolder<*>, position: Int) {
        val item = getItem(position)
        (holder as BaseChatViewHolder<AiChatModel>).bind(item)
        if (holder is TypingStateListener) {
            activeTypingHolder = holder
        }
    }

    override fun onBindViewHolder(
        holder: BaseChatViewHolder<*>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val item = getItem(position)
            payloads.forEach { payload ->
                when (payload) {
                    PAYLOAD_START_TYPING -> {
                        if (item is TypingAnimatable && holder is TypingStateListener) {
                            holder.startTyping(item)
                        }
                    }

                    PAYLOAD_TYPING_COMPLETE -> {
                        if (item is TypingAnimatable && holder is TypingStateListener) {
                            holder.completeTyping(item)
                        }
                    }
                }
            }
        }
    }

    override fun onViewRecycled(holder: BaseChatViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.cleanup()
    }

    fun stopCurrentTyping() {
        activeTypingHolder?.forceStopTyping()
        activeTypingHolder = null
    }

    fun skipTypingAnimation() {
        activeTypingHolder?.skipTypingAnimation()
        activeTypingHolder = null
    }

}