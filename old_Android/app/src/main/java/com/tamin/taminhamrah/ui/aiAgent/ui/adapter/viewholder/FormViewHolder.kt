package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentManager
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.databinding.ItemChatFormContainerBinding
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.FormHost
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.generator.SchemaBusinessGenerator

class FormViewHolder(
    private val binding: ItemChatFormContainerBinding,
    private val listener: ChatActionListener,
    private val fragmentManager: FragmentManager
) : BaseChatViewHolder<AiGenerativeModel>(binding.root), TypingStateListener {

    private var currentTypingItem: AiGenerativeModel? = null
    private var currentData: Map<String?, String?>? = null
    private var currentMessageId: String? = null
    private val generator = SchemaBusinessGenerator()

    private val formHost = object : FormHost {
        override val formContainer: ViewGroup get() = binding.formContainer
        override val hostContext: Context get() = binding.root.context
        override val fragmentManager: FragmentManager get() = this@FormViewHolder.fragmentManager
        override val currentData: Map<String?, String?>? get() = this@FormViewHolder.currentData
        override val messageId: String? get() = this@FormViewHolder.currentMessageId

        override fun getAdapterPosition(): Int {
            val pos = bindingAdapterPosition
            return if (pos == RecyclerView.NO_POSITION) 0 else pos
        }

        override fun dispatchFormAction(actionId: String, data: Map<String, Any?>) {
            listener.onFormAction(actionId, data, getAdapterPosition(), messageId)
        }

        override fun dispatchActionClick(action: AgentActionContent) {
            listener.onActionClick(action, getAdapterPosition())
        }

        override fun requestDocumentUpload(fieldId: String) {
            listener.onFormDocumentRequest(fieldId, getAdapterPosition(), messageId)
        }

        override fun showFallback(text: String) {
            this@FormViewHolder.showFallback(text)
        }

        override fun dp(value: Int): Int = this@FormViewHolder.dp(value)

        override fun updateFormData(data: Map<String, Any?>) {
            listener.onUpdateFormData(data, getAdapterPosition(), messageId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(message: AiGenerativeModel) {
        currentTypingItem = message
        currentData = message.data
        currentMessageId = message.id
        val schema = message.schema

        generator.bind(formHost, schema, message)

        if (message.shouldStartTyping && !message.isTypingComplete) {
            startTyping(message)
        } else {
            completeTyping(message)
        }
    }

    override fun cleanup() {
        generator.cleanup()
        binding.formContainer.removeAllViews()
        currentData = null
        currentTypingItem = null
    }

    override fun startTyping(message: TypingAnimatable) {
        if (message !is AiGenerativeModel) return
        binding.root.alpha = 0f
        binding.root.animate().alpha(1f).setDuration(500).withEndAction {
            completeTyping(message)
        }.start()
    }

    override fun completeTyping(message: TypingAnimatable) {
        if (message !is AiGenerativeModel) return
        val item = currentTypingItem ?: return
        if (!item.isTypingComplete) {
            item.isTypingComplete = true
            item.shouldStartTyping = false
            listener.onTypingComplete(item)
        } else {
            item.shouldStartTyping = false
        }
        binding.root.alpha = 1f
    }

    override fun forceStopTyping() {
        currentTypingItem?.let {
            it.isTypingComplete = true
            it.shouldStartTyping = false
        }
    }

    override fun skipTypingAnimation() {
        currentTypingItem?.let {
            it.skipTyping = true
            it.isTypingComplete = true
            it.shouldStartTyping = false
        }
    }

    private fun showFallback(text: String) {
        binding.formContainer.removeAllViews()
        val padding = dp(16)
        val margin = dp(16)
        binding.formContainer.addView(
            AppCompatTextView(binding.root.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(margin, margin, margin, margin) }
                setPadding(padding, padding, padding, padding)
                setBackgroundResource(R.drawable.bg_chat_bot_clickable)
                this.text = text
            }
        )
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            binding.root.context.resources.displayMetrics
        ).toInt()
    }
}
