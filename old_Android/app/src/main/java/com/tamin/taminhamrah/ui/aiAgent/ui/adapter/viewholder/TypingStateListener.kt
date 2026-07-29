package com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder

import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable

interface TypingStateListener {
    fun startTyping(message: TypingAnimatable)
    fun completeTyping(message: TypingAnimatable)
    fun forceStopTyping()
    fun skipTypingAnimation()
}