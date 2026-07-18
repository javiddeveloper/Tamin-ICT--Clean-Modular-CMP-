package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat

/**
 * Previews for the treatment component library.
 *
 * These wrap [PreviewRtlThemeContent] in an explicit right-to-left override because
 * TaminHamrahTheme currently provides a same-named CompositionLocal of its own rather
 * than the platform one, so it does not actually flip the preview. The override is
 * harmless once the theme starts providing the real local.
 */
@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    PreviewRtlThemeContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .background(LocalTaminColors.current.bgPage)
                    .padding(Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
            ) {
                content()
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun InsuranceCardPreview() {
    PreviewSurface {
        InsuranceCard(
            holderName = "علی رضایی",
            nationalId = "0079542318",
            coverageLabel = "وضعیت حمایت‌های درمانی: برخوردار هستید",
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InsuranceCardCarouselPreview() {
    val people = listOf("علی رضایی", "مریم رضایی", "سارا رضایی")
    PreviewRtlThemeContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = Modifier.background(LocalTaminColors.current.bgPage)) {
                InsuranceCardCarousel(
                    pageCount = people.size,
                    pagerState = rememberPagerState { people.size },
                ) { page ->
                    InsuranceCard(
                        holderName = people[page],
                        nationalId = "007954231$page",
                        coverageLabel = "وضعیت حمایت‌های درمانی: برخوردار هستید",
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun HubCardsPreview() {
    PreviewSurface {
        val colors = LocalTaminColors.current
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

@PreviewRtlTheme
@Composable
private fun CategoryGridPreview() {
    PreviewSurface {
        val colors = LocalTaminColors.current
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        ) {
            listOf(
                "نسخه‌های الکترونیک" to colors.blueText,
                "تاییدیه‌های پزشکی" to colors.teal,
                "خسارت متفرقه" to colors.orangeText,
            ).forEach { (label, tint) ->
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

@PreviewRtlTheme
@Composable
private fun TimelineRecordPreview() {
    PreviewSurface {
        val colors = LocalTaminColors.current
        RecordGroupHeader(text = "اسفند ۱۴۰۴")
        MedicalRecordCard(
            category = "نسخهٔ دارویی",
            date = "۱۴۰۴/۱۲/۰۳",
            title = "داروخانهٔ شبانه‌روزی مرکزی",
            subtitle = "دکتر محمدی · متخصص داخلی",
            shareAmount = 65_910L.toPriceFormat(),
            accentColor = colors.blueText,
            accentContainerColor = colors.blueBg,
            categoryIcon = Icons.Filled.Info,
            onClick = {},
        )
        MedicalRecordCard(
            category = "ویزیت",
            date = "۱۴۰۴/۱۱/۲۱",
            title = "درمانگاه تخصصی امام رضا",
            subtitle = "دکتر کریمی · قلب و عروق",
            shareAmount = 219_700L.toPriceFormat(),
            accentColor = colors.teal,
            accentContainerColor = colors.greenBg,
            categoryIcon = Icons.Filled.Favorite,
            onClick = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TimelineChromePreview() {
    PreviewRtlThemeContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = Modifier.background(LocalTaminColors.current.bgPage)) {
                TreatmentHeader(
                    title = "سوابق درمانی",
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "برگشت",
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
                TreatmentFilterChipRow(
                    categories = listOf("همه", "دارو", "ویزیت", "پاراکلینیک"),
                    selectedIndex = 0,
                    onSelect = {},
                )
                CostTotalsBar(
                    insuredShareLabel = "سهم بیمه‌شده",
                    insuredShareAmount = 65_910L.toPriceFormat(),
                    organizationShareLabel = "سهم سازمان",
                    organizationShareAmount = 153_790L.toPriceFormat(),
                    totalLabel = "جمع کل",
                    totalAmount = 219_700L.toPriceFormat(),
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun FilterAndPersonPreview() {
    PreviewSurface {
        val colors = LocalTaminColors.current
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            TreatmentFilterChip(label = "همه", selected = true, onClick = {})
            TreatmentFilterChip(label = "دارو", selected = false, onClick = {})
        }
        StatTile(
            label = "سهم شما",
            amount = 65_910L.toPriceFormat(),
            containerColor = colors.greenBg,
            contentColor = colors.greenText,
        )
        IconTile(
            icon = Icons.Filled.Person,
            tint = colors.blueText,
            background = Brush.linearGradient(listOf(colors.blueBg, colors.blueBg)),
        )
        StatusPill(
            text = "۶ ماه اخیر",
            containerColor = colors.orangeBg,
            contentColor = colors.orangeText,
            icon = Icons.Filled.DateRange,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun RecordDetailPrescriptionPreview() {
    PreviewSurface {
        RecordSummaryCard(
            metaLabel = "داروخانه",
            metaValue = "داروخانهٔ شبانه‌روزی مرکزی",
            trackingCode = "۸۸۲۴۵۱۹۰۳",
            date = "۱۴۰۴/۱۲/۰۳",
        )
        PrescriptionItemCard(
            name = "آموکسی‌سیلین ۵۰۰ میلی‌گرم",
            dose = "هر ۸ ساعت یک کپسول، به مدت ۷ روز، بعد از غذا",
            prescribedCount = "۲۱",
            receivedCount = "۲۱",
        )
        CostBreakdownCard(
            total = 219_700L.toPriceFormat(),
            organizationShare = 153_790L.toPriceFormat(),
            insuredShare = 65_910L.toPriceFormat(),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun RecordDetailVisitAndLabPreview() {
    PreviewSurface {
        val colors = LocalTaminColors.current
        VisitSummaryCard(
            reason = "درد قفسهٔ سینه و تنگی نفس هنگام فعالیت",
            diagnosis = "آنژین صدری پایدار",
            note = "انجام تست ورزش و مراجعهٔ مجدد پس از دو هفته",
        )
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
            normalRange = "< ۲۰۰",
            statusContainerColor = colors.orangeBg,
            statusContentColor = colors.orangeText,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun MedicalCentersPreview() {
    PreviewRtlThemeContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            val colors = LocalTaminColors.current
            Column(modifier = Modifier.background(colors.bgPage)) {
                TreatmentHeader(
                    title = "مراکز طرف قرارداد",
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "برگشت",
                            tint = Color.White,
                        )
                    },
                ) {
                    TreatmentSearchField(
                        value = "",
                        onValueChange = {},
                        placeholder = "جست‌وجوی نام مرکز، بیمارستان یا داروخانه",
                        searchIcon = Icons.Filled.Search,
                        modifier = Modifier.padding(top = Spacing.lg),
                    )
                }
                Column(
                    modifier = Modifier.padding(Spacing.page),
                    verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                ) {
                    MedicalCenterCard(
                        name = "بیمارستان میلاد",
                        type = "بیمارستان",
                        address = "تهران، بزرگراه همت، بین شیخ فضل‌الله و برق آلستوم",
                        distanceLabel = "۲٫۴ کیلومتر",
                        icon = Icons.Filled.Home,
                        accentColor = colors.blueText,
                        accentContainerColor = colors.blueBg,
                        distanceIcon = Icons.Filled.LocationOn,
                        callIcon = Icons.Filled.Phone,
                        onCallClick = {},
                    )
                    MedicalCenterCard(
                        name = "داروخانهٔ دکتر رضایی",
                        type = "داروخانه",
                        address = "تهران، خیابان ولیعصر، نبش کوچهٔ بهار",
                        distanceLabel = "۰٫۸ کیلومتر",
                        icon = Icons.Filled.Favorite,
                        accentColor = colors.teal,
                        accentContainerColor = colors.greenBg,
                        distanceIcon = Icons.Filled.LocationOn,
                        callIcon = Icons.Filled.Phone,
                        onCallClick = {},
                    )
                    TreatmentEmptyState(message = "مرکزی با این مشخصات پیدا نشد")
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun BottomBarPreview() {
    PreviewRtlThemeContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = Modifier.background(LocalTaminColors.current.bgPage)) {
                TreatmentBottomBar {
                    TreatmentPrimaryButton(
                        text = "دریافت نسخهٔ الکترونیک",
                        icon = Icons.Filled.KeyboardArrowDown,
                        onClick = {},
                    )
                }
            }
        }
    }
}
