package com.tamin.taminhamrah.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---- Brand navy (primary / insured-services / account) ----
val TaminNavy900 = Color(0xFF173D7E) // status bar / header base
val TaminNavy700 = Color(0xFF1F4FA3) // primary blue / links / active tab
val TaminNavy500 = Color(0xFF2B5FBF)
val TaminNavy300 = Color(0xFF3B6FD4)

// ---- Teal (medical / درمان section) ----
val TaminTeal900 = Color(0xFF0E7C82)
val TaminTeal700 = Color(0xFF17A2A8)
val TaminTeal500 = Color(0xFF2FB9BC)
val TaminTeal300 = Color(0xFF1FA6AD)

// ---- Purple accent (AI assistant / featured banner) ----
val TaminPurple900 = Color(0xFF3B1E86)
val TaminPurple700 = Color(0xFF5B2FC4)
val TaminPurple500 = Color(0xFF6D4BE0)
val TaminPurple300 = Color(0xFFA78BFA)

// ---- Insurance card identities ----
// One three-stop gradient per insured person, so adjacent cards in the treatment
// carousel stay visually distinct. The main insured person always takes the teal set.
val TaminCardTealStart = Color(0xFF0BA5A0)
val TaminCardTealMid = Color(0xFF0E7FA6)
val TaminCardTealEnd = Color(0xFF1655A8)

val TaminCardPurpleStart = Color(0xFFB25CC9)
val TaminCardPurpleMid = Color(0xFF8B4FC7)
val TaminCardPurpleEnd = Color(0xFF5A3AA8)

val TaminCardBlueStart = Color(0xFF3E9BE0)
val TaminCardBlueMid = Color(0xFF3E7BD6)
val TaminCardBlueEnd = Color(0xFF1F4FA3)

val TaminCardAmberStart = Color(0xFFF4A83D)
val TaminCardAmberMid = Color(0xFFE08A00)
val TaminCardAmberEnd = Color(0xFFB96B00)

// Coverage badge on the insurance card footer.
val TaminCoverageBadgeBg = Color(0xFF4BE3A0)
val TaminCoverageBadgeFg = Color(0xFF0B5F4F)

// ---- Semantic accents ----
val TaminGreen = Color(0xFF03AD5F)       // success / active dot
val TaminGreenDark = Color(0xFF03794A)   // success text (on light bg)
val TaminSpringGreen = Color(0xFF0B7A45)
val TaminOrange = Color(0xFFC97E0A)      // warning text
val TaminOrangeDark = Color(0xFF9A6B00)
val TaminAmber = Color(0xFFF9A825)
val TaminRed = Color(0xFFD32F2F)         // danger / logout
val TaminRedDark = Color(0xFFC42121)

// ---- Neutrals — Light mode ----
val TaminLightBgPage = Color(0xFFF8FAFC)
val TaminLightBgSurface = Color(0xFFFFFFFF)
val TaminLightBorder = Color(0xFFE5E7EB)
val TaminLightDivider = Color(0xFFF2F4F8)
val TaminLightTextPrimary = Color(0xFF0F172A)
val TaminLightTextSubProfile = Color(0xFFAFC4EC)
val TaminLightTextSecondary = Color(0xFF64748B)
val TaminLightTextTertiary = Color(0xFF64748B)
val TaminLightTextMuted = Color(0xFF9DB2CE)
val TaminLightChevron = Color(0xFFC7D2E0)
val TaminLightOuterBorder = Color(0xFFD1D9E6)
val TaminLightGreenBg = Color(0xFFE6F7ED)
val TaminLightBlueBg = Color(0xFFEFF6FF)
val TaminLightOrangeBg = Color(0xFFFFF8E1)
val TaminLightDangerBorder = Color(0xFFFDECEC)
val TaminLightIconBgSubtle = Color(0xFFEEF2FB)
val TaminLightIconTintSubtle = Color(0xFF5E7392)

// Icon-tile tints the کارگاه action list draws, taken from the design's own values.
val TaminLightMint = Color(0xFF3DA35D)
val TaminLightMintBg = Color(0xFFE9F7EE)
val TaminLightTealBg = Color(0xFFEAF7F7)
val TaminLightBlueBorder = Color(0xFFD7E6FF)
val TaminLightPurpleBg = Color(0xFFF1EAFB)

// Dark mode collapses every screen's hero onto one teal-to-blue wash.
val TaminDarkHeroStart = Color(0xFF10AEB9)
val TaminDarkHeroEnd = Color(0xFF1E6FD0)

