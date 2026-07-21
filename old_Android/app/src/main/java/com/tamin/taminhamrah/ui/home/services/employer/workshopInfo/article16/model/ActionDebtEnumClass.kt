package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model

import com.tamin.taminhamrah.R

enum class ActionDebtEnumClass(var title:Int, var id:String) {
    INVESTIGATION_DEBTS(title = R.string.investigation_debts_request_menu_item, id = "0"),
    EXPERT_MESSAGE(title = R.string.expert_message, id = "2"),
    MODIFY_REQUEST(title = R.string.modify_request, id = "3"),
    SHOW_REQUEST(title = R.string.show_request, id = "4")
}
