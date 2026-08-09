package com.tamin.taminhamrah.feature.profile.ui.contactUs.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.AttachEmail
import androidx.compose.material.icons.outlined.Fax
import androidx.compose.material.icons.outlined.LocalPostOffice
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.contactUs.ContactDetailPR
import com.tamin.taminhamrah.model.contactUs.ContactDetailTypePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminColors
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_details_section

@Composable
fun ContactDetailsCard(
    details: ImmutableList<ContactDetailPR>,
    onDetailClick: (ContactDetailPR) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(Res.string.contact_us_details_section),
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 13.sp,
                color = taminColors.textSecondary
            ),
            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xs)
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(taminColors.bgSurface)
                .border(1.dp, taminColors.border, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                details.forEachIndexed { index, detail ->
                    ContactDetailRow(
                        detail = detail,
                        onClick = { onDetailClick(detail) }
                    )
                    if (index < details.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = Spacing.md),
                            thickness = 0.8.dp,
                            color = taminColors.divider
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactDetailRow(
    detail: ContactDetailPR,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.4f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(
                        taminColors.chipBg
                    ),
                contentAlignment = Alignment.Center
            ) {
                ContactDetailIcon(
                    taminColors,
                    type = detail.type,
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            Text(
                text = detail.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = taminColors.textSecondary
                )
            )
        }

        Spacer(modifier = Modifier.width(Spacing.sm))

        Text(
            text = detail.value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (detail.type == ContactDetailTypePR.WEBSITE || detail.type == ContactDetailTypePR.NEWS) {
                    taminColors.blueText
                } else {
                    taminColors.textPrimary
                }
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun ContactDetailIcon(
    taminColors: TaminColors,
    type: ContactDetailTypePR,
    modifier: Modifier = Modifier
) {
    val iconVector = remember(type) { getContactDetailVector(type) }

    Image(
        painter = rememberVectorPainter(iconVector),
        contentDescription = null,
        modifier = modifier.size(16.dp),
        colorFilter = ColorFilter.tint(taminColors.blueText)
    )
}

private fun getContactDetailVector(type: ContactDetailTypePR): ImageVector {

    when (type) {
        ContactDetailTypePR.PHONE -> {
            return Icons.Outlined.Phone
        }

        ContactDetailTypePR.FAX -> {
            return Icons.Outlined.Fax
        }

        ContactDetailTypePR.ADDRESS -> {
            return Icons.Outlined.AddLocation
        }

        ContactDetailTypePR.POSTAL_CODE -> {
            return Icons.Outlined.LocalPostOffice
        }

        ContactDetailTypePR.WEBSITE -> {
            return Icons.Outlined.OpenInBrowser
        }

        ContactDetailTypePR.NEWS -> {
            return Icons.Outlined.Newspaper
        }

        ContactDetailTypePR.EMAIL -> {
            return Icons.Outlined.AttachEmail
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContactDetailsCardLight() {
    PreviewRtlThemeContent {
        ContactDetailsCard(
            details = PreviewSampleContactDetails,
            onDetailClick = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContactDetailsCardDark() {
    TaminHamrahTheme(darkTheme = true) {
        ContactDetailsCard(
            details = PreviewSampleContactDetails,
            onDetailClick = {}
        )
    }
}

private val PreviewSampleContactDetails = persistentListOf(
    ContactDetailPR("1", ContactDetailTypePR.PHONE, "تلفن", "۰۲۱-۶۴۵۰۱", "tel:02164501", true),
    ContactDetailPR("2", ContactDetailTypePR.FAX, "فکس", "۰۲۱-۶۶۹۳۱۰۰۸", "tel:02166931008", true),
    ContactDetailPR(
        "3",
        ContactDetailTypePR.ADDRESS,
        "نشانی",
        "تهران، خیابان آزادی، جنب وزارت تعاون، کار و رفاه اجتماعی، پلاک ۳۵۹، سازمان تأمین اجتماعی",
        "geo:35.7011,51.3752",
        true
    ),
    ContactDetailPR("4", ContactDetailTypePR.POSTAL_CODE, "کد پستی", "۱۴۵۷۹۶۵۵۹۵", null, true),
    ContactDetailPR(
        "5",
        ContactDetailTypePR.WEBSITE,
        "درگاه رسمی",
        "tamin.ir",
        "https://tamin.ir",
        false
    ),
    ContactDetailPR(
        "6",
        ContactDetailTypePR.NEWS,
        "پایگاه خبری",
        "news.tamin.ir",
        "https://news.tamin.ir",
        false
    ),
    ContactDetailPR(
        "7",
        ContactDetailTypePR.EMAIL,
        "پست الکترونیک",
        "info@tamin.ir",
        "mailto:info@tamin.ir",
        true
    )
)