// ---- Neutrals — Dark mode ----
val TaminDarkBgPage = Color(0xFF0A0F1E)
val TaminDarkBgSurface = Color(0xFF141B2E)
val TaminDarkBorder = Color(0x17FFFFFF)      // rgba(255,255,255,.09)
val TaminDarkDivider = Color(0x12FFFFFF)     // rgba(255,255,255,.07)
val TaminDarkTextPrimary = Color(0xFFEDF1F7)
val TaminDarkTextSubProfile = Color(0xFFDDE6F5)
val TaminDarkTextSecondary = Color(0xFF8B96AC)
val TaminDarkTextMuted = Color(0xFF7C8BA6)
val TaminDarkChevron = Color(0xFF48536B)
val TaminDarkOuterBorder = Color(0xFF1E293F)
val TaminDarkGreenText = Color(0xFF34D399)
val TaminDarkGreenAlpha = Color(0x5934D399)  // #34D399 with 59 (hex) alpha
val TaminDarkBlueBg = Color(0x293B82F6)      // rgba(59,130,246,.16)
val TaminDarkBlueText = Color(0xFF5B9CFF)
val TaminDarkOrangeBg = Color(0x29F59E0B)    // rgba(245,158,11,.16)
val TaminDarkOrangeText = Color(0xFFFBBF24)
val TaminDarkDangerBorder = Color(0x38F87171) // rgba(248,113,113,.22)
val TaminDarkDangerText = Color(0xFFF87171)
val TaminDarkGreenBg = Color(0x2910B981)     // rgba(16,185,129,.16)

// Dark counterparts of the کارگاه action tints: the same hue laid over the dark surface at .16,
// which is how every other tinted fill in this palette is built.
val TaminDarkMint = Color(0xFF6FCB8C)
val TaminDarkMintBg = Color(0x293DA35D)
val TaminDarkTealBg = Color(0x245BD8D4)      // rgba(91,216,212,.14)
val TaminDarkBlueBorder = Color(0x476396FF)  // rgba(99,150,255,.28)
val TaminDarkPurpleBg = Color(0x297C4BC0)

val Primary50 = Color(0xFFEFF4FF)
val Primary100 = Color(0xFFD8E4FA)
val Primary300 = Color(0xFF6B93D6)
val Primary700 = Color(0xFF173D7E)
val Primary900 = Color(0xFF0E2450)
val Secondary50 = Color(0xFFE7FBF4)
val Secondary500 = Color(0xFF2FB9BC)
val Secondary700 = Color(0xFF0E7C82)

// Neutral / Grays
val Gray50 = Color(0xFFF8FAFC)
val Gray100 = Color(0xFFF2F4F8)
val Gray200 = Color(0xFFE5E7EB)
val Gray300 = Color(0xFFD1D9E6)
val Gray400 = Color(0xFF757575)
val Gray500 = Color(0xFF64748B)
val Gray600 = Color(0xFF475569)
val Gray700 = Color(0xFF334155)
val Gray800 = Color(0xFF1E293B)
val Gray900 = Color(0xFF0F172A)

// Semantic Light
val TaminLightSuccess = Color(0xFF03AD5F)
val TaminLightInfo = Color(0xFF1F4FA3)
val TaminLightWarning = Color(0xFFC97E0A)
val TaminLightError = Color(0xFFD32F2F)
val TaminLightTextDefault = Color(0xFF0F172A)
val TaminLightBackground = Color(0xFFF8FAFC)
val TaminLightSurface = Color(0xFFFFFFFF)

// Semantic Dark
val TaminDarkSuccess = Color(0xFF34D399)
val TaminDarkInfo = Color(0xFF5B9CFF)
val TaminDarkWarning = Color(0xFFFBBF24)
val TaminDarkError = Color(0xFFF87171)
val TaminDarkTextDefault = Color(0xFFEDF1F7)
val TaminDarkBackground = Color(0xFF0A0F1E)
val TaminDarkSurface = Color(0xFF141B2E)

// Surface / Background (General)
val CoreSurface = Color(0xFFFFFFFF)
val CoreBackground = Color(0xFFF8FAFC)
val CoreBorder = Color(0xFFE5E7EB)
val CoreDivider = Color(0xFFF2F4F8)

// Profile Icon Gradients
val IconGradientBlueStart = Color(0xFF3B6FD4)

// The two card-button gradients the کارگاه screens use beside the blue one.
val GradientGreenStart = Color(0xFF22A06B)
val GradientGreenEnd = Color(0xFF03794A)
val GradientOrangeStart = Color(0xFFF0A83C)
val GradientOrangeEnd = Color(0xFFC97E0A)
val IconGradientBlueEnd = Color(0xFF173D7E)

