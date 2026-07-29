package com.tamin.taminhamrah.feature.profile.ui.identity

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInEvent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInIntent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInUiState
import com.tamin.taminhamrah.model.identity.IdentityInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.UserAvatar
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_ejtemaei_logo
import taminx.core.core_ui.ic_tamin_verified
import kotlin.math.roundToInt

@Composable
fun IdentityInRoute(
    viewModel: IdentityInViewModel,
    onNavigateToRoute: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(IdentityInIntent.LoadIdentity)
    }

    HandleIdentityInEvents(
        events = viewModel.events,
        onNavigateToRoute = onNavigateToRoute,
        onBackClicked = onBackClicked
    )

    IdentityInScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleIdentityInEvents(
    events: Flow<IdentityInEvent>,
    @Suppress("UNUSED_PARAMETER") onNavigateToRoute: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            IdentityInEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
fun IdentityInScreen(
    state: IdentityInUiState,
    onIntent: (IdentityInIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()
    val collapse = rememberCollapsingHeaderState(220.dp)
    val headerProgress = remember(collapse) { { collapse.progress } }
    var headerHeightPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(taminColors.bgPage)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection)
                .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.reservedHeight { headerHeightPx })

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = taminColors.teal)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxWidth().padding(Spacing.lg), contentAlignment = Alignment.Center) {
                    Text(text = state.error, color = taminColors.dangerText, textAlign = TextAlign.Center)
                }
            } else {
                state.identityInfo?.let { info ->
                    IdentityContent(info)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))
        }

        IdentityHeader(
            progress = headerProgress,
            onBack = { onIntent(IdentityInIntent.OnBackClicked) },
            identityInfo = state.identityInfo,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height }
        )
    }
}

@Composable
private fun IdentityHeader(
    progress: () -> Float,
    onBack: () -> Unit,
    identityInfo: IdentityInfoPR?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = "اطلاعات هویتی",
            centerTitle = true,
            background = taminTopAppBarGradient(LocalTaminColors.current.profileGradientStops),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape)
                )
            },
            bottomPadding = 48.dp,
            modifier = Modifier.graphicsLayer {
                // Fade out top bar title as we collapse
                // Actually TaminTopAppBar doesn't expose title alpha easily,
                // but we can overlap it with the card content.
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .rideUpIntoHeader(progress = progress, expandedOverlap = 48.dp, collapsedOverlap = 8.dp)
        ) {
            identityInfo?.let { info ->
                IdentityInsuranceCard(
                    info = info,
                    collapseProgress = progress,
                    modifier = Modifier.padding(horizontal = Spacing.page)
                )
            }
        }
    }
}

@Composable
private fun IdentityContent(info: IdentityInfoPR) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_tamin_verified),
                contentDescription = null,
                tint = Color(0xFF03AD5F),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "اطلاعات از سازمان ثبت احوال استعلام و تأیید شده است",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Color(0xFF8E8E8E)
            )
        }

        IdentitySectionCard(title = "اطلاعات فردی") {
            IdentityInfoRow(label = "نام", value = info.firstName)
            IdentityInfoRow(label = "نام خانوادگی", value = info.lastName)
            IdentityInfoRow(label = "نام پدر", value = info.fatherName)
            IdentityInfoRow(label = "جنسیت", value = info.genderDisplay)
            IdentityInfoRow(label = "تاریخ تولد", value = info.dateOfBirthFormatted.toPersianDigits())
            IdentityInfoRow(label = "ملیت", value = "ایرانی", showDivider = false)
        }

        IdentitySectionCard(title = "شماره شناسایی") {
            IdentityInfoRow(label = "کد ملی", value = info.nationalId.toPersianDigits(), showDivider = false)
        }

        IdentitySectionCard(title = "اطلاعات شناسنامه") {
            IdentityInfoRow(label = "شماره شناسنامه", value = info.idCardNumber.toPersianDigits())
            IdentityInfoRow(label = "سری شناسنامه", value = info.idCardSerial1.toPersianDigits())
            IdentityInfoRow(label = "سریال شناسنامه", value = info.idCardSerial2.toPersianDigits())
            IdentityInfoRow(label = "شهر محل تولد", value = info.cityOfBirthName)
            IdentityInfoRow(label = "شهر محل صدور", value = info.cityOfIssueName, showDivider = false)
        }

        IdentitySectionCard(title = "اطلاعات تماس") {
            IdentityInfoRow(label = "شماره تلفن همراه", value = "09122773843".toPersianDigits())
            IdentityInfoRow(label = "پست الکترونیک", value = "ثبت نشده", isPlaceholder = true, showDivider = false)
        }
    }
}

@Composable
private fun IdentitySectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF8E8E8E),
            modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm),
            textAlign = TextAlign.Start
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.card))
                .background(Color.White)
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(CornerRadius.card))
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        ) {
            content()
        }
    }
}

@Composable
private fun IdentityInfoRow(
    label: String,
    value: String,
    isPlaceholder: Boolean = false,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF8E8E8E)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isPlaceholder) Color(0xFF9DB2CE) else Color(0xFF0F172A)
            )
        }
        if (showDivider) {
            TaminDivider()
        }
    }
}

