package com.tamin.taminhamrah.feature.profile.ui.editPhoto.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.PhotoDialogState
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.alphanumericOnly
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_camera_lens
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_exclamation
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.profile_photo_dependants_placeholder
import taminx.core.core_ui.profile_photo_dependants_subtitle
import taminx.core.core_ui.profile_photo_dependants_title
import taminx.core.core_ui.profile_photo_error_title
import taminx.core.core_ui.profile_photo_full_name_label
import taminx.core.core_ui.profile_photo_guide_button
import taminx.core.core_ui.profile_photo_guide_receipt_desc
import taminx.core.core_ui.profile_photo_guide_receipt_title
import taminx.core.core_ui.profile_photo_guide_smart_card_desc
import taminx.core.core_ui.profile_photo_guide_smart_card_title
import taminx.core.core_ui.profile_photo_guide_title
import taminx.core.core_ui.profile_photo_info_desc
import taminx.core.core_ui.profile_photo_insurance_number_label
import taminx.core.core_ui.profile_photo_national_code_label
import taminx.core.core_ui.profile_photo_screen_title
import taminx.core.core_ui.profile_photo_serial_hint
import taminx.core.core_ui.profile_photo_serial_label
import taminx.core.core_ui.profile_photo_serial_placeholder
import taminx.core.core_ui.profile_photo_success_body
import taminx.core.core_ui.profile_photo_success_title

/** The old app's `maxLength` on the serial / tracking-code field. */
private const val SERIAL_LENGTH = 10

/** The smart-card serial is printed in Latin letters and digits, e.g. `1G50497996`. */
private val SerialFilter: (String) -> String = { it.alphanumericOnly().take(SERIAL_LENGTH) }

/** «کد ملی - نام - نسبت», the one line both the old app and the design print for a dependant. */
internal fun SubdominantItemPR.pickerLabel(): String =
    "${nationalCode.toPersianDigits()} - $fullName - $relationDescription"

/** The profile subpages' header, as on the bank-account and dependants pages. */
@Composable
internal fun EditProfilePhotoHeader(onBack: () -> Unit) {
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
                AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_tamin_camera_lens))
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

@Composable
internal fun EditProfilePhotoUserCard(
    userName: String,
    nationalCode: String,
    insuranceNumber: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = LocalTaminColors.current.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Column(modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)) {
            DetailRow(
                label = stringResource(Res.string.profile_photo_full_name_label),
                value = userName.orDash(),
                numeric = false,
            )
            TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
            DetailRow(
                label = stringResource(Res.string.profile_photo_national_code_label),
                value = nationalCode.toPersianDigits().orDash(),
            )
            TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
            DetailRow(
                label = stringResource(Res.string.profile_photo_insurance_number_label),
                value = insuranceNumber.toPersianDigits().orDash(),
            )
        }
    }
}

/** An empty submit only turns the border red; the message is the dialog, as in the design. */
@Composable
internal fun EditProfilePhotoSerialField(
    serial: String,
    isError: Boolean,
    onSerialChange: (String) -> Unit,
    onOpenGuide: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.profile_photo_serial_label),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.xs))
                    .clickable(onClick = onOpenGuide)
                    .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_info),
                    contentDescription = null,
                    tint = taminColors.blueText,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = stringResource(Res.string.profile_photo_guide_button),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = taminColors.blueText,
                )
            }
        }

        SegmentedInputField(
            value = serial,
            onValueChange = onSerialChange,
            slotCount = SERIAL_LENGTH,
            error = isError,
            placeholderText = stringResource(Res.string.profile_photo_serial_placeholder),
            leadingIcon = vectorResource(Res.drawable.ic_number),
            keyboardType = KeyboardType.Ascii,
            formatAsPersianDigits = false,
            valueFilter = SerialFilter,
        )

        Row(
            modifier = Modifier.padding(top = Spacing.xxs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(taminColors.orangeText, CircleShape),
            )
            Text(
                text = stringResource(Res.string.profile_photo_serial_hint),
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textTertiary,
            )
        }
    }
}