val IconGradientPurpleStart = Color(0xFF8B7CE8)
val IconGradientPurpleEnd = Color(0xFF5B4CC4)

val IconGradientGrayStart = Color(0xFF8C97A8)
val IconGradientGrayEnd = Color(0xFF4A5567)

val IconGradientRedStart = Color(0xFFF0635F)
val IconGradientRedEnd = Color(0xFFC42121)

// Identity card — the insured-person card on the profile's identity screen. Fixed in both themes,
// like the treatment cards: it stands in for a physical card, so it keeps its own identity.
val TaminIdentityCardStart = Color(0xFF2C5CB0)
val TaminIdentityCardMid = Color(0xFF1C4488)
val TaminIdentityCardEnd = Color(0xFF123566)

// The card's gold contact chip.
val TaminIdentityChipStart = Color(0xFFF4E1A0)
val TaminIdentityChipMid = Color(0xFFD6AE5C)
val TaminIdentityChipEnd = Color(0xFFBC934A)
val TaminIdentityChipTrace = Color(0x8078541C)

// Captions on the identity card read as a light blue, not white at low alpha (Figma #9FB6DE).
val TaminIdentityCardMuted = Color(0xFF9FB6DE)

// The card's drop shadow: a deep blue, not black (Figma rgba(14, 42, 90, 0.34)).
val TaminIdentityCardShadow = Color(0x570E2A5A)

/** The identity card's face. */
val TaminIdentityCardGradient = Brush.linearGradient(
    listOf(TaminIdentityCardStart, TaminIdentityCardMid, TaminIdentityCardEnd),
)

/** The card's gold contact plate. */
val TaminIdentityChipGradient = Brush.linearGradient(
    listOf(TaminIdentityChipStart, TaminIdentityChipMid, TaminIdentityChipEnd),
)

/**
 * The card's gloss: a narrow diagonal streak rather than a broad wash — the design export puts
 * the whole band between 0.44 and 0.56, peaking at 7%. Left at the default corner-to-corner span
 * so it scales with whatever the card measures to.
 */
val TaminIdentityCardShine = Brush.linearGradient(
    0.44f to Color.Transparent,
    0.50f to Color.White.copy(alpha = 0.07f),
    0.56f to Color.Transparent,
)

/** The pane the holder's photo sits behind, lighter at the top than at the bottom. */
val TaminIdentityAvatarGlass = Brush.verticalGradient(
    listOf(Color.White.copy(alpha = 0.125f), Color.White.copy(alpha = 0.05f)),
)

/* ---- Bank cards ---------------------------------------------------------------------------- */

/**
 * Bank card palettes, taken from the design's own theme table.
 *
 * A bank's colors belong to the bank, so these are the one group here with no light/dark variant.
 * Each card is a pale two-stop wash carrying the brand in its ink, and the account number has a
 * tone of its own — رفاه signs its name in navy but prints its number in magenta.
 */
val TaminBankRefahSurfaceTop = Color(0xFFECEFF7)
val TaminBankRefahSurfaceBottom = Color(0xFFC5CFE5)
val TaminBankRefahInk = Color(0xFF1B2C74)
val TaminBankRefahNumberInk = Color(0xFF8E0F4A)
val TaminBankRefahChipInk = Color(0xFFC0176B)
val TaminBankRefahChipSurface = Color(0xFFC0176B).copy(alpha = 0.10f)

val TaminBankMelliSurfaceTop = Color(0xFFF6E1A8)
val TaminBankMelliSurfaceBottom = Color(0xFFD5AE59)
val TaminBankMelliInk = Color(0xFF3B2A0B)
val TaminBankMelliNumberInk = Color(0xFF2C1F06)
val TaminBankMelliChipInk = Color(0xFF9A6A16)
val TaminBankMelliChipSurface = Color(0xFFFDA726).copy(alpha = 0.14f)

val TaminBankMellatSurfaceTop = Color(0xFFFBE1DB)
val TaminBankMellatSurfaceBottom = Color(0xFFEDB1A6)
val TaminBankMellatInk = Color(0xFF6E1710)
val TaminBankMellatNumberInk = Color(0xFF8A1A11)
val TaminBankMellatChipInk = Color(0xFFC4291F)
val TaminBankMellatChipSurface = Color(0xFFC4291F).copy(alpha = 0.10f)

val TaminBankTejaratSurfaceTop = Color(0xFFE1F0FA)
val TaminBankTejaratSurfaceBottom = Color(0xFFA4CFE9)
val TaminBankTejaratInk = Color(0xFF10496E)
val TaminBankTejaratNumberInk = Color(0xFF0D3E5E)
val TaminBankTejaratChipInk = Color(0xFF17557E)
val TaminBankTejaratChipSurface = Color(0xFF17557E).copy(alpha = 0.10f)

