package com.tamin.taminhamrah.feature.myinbox.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.ui.ActionMenuItem
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.RecordCard
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_inbox
import taminx.core.core_ui.ic_tamin_track
import taminx.core.core_ui.identity_field_email
import taminx.core.core_ui.identity_field_mobile
import taminx.core.core_ui.identity_field_national_code
import taminx.core.core_ui.inbox_action_operations
import taminx.core.core_ui.inbox_inquiry_password
import taminx.core.core_ui.inbox_status_delivered
import taminx.core.core_ui.inbox_status_rejected
import taminx.core.core_ui.inbox_tracking_code

@Composable
fun InboxItemCard(
    item: PersonalInboxItemPR,
    actions: ImmutableList<ActionMenuItem<String>>,
    onActionSelect: (String) -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    val colors = LocalTaminColors.current
    var isExpanded by remember(item.id) { mutableStateOf(initialExpanded) }
    // The inbox rail is flat, so a solid brush rather than a gradient of one colour twice.
    val rail = remember(colors.blueText) { SolidColor(colors.blueText) }

    RecordCard(
        chipLabel = item.system,
        chipIcon = vectorResource(Res.drawable.ic_inbox),
        chipContainerColor = colors.blueBg,
        chipContentColor = colors.blueText,
        date = item.requestDate,
        title = item.subject,
        stampLabel = if (item.seen) {
            stringResource(Res.string.inbox_status_delivered)
        } else {
            stringResource(Res.string.inbox_status_rejected)
        },
        stampColor = if (item.seen) colors.greenText else colors.border,
        codeLabel = stringResource(Res.string.inbox_tracking_code),
        code = item.id.toString(),
        codeIcon = vectorResource(Res.drawable.ic_tamin_track),
        onCopyCode = onCopyClick,
        actionsLabel = stringResource(Res.string.inbox_action_operations),
        actions = actions,
        onActionSelect = onActionSelect,
        railBrush = rail,
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
        toggleButtonContentColor = colors.blueText,
    ) {
        DetailRow(
            label = stringResource(Res.string.identity_field_national_code),
            value = item.natCode.toPersianDigits(),
        )
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
        DetailRow(
            label = stringResource(Res.string.identity_field_email),
            value = item.email,
            numeric = false,
        )
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
        DetailRow(
            label = stringResource(Res.string.identity_field_mobile),
            value = item.mobile.toPersianDigits(),
        )
        TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
        DetailRow(
            label = stringResource(Res.string.inbox_inquiry_password),
            value = item.permissionPassword.toPersianDigits(),
        )
    }
}



@PreviewRtlTheme
@Composable
private fun InboxItemCardExpandedPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        Box(modifier = Modifier.fillMaxWidth().padding(Spacing.page)) {
            InboxItemCard(
                item = PersonalInboxItemPR(
                    id = 1,
                    refCode = "۳۱۸۶۲۲۶۲۱",
                    requestDate = "۱۴۰۴/۱۲/۱۹",
                    subject = "اعلام سابقه به مؤسسات",
                    seen = false,
                    system = "سازمان تأمین اجتماعی",
                    passwordCode = "۱۲۳۴۵۶",
                    natCode = "2222222",
                    email = "",
                    mobile = "",
                    permissionPassword = ""
                ),
                actions = kotlinx.collections.immutable.persistentListOf(),
                onActionSelect = {},
                initialExpanded = true,
                onCopyClick = {}
            )
        }
    }
}

