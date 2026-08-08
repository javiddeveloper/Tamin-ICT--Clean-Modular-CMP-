package com.tamin.taminhamrah.model.bankAccount

import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.theme.TaminBankMellatChipInk
import com.tamin.taminhamrah.ui.theme.TaminBankMellatChipSurface
import com.tamin.taminhamrah.ui.theme.TaminBankMellatInk
import com.tamin.taminhamrah.ui.theme.TaminBankMellatNumberInk
import com.tamin.taminhamrah.ui.theme.TaminBankMellatSurfaceBottom
import com.tamin.taminhamrah.ui.theme.TaminBankMellatSurfaceTop
import com.tamin.taminhamrah.ui.theme.TaminBankMelliChipInk
import com.tamin.taminhamrah.ui.theme.TaminBankMelliChipSurface
import com.tamin.taminhamrah.ui.theme.TaminBankMelliInk
import com.tamin.taminhamrah.ui.theme.TaminBankMelliNumberInk
import com.tamin.taminhamrah.ui.theme.TaminBankMelliSurfaceBottom
import com.tamin.taminhamrah.ui.theme.TaminBankMelliSurfaceTop
import com.tamin.taminhamrah.ui.theme.TaminBankRefahChipInk
import com.tamin.taminhamrah.ui.theme.TaminBankRefahChipSurface
import com.tamin.taminhamrah.ui.theme.TaminBankRefahInk
import com.tamin.taminhamrah.ui.theme.TaminBankRefahNumberInk
import com.tamin.taminhamrah.ui.theme.TaminBankRefahSurfaceBottom
import com.tamin.taminhamrah.ui.theme.TaminBankRefahSurfaceTop
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatChipInk
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatChipSurface
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatInk
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatNumberInk
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatSurfaceBottom
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatSurfaceTop
import com.tamin.taminhamrah.ui.theme.TaminBankSepahChipInk
import com.tamin.taminhamrah.ui.theme.TaminBankSepahChipSurface
import com.tamin.taminhamrah.ui.theme.TaminBankSepahInk
import com.tamin.taminhamrah.ui.theme.TaminBankSepahNumberInk
import com.tamin.taminhamrah.ui.theme.TaminBankSepahSurfaceBottom
import com.tamin.taminhamrah.ui.theme.TaminBankSepahSurfaceTop
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratChipInk
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratChipSurface
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratInk
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratNumberInk
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratSurfaceBottom
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratSurfaceTop
import com.tamin.taminhamrah.ui.theme.TaminBankUnknownInk
import com.tamin.taminhamrah.ui.theme.TaminBankUnknownSurfaceBottom
import com.tamin.taminhamrah.ui.theme.TaminBankUnknownSurfaceTop
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_mellat
import taminx.core.core_ui.bank_melli
import taminx.core.core_ui.bank_name_mellat
import taminx.core.core_ui.bank_name_melli
import taminx.core.core_ui.bank_name_refah
import taminx.core.core_ui.bank_name_saderat
import taminx.core.core_ui.bank_name_sepah
import taminx.core.core_ui.bank_name_tejarat
import taminx.core.core_ui.bank_refah
import taminx.core.core_ui.bank_saderat
import taminx.core.core_ui.bank_sepah
import taminx.core.core_ui.bank_tejarat

/**
 * The banks the service accepts, and everything that differs between them.
 *
 * One table rather than several keyed by name: the codes come from the previous app, the digit
 * counts from the product owner and the palette from the design, and parallel tables would drift
 * the first time one of them was edited.
 *
 * [code] is the identity — the list endpoint returns `bankName` as "رفاه کارگران", which matches no
 * label shown anywhere in the app, so nothing may be looked up by name.
 */