val TaminBankSaderatSurfaceTop = Color(0xFFE4F0FB)
val TaminBankSaderatSurfaceBottom = Color(0xFFA3C8EB)
val TaminBankSaderatInk = Color(0xFF003A66)
val TaminBankSaderatNumberInk = Color(0xFF004270)
val TaminBankSaderatChipInk = Color(0xFF0072BE)
val TaminBankSaderatChipSurface = Color(0xFF0072BE).copy(alpha = 0.10f)

val TaminBankSepahSurfaceTop = Color(0xFFFFE6C0)
val TaminBankSepahSurfaceBottom = Color(0xFFF7B45F)
val TaminBankSepahInk = Color(0xFF232571)
val TaminBankSepahNumberInk = Color(0xFF1C1E5E)
val TaminBankSepahChipInk = Color(0xFF2E3192)
val TaminBankSepahChipSurface = Color(0xFF2E3192).copy(alpha = 0.10f)

/** A bank the service returns that the app has no palette for still has to draw a card. */
val TaminBankUnknownSurfaceTop = Color(0xFFECEFF7)
val TaminBankUnknownSurfaceBottom = Color(0xFFC5CFE5)
val TaminBankUnknownInk = TaminNavy900

/* ---- Treatment costs ------------------------------------------------------------------------ */

/**
 * The refund card's own gradients, from the design.
 *
 * Fixed in both themes like the bank palettes: the card is a document, and its accent identifies
 * the service rather than following the app's light/dark surface.
 */
val TaminCostsAccentTop = Color(0xFF2FB9BC)
val TaminCostsAccentBottom = Color(0xFF0E7C82)

/** The «عملیات» button is green, not the card's teal. */
val TaminCostsOperationsStart = Color(0xFF16C26B)
val TaminCostsOperationsEnd = Color(0xFF03794A)

/**
 * Ink on the operations button. Named rather than `Color.White` at the call site so the button's
 * two colors are declared together, and fixed in both themes because its background is.
 */
val TaminCostsOperationsInk = Color(0xFFFFFFFF)

/* ---- Insurance card -------------------------------------------------------------------------- */

/**
 * Ink and translucency layers on the insured-person card.
 *
 * Fixed rather than theme-varying: the card carries its own dark teal gradient in both themes, so
 * everything on it is a wash of white at a set strength rather than a surface color.
 */
val TaminInsuranceCardInk = Color(0xFFFFFFFF)
val TaminInsuranceCardInkMuted = TaminInsuranceCardInk.copy(alpha = 0.75f)

/** The translucent chips and pills the card sets on its own gradient. */
val TaminInsuranceCardChipBg = TaminInsuranceCardInk.copy(alpha = 0.13f)
val TaminInsuranceCardTrackBg = TaminInsuranceCardInk.copy(alpha = 0.08f)

/* ---- Ink on accent surfaces ------------------------------------------------------------------ */

/**
 * White at the strengths the design uses on a filled or gradient surface — selected chips, the hub
 * header, the costs hero, timeline actions.
 *
 * Named rather than `Color.White.copy(alpha = …)` at each call site so the set is countable: every
 * value here is one of the design actually specifies, and a new one has to be added deliberately.
 */
val TaminOnAccentInk = Color(0xFFFFFFFF)
val TaminOnAccentInkSoft = TaminOnAccentInk.copy(alpha = 0.90f)
val TaminOnAccentInkMuted = TaminOnAccentInk.copy(alpha = 0.80f)
/** Dimmed white for inactive hero step segments (current-only highlight). */
val TaminOnAccentInkFaint = TaminOnAccentInk.copy(alpha = 0.35f)

/** Translucent fills and hairlines the same surfaces set on themselves. */
val TaminOnAccentFill = TaminOnAccentInk.copy(alpha = 0.10f)
val TaminOnAccentFillStrong = TaminOnAccentInk.copy(alpha = 0.16f)
val TaminOnAccentBorder = TaminOnAccentInk.copy(alpha = 0.18f)

/* ---- Home campaigns carousel ------------------------------------------------------------------ */

/**
 * The three promo gradients on the home page's campaign cards, plus the shadow each one casts and
 * the dark tone its white CTA pill prints in.
 *
 * Brand-fixed: every card is a dark gradient in both themes, so these do not live on
 * [com.tamin.taminhamrah.ui.theme.TaminColors] and do not follow the page. They are consumed only
 * through [com.tamin.taminhamrah.model.campaign.CampaignKind], which keeps a gradient and its CTA
 * tone in one row so the two cannot drift apart.
 */
