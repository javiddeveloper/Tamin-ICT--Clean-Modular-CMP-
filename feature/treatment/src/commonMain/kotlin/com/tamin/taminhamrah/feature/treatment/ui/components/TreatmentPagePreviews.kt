package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import kotlinx.collections.immutable.persistentListOf
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminSearchField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminHeroGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_medical_approvals
import taminx.core.core_ui.ic_tamin_misc_claims
import taminx.core.core_ui.ic_tamin_prescriptions
import taminx.core.core_ui.ic_tamin_print
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.ic_tamin_verified

/**
 * Whole-page previews that assemble the treatment components into the four screens they
 * were designed for. They exist to catch things a per-component preview cannot: spacing
 * between cards, how the teal header meets the content below it, and whether a pinned
 * bottom bar leaves enough room for the last row.
 *
 * These are previews only — no ViewModel, no navigation. Screens wire the same
 * components to real state.
 */


/**
 * Phone-sized, right-to-left, page-colored frame shared by every page preview.
 * The slot is [BoxScope] so pages can pin a bottom bar with `Modifier.align`.
 */
@Composable
private fun PreviewPage(content: @Composable BoxScope.() -> Unit) {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier
                .size(TreatmentDimens.pageWidth, TreatmentDimens.pageHeight)
                .background(LocalTaminColors.current.bgPage),
            content = content,
        )
    }
}

@Composable
private fun BackButton() {
    TaminTopAppBarButton(
        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
        contentDescription = "برگشت",
        onClick = {},
    )
}

// Page 1 · Treatment hub (درمان)

@PreviewRtlTheme
@Composable
private fun TreatmentHubPagePreview() {
    PreviewPage {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TaminTopAppBar(
                title = "درمان",
                centerTitle = false,
                background = taminHeroGradient(LocalTaminColors.current.treatmentHubStops),
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_search),
                        contentDescription = "جست‌وجو",
                        onClick = {},
                        bordered = true,
                    )
                },
                // Deep enough that the carousel can ride up into it without covering
                // the title, matching the design's tall header plus negative margin.
                bottomPadding = TreatmentDimens.cardOverlap + Spacing.xl,
            )
            // Everything below the header shifts up together, so the overlap does not
            // leave a gap the way offsetting the carousel alone would.
            Column(modifier = Modifier.offset(y = -TreatmentDimens.cardOverlap)) {
                HubCarousel()
                HubCategories()
                Spacer(modifier = Modifier.height(Spacing.xxl + TreatmentDimens.cardOverlap))
            }
        }
    }
}

@Composable
private fun HubCarousel(modifier: Modifier = Modifier) {
    // First entry is the main insured person; the rest are dependants, so each takes
    // the next card identity.
    val people = listOf(
        Triple("سنا حقیقی", "0441456789", false),
        Triple("نگین رضایی", "0012345678", true),
        Triple("آرمین حقیقی", "0098765432", true),
        Triple("آوا حقیقی", "0055443322", true),
    )
    InsuranceCardCarousel(
        pageCount = people.size,
        pagerState = rememberPagerState { people.size },
        modifier = modifier,
    ) { page ->
        val (name, nid, isDependent) = people[page]
        InsuranceCard(
            holderName = name,
            nationalId = nid,
            coverageLabel = "وضعیت حمایت‌های درمانی: برخوردار هستید",
            coverageBadge = { CoverageBadge(icon = vectorResource(Res.drawable.ic_tamin_verified)) },
            background = insuranceCardGradient(
                isDependent = isDependent,
                dependantOrdinal = people.take(page).count { it.third },
            ),
        )
    }
}



