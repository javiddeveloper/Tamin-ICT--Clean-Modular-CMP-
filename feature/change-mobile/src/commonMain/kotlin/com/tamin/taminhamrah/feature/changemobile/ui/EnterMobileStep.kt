package com.tamin.taminhamrah.feature.changemobile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.ui.theme.shimmer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileIntent
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileUiState
import com.tamin.taminhamrah.ui.components.AnimatedIconBadge
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.PhoneNumberField
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_mobile
import taminx.core.core_ui.ic_privacy
import taminx.core.core_ui.ic_send
import taminx.core.core_ui.profile_change_mobile_banner_info
import taminx.core.core_ui.profile_get_otp_code

@Composable
fun EnterMobileStep(
    uiState: ChangeMobileUiState,
    onIntent: (ChangeMobileIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg)
    ) {
        SectionHeaderTitle(title = "شماره همراه فعلی")
        CurrentMobileSection(
            currentMobile = uiState.currentMobile,
            isLoading = uiState.isLoading
        )
        Spacer(modifier = Modifier.height(Spacing.xl))
        NewMobileSection(
            newMobile = uiState.newMobile,
            isMobileError = uiState.isMobileError,
            onMobileChanged = { onIntent(ChangeMobileIntent.NewMobileChanged(it)) }
        )
        Spacer(modifier = Modifier.height(Spacing.xl))
        BannerCard(
            message = stringResource(Res.string.profile_change_mobile_banner_info),
            type = BannerType.Info
        )
        Spacer(modifier = Modifier.height(Spacing.xl))
        SubmitOtpButton(
            newMobile = uiState.newMobile,
            isLoading = uiState.isLoading,
            onSubmit = { onIntent(ChangeMobileIntent.GetOtpCode) }
        )
    }
}

@Composable
private fun CurrentMobileSection(
    currentMobile: String,
    isLoading: Boolean
) {
    val taminColors = LocalTaminColors.current

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedContent(
            targetState = currentMobile.isEmpty() && isLoading,
            label = "CurrentMobileLoadingState"
        ) { loading ->
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmer(
                            colorBase = taminColors.border,
                            colorHighlight = taminColors.bgSurface
                        )
                )
            } else {
                ListGroupView(
                    containerBackgroundColor = taminColors.verifiedContainerBg,
                    containerBorder = BorderStroke(1.dp, taminColors.verifiedContainerBorder),
                    items = persistentListOf(
                        ListItemData(
                            title = currentMobile.toPersianDigits(),
                            leadingIconPainter = painterResource(Res.drawable.ic_privacy),
                            leadingIconElevation = Elevation.xs,
                            colors = ListItemColors(
                                leadingIconBackgroundGradient = taminColors.verifiedIconGradient,
                                leadingIconBackgroundColor = taminColors.verifiedIconBg,
                                leadingIconTintColor = taminColors.verifiedIconTint,
                                titleColor = taminColors.springGreenText
                            ),
                            showArrow = false,
                            badge = ListItemBadge(
                                text = "تأییدشده",
                                backgroundColor = taminColors.verifiedBadgeBg,
                                textColor = taminColors.greenText
                            ),
                            titleStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    )
                )
            }
        }

        if (currentMobile.isNotEmpty() || !isLoading) {
            AnimatedIconBadge(
                icon = painterResource(Res.drawable.ic_arrow_down),
                modifier = Modifier.offset(y = 16.dp)
            )
        }
    }
}

@Composable
private fun NewMobileSection(
    newMobile: String,
    isMobileError: Boolean,
    onMobileChanged: (String) -> Unit
) {
    val taminColors = LocalTaminColors.current

    SectionHeaderTitle(
        title = "شماره همراه جدید",
        color = taminColors.blueText,
    )
    PhoneNumberField(
        value = newMobile,
        onValueChange = onMobileChanged,
        leadingIcon = vectorResource(Res.drawable.ic_mobile),
        error = isMobileError,
        showClearButton = true,
        errorMessage = if (isMobileError) "شماره موبایل باید ۱۱ رقم و با ۰۹ شروع شود." else null,
    )
}

@Composable
private fun SubmitOtpButton(
    newMobile: String,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    LoadingButton(
        text = stringResource(Res.string.profile_get_otp_code),
        onClick = onSubmit,
        enabled = newMobile.isNotEmpty() && !isLoading,
        isLoading = isLoading && newMobile.isNotEmpty(),
        icon = vectorResource(Res.drawable.ic_send),
        modifier = Modifier.fillMaxWidth()
    )
}