enum class Bank(
    val code: String,
    val label: StringResource,
    val accountNumberLength: Int,
    /** The card wash: pale at the top corner, brand-tinted at the bottom one. */
    val gradient: ImmutableList<Color>,
    /** The name, the dates and the status are written in this. */
    val ink: Color,
    /** The account number has its own tone — رفاه signs in navy but numbers in magenta. */
    val numberInk: Color,
    /** The digit-count chip in the picker, which is brighter than the card ink. */
    val chipInk: Color,
    /** Its background: a wash of the same bright hue, not a tint of [ink]. */
    val chipSurface: Color,
    val logo: DrawableResource,
) {
    REFAH(
        code = "01",
        label = Res.string.bank_name_refah,
        accountNumberLength = 9,
        gradient = persistentListOf(TaminBankRefahSurfaceTop, TaminBankRefahSurfaceBottom),
        ink = TaminBankRefahInk,
        numberInk = TaminBankRefahNumberInk,
        chipInk = TaminBankRefahChipInk,
        chipSurface = TaminBankRefahChipSurface,
        logo = Res.drawable.bank_refah,
    ),
    MELLI(
        code = "02",
        label = Res.string.bank_name_melli,
        accountNumberLength = 13,
        gradient = persistentListOf(TaminBankMelliSurfaceTop, TaminBankMelliSurfaceBottom),
        ink = TaminBankMelliInk,
        numberInk = TaminBankMelliNumberInk,
        chipInk = TaminBankMelliChipInk,
        chipSurface = TaminBankMelliChipSurface,
        logo = Res.drawable.bank_melli,
    ),
    MELLAT(
        code = "03",
        label = Res.string.bank_name_mellat,
        accountNumberLength = 10,
        gradient = persistentListOf(TaminBankMellatSurfaceTop, TaminBankMellatSurfaceBottom),
        ink = TaminBankMellatInk,
        numberInk = TaminBankMellatNumberInk,
        chipInk = TaminBankMellatChipInk,
        chipSurface = TaminBankMellatChipSurface,
        logo = Res.drawable.bank_mellat,
    ),
    TEJARAT(
        code = "04",
        label = Res.string.bank_name_tejarat,
        accountNumberLength = 10,
        gradient = persistentListOf(TaminBankTejaratSurfaceTop, TaminBankTejaratSurfaceBottom),
        ink = TaminBankTejaratInk,
        numberInk = TaminBankTejaratNumberInk,
        chipInk = TaminBankTejaratChipInk,
        chipSurface = TaminBankTejaratChipSurface,
        logo = Res.drawable.bank_tejarat,
    ),
    SADERAT(
        code = "05",
        label = Res.string.bank_name_saderat,
        accountNumberLength = 13,
        gradient = persistentListOf(TaminBankSaderatSurfaceTop, TaminBankSaderatSurfaceBottom),
        ink = TaminBankSaderatInk,
        numberInk = TaminBankSaderatNumberInk,
        chipInk = TaminBankSaderatChipInk,
        chipSurface = TaminBankSaderatChipSurface,
        logo = Res.drawable.bank_saderat,
    ),
    SEPAH(
        code = "07",
        label = Res.string.bank_name_sepah,
        accountNumberLength = 13,
        gradient = persistentListOf(TaminBankSepahSurfaceTop, TaminBankSepahSurfaceBottom),
        ink = TaminBankSepahInk,
        numberInk = TaminBankSepahNumberInk,
        chipInk = TaminBankSepahChipInk,
        chipSurface = TaminBankSepahChipSurface,
        logo = Res.drawable.bank_sepah,
    ),
    ;

    companion object {
        /**
         * The order the picker lists them in. Taken from the design, which is neither alphabetical
         * nor by code, so it cannot be derived — declaring it here keeps it beside the table it
         * orders rather than in the sheet that draws it.
         */
        val displayOrder: ImmutableList<Bank> =
            persistentListOf(REFAH, SEPAH, MELLI, SADERAT, TEJARAT, MELLAT)

        /** The sequence skips `06`; [SEPAH] is `07`. */
        fun fromCode(code: String?): Bank? =
            code?.trim()?.let { wanted -> entries.firstOrNull { it.code == wanted } }
    }
}

/**
 * A bank the app does not know still has to draw a card, so every visual property is read through
 * a nullable receiver rather than guarded at each call site.
 */
private val UnknownGradient =
    persistentListOf(TaminBankUnknownSurfaceTop, TaminBankUnknownSurfaceBottom)

private const val SUB_INK_ALPHA = 0.80f
private const val PILL_ALPHA = 0.12f

val Bank?.cardGradient: ImmutableList<Color> get() = this?.gradient ?: UnknownGradient

val Bank?.cardInk: Color get() = this?.ink ?: TaminBankUnknownInk

val Bank?.cardNumberInk: Color get() = this?.numberInk ?: TaminBankUnknownInk

/** Labels and secondary text: the same ink, stepped back. */
val Bank?.subInk: Color get() = cardInk.copy(alpha = SUB_INK_ALPHA)

val Bank?.pillBackground: Color get() = cardInk.copy(alpha = PILL_ALPHA)

/**
 * How strongly the logo prints through the card.
 *
 * Low, because the card is pale now: the same mark that vanished on the old saturated card would
 * compete with the account number sitting over it here.
 */
val Bank?.watermarkAlpha: Float get() = 0.12f
