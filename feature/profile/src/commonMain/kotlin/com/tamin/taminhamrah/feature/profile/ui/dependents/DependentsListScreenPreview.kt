package com.tamin.taminhamrah.feature.profile.ui.dependents

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListIntent
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListState
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

// ─────────────────────────────────────────
// Shared mock data
// ─────────────────────────────────────────

private val mockWife = SubdominantItemPR(
    id = 1L,
    fullName = "منصوره آزادی",
    firstName = "منصوره",
    lastName = "آزادی",
    relationDescription = "همسر",
    nationalCode = "0073160997",
    birthDate = "۱۳۶۹/۰۵/۱۲",
    fatherName = "علی",
    insuranceId = "1346",
    status = "فعال"
)

private val mockSon = SubdominantItemPR(
    id = 2L,
    fullName = "امیرعلی آزادی",
    firstName = "امیرعلی",
    lastName = "آزادی",
    relationDescription = "فرزند پسر",
    nationalCode = "0052213341",
    birthDate = "۱۳۹۶/۰۳/۰۷",
    fatherName = "حسین",
    insuranceId = "2891",
    status = "فعال"
)

private val mockDaughter = SubdominantItemPR(
    id = 3L,
    fullName = "روناک موسوی",
    firstName = "روناک",
    lastName = "موسوی",
    relationDescription = "فرزند دختر",
    nationalCode = "0041108876",
    birthDate = "۱۳۸۵/۰۸/۲۰",
    fatherName = "رضا",
    insuranceId = "",
    status = "غیرفعال"
)

// ─────────────────────────────────────────
// Previews
// ─────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun DependentsListPreview_WithData() {
    PreviewRtlThemeContent {
        DependentsListScreen(
            state = DependentsListState(
                dependentsList = listOf(mockWife, mockSon, mockDaughter),
                expandedIds = setOf(1L)
            ),
            onIntent = {},
            onBackClicked = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DependentsListPreview_WithData_Dark() {
    PreviewRtlThemeContent(darkTheme = true) {
        DependentsListScreen(
            state = DependentsListState(
                dependentsList = listOf(mockWife, mockSon),
                expandedIds = setOf(2L)
            ),
            onIntent = {},
            onBackClicked = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DependentsListPreview_Empty() {
    PreviewRtlThemeContent {
        DependentsListScreen(
            state = DependentsListState(
                dependentsList = emptyList()
            ),
            onIntent = {},
            onBackClicked = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DependentsListPreview_Loading() {
    PreviewRtlThemeContent {
        DependentsListScreen(
            state = DependentsListState(isLoading = true),
            onIntent = {},
            onBackClicked = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DependentsListPreview_Error() {
    PreviewRtlThemeContent {
        DependentsListScreen(
            state = DependentsListState(
                error = "خطا در دریافت اطلاعات. لطفاً دوباره تلاش کنید."
            ),
            onIntent = {},
            onBackClicked = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
