package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import android.view.View
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel
import com.tamin.taminhamrah.databinding.ItemChatBotVoiceBinding
import com.tamin.taminhamrah.utils.Utility.formatChatTime

class VoiceViewHolder(
    private val binding: ItemChatBotVoiceBinding,
    private val listener: ChatActionListener
) : BaseChatViewHolder<VoiceModel>(binding.root) {

    private var currentSamplePath: String? = null

    override fun bind(message: VoiceModel) {
        updateVoicePlayer(message)
    }

    fun updateVoicePlayer(message: VoiceModel) {
        if (message.duration == 0 && !message.path.isNullOrEmpty()) {
            try {
                val file = java.io.File(message.path)
                if (file.exists()) {
                    val retriever = android.media.MediaMetadataRetriever()
                    retriever.setDataSource(file.absolutePath)
                    val timeString = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)
                    message.duration = timeString?.toIntOrNull() ?: 0
                    retriever.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.apply {
            audioPlayer.apply {
                maxProgress = message.duration.toFloat()
                if (currentSamplePath != message.path) {
                    setSampleFrom(message.path ?: "")
                    currentSamplePath = message.path
                }
                progress = message.playerProgress
            }

            buttonPlayPause.setImageResource(
                if (message.isPlaying)
                    R.drawable.ic_pause
                else
                    R.drawable.ic_play
            )

            buttonPlayPause.setOnClickListener {
                if (message.isPlaying) {
                    listener.onPauseClick(message, bindingAdapterPosition)
                } else {
                    listener.onPlayClick(message, bindingAdapterPosition)
                }
            }
            binding.textTime.text = formatChatTime(message.createdAt)
            binding.textTime.visibility = View.VISIBLE
        }
    }

    override fun cleanup() {
//        binding.buttonPlayPause.setOnClickListener(null)
    }
}