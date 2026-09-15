package com.tamin.taminhamrah.feature.stories.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.stories.ui.model.StoryChannelPR
import com.tamin.taminhamrah.feature.stories.ui.model.StoryCtaPR
import com.tamin.taminhamrah.feature.stories.ui.model.StoryItemPR
import com.tamin.taminhamrah.feature.stories.ui.model.StoryMediaPR
import com.tamin.taminhamrah.feature.stories.ui.rail.StoryRailBody
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailUiState
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryPalette
import com.tamin.taminhamrah.feature.stories.ui.viewer.StoryViewerBody
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import taminx.feature.stories.generated.resources.Res
import taminx.feature.stories.generated.resources.ic_story_assistant
import taminx.feature.stories.generated.resources.ic_story_public_relations
/*
 * The rail and the viewer, without their ViewModels. Every state either can be in is one preview
 * away, which is the point of the two `*Body` composables taking plain state.
 */
/* ---- Rail ------------------------------------------------------------------------------- */
@Composable
private fun RailPreviewBody(state: StoryRailUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalTaminColors.current.bgPage)
            .padding(vertical = Spacing.xlg),
    ) {
        StoryRailBody(state = state, onChannelClick = {}, onRetry = {})
    }
}
@PreviewRtlTheme
@Composable
private fun StoryRailLightPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        RailPreviewBody(PreviewRailState)
    }
}
@PreviewRtlTheme
@Composable
private fun StoryRailDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        RailPreviewBody(PreviewRailState)
    }
}
/** The first channel already watched: grey ring, muted label, receded icon. */
@PreviewRtlTheme
@Composable
private fun StoryRailSeenPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        RailPreviewBody(PreviewRailState.copy(seenKeys = persistentSetOf("pr")))
    }
}
@PreviewRtlTheme
@Composable
private fun StoryRailLoadingPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        RailPreviewBody(StoryRailUiState(isLoading = true))
    }
}
@PreviewRtlTheme
@Composable
private fun StoryRailEmptyPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        RailPreviewBody(StoryRailUiState(isLoading = false))
    }
}
@PreviewRtlTheme
@Composable
private fun StoryRailErrorPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        RailPreviewBody(StoryRailUiState(isLoading = false, error = "خطا"))
    }
}
/* ---- Viewer ----------------------------------------------------------------------------- */
/**
 * Boxed to a phone-sized frame rather than left to fill the preview, so the bottom-anchored copy
 * and the top-anchored progress bar land where they would on a device.
 */
@Composable
private fun ViewerPreviewBody(state: StoryViewerUiState) {
    Box(modifier = Modifier.fillMaxWidth().height(PreviewViewerHeight)) {
        StoryViewerBody(
            state = state,
            onIntent = {},
            onDismissError = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
private val PreviewViewerHeight = 780.dp
/** The slide with a call to action, which is the taller of the two layouts. */
@PreviewRtlTheme
@Composable
private fun StoryViewerPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        ViewerPreviewBody(PreviewViewerState)
    }
}
/** The design's reference screenshot: second slide, no call to action, nothing liked yet. */
@PreviewRtlTheme
@Composable
private fun StoryViewerWithoutCtaPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        ViewerPreviewBody(PreviewViewerState.copy(itemIndex = 1))
    }
}
/** Liked and bookmarked, and the media having failed — every marker the action bar can show. */
@PreviewRtlTheme
@Composable
private fun StoryViewerEngagedPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        ViewerPreviewBody(
            PreviewViewerState.copy(
                itemIndex = 1,
                mediaFailed = true,
                likedItems = persistentSetOf("pr:1"),
                savedItems = persistentSetOf("pr:1"),
            ),
        )
    }
}
/**
 * The comment field in use: the story is held, the field has the bar to itself, and the send
 * button has appeared because there is something to send.
 */
@PreviewRtlTheme
@Composable
private fun StoryViewerComposingCommentPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        ViewerPreviewBody(
            PreviewViewerState.copy(
                itemIndex = 1,
                isComposingComment = true,
                commentDraft = "خیلی خوب شد که همهٔ خدمات یک‌جا جمع شده",
            ),
        )
    }
}
/* ---- Fixtures --------------------------------------------------------------------------- */
private val PreviewPalette = StoryPalette(
    ringStart = Color(0xFF7FB4FF),
    ringEnd = Color(0xFF1F4FA3),
    avatarStart = Color(0xFF3B6FE8),
    avatarEnd = Color(0xFF1FB6D8),
    backdropStart = Color(0xFF0B2450),
    backdropMid = Color(0xFF123B77),
    backdropMidStop = 0.55f,
    backdropEnd = Color(0xFF0E5E84),
    ctaTone = Color(0xFF123B77),
    iconTint = Color(0xFFEAF1FF),
    iconTone = Color(0xFF1F4FA3),
)
private val PreviewChannel = StoryChannelPR(
    key = "pr",
    name = "روابط عمومی سازمان",
    shortName = "روابط عمومی",
    time = "امروز · ۰۹:۴۰",
    palette = PreviewPalette,
    icon = Res.drawable.ic_story_public_relations,
    items = persistentListOf(
        StoryItemPR(
            id = "pr:0",
            title = "تأمین‌من به‌روز شد",
            body = "پرداخت حق بیمه، مشاهدهٔ سوابق و دریافت فیش، همه در یک صفحه جمع شده است.",
            media = StoryMediaPR.None,
            cta = StoryCtaPR("دیدن سوابق من", "tamin://feature/WAGE_AND_HISTORY"),
        ),
        StoryItemPR(
            id = "pr:1",
            title = "خدمات غیرحضوری",
            body = "بیشتر درخواست‌ها را از همین اپ ثبت کنید؛ مراجعه به شعبه فقط برای موارد ضروری لازم است.",
            media = StoryMediaPR.None,
        ),
        StoryItemPR(
            id = "pr:2",
            title = "ارتباط با ما",
            body = "پاسخ‌گویی تلفنی و پیام‌رسان سازمان، همهٔ روزهای هفته در دسترس شماست.",
            media = StoryMediaPR.None,
        ),
    ),
)
private val PreviewAssistantChannel = PreviewChannel.copy(
    key = "ai",
    name = "هوش مصنوعی · یارا",
    shortName = "هوش مصنوعی",
    time = "امروز · ۰۸:۱۵",
    icon = Res.drawable.ic_story_assistant,
    palette = PreviewPalette.copy(
        ringStart = Color(0xFFB9A6FF),
        ringEnd = Color(0xFF7C5CFF),
        iconTint = Color(0xFFF1ECFF),
    ),
)
private val PreviewRailState = StoryRailUiState(
    isLoading = false,
    channels = listOf(PreviewChannel, PreviewAssistantChannel).toImmutableList(),
)
private val PreviewViewerState = StoryViewerUiState(
    isLoading = false,
    channels = persistentListOf(PreviewChannel),
)