/** A missing pick only turns the picker's border red; the message is the dialog. */
@Composable
internal fun EditProfilePhotoDependantsCard(
    isDependantMode: Boolean,
    selectedDependant: SubdominantItemPR?,
    isError: Boolean,
    onToggle: (Boolean) -> Unit,
    onOpenPicker: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.profile_photo_dependants_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = stringResource(Res.string.profile_photo_dependants_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textTertiary,
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.md))
                TaminSwitchButton(checked = isDependantMode, onCheckedChange = onToggle)
            }

            AnimatedVisibility(visible = isDependantMode) {
                PickerRow(
                    text = selectedDependant?.pickerLabel()
                        ?: stringResource(Res.string.profile_photo_dependants_placeholder),
                    onClick = onOpenPicker,
                    modifier = Modifier.padding(top = Spacing.md),
                    isError = isError,
                    isPlaceholder = selectedDependant == null,
                    icon = vectorResource(Res.drawable.ic_tamin_user),
                    showChevron = true,
                )
            }
        }
    }
}

@Composable
internal fun EditProfilePhotoDialog(
    dialogState: PhotoDialogState,
    onDismiss: () -> Unit,
) {
    when (dialogState) {
        PhotoDialogState.Guide -> GuideDialog(onDismiss = onDismiss)
        PhotoDialogState.Success -> ResultDialog(
            title = stringResource(Res.string.profile_photo_success_title),
            description = stringResource(Res.string.profile_photo_success_body),
            isError = false,
            onDismiss = onDismiss,
        )
        is PhotoDialogState.ValidationError -> ResultDialog(
            title = stringResource(Res.string.profile_photo_error_title),
            description = stringResource(dialogState.message),
            isError = true,
            onDismiss = onDismiss,
        )
        is PhotoDialogState.ServerError -> ResultDialog(
            title = stringResource(Res.string.profile_photo_error_title),
            description = dialogState.message,
            isError = true,
            onDismiss = onDismiss,
        )
    }
}

/** The design's one result dialog: a green tick or a red exclamation, and «متوجه شدم». */
@Composable
private fun ResultDialog(
    title: String,
    description: String,
    isError: Boolean,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = title,
        description = description,
        icon = vectorResource(if (isError) Res.drawable.ic_tamin_exclamation else Res.drawable.ic_tamin_check),
        iconTint = if (isError) taminColors.dangerText else taminColors.greenText,
        iconBackground = if (isError) taminColors.dangerBg else taminColors.greenBg,
        confirmButton = { UnderstoodButton(onDismiss) },
        dismissButton = {},
        onDismissRequest = onDismiss,
    )
}

/** Where to find the code: the smart card's back, or the enrolment receipt's tracking code. */
@Composable
private fun GuideDialog(onDismiss: () -> Unit) {
    TaminConfirmationDialog(
        title = stringResource(Res.string.profile_photo_guide_title),
        description = "",
        icon = vectorResource(Res.drawable.ic_info),
        confirmButton = { UnderstoodButton(onDismiss) },
        dismissButton = {},
        onDismissRequest = onDismiss,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                GuideSection(
                    title = stringResource(Res.string.profile_photo_guide_smart_card_title),
                    description = stringResource(Res.string.profile_photo_guide_smart_card_desc),
                )
                GuideSection(
                    title = stringResource(Res.string.profile_photo_guide_receipt_title),
                    description = stringResource(Res.string.profile_photo_guide_receipt_desc),
                )
            }
        },
    )
}

@Composable
private fun UnderstoodButton(onClick: () -> Unit) {
    TaminFilledButton(
        text = stringResource(Res.string.btn_understood),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun GuideSection(
    title: String,
    description: String,
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(taminColors.bgPage, RoundedCornerShape(CornerRadius.md))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = taminColors.blueText,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textSecondary,
            lineHeight = 19.sp,
        )
    }
}