@Composable
private fun IdentityInsuranceCard(
    info: IdentityInfoPR,
    collapseProgress: () -> Float,
    modifier: Modifier = Modifier,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .identityCardBackground()
            .identityCardDecoration(rtl),
    ) {
        Layout(
            content = {
                CardBrandRow(modifier = Modifier.layoutId(IdSlot.Brand).vanishOnCollapse(collapseProgress))
                IdentityGoldenChip(modifier = Modifier.layoutId(IdSlot.Chip).vanishOnCollapse(collapseProgress))

                Box(
                    modifier = Modifier
                        .layoutId(IdSlot.Avatar)
                        .size(76.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .padding(4.dp)
                ) {
                    UserAvatar(
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                        model = null
                    )
                }

                Text(
                    text = info.fullName,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.layoutId(IdSlot.Name).shrinkOnCollapse(collapseProgress, 0.8f, rtl),
                )

                Text(
                    text = "فرزند ${info.fatherName} . ${info.genderDisplay} . ایرانی",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.layoutId(IdSlot.Details).vanishOnCollapse(collapseProgress),
                )

                Column(modifier = Modifier.layoutId(IdSlot.SsnBlock).vanishOnCollapse(collapseProgress), horizontalAlignment = Alignment.End) {
                    Text(text = "شماره تأمین اجتماعی", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                    NumericText(
                        text = info.ssn.toPersianDigits(),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Row(modifier = Modifier.layoutId(IdSlot.Footer).vanishOnCollapse(collapseProgress), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = "کد ملی", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                        NumericText(text = info.nationalId.toPersianDigits(), style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "تاریخ تولد", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                        NumericText(text = info.dateOfBirthFormatted.toPersianDigits(), style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                }
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val pad = Spacing.lg.roundToPx()
            val innerC = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))

            val brand = measurables.slot(IdSlot.Brand).measure(innerC)
            val chip = measurables.slot(IdSlot.Chip).measure(Constraints())
            val avatar = measurables.slot(IdSlot.Avatar).measure(Constraints.fixed(76.dp.roundToPx(), 76.dp.roundToPx()))
            val name = measurables.slot(IdSlot.Name).measure(innerC)
            val details = measurables.slot(IdSlot.Details).measure(innerC)
            val ssn = measurables.slot(IdSlot.SsnBlock).measure(innerC)
            val footer = measurables.slot(IdSlot.Footer).measure(Constraints.fixedWidth(width - 2 * pad))

            val expandedH = 276.dp.roundToPx()
            val barH = 64.dp.roundToPx()

            val t = FastOutSlowInEasing.transform(collapseProgress())

            layout(width, lerp(expandedH, barH, t)) {
                // Expanded positions from Start (Right in RTL)
                val avatarX = pad
                val avatarY = 88.dp.roundToPx()

                val nameX = avatarX + avatar.width + 16.dp.roundToPx()
                val nameY = 92.dp.roundToPx()

                val detailsX = nameX
                val detailsY = nameY + name.height + 4.dp.roundToPx()

                val chipX = width - pad - chip.width // Left edge in RTL
                val chipY = 24.dp.roundToPx()

                val brandX = pad
                val brandY = 24.dp.roundToPx()

                val ssnX = pad
                val ssnY = 168.dp.roundToPx()

                val footerX = pad
                val footerY = expandedH - footer.height - 24.dp.roundToPx()

                brand.placeRelative(brandX, brandY)
                chip.placeRelative(chipX, chipY)
                details.placeRelative(detailsX, detailsY)
                ssn.placeRelative(ssnX, ssnY)
                footer.placeRelative(footerX, footerY)

                // Collapsed Target: Move into Top Bar Row
                // In TaminTopAppBar, the back button is at pad (Right).
                // We'll move the avatar to the LEFT of the back button?
                // Or to the far LEFT (End) of the row.
                // Let's target center-ish or next to back button.

                val avatarTargetX = pad + 44.dp.roundToPx() // Next to back button
                val avatarTargetSize = 36.dp.roundToPx()
                val avatarTargetY = (barH - avatarTargetSize) / 2

                avatar.placeRelativeWithLayer(
                    lerp(avatarX.toFloat(), avatarTargetX.toFloat(), t).toInt(),
                    lerp(avatarY.toFloat(), avatarTargetY.toFloat(), t).toInt()
                ) {
                    val scale = lerp(1f, avatarTargetSize.toFloat() / avatar.width, t)
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                }

                val nameTargetX = avatarTargetX + avatarTargetSize + 8.dp.roundToPx()
                val nameTargetY = (barH - name.height) / 2
                name.placeRelative(
                    lerp(nameX.toFloat(), nameTargetX.toFloat(), t).toInt(),
                    lerp(nameY.toFloat(), nameTargetY.toFloat(), t).toInt()
                )
            }
        }
    }
}

@Composable
private fun IdentityGoldenChip(modifier: Modifier = Modifier) {
    val chipGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF4E1A0), Color(0xFFD6AE5C), Color(0xFFBC934A))
    )
    Box(
        modifier = modifier
            .size(width = 38.dp, height = 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(chipGradient)
            .drawBehind {
                val lineColor = Color(0x8078541C)
                drawLine(lineColor, Offset(size.width * 0.35f, 0f), Offset(size.width * 0.35f, size.height), 0.5.dp.toPx())
                drawLine(lineColor, Offset(size.width * 0.65f, 0f), Offset(size.width * 0.65f, size.height), 0.5.dp.toPx())
                drawLine(lineColor, Offset(0f, size.height * 0.45f), Offset(size.width, size.height * 0.45f), 0.5.dp.toPx())
            }
    )
}

private fun Modifier.identityCardBackground(): Modifier = drawBehind {
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF2C5CB0), Color(0xFF1C4488), Color(0xFF123566))
    )
    drawRect(backgroundGradient)

    // Diagonal Shine Band from SVG paint1
    val shineGradient = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = 0f), Color.White.copy(alpha = 0.07f), Color.White.copy(alpha = 0f)),
        start = Offset(0f, size.height * 0.2f),
        end = Offset(size.width, size.height * 0.8f)
    )
    drawRect(shineGradient)

    // Subtle Inner Glow
    drawRect(
        color = Color.White.copy(alpha = 0.16f),
        size = Size(size.width, 1.dp.toPx())
    )
}