val CampaignHousewifeStart = TaminNavy700           // #1F4FA3
val CampaignHousewifeMid = Color(0xFF3B6FE8)
val CampaignHousewifeEnd = Color(0xFF1FB6D8)
val CampaignHousewifeShadow = Color(0x47173D7E)     // rgba(23,61,126,.28)

val CampaignFreelanceStart = Color(0xFF0E5F66)
val CampaignFreelanceMid = Color(0xFF0E7C82)
val CampaignFreelanceEnd = Color(0xFF5FD8D2)
val CampaignFreelanceShadow = Color(0x470E5F66)     // rgba(14,95,102,.28)

val CampaignStudentStart = Color(0xFF4B2E86)
val CampaignStudentMid = Color(0xFF7C5CFF)
val CampaignStudentEnd = Color(0xFF22B8D6)
val CampaignStudentShadow = Color(0x474B2E86)       // rgba(75,46,134,.28)

/** The idle page dot under the carousel; the active one is `blueText`. */
val CampaignDotIdle = Color(0xFFD7E0EC)

/** The two aria-hidden decoration circles every campaign card carries. */
val CampaignGlowCore = TaminOnAccentInk.copy(alpha = 0.22f)
val CampaignBubbleFill = TaminOnAccentInk.copy(alpha = 0.07f)

/** The card's own ink: badge fill and hairline, body copy, and the footer caption. */
val CampaignBadgeFill = TaminOnAccentInk.copy(alpha = 0.18f)
val CampaignBadgeBorder = TaminOnAccentInk.copy(alpha = 0.26f)
val CampaignBodyInk = TaminOnAccentInk.copy(alpha = 0.82f)
val CampaignCaptionInk = TaminOnAccentInk.copy(alpha = 0.55f)


// ─── خلاصهٔ سابقه — the home page's history summary card ──────────────────────────────────
// Straight off the design file (`Tamin Man App` home). Its blues are a shade of their own rather
// than the brand primaries, so they are named here instead of borrowed from a token that would
// drag the card along the next time the primary palette moves.

val HistoryCardBorder = Color(0xFFE9EEF6)
val HistoryCardShadow = Color(0x12173D7E)      // 0 12px 30px rgba(23,61,126,.07)
val HistoryCardDivider = Color(0xFFEEF1F6)
val HistoryCardGlow = Color(0x215C8EF6)        // rgba(92,142,246,.13)
val HistoryYearPillBorder = Color(0xFFD7E6FF)
val HistoryIconStart = Color(0xFF3B6FE8)
val HistoryIconEnd = Color(0xFF1FB6D8)
val HistoryFigure = Color(0xFF2C67D8)
val HistoryMonthStart = Color(0xFF5C8EF6)
val HistoryMonthEnd = Color(0xFF2C67D8)
val HistoryMonthCurrentStart = Color(0xFF3F79EA)
val HistoryMonthCurrentEnd = Color(0xFF1E51B8)
val HistoryUnpaidBg = Color(0xFFFFF7E6)
val HistoryUnpaidBorder = Color(0xFFE9BE62)
val HistoryUnpaidStripe = Color(0xFFF1C572)
val HistoryUnpaidStripeSoft = Color(0xFFFBE6BE)
val HistoryUpcomingBg = Color(0xFFF7F9FC)
val HistoryUpcomingBorder = Color(0xFFDCE3EE)
val HistoryProgressTrack = Color(0xFFEDF1F8)
val HistoryWarningBorder = Color(0xFFF3DFB4)
val HistoryWarningText = Color(0xFF8A5406)

// The same card in dark: the blues carry over — they are the card's identity — while every
// surface, border and paper-coloured fill is restated against the dark page.
val HistoryDarkCardGlow = Color(0x2E5C8EF6)
val HistoryDarkCardShadow = Color(0x33000000)
val HistoryDarkUnpaidBg = Color(0x29F59E0B)
val HistoryDarkUnpaidBorder = Color(0x8AF59E0B)
val HistoryDarkUnpaidStripe = Color(0x66F59E0B)
val HistoryDarkUnpaidStripeSoft = Color(0x2EF59E0B)
val HistoryDarkUpcomingBg = Color(0x0DFFFFFF)
val HistoryDarkUpcomingBorder = Color(0x1FFFFFFF)
val HistoryDarkProgressTrack = Color(0x14FFFFFF)
val HistoryDarkWarningText = Color(0xFFFBBF24)
