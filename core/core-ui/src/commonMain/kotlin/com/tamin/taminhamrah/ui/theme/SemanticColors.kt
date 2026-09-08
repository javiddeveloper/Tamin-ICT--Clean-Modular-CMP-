package com.tamin.taminhamrah.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class TaminColors(
    // Backgrounds / surfaces
    val bgPage: Color,
    val bgSurface: Color,
    val border: Color,
    val divider: Color,
    val outerBorder: Color,
    val bgIconProfile: Color,

    // Profile Icon Gradients
    val iconGradientPrimary: Brush,
    val iconGradientSecondary: Brush,
    val iconGradientNeutral: Brush,
    val iconGradientDanger: Brush,
    val iconGradientSuccess: Brush,
    val iconGlassShine: Brush,
    val iconGlassBorder: Brush,
    val glassIconTileBg: Color,
    val glassIconTileBorder: Color,
    val glassIconTileShine: Brush,
    val glassIconTileIconTint: Color,
    val glassIconRipple1: Color,
    val glassIconRipple2: Color,
    val validationCardGradient: Brush,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textMuted: Color,
    val textHeaderSubtitle: Color,
    val chevron: Color,

    // Status pills (background + foreground pairs)
    val greenBg: Color,
    val greenBorder: Color,
    val greenText: Color,
    val springGreenText: Color,
    val blueBg: Color,
    val blueText: Color,
    val iconBgSubtle: Color,
    val iconTintSubtle: Color,
    val orangeBg: Color,
    val orangeText: Color,
    /**
     * The design's muted green — `#3DA35D` on `#E9F7EE`. Distinct from [greenText]/[greenBg],
     * which are the brighter success pair a status pill wears; this one only tints an icon.
     */
    val mintText: Color,
    val mintBg: Color,
    val dangerBg: Color,
    val dangerBorder: Color,
    val dangerText: Color,
    val disabledAlpha: Float,

    // Medical / Teal
    val teal: Color,
    /** The fill [teal] sits on when it tints an icon tile. */
    val tealBg: Color,
    /**
     * The hairline a blue-on-white control is outlined with — a dashed code chip, a quiet card
     * action, a filter chip, a selected option.
     *
     * Was two tokens: this one and `hawkesBlue`, a fixed light blue seven features already used.
     * They were the same color under two names, except that the older one went gray in dark
     * theme; folded into this one, which the design themes properly in both.
     */
    val blueBorder: Color,

    // Shadows
    val shadowPrimary: Color,
    val shadowSubtle: Color,

    // Glass / tab-bar tokens (translucency layers for the bottom nav)
    val glassA1: Color,
    val glassA2: Color,
    val glassB1: Color,
    val glassB2: Color,
    val glassC1: Color,
    val glassC2: Color,
    val glassSolid: Color,
    val glassBorder: Color,
    val tabbarBorder: Color,
    val tabbarShine: Color,
    val tabActiveBg: Color,

    val txtNameProfile: Color,
    val txtNatProfile: Color,
    val shadowAvatarProfile: Color,

    // Brand gradients (hero headers, feature cards)
    val heroGradient: Brush,
    val medicalGradient: Brush,

    // Top app bar. Held as stops rather than a Brush so the bar owns its sweep
    // direction; the strip behind the status bar shares this same wash.
    val topAppBarStops: List<Color>,
    /** The treatment hub's own wash — the design ends it a shade deeper than [topAppBarStops]. */
    val treatmentHubStops: List<Color>,
    val profileGradientStops: List<Color>,
    val aiAssistantGradient: Brush,
    val grey900 : Color,

    val chipBg: Color,
    val warning: Color,
    val fuchsiaBlue: Color ,
    /** The fill [fuchsiaBlue] sits on when it tints an icon tile. */
    val fuchsiaBlueBg: Color,
    // Solid tint derived from the AI-assistant gradient family — used for blur tints
    // and fallbacks where a single color (not a Brush) is required.
    val aiAssistantTint: Color,
    val verifiedBadgeBg: Color,
    val buttonGradient: Brush,
    /** Confirming fill — «تأیید و ارسال». */
    val successGradient: Brush,
    /** A time-limited action that must be noticed — «اعتراض به بدهی برآوردی». */
    val alertGradient: Brush,
    val buttonDisabledGradient: Brush,

    // Verified Status Tokens
    val verifiedContainerBg: Color,
    val verifiedContainerBorder: Color,
    val verifiedIconGradient: Brush?,
    val verifiedIconBg: Color,
    val verifiedIconTint: Color,

    /**
     * Content drawn on top of a brand gradient — the hero header, the gradient buttons, the
     * selected tab. The same in both themes on purpose: those gradients are dark in both, so the
     * content on them does not follow the page.
     */
    val onGradient: Color,

    /**
     * The unselected page dot under the home campaigns carousel. The selected one is [blueText],
     * which the design names directly (`--tm-blue-text`); only the idle tone needed a token of its
     * own, because it sits on the page rather than on a card and so has to follow the theme.
     */
    val campaignDotIdle: Color,
)

