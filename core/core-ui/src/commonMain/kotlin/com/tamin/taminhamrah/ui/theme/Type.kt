package com.tamin.taminhamrah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Default OpenType features applied to every [TextStyle] in [taminHamrahTypography].
 *
 * The bundled UI font is Vazirmatn (v33.003). Its `ss01` stylistic set substitutes ASCII
 * digits `0-9` for Persian digits `U+06F0`–`U+06F9` at paint time; `tnum` makes those
 * glyphs equal-width so prices and dates do not shift as values change. Verified against
 * the GSUB table of `core-ui/.../font/regular.ttf`.
 *
 * This is visual only: the Compose string still holds ASCII codepoints, so copy/paste,
 * APIs, and logs keep Latin digits. When the *string* itself must contain Persian numeral
 * characters (share text, clipboard, notifications, non-Compose surfaces), use
 * [com.tamin.taminhamrah.util.toPersianDigits] instead. The two coexist on purpose —
 * `ss01` is a no-op on digits that are already `U+06F0`–`U+06F9`.
 *
 * Do not copy `fontFeatureSettings` unless you intend to replace this default.
 * `TextStyle.copy(fontFeatureSettings = "tnum")` drops `ss01`.
 */
private const val FONT_FEATURE_PERSIAN_DIGITS = "ss01"
private const val FONT_FEATURE_TABULAR_NUMBERS = "tnum"
const val DEFAULT_FONT_FEATURES = "$FONT_FEATURE_TABULAR_NUMBERS, $FONT_FEATURE_PERSIAN_DIGITS"

@Composable
expect fun applicationFont(): FontFamily

@Composable
fun taminHamrahTypography(): Typography {
    val fontFamily = applicationFont()
    val defaultTypography = Typography()

    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(800),
            fontSize = 34.sp,
            lineHeight = 40.8.sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = defaultTypography.displayMedium.withAppDefaults(fontFamily),
        displaySmall = defaultTypography.displaySmall.withAppDefaults(fontFamily),

        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(800),
            fontSize = 28.sp,
            lineHeight = 35.sp,
            letterSpacing = (-0.3).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 24.sp,
            lineHeight = 31.2.sp,
            letterSpacing = (-0.2).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 20.sp,
            lineHeight = 27.sp,
            letterSpacing = 0.sp
        ),

        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 17.sp,
            lineHeight = 23.8.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = defaultTypography.titleMedium.withAppDefaults(fontFamily),
        titleSmall = defaultTypography.titleSmall.withAppDefaults(fontFamily),

        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(400),
            fontSize = 16.sp,
            lineHeight = 27.2.sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
            lineHeight = 23.8.sp,
            letterSpacing = 0.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(400),
            fontSize = 13.sp,
            lineHeight = 20.8.sp,
            letterSpacing = 0.sp
        ),

        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 12.5.sp,
            lineHeight = 17.5.sp,
            letterSpacing = 0.2.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(500),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = defaultTypography.labelSmall.withAppDefaults(fontFamily)
    )
}

/**
 * Extension to apply standard application font and digit features to an existing [TextStyle].
 */
private fun TextStyle.withAppDefaults(fontFamily: FontFamily): TextStyle = this.copy(
    fontFamily = fontFamily,
    fontFeatureSettings = DEFAULT_FONT_FEATURES
)

/**
 * Type for the home campaigns carousel
 * ([com.tamin.taminhamrah.ui.components.CampaignCarousel]).
 *
 * Declared here rather than assembled at the call site for two reasons. None of the design's sizes
 * — 8.5 / 10.5 / 15.5 / 16 — has a role in [taminHamrahTypography]. And leaving the rest to
 * `LocalTextStyle`, which Material3 sets to `bodyLarge`, would drag its 27.2sp line height onto an
 * 8.5sp pill, so every style names its own. Each one takes [applicationFont] and
 * [DEFAULT_FONT_FEATURES] the same way the roles above do.
 *
 * ⚠️ **700 and 800 are synthesized.** [applicationFont] registers Light, Normal, Medium and
 * SemiBold only — there is no Bold or ExtraBold file in `composeResources/font/`. That is true of
 * the whole app (`titleLarge`, `headlineLarge` and ~350 call sites already ask for 700 or 800), so
 * these weights match their surroundings rather than quietly diverging. Dropping `bold.ttf` and
 * `extra_bold.ttf` in and registering them would fix every one of those at once.
 */
