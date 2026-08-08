package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.bankAccount.AccountType
import com.tamin.taminhamrah.model.bankAccount.Bank
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_digits_suffix
import taminx.core.core_ui.ic_number

private val SheetCorner = 28.dp
private val TileSize = 36.dp
private val TileCorner = 10.dp
private val LogoSize = 24.dp
private val IconSize = 18.dp

/**
 * The bank chooser.
 *
 * Each row carries its logo — the fastest way to find a bank in a list of six, and the reason the
 * marks were cropped away from their wordmarks — and its digit count, so the choice is made knowing
 * how long that bank's numbers are.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankPickerSheet(
    title: String,
    onSelect: (Bank) -> Unit,
    onDismiss: () -> Unit,
) {
    OptionSheet(onDismiss = onDismiss) {
        OptionSheetContent(title = title) { BankPickerRows(onSelect = onSelect) }
    }
}

/** The bank rows on their own, so a preview can draw them without a sheet around them. */
@Composable
internal fun ColumnScope.BankPickerRows(onSelect: (Bank) -> Unit) {
    Bank.displayOrder.forEachIndexed { index, bank ->
            if (index > 0) SheetDivider()
            OptionRow(
                label = stringResource(bank.label),
                onClick = { onSelect(bank) },
                leading = {
                    Box(
                        modifier = Modifier
                            .size(TileSize)
                            .clip(RoundedCornerShape(TileCorner))
                            .background(bank.chipSurface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(bank.logo),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(LogoSize),
                        )
                    }
                },
                trailing = {
                    CustomChip(
                        text = stringResource(
                            Res.string.bank_account_digits_suffix,
                            bank.accountNumberLength.toString().toPersianDigits(),
                        ),
                        containerColor = bank.chipSurface,
                        textColor = bank.chipInk,
                    )
            },
        )
    }
}

/** The account-kind chooser: five fixed options, each behind the same neutral tile. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTypePickerSheet(
    title: String,
    onSelect: (AccountType) -> Unit,
    onDismiss: () -> Unit,
) {
    OptionSheet(onDismiss = onDismiss) {
        OptionSheetContent(title = title) { AccountTypePickerRows(onSelect = onSelect) }
    }
}

/** The account-kind rows on their own, for the same reason as [BankPickerRows]. */
@Composable
internal fun ColumnScope.AccountTypePickerRows(onSelect: (AccountType) -> Unit) {
    val colors = LocalTaminColors.current
    AccountType.displayOrder.forEachIndexed { index, type ->
            if (index > 0) SheetDivider()
            OptionRow(
                label = stringResource(type.label),
                onClick = { onSelect(type) },
                leading = {
                    Box(
                        modifier = Modifier
                            .size(TileSize)
                            .clip(RoundedCornerShape(TileCorner))
                            .background(colors.blueBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_number),
                            contentDescription = null,
                            tint = colors.blueText,
                            modifier = Modifier.size(IconSize),
                        )
                    }
            },
        )
    }
}

/** The sheet itself. Split from [OptionSheetContent] so previews can render the content alone. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionSheet(
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        // Rounder than the app default: these two sheets carry a card of their own, and the
        // design gives the outer corner more curve so the inner list does not look pinched.
        shape = RoundedCornerShape(topStart = SheetCorner, topEnd = SheetCorner),
        content = content,
    )
}

/** Centred title over one rounded, divided list -- what both choosers put inside the sheet. */
@Composable
internal fun OptionSheetContent(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = Spacing.page)
            .padding(bottom = Spacing.md),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(Spacing.md))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.bgSurface),
            content = content,
        )
    }
}

@Composable
private fun OptionRow(
    label: String,
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        leading()
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke()
    }
}

@Composable
private fun SheetDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = Spacing.md),
        color = LocalTaminColors.current.divider,
    )
}