val LightTaminColors = TaminColors(
    bgPage = TaminLightBackground,
    bgSurface = TaminLightSurface,
    border = CoreBorder,
    divider = CoreDivider,
    outerBorder = Gray300,
    textPrimary = TaminLightTextDefault,
    textSecondary = TaminLightTextSecondary,
    textTertiary = Gray400,
    textMuted = TaminLightTextMuted,
    textHeaderSubtitle = TaminLightTextSubProfile,
    chevron = Gray300,
    greenBg = Secondary50,
    greenBorder = TaminDarkGreenAlpha,
    greenText = TaminLightSuccess,
    blueBg = Primary50,
    blueText = TaminLightInfo,
    iconBgSubtle = TaminLightIconBgSubtle,
    iconTintSubtle = TaminLightIconTintSubtle,
    orangeBg = TaminLightOrangeBg,
    orangeText = TaminLightWarning,
    mintText = TaminLightMint,
    mintBg = TaminLightMintBg,
    dangerBg = TaminLightSurface,
    dangerBorder = TaminLightDangerBorder,
    dangerText = TaminLightError,
    teal = Secondary700,
    tealBg = TaminLightTealBg,
    blueBorder = TaminLightBlueBorder,
    bgIconProfile = TaminLightSurface,
    iconGradientPrimary = Brush.verticalGradient(
        listOf(
            IconGradientBlueStart,
            IconGradientBlueEnd
        )
    ),
    iconGradientSecondary = Brush.verticalGradient(
        listOf(
            IconGradientPurpleStart,
            IconGradientPurpleEnd
        )
    ),
    iconGradientNeutral = Brush.verticalGradient(
        listOf(
            IconGradientGrayStart,
            IconGradientGrayEnd
        )
    ),
    iconGradientDanger = Brush.verticalGradient(listOf(IconGradientRedStart, IconGradientRedEnd)),
    iconGradientSuccess = Brush.verticalGradient(listOf(TaminGreen, TaminGreenDark)),
    iconGlassShine = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.40f),
            Color.White.copy(alpha = 0.0f)
        )
    ),
    iconGlassBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.60f),
            Color.White.copy(alpha = 0.05f)
        )
    ),
    glassIconTileBg = Color.White.copy(alpha = 0.12f),
    glassIconTileBorder = Color.White.copy(alpha = 0.227f),
    glassIconTileShine = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.26f),
            Color.Transparent
        )
    ),
    glassIconTileIconTint = Color.White,
    glassIconRipple1 = Color.White.copy(alpha = 0.1f),
    glassIconRipple2 = Color.White.copy(alpha = 0.16f),
    validationCardGradient = Brush.verticalGradient(listOf(Color(0xADFFFFFF), Color(0x6BFFFFFF))),
    disabledAlpha = 0.38f,
    glassA1 = Color(0x8CFFFFFF),
    glassA2 = Color(0x52FFFFFF),
    glassB1 = Color(0xADFFFFFF),
    glassB2 = Color(0x6BFFFFFF),
    glassC1 = Color(0x61FFFFFF),
    glassC2 = Color(0x24FFFFFF),
    glassSolid = Color(0xD9F8FAFC),
    glassBorder = Color(0x99FFFFFF),
    tabbarBorder = Color(0xD9FFFFFF),
    tabbarShine = Color(0x80FFFFFF),
    tabActiveBg = Color(0x1A1F4FA3),
    heroGradient = Brush.linearGradient(listOf(Primary700,Primary900 )),
    medicalGradient = Brush.linearGradient(listOf(Secondary500, Secondary700)),
    // Same stops as the quick-access card; the bar just sweeps the other way.
    topAppBarStops = listOf(TaminTeal900, TaminTeal500),
    treatmentHubStops = listOf(TaminTeal900, TaminTeal700),
    profileGradientStops = listOf(TaminNavy900, TaminNavy700),
    aiAssistantGradient = Brush.linearGradient(
        listOf(TaminPurple900, TaminPurple700, Color(0xFF3F5BD9), TaminNavy700)
    ),
    aiAssistantTint = TaminPurple900,
    shadowPrimary = Primary700.copy(alpha = 0.5f),
    shadowSubtle = Gray900.copy(alpha = 0.1f),

    txtNameProfile = TaminLightSurface,
    txtNatProfile = TaminLightTextSubProfile,
    shadowAvatarProfile = Color.Black,
    chipBg = Color(0xFFEFF6FF),
    grey900 = Color(0xFFE2E8F0),
    warning = Color(0xFFC97E0A),
    fuchsiaBlue = Color(0xFF7C4BC0),
    fuchsiaBlueBg = TaminLightPurpleBg,
    springGreenText = TaminSpringGreen,
    verifiedBadgeBg = TaminLightSurface,
    buttonGradient = Brush.horizontalGradient(listOf(IconGradientBlueStart, IconGradientBlueEnd)),
    successGradient = Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd)),
    alertGradient = Brush.linearGradient(listOf(GradientOrangeStart, GradientOrangeEnd)),
    buttonDisabledGradient = Brush.horizontalGradient(
        listOf(
            TaminLightTextMuted.copy(alpha = 0.4f),
            TaminLightTextMuted.copy(alpha = 0.6f)
        )
    ),
    verifiedContainerBg = Secondary50, // greenBg
    verifiedContainerBorder = TaminLightSuccess.copy(alpha = 0.2f),
    verifiedIconGradient = null,
    verifiedIconBg = TaminLightSurface,
    verifiedIconTint = TaminLightSuccess, // greenText
    onGradient = Color.White,
    campaignDotIdle = CampaignDotIdle,
)

