package com.tamin.taminhamrah.data.repository.ai.model

import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.DeepLinkData
import com.tamin.taminhamrah.utils.extentions.randomUUID


abstract class AiChatModel(
    var isError: Boolean = false,
    var feedback: FeedbackStatus = FeedbackStatus.NONE,
    val createdAt: Long = System.currentTimeMillis()
) {
    abstract val id: String
}
enum class FeedbackStatus {
    NONE, LIKE, DISLIKE
}

// Interface for messages that support typing animation
interface TypingAnimatable {
    var id: String
    var isTypingComplete: Boolean
    var shouldStartTyping: Boolean
    var skipTyping: Boolean
}

// Text-based chat model (for regular chat messages)
data class AiLawChatModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = false,
    override var skipTyping: Boolean = false,
    val body: String? = null,
    val content: String? = null,
    val idx: Int? = 0,
    val name: String? = null,
    val reference: String? = null,
    val score: Double? = 0.0,
    val sourceFile: String? = null,
    val clickableItems: ClickableItemModel,
) : AiChatModel(), TypingAnimatable


// Text-based chat model (for regular chat messages)
data class AiTextModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = false,
    override var skipTyping: Boolean = false,
    val title: String?= null,
    val message: String,
    val isUserMessage: Boolean = false,
) : AiChatModel(), TypingAnimatable

// Key-Value data model (for structured data display)
data class AiKeyValueModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,
    val title: String? = null,
    val keyValueItems: List<KeyValueModel>
) : AiChatModel(), TypingAnimatable

// KeyValue model (keeping the existing structure)
data class KeyValueModel(
    val _key: String,
    val _value: String,
    val _type: String? = null,
)
// KeyValue model (keeping the existing structure)
data class PromptModel(
    val prompt: String,
    val action: AgentActionContent? = null
)



//"doctorName": "ali",
//"proficiency": "3",
//"nezam": 11111

// Error model (for error messages)
data class AiErrorModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,
    val message: String,
) : AiChatModel(), TypingAnimatable


// Clickable item model (for laws search results)
data class AiClickableModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,
    val content: String,
    val clickableItems: ClickableItemModel,
) : AiChatModel(), TypingAnimatable

data class ClickableItemModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,
    val title: String? = null,
    val customData: AiChatModel,
    val clickType: AIClickType = AIClickType.Default,
): TypingAnimatable

enum class AIClickType{
    Law,Prescription,Default,SchedulePatient
}

data class VoiceModel(
    override var id: String = randomUUID(),
    val path: String? = null,
    var duration: Int = 0,
    var isPlaying: Boolean = false,
    var playerProgress: Float = -1F,
) : AiChatModel()


data class AiHeaderModel(
    val title: String?= null,
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,
    override var id: String = randomUUID(),
    ) : AiChatModel(), TypingAnimatable

data class AgentClickableModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,
    val title: String?,
    val content: List<KeyValueModel>,
    val actionContent: AgentActionContent,
) : AiChatModel(), TypingAnimatable

data class AgentGroupButtonModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,

    val title: String?,
    val prompts: List<PromptModel>,
    val actionContent: AgentActionContent,
    val color: String? = null,
    val content: List<KeyValueModel>? = null,
) : AiChatModel(), TypingAnimatable

data class AiGenerativeModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = true,
    override var skipTyping: Boolean = false,
    val schema: FormSchema,
    val data: Map<String?, String?>? = null,
    var isExpanded: Boolean = false
) : AiChatModel(), TypingAnimatable

data class AgentLawChatModel(
    override var id: String = randomUUID(),
    override var isTypingComplete: Boolean = false,
    override var shouldStartTyping: Boolean = false,
    override var skipTyping: Boolean = false,
    val name: String? = null,
    val reference: String? = null,
    val content: String? = null,
    val sourceFile: String? = null,
    val score: Double? = 0.0,
    val url: String ,
    val actionContent: AgentActionContent,
) : AiChatModel(), TypingAnimatable

sealed  class AgentActionContent(open val actionText: String?,open val color: String? = null ){
    data class SendPrompt(val prompt: String,override val actionText: String?): AgentActionContent(actionText)
    data class Web(val url: String, override val actionText: String?): AgentActionContent(actionText)
    data class DeepLink(val deepLinkData: DeepLinkData,override val actionText: String?): AgentActionContent(actionText)
    data class LocalDeepLink(val uri: String, override val actionText: String?) : AgentActionContent(actionText)
    data class Dial(val phoneNumber: String, override val actionText: String?) : AgentActionContent(actionText)
    data class EditMobile(override val actionText: String?) : AgentActionContent(actionText)
    data class CancelDependent(override val actionText: String?) : AgentActionContent(actionText)
    data class WeddingPresent(override val actionText: String?) : AgentActionContent(actionText)
    data class AddAccountNumber(override val actionText: String?) : AgentActionContent(actionText)
    data class DisplayReport(val title: String? = null, override val actionText: String?, val customColor: String? = null) : AgentActionContent(actionText,customColor)
    data class InquiryEducation(override val actionText: String?) : AgentActionContent(actionText)
    data class OccurrenceReportGet(override val actionText: String?) : AgentActionContent(actionText)
}
