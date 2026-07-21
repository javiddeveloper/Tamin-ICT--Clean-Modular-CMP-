package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel
import com.tamin.taminhamrah.databinding.ItemChatBotBinding
import com.tamin.taminhamrah.databinding.ItemChatBotClickableBinding
import com.tamin.taminhamrah.databinding.ItemChatBotErrorBinding
import com.tamin.taminhamrah.databinding.ItemChatBotGroupButtonBinding
import com.tamin.taminhamrah.databinding.ItemChatBotVoiceBinding
import com.tamin.taminhamrah.databinding.ItemChatFormContainerBinding
import com.tamin.taminhamrah.databinding.ItemChatKeyValueBinding
import com.tamin.taminhamrah.databinding.ItemChatLawBinding
import com.tamin.taminhamrah.databinding.ItemChatUserBinding
import com.tamin.taminhamrah.databinding.ItemHeaderBotBinding
import com.tamin.taminhamrah.utils.ChatViewType

class ChatViewHolderFactory(
    private val actionListener: ChatActionListener,
    private val fragmentManager: FragmentManager
) {

    fun getItemViewType(item: AiChatModel): Int {
        return when (item) {
            is AiTextModel -> if (item.isUserMessage) ChatViewType.VIEW_TYPE_USER_TEXT else ChatViewType.VIEW_TYPE_BOT_TEXT
            is AiKeyValueModel -> ChatViewType.VIEW_TYPE_KEY_VALUE
            is AiClickableModel -> ChatViewType.VIEW_TYPE_CLICKABLE
            is VoiceModel -> ChatViewType.VIEW_TYPE_VOICE
            is AgentLawChatModel -> ChatViewType.VIEW_TYPE_LAW_CHAT
            is AiErrorModel -> ChatViewType.VIEW_TYPE_ERROR
            is AgentClickableModel -> ChatViewType.AGENT_CLICKABLE_MODEL
            is AiHeaderModel -> ChatViewType.VIEW_TYPE_HEADER
            is AgentGroupButtonModel -> ChatViewType.VIEW_GROUP_BUTTON
            is AiGenerativeModel -> ChatViewType.VIEW_TYPE_FORM
            else -> ChatViewType.VIEW_TYPE_BOT_TEXT
        }
    }

    fun createViewHolder(parent: ViewGroup, viewType: Int): BaseChatViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ChatViewType.VIEW_TYPE_USER_TEXT -> {
                UserTextViewHolder(
                    ItemChatUserBinding.inflate(inflater, parent, false)
                )
            }

            ChatViewType.VIEW_TYPE_BOT_TEXT -> {
                BotTextViewHolder(
                    ItemChatBotBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_TYPE_CLICKABLE -> {
                ClickableItemViewHolder(
                    ItemChatBotClickableBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_TYPE_KEY_VALUE -> {
                KeyValueViewHolder(
                    ItemChatKeyValueBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_TYPE_VOICE -> {
                VoiceViewHolder(
                    ItemChatBotVoiceBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_TYPE_LAW_CHAT -> {
                LawChatViewHolder(
                    ItemChatLawBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_TYPE_ERROR -> {
                ErrorViewHolder(
                    ItemChatBotErrorBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.AGENT_CLICKABLE_MODEL -> {
                ClickableItemViewHolder(
                    ItemChatBotClickableBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_TYPE_HEADER -> {
                BotHeaderViewHolder(
                    ItemHeaderBotBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_GROUP_BUTTON -> {
                GroupButtonItemViewHolder(
                    ItemChatBotGroupButtonBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }

            ChatViewType.VIEW_TYPE_FORM -> {
                FormViewHolder(
                    ItemChatFormContainerBinding.inflate(inflater, parent, false),
                    actionListener,
                    fragmentManager
                )
            }
            else -> {
                BotTextViewHolder(
                    ItemChatBotBinding.inflate(inflater, parent, false),
                    actionListener
                )
            }
        }
    }
}