val DarkTaminColors = TaminColors(
    bgPage = TaminDarkBackground,
    bgSurface = TaminDarkSurface,
    border = TaminDarkBorder,
    divider = TaminDarkDivider,
    outerBorder = TaminDarkOuterBorder,
    textPrimary = TaminDarkTextDefault,
    textSecondary = TaminDarkTextSecondary,
    textTertiary = TaminDarkTextSecondary,
    textMuted = TaminDarkTextMuted,
    textHeaderSubtitle = TaminDarkTextSubProfile,
    chevron = TaminDarkChevron,
    greenBg = TaminDarkGreenBg,
    greenBorder = TaminDarkGreenAlpha,
    greenText = TaminDarkSuccess,
    blueBg = TaminDarkBlueBg,
    blueText = TaminDarkInfo,
    iconBgSubtle = TaminDarkBlueBg,
    iconTintSubtle = TaminDarkTextDefault,
    orangeBg = TaminDarkOrangeBg,
    orangeText = TaminDarkWarning,
    mintText = TaminDarkMint,
    mintBg = TaminDarkMintBg,
    dangerBg = TaminDarkSurface,
    dangerBorder = TaminDarkDangerBorder,
    dangerText = TaminDarkError,
    teal = Secondary500,
    tealBg = TaminDarkTealBg,
    blueBorder = TaminDarkBlueBorder,
    bgIconProfile = TaminLightSurface,
    iconGradientPrimary = Brush.verticalGradient(
        listOf(
            IconGradientBlueStart,
            IconGradientBlueEnd
        )
    ),
    iconGradientSecondary = Brush.verticalGradient(
        listOf(
            IconGradientPurpleStart,
            IconGradientPurpleEnd
        )
    ),
    iconGradientNeutral = Brush.verticalGradient(
        listOf(
            IconGradientGrayStart,
            IconGradientGrayEnd
        )
    ),
    iconGradientDanger = Brush.verticalGradient(listOf(IconGradientRedStart, IconGradientRedEnd)),
    iconGradientSuccess = Brush.verticalGradient(listOf(TaminDarkSuccess, TaminGreenDark)),
    glassIconTileBg = Color.White.copy(alpha = 0.12f),
    glassIconTileBorder = Color.White.copy(alpha = 0.227f),
    glassIconTileShine = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.26f),
            Color.Transparent
        )
    ),
    glassIconTileIconTint = Color.White,
    glassIconRipple1 = Color.White.copy(alpha = 0.1f),
    glassIconRipple2 = Color.White.copy(alpha = 0.16f),
    iconGlassShine = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.40f),
            Color.White.copy(alpha = 0.0f)
        )
    ),
    iconGlassBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.60f),
            Color.White.copy(alpha = 0.05f)
        )
    ),
    validationCardGradient = Brush.horizontalGradient(
        listOf(
            TaminTeal700.copy(alpha = 0.35f),
            TaminTeal700.copy(alpha = 0.15f)
        )
    ),
    disabledAlpha = 0.38f,
    glassA1 = Color(0x8C1E293B),
    glassA2 = Color(0x47111827),
    glassB1 = Color(0xA6283448),
    glassB2 = Color(0x66141C2D),
    glassC1 = Color(0x731E293B),
    glassC2 = Color(0x33111827),
    glassSolid = Color(0xD90A0F1A),
    glassBorder = Color(0x14FFFFFF),
    tabbarBorder = Color(0x17FFFFFF),
    tabbarShine = Color(0x0DFFFFFF),
    tabActiveBg = Color(0x295B9CFF),
    heroGradient = Brush.linearGradient(listOf(TaminDarkHeroStart, TaminDarkHeroEnd)),
    medicalGradient = Brush.linearGradient(listOf(Secondary500, Secondary700)),
    // Dark mode overrides every hero to the same teal-to-blue wash, status bar included,
    // so the bar and the strip above it join into one continuous band.
    topAppBarStops = listOf(TaminDarkHeroStart, TaminDarkHeroEnd),
    treatmentHubStops = listOf(TaminDarkHeroStart, TaminDarkHeroEnd),
    profileGradientStops = listOf(TaminDarkHeroStart, TaminDarkHeroEnd),
    aiAssistantGradient = Brush.linearGradient(
        listOf(TaminPurple900, TaminPurple700, Color(0xFF3F5BD9), TaminNavy700)
    ),

    chipBg = Color(0x293B82F6),
    shadowPrimary = Color.Black.copy(alpha = 0.4f),
    shadowSubtle = Color.Black.copy(alpha = 0.3f),
    txtNameProfile = TaminLightSurface,
    txtNatProfile = TaminLightTextSubProfile,
    shadowAvatarProfile = Color.Black,
    aiAssistantTint = TaminPurple900,
    grey900 = Color(0xFFE2E8F0),
    warning = Color(0xFFFBBF24),
    fuchsiaBlue = Color(0xFFB79AEE),
    fuchsiaBlueBg = TaminDarkPurpleBg,
    springGreenText = TaminDarkSuccess,
    verifiedBadgeBg = TaminDarkGreenBg,
    buttonGradient = Brush.horizontalGradient(listOf(IconGradientBlueStart, IconGradientBlueEnd)),
    successGradient = Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd)),
    alertGradient = Brush.linearGradient(listOf(GradientOrangeStart, GradientOrangeEnd)),
    buttonDisabledGradient = Brush.horizontalGradient(
        listOf(
            TaminDarkTextMuted.copy(alpha = 0.4f),
            TaminDarkTextMuted.copy(alpha = 0.6f)
        )
    ),
    verifiedContainerBg = TaminDarkSurface, // bgSurface
    verifiedContainerBorder = TaminDarkSuccess.copy(alpha = 0.15f),
    verifiedIconGradient = null,
    verifiedIconBg = TaminDarkGreenBg, // greenBg
    verifiedIconTint = TaminDarkSuccess, // greenText
    onGradient = Color.White,
    // The design has no dark variant for this section; the page's own chevron gray is the closest
    // token that stays legible against the dark page.
    campaignDotIdle = TaminDarkChevron,
)