@Composable
private fun HubCategories() {
    val colors = LocalTaminColors.current
    val categories = persistentListOf(
        Triple("نسخه‌های الکترونیک", colors.blueText, vectorResource(Res.drawable.ic_tamin_prescriptions)),
        Triple("تاییدیه‌های پزشکی", colors.teal, vectorResource(Res.drawable.ic_tamin_medical_approvals)),
        Triple("خسارت متفرقه", colors.orangeText, vectorResource(Res.drawable.ic_tamin_misc_claims)),
    )
    Column(
        modifier = Modifier.padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
            categories.forEach { (label, tint, glyph) ->
                CategoryTile(
                    label = label,
                    icon = glyph,
                    iconTint = tint,
                    iconBackground = Brush.linearGradient(
                        listOf(colors.bgPage, colors.divider),
                    ),
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }
        CostSummaryCard(
            title = "هزینه‌های سال ۱۴۰۵ (سال جاری)",
            insuredShareLabel = "سهم بیمه‌شده",
            insuredShareAmount = 6_591_000L.toPriceFormat(),
            organizationShareLabel = "سهم سازمان",
            organizationShareAmount = 15_379_000L.toPriceFormat(),
        )
    }
}

// ---------------------------------------------------------------------------
// Page 2 · Medical records timeline (سوابق درمانی)
// ---------------------------------------------------------------------------

private data class PreviewRecord(
    val category: String,
    val date: String,
    val title: String,
    val subtitle: String,
    val share: Long,
    val isVisit: Boolean,
)

private val previewRecords = listOf(
    "اسفند ۱۴۰۴" to listOf(
        PreviewRecord(
            category = "نسخهٔ دارویی",
            date = "۱۴۰۴/۱۲/۰۳",
            title = "داروخانهٔ شبانه‌روزی مرکزی",
            subtitle = "دکتر محمدی · متخصص داخلی",
            share = 65_910L,
            isVisit = false,
        ),
        PreviewRecord(
            category = "ویزیت",
            date = "۱۴۰۴/۱۲/۰۱",
            title = "درمانگاه تخصصی امام رضا",
            subtitle = "دکتر کریمی · قلب و عروق",
            share = 120_000L,
            isVisit = true,
        ),
    ),
    "بهمن ۱۴۰۴" to listOf(
        PreviewRecord(
            category = "پاراکلینیک",
            date = "۱۴۰۴/۱۱/۲۱",
            title = "آزمایشگاه مرکزی پاستور",
            subtitle = "آزمایش خون و ادرار",
            share = 33_790L,
            isVisit = false,
        ),
    ),
)

@PreviewRtlTheme
@Composable
private fun MedicalRecordsPagePreview() {
    PreviewPage {
        val colors = LocalTaminColors.current
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            RecordsHeader()
            TreatmentFilterChipRow(
                categories = persistentListOf("همه", "دارو", "ویزیت", "پاراکلینیک", "خدمات پزشکی"),
                selectedIndex = 0,
                onSelect = {},
            )
            previewRecords.forEach { (group, records) ->
                RecordGroupHeader(text = group)
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.page),
                    verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                ) {
                    records.forEach { record ->
                        MedicalRecordCard(
                            category = record.category,
                            date = record.date,
                            title = record.title,
                            subtitle = record.subtitle,
                            shareAmount = record.share.toPriceFormat(),
                            accentColor = if (record.isVisit) colors.teal else colors.blueText,
                            accentContainerColor =
                                if (record.isVisit) colors.greenBg else colors.blueBg,
                            categoryIcon = if (record.isVisit) {
                                Icons.Filled.Favorite
                            } else {
                                Icons.Filled.Info
                            },
                            onClick = {},
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
            // Clearance so the last card is not hidden behind the pinned totals bar.
            Spacer(modifier = Modifier.height(120.dp))
        }
        CostTotalsBar(
            insuredShareLabel = "سهم بیمه‌شده",
            insuredShareAmount = 219_700L.toPriceFormat(),
            organizationShareLabel = "سهم سازمان",
            organizationShareAmount = 153_790L.toPriceFormat(),
            totalLabel = "جمع کل",
            totalAmount = 373_490L.toPriceFormat(),
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun RecordsHeader() {
    TaminTopAppBar(
        title = "سوابق درمانی",
        navigationIcon = { BackButton() },
        action = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_print),
                contentDescription = "اشتراک‌گذاری",
                onClick = {},
            )
        },
    ) {
        TimelineFilterBar(
            personLabel = "علی رضایی",
            dateLabel = "۶ ماه اخیر",
            dropdownIcon = Icons.Filled.KeyboardArrowDown,
            searchIcon = vectorResource(Res.drawable.ic_tamin_search),
            onPersonClick = {},
            onDateClick = {},
            onSearchClick = {},
        )
    }
}

// ---------------------------------------------------------------------------
// Page 3 · Record detail (جزئیات نسخه)
// ---------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun RecordDetailPagePreview() {
    PreviewPage {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TaminTopAppBar(
                title = "نسخهٔ الکترونیک",
                background = taminHeroGradient(LocalTaminColors.current.treatmentHubStops),
                navigationIcon = { BackButton() },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_print),
                        contentDescription = "اشتراک‌گذاری",
                        onClick = {},
                    )
                },
            )
            Column(
                modifier = Modifier.padding(Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
            ) {
                RecordSummaryCard(
                    metaLabel = "داروخانه",
                    metaValue = "داروخانهٔ شبانه‌روزی مرکزی",
                    trackingCode = "۸۸۲۴۵۱۹۰۳",
                    date = "۱۴۰۴/۱۲/۰۳",
                )
                SectionLabel(text = "اقلام دارویی")
                PrescriptionItemCard(
                    name = "آموکسی‌سیلین ۵۰۰ میلی‌گرم",
                    dose = "هر ۸ ساعت یک کپسول، به مدت ۷ روز، بعد از غذا",
                    prescribedCount = "۲۱",
                    receivedCount = "۲۱",
                )
                PrescriptionItemCard(
                    name = "استامینوفن کدئین ۳۰۰ میلی‌گرم",
                    dose = "در صورت درد، حداکثر سه عدد در شبانه‌روز",
                    prescribedCount = "۲۰",
                    receivedCount = "۱۰",
                )
                CostBreakdownCard(
                    total = 219_700L.toPriceFormat(),
                    organizationShare = 153_790L.toPriceFormat(),
                    insuredShare = 65_910L.toPriceFormat(),
                )
            }
            // Clearance so the cost card is not hidden behind the pinned action bar.
            Spacer(modifier = Modifier.height(100.dp))
        }
        TaminBottomBar(modifier = Modifier.align(Alignment.BottomCenter)) {
            TaminPrimaryButton(
                text = "دریافت نسخهٔ الکترونیک",
                icon = Icons.Filled.KeyboardArrowDown,
                onClick = {},
            )
        }
    }
}

