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
 * everything on it is a wash of white at a set strength rather than a surface colour.
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
 * value here is one the design actually specifies, and a new one has to be added deliberately.
 */
val TaminOnAccentInk = Color(0xFFFFFFFF)
val TaminOnAccentInkSoft = TaminOnAccentInk.copy(alpha = 0.90f)
val TaminOnAccentInkMuted = TaminOnAccentInk.copy(alpha = 0.80f)

/** Translucent fills and hairlines the same surfaces set on themselves. */
val TaminOnAccentFill = TaminOnAccentInk.copy(alpha = 0.10f)
val TaminOnAccentFillStrong = TaminOnAccentInk.copy(alpha = 0.16f)
val TaminOnAccentBorder = TaminOnAccentInk.copy(alpha = 0.18f)

