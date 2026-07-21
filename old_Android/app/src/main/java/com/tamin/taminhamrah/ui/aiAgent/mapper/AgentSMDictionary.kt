package com.tamin.taminhamrah.ui.aiAgent.mapper

import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import javax.inject.Inject

class AgentSMDictionary @Inject constructor() {

    fun getFilledMessage(
        inputParams: List<AgentSuccessMessageParamsNames>,
        inputMessage: String
    ): String {
        var messageFilledMessage: String = inputMessage
        inputParams.forEach {
            messageFilledMessage = messageFilledMessage.replace(it.key, it.value)
        }
        return messageFilledMessage
    }


//
//        // Return the map with the newly processed filter list
//        return mapOf(
//            "filter" to processedFilter,
//            "itemType" to this.itemType,
//            "limit" to this.limit,
//            "page" to this.page,
//            "sort" to this.sort,
//            "start" to this.start,
//        )
//    }

}