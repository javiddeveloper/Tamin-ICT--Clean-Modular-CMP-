package com.tamin.taminhamrah.mapper.inbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizePR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun PersonalInboxItemDN.toPresentation(): PersonalInboxItemPR {
    val requestTimestamp = receiveDate ?: sentDate
    return PersonalInboxItemPR(
        id = id,
        refCode = id.toString(),
        requestDate = PersianDateFormatter.formatTimestamp(requestTimestamp),
        system = type?.typeDesc.orEmpty(),
        subject = subType?.typeDesc.orEmpty(),
        passwordCode = resolvePasswordCode(),
        seen = seen == true,
    )
}

fun List<PersonalInboxItemDN>.toPresentation(): List<PersonalInboxItemPR> = map { it.toPresentation() }

fun PersonalInboxSizeDN.toPresentation(): PersonalInboxSizePR {
    val usage = usage.orEmpty()
    val total = total.orEmpty()
    return PersonalInboxSizePR(
        usageMb = usage,
        totalMb = total,
        usageLabel = "$usage مگابایت",
        totalLabel = "$total مگابایت",
    )
}

private fun PersonalInboxItemDN.resolvePasswordCode(): String {
    if (permission == null || permission?.dateTo == null) {
        return "-"
    }
    return permission?.password?.toString() ?: "-"
}