private fun Modifier.identityCardDecoration(rtl: Boolean): Modifier = drawBehind {
    // Circle positions relative to card (from SVG coords relative to card TL 19, 22)
    val startX = if (rtl) size.width * 0.1f else size.width * 0.9f
    val endX = if (rtl) size.width * 0.9f else size.width * 0.1f

    // Top-Right Circles (Start in RTL)
    drawCircle(
        color = Color.White.copy(alpha = 0.09f),
        radius = 105.dp.toPx(),
        center = Offset(startX, 21.dp.toPx())
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.07f),
        radius = 75.dp.toPx(),
        center = Offset(startX, 21.dp.toPx())
    )

    // Bottom-Left Circle (End in RTL)
    drawCircle(
        color = Color.White.copy(alpha = 0.06f),
        radius = 85.dp.toPx(),
        center = Offset(endX, size.height - 9.dp.toPx())
    )
}

private enum class IdSlot { Brand, Chip, Avatar, Name, Details, SsnBlock, Footer }
private fun List<Measurable>.slot(id: IdSlot): Measurable = first { it.layoutId == id }

@Composable
private fun CardBrandRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(vectorResource(Res.drawable.ic_tamin_ejtemaei_logo), null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "سازمان تأمین اجتماعی", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                Icon(vectorResource(Res.drawable.ic_tamin_verified), null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
            }
            Text(text = "کارت بیمه‌شده", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = Color.White.copy(alpha = 0.7f))
        }
        Spacer(Modifier.weight(1f))
    }
}

private fun Modifier.vanishOnCollapse(progress: () -> Float): Modifier = graphicsLayer {
    alpha = (1f - progress() * 3f).coerceIn(0f, 1f)
}

private fun Modifier.shrinkOnCollapse(progress: () -> Float, minScale: Float, rtl: Boolean = false): Modifier = graphicsLayer {
    val scale = lerp(1f, minScale, FastOutSlowInEasing.transform(progress()))
    scaleX = scale
    scaleY = scale
    transformOrigin = TransformOrigin(if (rtl) 1f else 0f, 0.5f)
}

private fun Modifier.rideUpIntoHeader(
    progress: () -> Float,
    expandedOverlap: Dp,
    collapsedOverlap: Dp,
): Modifier = layout { measurable, constraints ->
    val overlapPx = lerp(expandedOverlap.toPx(), collapsedOverlap.toPx(), progress())
    val placeable = measurable.measure(constraints)
    val reserved = (placeable.height - overlapPx).coerceAtLeast(0f).roundToInt()
    layout(placeable.width, reserved) {
        placeable.placeRelative(0, -overlapPx.roundToInt())
    }
}

@PreviewRtlTheme
@Composable
fun PreviewIdentityInScreen() {
    PreviewRtlThemeContent {
        IdentityInScreen(
            onIntent = {},
            state = IdentityInUiState(
                identityInfo = IdentityInfoPR(
                    firstName = "حمید",
                    lastName = "چیداز",
                    fullName = "حمید چیداز",
                    fatherName = "اکبر",
                    genderDisplay = "مرد",
                    nationalId = "0946168113",
                    ssn = "2488589157",
                    dateOfBirthFormatted = "1351/11/15",
                    cityOfBirthName = "مشهد",
                    cityOfIssueName = "مشهد",
                    gender = "M",
                    id = 0,
                    idCardNumber = "16597",
                    idCardSerial1 = "201",
                    idCardSerial2 = "436932",
                    idCardSerial = "",
                    cityOfBirthId = "",
                    cityOfIssueId = "",
                    countryId = "",
                    dateOfBirth = 0L
                )
            )
        )
    }
}
