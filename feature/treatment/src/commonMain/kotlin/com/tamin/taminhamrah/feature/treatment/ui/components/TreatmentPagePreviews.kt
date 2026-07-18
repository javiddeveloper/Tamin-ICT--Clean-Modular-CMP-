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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat

/**
 * Whole-page previews that assemble the treatment components into the four screens they
 * were designed for. They exist to catch things a per-component preview cannot: spacing
 * between cards, how the teal header meets the content below it, and whether a pinned
 * bottom bar leaves enough room for the last row.
 *
 * These are previews only — no ViewModel, no navigation. Screens wire the same
 * components to real state.
 */

private val PAGE_WIDTH = 412.dp
private val PAGE_HEIGHT = 892.dp

/** How far the insurance carousel rides up into the hub header. */
private val CARD_OVERLAP = 40.dp

/**
 * Phone-sized, right-to-left, page-colored frame shared by every page preview.
 * The slot is [BoxScope] so pages can pin a bottom bar with `Modifier.align`.
 */
@Composable
private fun PreviewPage(content: @Composable BoxScope.() -> Unit) {
    PreviewRtlThemeContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .size(PAGE_WIDTH, PAGE_HEIGHT)
                    .background(LocalTaminColors.current.bgPage),
                content = content,
            )
        }
    }
}

@Composable
private fun BackIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "برگشت",
        tint = Color.White,
    )
}

// ---------------------------------------------------------------------------
// Page 1 · Treatment hub (درمان)
// ---------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun TreatmentHubPagePreview() {
    PreviewPage {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TreatmentHeader(
                title = "درمان",
                action = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "جست‌وجو",
                        tint = Color.White,
                    )
                },
                // Deep enough that the carousel can ride up into it without covering
                // the title, matching the design's tall header plus negative margin.
                modifier = Modifier.padding(bottom = CARD_OVERLAP + Spacing.xl),
            )
            // Everything below the header shifts up together, so the overlap does not
            // leave a gap the way offsetting the carousel alone would.
            Column(modifier = Modifier.offset(y = -CARD_OVERLAP)) {
                HubCarousel()
                HubQuickAccess()
                HubCategories()
                Spacer(modifier = Modifier.height(Spacing.xxl + CARD_OVERLAP))
            }
        }
    }
}

@Composable
private fun HubCarousel(modifier: Modifier = Modifier) {
    val people = listOf(
        "علی رضایی" to "0079542318",
        "مریم رضایی" to "0079542319",
        "سارا رضایی" to "0079542320",
    )
    InsuranceCardCarousel(
        pageCount = people.size,
        pagerState = rememberPagerState { people.size },
        modifier = modifier,
    ) { page ->
        InsuranceCard(
            holderName = people[page].first,
            nationalId = people[page].second,
            coverageLabel = "وضعیت حمایت‌های درمانی: برخوردار هستید",
        )
    }
}

@Composable
private fun HubQuickAccess() {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier.padding(horizontal = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        SectionLabel(text = "دسترسی سریع")
        QuickAccessCard(
            title = "سوابق درمانی من",
            subtitle = "تاریخچهٔ نسخه، ویزیت، پاراکلینیک و آزمایش",
            icon = Icons.AutoMirrored.Filled.List,
            trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            onClick = {},
        )
        TreatmentNavigationCard(
            title = "پروندهٔ سلامت من",
            subtitle = "خوداظهاری سلامت و اطلاعات پزشکی",
            icon = Icons.Filled.Favorite,
            iconTint = colors.blueText,
            iconBackground = Brush.linearGradient(listOf(colors.blueBg, colors.blueBg)),
            trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            onClick = {},
            status = {
                StatusPill(
                    text = "تکمیل نشده",
                    containerColor = colors.orangeBg,
                    contentColor = colors.orangeText,
                )
            },
        )
        TreatmentNavigationCard(
            title = "مراکز درمانی طرف قرارداد",
            subtitle = "جست‌وجوی بیمارستان و داروخانه",
            icon = Icons.Filled.LocationOn,
            iconTint = colors.teal,
            iconBackground = Brush.linearGradient(listOf(colors.greenBg, colors.greenBg)),
            trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            onClick = {},
        )
    }
}

@Composable
private fun HubCategories() {
    val colors = LocalTaminColors.current
    val categories = listOf(
        "نسخه‌های الکترونیک" to colors.blueText,
        "تاییدیه‌های پزشکی" to colors.teal,
        "خسارت متفرقه" to colors.orangeText,
    )
    Column(
        modifier = Modifier.padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
            categories.forEach { (label, tint) ->
                CategoryTile(
                    label = label,
                    icon = Icons.Filled.Info,
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
                categories = listOf("همه", "دارو", "ویزیت", "پاراکلینیک", "بستری"),
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
    TreatmentHeader(
        title = "سوابق درمانی",
        navigationIcon = { BackIcon() },
        action = {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "اشتراک‌گذاری",
                tint = Color.White,
            )
        },
    ) {
        TimelineFilterBar(
            personLabel = "علی رضایی",
            dateLabel = "۶ ماه اخیر",
            dropdownIcon = Icons.Filled.KeyboardArrowDown,
            searchIcon = Icons.Filled.Search,
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
            TreatmentHeader(
                title = "نسخهٔ الکترونیک",
                navigationIcon = { BackIcon() },
                action = {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "اشتراک‌گذاری",
                        tint = Color.White,
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
        TreatmentBottomBar(modifier = Modifier.align(Alignment.BottomCenter)) {
            TreatmentPrimaryButton(
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
            TreatmentHeader(
                title = "تاییدیهٔ پزشکی",
                navigationIcon = { BackIcon() },
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
            TreatmentHeader(
                title = "مراکز طرف قرارداد",
                navigationIcon = { BackIcon() },
            ) {
                TreatmentSearchField(
                    value = "",
                    onValueChange = {},
                    placeholder = "جست‌وجوی نام مرکز، بیمارستان یا داروخانه",
                    searchIcon = Icons.Filled.Search,
                    modifier = Modifier.padding(top = Spacing.lg),
                )
            }
            TreatmentFilterChipRow(
                categories = listOf("همه", "بیمارستان", "داروخانه", "آزمایشگاه", "درمانگاه"),
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
            TreatmentHeader(
                title = "مراکز طرف قرارداد",
                navigationIcon = { BackIcon() },
            ) {
                TreatmentSearchField(
                    value = "بیمارستان نامشخص",
                    onValueChange = {},
                    placeholder = "جست‌وجوی نام مرکز، بیمارستان یا داروخانه",
                    searchIcon = Icons.Filled.Search,
                    modifier = Modifier.padding(top = Spacing.lg),
                )
            }
            TreatmentFilterChipRow(
                categories = listOf("همه", "بیمارستان", "داروخانه", "آزمایشگاه"),
                selectedIndex = 1,
                onSelect = {},
            )
            TreatmentEmptyState(message = "مرکزی با این مشخصات پیدا نشد")
        }
    }
}
