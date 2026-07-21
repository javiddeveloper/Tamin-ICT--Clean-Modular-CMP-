package com.tamin.taminhamrah.ui.aiAgent.ui.strategy

import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.PromptModel
import com.tamin.taminhamrah.ui.aiAgent.ui.adapter.viewholder.ChatActionListener

interface PromptActionStrategy {
    fun execute(action: AgentActionContent, listener: ChatActionListener?)
}

class SendPromptStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.SendPrompt) {
            listener?.onChipClicked(action.prompt)
        }
    }
}

class OpenLinkStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.Web) {
            listener?.onActionClick(action, -1)
        }
    }
}

class DialStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.Dial) {
            listener?.onActionClick(action, -1)
        }
    }
}

class DeepLinkStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.DeepLink) {
            listener?.onActionClick(action, -1)
        }
    }
}

class LocalDeepLinkStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.LocalDeepLink) {
            listener?.onActionClick(action, -1)
        }
    }
}

class EditMobileStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.EditMobile) {
            listener?.onActionClick(action, -1)
        }
    }
}

class DisplayReportStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.DisplayReport) {
            listener?.onDisplayReport(action)
        }
    }
}

class AddAccountNumberStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.AddAccountNumber) {
            listener?.onActionClick(action, -1)
        }
    }
}

class CancelDependentStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.CancelDependent) {
            listener?.onActionClick(action, -1)
        }
    }
}

class WeddingPresentStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.WeddingPresent) {
            listener?.onActionClick(action, -1)
        }
    }
}

class InquiryEducationStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.InquiryEducation) {
            listener?.onActionClick(action, -1)
        }
    }
}

class OccurrenceReportGetStrategy : PromptActionStrategy {
    override fun execute(action: AgentActionContent, listener: ChatActionListener?) {
        if (action is AgentActionContent.OccurrenceReportGet) {
            listener?.onActionClick(action, -1)
        }
    }
}

object PromptActionStrategyFactory {
    fun getStrategyAndAction(
        prompt: PromptModel,
        parentAction: AgentActionContent?
    ): Pair<PromptActionStrategy, AgentActionContent> {
        val action = prompt.action
        if (action != null) {
            return getStrategy(action) to action
        }

        if (parentAction is AgentActionContent.LocalDeepLink) {
            return LocalDeepLinkStrategy() to parentAction
        }

        // Default fallback: SendPrompt
        return SendPromptStrategy() to AgentActionContent.SendPrompt(prompt.prompt, null)
    }

    fun getStrategy(action: AgentActionContent): PromptActionStrategy {
        return when (action) {
            is AgentActionContent.SendPrompt -> SendPromptStrategy()
            is AgentActionContent.Web -> OpenLinkStrategy()
            is AgentActionContent.Dial -> DialStrategy()
            is AgentActionContent.DeepLink -> DeepLinkStrategy()
            is AgentActionContent.LocalDeepLink -> LocalDeepLinkStrategy()
            is AgentActionContent.EditMobile -> EditMobileStrategy()
            is AgentActionContent.AddAccountNumber -> AddAccountNumberStrategy()
            is AgentActionContent.DisplayReport -> DisplayReportStrategy()
            is AgentActionContent.CancelDependent -> CancelDependentStrategy()
            is AgentActionContent.WeddingPresent -> WeddingPresentStrategy()
            is AgentActionContent.InquiryEducation -> InquiryEducationStrategy()
            is AgentActionContent.OccurrenceReportGet -> OccurrenceReportGetStrategy()
        }
    }
}
