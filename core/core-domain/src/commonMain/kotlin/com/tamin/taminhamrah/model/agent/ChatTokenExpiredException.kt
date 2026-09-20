package com.tamin.taminhamrah.model.agent

/**
 * The assistant rejected the chat token sent with a prompt (HTTP 400, or a body marked
 * `INVALID_OR_EXPIRED_CHAT_TOKEN`). Raised by the data layer so the prompt can be sent once more
 * with a fresh token; see `SendAgentPromptUseCase`.
 */
class ChatTokenExpiredException : Exception("Chat token expired")
