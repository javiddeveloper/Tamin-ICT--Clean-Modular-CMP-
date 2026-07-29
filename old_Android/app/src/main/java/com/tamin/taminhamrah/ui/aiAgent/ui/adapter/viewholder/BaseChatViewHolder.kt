package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.FeedbackStatus
import com.tamin.taminhamrah.utils.Utility.copyToClipBoard
import com.tamin.taminhamrah.utils.Utility.extractMessageText
import com.tamin.taminhamrah.utils.Utility.formatChatTime
import com.tamin.taminhamrah.utils.Utility.shareText

abstract class BaseChatViewHolder<T : AiChatModel>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected val context: Context = itemView.context
    abstract fun bind(message: T)
    open fun cleanup() {}

    protected fun setupChatAction(
        message: AiChatModel,
        btnLike: ImageView? = null,
        btnDislike: ImageView? = null,
        btnTime : TextView? = null,
        btnCopy: ImageView? = null,
        btnShare: ImageView? = null
    ) {
        fun updateFeedbackUI() {
            when (message.feedback) {
                FeedbackStatus.LIKE -> {
                    btnLike?.setImageResource(R.drawable.like_fill)
                    btnDislike?.setImageResource(R.drawable.dis_like_out)
                }
                FeedbackStatus.DISLIKE -> {
                    btnLike?.setImageResource(R.drawable.like_out)
                    btnDislike?.setImageResource(R.drawable.dis_like_fill)
                }
                FeedbackStatus.NONE -> {
                    btnLike?.setImageResource(R.drawable.like_out)
                    btnDislike?.setImageResource(R.drawable.dis_like_out)
                }
            }
        }
        updateFeedbackUI()
        btnLike?.setOnClickListener {
            message.feedback = if (message.feedback == FeedbackStatus.LIKE) {
                FeedbackStatus.NONE
            } else {
                FeedbackStatus.LIKE
            }
            updateFeedbackUI()
        }

        btnDislike?.setOnClickListener {
            message.feedback = if (message.feedback == FeedbackStatus.DISLIKE) {
                FeedbackStatus.NONE
            } else {
                FeedbackStatus.DISLIKE
            }
            updateFeedbackUI()
        }
        btnTime?.text = formatChatTime(message.createdAt)
        btnCopy?.setOnClickListener {
            val text = message.extractMessageText()
            if (text.isNotBlank()) {
                copyToClipBoard(context, text)
                Toast.makeText(context, "متن کپی شد", Toast.LENGTH_SHORT).show()
            }
        }
        btnShare?.setOnClickListener {
            val text = message.extractMessageText()
            context.shareText(text)
        }
    }
}