/** Visit and paraclinic records reuse the same page shell with different body cards. */
@PreviewRtlTheme
@Composable
private fun RecordDetailVisitPagePreview() {
    PreviewPage {
        val colors = LocalTaminColors.current
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TaminTopAppBar(
                title = "تاییدیهٔ پزشکی",
                background = taminHeroGradient(LocalTaminColors.current.treatmentHubStops),
                navigationIcon = { BackButton() },
            )
            Column(
                modifier = Modifier.padding(Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
            ) {
                RecordSummaryCard(
                    metaLabel = "مرکز درمانی",
                    metaValue = "درمانگاه تخصصی امام رضا",
                    trackingCode = "۷۷۱۹۰۳۴۵۲",
                    date = "۱۴۰۴/۱۱/۲۱",
                )
                SectionLabel(text = "خلاصهٔ ویزیت")
                VisitSummaryCard(
                    reason = "درد قفسهٔ سینه و تنگی نفس هنگام فعالیت",
                    diagnosis = "آنژین صدری پایدار",
                    note = "انجام تست ورزش و مراجعهٔ مجدد پس از دو هفته",
                )
                SectionLabel(text = "نتایج آزمایش")
                LabTestCard(
                    name = "قند خون ناشتا",
                    status = "طبیعی",
                    result = "۹۴",
                    normalRange = "۷۰ - ۱۰۰",
                    statusContainerColor = colors.greenBg,
                    statusContentColor = colors.greenText,
                )
                LabTestCard(
                    name = "کلسترول تام",
                    status = "بالاتر از حد",
                    result = "۲۴۵",
                    normalRange = "کمتر از ۲۰۰",
                    statusContainerColor = colors.orangeBg,
                    statusContentColor = colors.orangeText,
                )
                CostBreakdownCard(
                    total = 480_000L.toPriceFormat(),
                    organizationShare = 360_000L.toPriceFormat(),
                    insuredShare = 120_000L.toPriceFormat(),
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

// ---------------------------------------------------------------------------
// Page 4 · Contracted centres (مراکز طرف قرارداد)
// ---------------------------------------------------------------------------

private data class PreviewCentre(
    val name: String,
    val type: String,
    val address: String,
    val distance: String,
    val isHospital: Boolean,
)

private val previewCentres = listOf(
    PreviewCentre(
        name = "بیمارستان میلاد",
        type = "بیمارستان",
        address = "تهران، بزرگراه همت، بین شیخ فضل‌الله و برق آلستوم",
        distance = "۲٫۴ کیلومتر",
        isHospital = true,
    ),
    PreviewCentre(
        name = "داروخانهٔ دکتر رضایی",
        type = "داروخانه",
        address = "تهران، خیابان ولیعصر، نبش کوچهٔ بهار",
        distance = "۰٫۸ کیلومتر",
        isHospital = false,
    ),
    PreviewCentre(
        name = "آزمایشگاه پاستور",
        type = "آزمایشگاه",
        address = "تهران، خیابان انقلاب، روبه‌روی دانشگاه تهران",
        distance = "۳٫۱ کیلومتر",
        isHospital = false,
    ),
)

@PreviewRtlTheme
@Composable
private fun MedicalCentresPagePreview() {
    PreviewPage {
        val colors = LocalTaminColors.current
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TaminTopAppBar(
                title = "مراکز طرف قرارداد",
                background = taminHeroGradient(LocalTaminColors.current.treatmentHubStops),
                navigationIcon = { BackButton() },
            ) {
                TaminSearchField(
                    value = "",
                    onValueChange = {},
                    placeholder = "جست‌وجوی نام مرکز، بیمارستان یا داروخانه",
                    searchIcon = vectorResource(Res.drawable.ic_tamin_search),
                    modifier = Modifier.padding(top = Spacing.lg),
                )
            }
            TreatmentFilterChipRow(
                categories = persistentListOf("همه", "بیمارستان", "داروخانه", "آزمایشگاه", "درمانگاه"),
                selectedIndex = 0,
                onSelect = {},
            )
            Text(
                text = "۳ مرکز یافت شد · نزدیک‌ترین به دورترین",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page, vertical = Spacing.sm),
            )
            Column(
                modifier = Modifier.padding(horizontal = Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                previewCentres.forEach { centre ->
                    MedicalCenterCard(
                        name = centre.name,
                        type = centre.type,
                        address = centre.address,
                        distanceLabel = centre.distance,
                        icon = if (centre.isHospital) Icons.Filled.Home else Icons.Filled.Favorite,
                        accentColor = if (centre.isHospital) colors.blueText else colors.teal,
                        accentContainerColor =
                            if (centre.isHospital) colors.blueBg else colors.greenBg,
                        distanceIcon = Icons.Filled.LocationOn,
                        callIcon = Icons.Filled.Phone,
                        onCallClick = {},
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }
    }
}

/** The same screen with a query that matched nothing. */
@PreviewRtlTheme
@Composable
private fun MedicalCentresEmptyPagePreview() {
    PreviewPage {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TaminTopAppBar(
                title = "مراکز طرف قرارداد",
                background = taminHeroGradient(LocalTaminColors.current.treatmentHubStops),
                navigationIcon = { BackButton() },
            ) {
                TaminSearchField(
                    value = "بیمارستان نامشخص",
                    onValueChange = {},
                    placeholder = "جست‌وجوی نام مرکز، بیمارستان یا داروخانه",
                    searchIcon = vectorResource(Res.drawable.ic_tamin_search),
                    modifier = Modifier.padding(top = Spacing.lg),
                )
            }
            TreatmentFilterChipRow(
                categories = persistentListOf("همه", "بیمارستان", "داروخانه", "آزمایشگاه"),
                selectedIndex = 1,
                onSelect = {},
            )
            TaminEmptyState(message = "مرکزی با این مشخصات پیدا نشد")
        }
    }
}
