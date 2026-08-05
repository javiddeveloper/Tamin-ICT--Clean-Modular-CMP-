package com.tamin.taminhamrah.model.bankAccount

import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.theme.TaminBankInkOnLight
import com.tamin.taminhamrah.ui.theme.TaminBankMellatEnd
import com.tamin.taminhamrah.ui.theme.TaminBankMellatMid
import com.tamin.taminhamrah.ui.theme.TaminBankMellatStart
import com.tamin.taminhamrah.ui.theme.TaminBankMelliEnd
import com.tamin.taminhamrah.ui.theme.TaminBankMelliMid
import com.tamin.taminhamrah.ui.theme.TaminBankMelliStart
import com.tamin.taminhamrah.ui.theme.TaminBankRefahEnd
import com.tamin.taminhamrah.ui.theme.TaminBankRefahMid
import com.tamin.taminhamrah.ui.theme.TaminBankRefahStart
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatEnd
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatMid
import com.tamin.taminhamrah.ui.theme.TaminBankSaderatStart
import com.tamin.taminhamrah.ui.theme.TaminBankSepahEnd
import com.tamin.taminhamrah.ui.theme.TaminBankSepahMid
import com.tamin.taminhamrah.ui.theme.TaminBankSepahStart
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratEnd
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratMid
import com.tamin.taminhamrah.ui.theme.TaminBankTejaratStart
import com.tamin.taminhamrah.ui.theme.TaminBankUnknownEnd
import com.tamin.taminhamrah.ui.theme.TaminBankUnknownMid
import com.tamin.taminhamrah.ui.theme.TaminBankUnknownStart
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
 * One table rather than three keyed by name: the codes come from the previous app, the digit counts
 * from the product owner and the palette from the design, and three parallel tables would drift the
 * first time one of them was edited.
 *
 * [code] is the identity — the list endpoint returns `bankName` as "رفاه کارگران", which matches no
 * label shown anywhere in the app, so nothing may be looked up by name.
 */
enum class Bank(
    val code: String,
    val label: StringResource,
    val accountNumberLength: Int,
    val gradient: ImmutableList<Color>,
    /** Where the middle gradient stop sits; the design does not use the same fraction for all. */
    val gradientMidStop: Float,
    val isLight: Boolean,
    val logo: DrawableResource,
) {
    REFAH(
        code = "01",
        label = Res.string.bank_name_refah,
        accountNumberLength = 9,
        gradient = persistentListOf(TaminBankRefahStart, TaminBankRefahMid, TaminBankRefahEnd),
        gradientMidStop = 0.52f,
        isLight = false,
        logo = Res.drawable.bank_refah,
    ),
    MELLI(
        code = "02",
        label = Res.string.bank_name_melli,
        accountNumberLength = 13,
        gradient = persistentListOf(TaminBankMelliStart, TaminBankMelliMid, TaminBankMelliEnd),
        gradientMidStop = 0.45f,
        isLight = true,
        logo = Res.drawable.bank_melli,
    ),
    MELLAT(
        code = "03",
        label = Res.string.bank_name_mellat,
        accountNumberLength = 10,
        gradient = persistentListOf(TaminBankMellatStart, TaminBankMellatMid, TaminBankMellatEnd),
        gradientMidStop = 0.55f,
        isLight = false,
        logo = Res.drawable.bank_mellat,
    ),
    TEJARAT(
        code = "04",
        label = Res.string.bank_name_tejarat,
        accountNumberLength = 10,
        gradient = persistentListOf(TaminBankTejaratStart, TaminBankTejaratMid, TaminBankTejaratEnd),
        gradientMidStop = 0.55f,
        isLight = false,
        logo = Res.drawable.bank_tejarat,
    ),
    SADERAT(
        code = "05",
        label = Res.string.bank_name_saderat,
        accountNumberLength = 13,
        gradient = persistentListOf(TaminBankSaderatStart, TaminBankSaderatMid, TaminBankSaderatEnd),
        gradientMidStop = 0.55f,
        isLight = false,
        logo = Res.drawable.bank_saderat,
    ),
    SEPAH(
        code = "07",
        label = Res.string.bank_name_sepah,
        accountNumberLength = 13,
        gradient = persistentListOf(TaminBankSepahStart, TaminBankSepahMid, TaminBankSepahEnd),
        gradientMidStop = 0.55f,
        isLight = false,
        logo = Res.drawable.bank_sepah,
    ),
    ;

    companion object {
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
    persistentListOf(TaminBankUnknownStart, TaminBankUnknownMid, TaminBankUnknownEnd)

val Bank?.cardGradient: ImmutableList<Color> get() = this?.gradient ?: UnknownGradient

val Bank?.cardGradientMidStop: Float get() = this?.gradientMidStop ?: 0.55f

val Bank?.ink: Color get() = if (this?.isLight == true) TaminBankInkOnLight else Color.White

val Bank?.subInk: Color
    get() = if (this?.isLight == true) TaminBankInkOnLight.copy(alpha = 0.60f)
    else Color.White.copy(alpha = 0.68f)

val Bank?.hairline: Color
    get() = if (this?.isLight == true) TaminBankInkOnLight.copy(alpha = 0.16f)
    else Color.White.copy(alpha = 0.20f)

val Bank?.pillBackground: Color
    get() = if (this?.isLight == true) TaminBankInkOnLight.copy(alpha = 0.12f)
    else Color.White.copy(alpha = 0.16f)

/** The halo behind the watermark, which keeps a dark logo legible on a dark card. */
val Bank?.watermarkGlow: Color
    get() = if (this?.isLight == true) Color.White.copy(alpha = 0.55f)
    else Color.White.copy(alpha = 0.22f)

val Bank?.watermarkAlpha: Float get() = if (this?.isLight == true) 0.20f else 0.13f

