package com.tamin.taminhamrah.feature.profile.ui.editPhoto.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_camera
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.profile_photo_dependants_placeholder
import taminx.core.core_ui.profile_photo_dependants_subtitle
import taminx.core.core_ui.profile_photo_dependants_title
import taminx.core.core_ui.profile_photo_dialog_understood
import taminx.core.core_ui.profile_photo_error_select_dependant
import taminx.core.core_ui.profile_photo_full_name_label
import taminx.core.core_ui.profile_photo_guide_old_card_desc
import taminx.core.core_ui.profile_photo_guide_old_card_title
import taminx.core.core_ui.profile_photo_guide_receipt_desc
import taminx.core.core_ui.profile_photo_guide_receipt_title
import taminx.core.core_ui.profile_photo_guide_smart_card_desc
import taminx.core.core_ui.profile_photo_guide_smart_card_title
import taminx.core.core_ui.profile_photo_guide_title
import taminx.core.core_ui.profile_photo_info_desc
import taminx.core.core_ui.profile_photo_insurance_number_label
import taminx.core.core_ui.profile_photo_national_code_label
import taminx.core.core_ui.profile_photo_screen_title

/**
 * User identification summary card displaying full name, national code, and optional insurance number
 * reusing standard [DetailRow] and [TaminDivider] components.
 */
@Composable
fun EditProfilePhotoUserCard(
    userName: String,
    nationalCode: String,
    insuranceNumber: String,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            DetailRow(
                label = stringResource(Res.string.profile_photo_full_name_label),
                value = userName.ifBlank { "—" },
            )
            TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
            DetailRow(
                label = stringResource(Res.string.profile_photo_national_code_label),
                value = nationalCode.ifBlank { "—" },
                numeric = true,
            )
            if (insuranceNumber.isNotBlank()) {
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                DetailRow(
                    label = stringResource(Res.string.profile_photo_insurance_number_label),
                    value = insuranceNumber,
                    numeric = true,
                )
            }
        }
    }
}

/**
 * Dependants inquiry card reusing standard [TaminSwitchButton] and [PickerRow].
 * Toggling the switch animates open the picker row allowing the user to select an eligible dependant.
 */
@Composable
fun EditProfilePhotoDependantsCard(
    isDependantMode: Boolean,
    onToggle: (Boolean) -> Unit,
    selectedDependant: SubdominantItemPR?,
    onOpenPicker: () -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.profile_photo_dependants_title),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = stringResource(Res.string.profile_photo_dependants_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textSecondary,
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.md))
                TaminSwitchButton(
                    checked = isDependantMode,
                    onCheckedChange = onToggle,
                )
            }

            AnimatedVisibility(
                visible = isDependantMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(modifier = Modifier.padding(top = Spacing.md)) {
                    PickerRow(
                        text = selectedDependant?.let { "${it.fullName} (${it.relationDescription})" }
                            ?: stringResource(Res.string.profile_photo_dependants_placeholder),
                        onClick = onOpenPicker,
                        isPlaceholder = selectedDependant == null,
                        isError = isError,
                        icon = vectorResource(Res.drawable.ic_tamin_user),
                        subtitle = selectedDependant?.nationalCode?.let {
                            "${stringResource(Res.string.profile_photo_national_code_label)}: ${it.toPersianDigits()}"
                        },
                        showChevron = true,
                    )
                    if (isError) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = stringResource(Res.string.profile_photo_error_select_dependant),
                            style = MaterialTheme.typography.labelSmall,
                            color = taminColors.dangerText,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard feature header with collapsing title bar, back affordance, radial decoration,
 * camera animated ring icon, and explanatory subtitle.
 */
@Composable
fun EditProfilePhotoHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    TaminTopAppBar(
        title = stringResource(Res.string.profile_photo_screen_title),
        centerTitle = true,
        background = profileGradientBrush,
        bottomPadding = Spacing.xl,
        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = stringResource(Res.string.action_back),
                onClick = onBack,
                bordered = true,
            )
        },
        modifier = modifier,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            DecorativeBackgroundCircle(
                size = 190.dp,
                xOffset = 450.dp,
                yOffset = (-150).dp,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_tamin_camera))
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = stringResource(Res.string.profile_photo_info_desc),
                    style = MaterialTheme.typography.labelSmall,
                    color = taminColors.textHeaderSubtitle,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }
        }
    }
}

/**
 * Educational guide dialog detailing the 3 methods to locate the serial or tracking number:
 * 1. Smart ID Card (back vertical 10-char serial e.g. 1P12863319)
 * 2. Paper registration receipt from Civil Registry / Post / Pishkhan (10-digit tracking code e.g. 6035130378)
 * 3. Old National Card (bottom-left digits before first dash e.g. 010524678)
 */
@Composable
fun EditProfilePhotoGuideDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(Res.string.profile_photo_guide_title),
        description = "",
        icon = vectorResource(Res.drawable.ic_info),
        iconTint = taminColors.blueText,
        iconBackground = taminColors.blueBg,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.profile_photo_dialog_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                GuideSectionCard(
                    title = stringResource(Res.string.profile_photo_guide_smart_card_title),
                    desc = stringResource(Res.string.profile_photo_guide_smart_card_desc),
                )
                GuideSectionCard(
                    title = stringResource(Res.string.profile_photo_guide_receipt_title),
                    desc = stringResource(Res.string.profile_photo_guide_receipt_desc),
                )
                GuideSectionCard(
                    title = stringResource(Res.string.profile_photo_guide_old_card_title),
                    desc = stringResource(Res.string.profile_photo_guide_old_card_desc),
                )
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun GuideSectionCard(
    title: String,
    desc: String,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.md),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgPage),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.blueText,
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textSecondary,
                lineHeight = 19.sp,
            )
        }
    }
}

