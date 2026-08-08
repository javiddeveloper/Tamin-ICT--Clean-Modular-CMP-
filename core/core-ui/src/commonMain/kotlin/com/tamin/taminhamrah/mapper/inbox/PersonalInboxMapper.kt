package com.tamin.taminhamrah.mapper.inbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizePR
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.ui.ActionMenuItem
import kotlinx.collections.immutable.toPersistentList
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_email
import taminx.core.core_ui.ic_trash
import taminx.core.core_ui.ic_license

fun PersonalInboxItemDN.toPresentation(): PersonalInboxItemPR {
    val requestTimestamp = receiveDate ?: sentDate
    return PersonalInboxItemPR(
        id = id,
        refCode = id.toString(),
        requestDate = PersianDateFormatter.formatTimestamp(requestTimestamp),
        system = type?.typeDesc?:"",
        subject = subType?.typeDesc?:"",
        passwordCode = resolvePasswordCode(),
        seen = seen ?: false,
        natCode = nationalCode ?: "",
        email = email?: "_",
        mobile = mobileNumber?: "",
        permissionPassword = permission?.password?.toString() ?: "-",
        actions = buildList {
            if (permission == null || permission?.dateTo == null) {
                add(
                    ActionMenuItem(
                        value = "ISSUE_LICENSE",
                        label = "صدور مجوز استعلام",
                        icon = Res.drawable.ic_license,
                        hasDivider = true,
                        isWarningIcon = true
                    )
                )
            }
            add(
                ActionMenuItem(
                    value = "CORRESPONDENCE",
                    label = "مکاتبه",
                    icon = Res.drawable.ic_email,
                    hasDivider = true
                )
            )
            add(
                ActionMenuItem(
                    value = "DELETE",
                    label = "حذف",
                    icon = Res.drawable.ic_trash,
                    isDestructive = true
                )
            )
        }.toPersistentList()
    )
}

fun List<PersonalInboxItemDN>.toPresentation(): List<PersonalInboxItemPR> = map { it.toPresentation() }

fun PersonalInboxSizeDN.toPresentation(): PersonalInboxSizePR {
    val usage = usage?:""
    val total = total?:""
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