@Immutable
data class CampaignTextStyles(
    val sectionTitle: TextStyle,
    val sectionHint: TextStyle,
    val badge: TextStyle,
    val cardTitle: TextStyle,
    val body: TextStyle,
    val ctaLabel: TextStyle,
    val caption: TextStyle,
)

@Composable
fun campaignTextStyles(): CampaignTextStyles {
    val fontFamily = applicationFont()
    return remember(fontFamily) { campaignTextStyles(fontFamily) }
}

private fun campaignTextStyles(fontFamily: FontFamily) = CampaignTextStyles(
    sectionTitle = campaignStyle(fontFamily, 16.sp, 19.sp, weight = 700),
    sectionHint = campaignStyle(fontFamily, 10.5.sp, 13.sp, weight = 600),
    badge = campaignStyle(fontFamily, 8.5.sp, 11.sp, weight = 800),
    cardTitle = campaignStyle(
        fontFamily,
        fontSize = 15.5.sp,
        lineHeight = 19.sp,
        weight = 800,
        letterSpacing = (-0.2).sp,
    ),
    // line-height: 1.8
    body = campaignStyle(fontFamily, 10.5.sp, 18.9.sp, weight = 500),
    ctaLabel = campaignStyle(fontFamily, 10.5.sp, 13.sp, weight = 800),
    caption = campaignStyle(fontFamily, 8.5.sp, 11.sp, weight = 600),
)

private fun campaignStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    weight: Int,
    letterSpacing: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = fontFamily,
    fontFeatureSettings = DEFAULT_FONT_FEATURES,
    fontWeight = FontWeight(weight),
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)

/**
 * Type for the electronic health-insurance card
 * ([com.tamin.taminhamrah.feature.treatment.ui.components.InsuranceCard]).
 *
 * Declared here for the same two reasons as [CampaignTextStyles]: none of the design's sizes —
 * 9 / 10.5 / 11.5 / 13 / 14.5 — has a role in [taminHamrahTypography], and leaving the rest to
 * `LocalTextStyle` would drag `bodyLarge`'s 27.2sp line height onto a 9sp caption.
 *
 * Weights are the mock's own: 800 where it says so, 400 where it says nothing (the design frame
 * sets no base weight, so an unstated weight is normal). The same ⚠️ as [CampaignTextStyles]
 * applies — 700 and 800 are synthesized, since no Bold or ExtraBold face is registered.
 */
@Immutable
data class InsuranceCardTextStyles(
    /** The holder's initial, in the tile at the top of the card. */
    val initial: TextStyle,
    val orgName: TextStyle,
    val orgSubtitle: TextStyle,
    val holderName: TextStyle,
    val codeLabel: TextStyle,
    val code: TextStyle,
    val coverage: TextStyle,
)

@Composable
fun insuranceCardTextStyles(): InsuranceCardTextStyles {
    val fontFamily = applicationFont()
    return remember(fontFamily) { insuranceCardTextStyles(fontFamily) }
}

private fun insuranceCardTextStyles(fontFamily: FontFamily) = InsuranceCardTextStyles(
    initial = cardStyle(fontFamily, 13.sp, 16.sp, weight = 800),
    orgName = cardStyle(fontFamily, 11.5.sp, 15.sp, weight = 800),
    orgSubtitle = cardStyle(fontFamily, 9.sp, 12.sp, weight = 400),
    holderName = cardStyle(fontFamily, 14.5.sp, 19.sp, weight = 800),
    codeLabel = cardStyle(fontFamily, 9.sp, 12.sp, weight = 400),
    // font: 700 11.5px 'JetBrains Mono'; letter-spacing: .5px. There is no mono face in the app,
    // so the code keeps the UI font — its `tnum` feature already holds the digits to one width.
    code = cardStyle(fontFamily, 11.5.sp, 15.sp, weight = 700, letterSpacing = 0.5.sp),
    coverage = cardStyle(fontFamily, 10.5.sp, 14.sp, weight = 700),
)

private fun cardStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    weight: Int,
    letterSpacing: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = fontFamily,
    fontFeatureSettings = DEFAULT_FONT_FEATURES,
    fontWeight = FontWeight(weight),
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)
