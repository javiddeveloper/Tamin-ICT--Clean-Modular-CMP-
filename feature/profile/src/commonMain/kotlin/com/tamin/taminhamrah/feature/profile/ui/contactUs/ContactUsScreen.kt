package com.tamin.taminhamrah.feature.profile.ui.contactUs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.tamin.taminhamrah.feature.profile.ui.contactUs.components.ContactDetailsCard
import com.tamin.taminhamrah.feature.profile.ui.contactUs.components.ContactUsFooter
import com.tamin.taminhamrah.feature.profile.ui.contactUs.components.ContactUsHeader
import com.tamin.taminhamrah.feature.profile.ui.contactUs.components.HotlineCard
import com.tamin.taminhamrah.feature.profile.ui.contactUs.components.SocialChannelsGrid
import com.tamin.taminhamrah.feature.profile.ui.contactUs.contract.ContactUsEvent
import com.tamin.taminhamrah.feature.profile.ui.contactUs.contract.ContactUsIntent
import com.tamin.taminhamrah.feature.profile.ui.contactUs.contract.ContactUsUiState
import com.tamin.taminhamrah.model.contactUs.ContactDetailPR
import com.tamin.taminhamrah.model.contactUs.ContactDetailTypePR
import com.tamin.taminhamrah.model.contactUs.ContactUsPR
import com.tamin.taminhamrah.model.contactUs.HotlinePR
import com.tamin.taminhamrah.model.contactUs.SocialChannelPR
import com.tamin.taminhamrah.model.contactUs.SocialChannelTypePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_copied

@Composable
fun ContactUsRoute(
    viewModel: ContactUsViewModel,
    onOpenUrl: (String) -> Unit = {},
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val toaster = LocalToaster.current
    val copiedText = stringResource(Res.string.contact_us_copied)

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ContactUsIntent.LoadContactUs)
    }

    HandleContactUsEvents(
        events = viewModel.events,
        onOpenUrl = onOpenUrl,
        onCopyToClipboard = { text, label ->
            clipboardManager.setText(AnnotatedString(text))
            toaster.success("$label: $copiedText")
        },
        onBackClicked = onBackClicked
    )

    ContactUsScreen(
        state = uiState,
        onIntent = viewModel::sendIntent
    )
}

@Composable
fun HandleContactUsEvents(
    events: Flow<ContactUsEvent>,
    onOpenUrl: (String) -> Unit,
    onCopyToClipboard: (text: String, label: String) -> Unit,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ContactUsEvent.NavigateBack -> onBackClicked()
            is ContactUsEvent.OpenUrl -> onOpenUrl(event.url)
            is ContactUsEvent.CopyToClipboard -> onCopyToClipboard(event.text, event.label)
            is ContactUsEvent.ShowToast -> {
            }
        }
    }
}

@Composable
fun ContactUsScreen(
    modifier: Modifier = Modifier,
    state: ContactUsUiState,
    onIntent: (ContactUsIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ContactUsHeader(
                onBackClicked = { onIntent(ContactUsIntent.OnBackClicked) }
            )

            if (state.isLoading) {
                LoadingStateOverlay()
            } else {
                state.contactInfo?.let { info ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        overscrollEffect = rememberJellyOverscroll(),
                        contentPadding = PaddingValues(
                            top = Spacing.md,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + Spacing.xxl,
                            start = Spacing.page,
                            end = Spacing.page
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        item(key = "hotline_card") {
                            HotlineCard(
                                hotline = info.hotline,
                                onCallClicked = { onIntent(ContactUsIntent.OnCallHotline) }
                            )
                        }

                        item(key = "social_channels") {
                            SocialChannelsGrid(
                                channels = info.socialChannels,
                                onChannelClick = { onIntent(ContactUsIntent.OnSocialChannelClick(it)) }
                            )
                        }

                        item(key = "contact_details") {
                            ContactDetailsCard(
                                details = info.contactDetails,
                                onDetailClick = { onIntent(ContactUsIntent.OnContactDetailClick(it)) }
                            )
                        }

                        item(key = "footer") {
                            ContactUsFooter()
                        }
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContactUsScreenLightPreview() {
    PreviewRtlThemeContent {
        ContactUsScreen(
            state = ContactUsUiState(
                isLoading = false,
                contactInfo = getSampleContactUsPR()
            ),
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContactUsScreenDarkPreview() {
    com.tamin.taminhamrah.ui.theme.TaminHamrahTheme(darkTheme = true) {
        ContactUsScreen(
            state = ContactUsUiState(
                isLoading = false,
                contactInfo = getSampleContactUsPR()
            ),
            onIntent = {}
        )
    }
}

private fun getSampleContactUsPR() = ContactUsPR(
    hotline = HotlinePR(
        title = "مرکز تماس شبانه‌روزی",
        number = "۱۴۲۰",
        dialNumber = "1420"
    ),
    socialChannels = listOf(
        SocialChannelPR("1", "ایمیل", SocialChannelTypePR.EMAIL, "mailto:info@tamin.ir"),
        SocialChannelPR("2", "سوالات متداول", SocialChannelTypePR.FAQ, "https://tamin.ir/faq"),
        SocialChannelPR("3", "واتساپ", SocialChannelTypePR.WHATSAPP, "https://wa.me/989000000000"),
        SocialChannelPR("4", "آی‌گپ", SocialChannelTypePR.IGAP, "https://igap.net/tamin"),
        SocialChannelPR("5", "بله", SocialChannelTypePR.BALE, "https://ble.ir/tamin"),
        SocialChannelPR("6", "بیسفون", SocialChannelTypePR.BISPHONE, "https://bisphone.com"),
        SocialChannelPR("7", "گپ", SocialChannelTypePR.GAP, "https://gap.im/tamin"),
        SocialChannelPR("8", "سروش", SocialChannelTypePR.SOROUSH, "https://splus.ir/tamin"),
        SocialChannelPR("9", "روبیکا", SocialChannelTypePR.RUBIKA, "https://rubika.ir/tamin"),
        SocialChannelPR("10", "ایتا", SocialChannelTypePR.EITAA, "https://eitaa.com/tamin")
    ).toImmutableList(),
    contactDetails = listOf(
        ContactDetailPR("1", ContactDetailTypePR.PHONE, "تلفن", "۰۲۱-۶۴۵۰۱", "tel:02164501", true),
        ContactDetailPR(
            "2",
            ContactDetailTypePR.FAX,
            "فکس",
            "۰۲۱-۶۶۹۳۱۰۰۸",
            "tel:02166931008",
            true
        ),
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
    ).toImmutableList(),
    footerTitle = "پاسخگوی شما هستیم",
    footerSubtitle = "۲۴ ساعته، ۷ روز هفته در کنار شما"
)
