package com.tamin.taminhamrah.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.ViewModel
import com.tamin.taminhamrah.util.toPersianDigits
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}

@Composable
inline fun <reified T> Flow<T>.collectWithLifecycleAware(
    key: Any = Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit
) {
    /**
     * Collects values from this [Flow] within a Composable function, automatically cancelling
     * the collection when the LifecycleOwner's state is less than the specified [minActiveState].
     *
     * @param key An optional key to restart the collection when changed.
     * @param lifecycleOwner The LifecycleOwner that controls the lifecycle of the collection.
     * @param minActiveState The minimum active state in which the collection should occur.
     * @param action The action to perform with each value collected from the flow.
     */
    val lifecycleAwareFlow = remember(this, lifecycleOwner) {
        this.flowWithLifecycle(lifecycleOwner.lifecycle, minActiveState)
    }

    LaunchedEffect(key) {
        lifecycleAwareFlow.collect(action)
    }
}

@Composable
fun <T> StateFlow<T>.collectAsStateWithLifecycle(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<T> = collectAsStateWithLifecycle(
    initialValue = remember { this.value },
    lifecycle = lifecycle,
    minActiveState = minActiveState
)

@Composable
fun <T> Flow<T>.collectAsStateWithLifecycleAware(
    initialValue: T,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): State<T> {
    /**
     * Collects values from this [Flow] as a [State] within a Composable function,
     * initializing with [initialValue] and automatically stopping the collection
     * when the LifecycleOwner's state is less than the specified [minActiveState].
     *
     * @param initialValue The initial value to start with.
     * @param lifecycle The Lifecycle that controls the lifecycle of the collection.
     * @param minActiveState The minimum active state in which the collection should occur.
     * @return A [State] object containing the latest value collected from this [Flow].
     */
    val currentValue = remember(this) { initialValue }
    return produceState(
        initialValue = currentValue,
        key1 = this,
        key2 = lifecycle,
        key3 = minActiveState
    ) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            this@collectAsStateWithLifecycleAware.collect {
                this@produceState.value = it
            }
        }
    }
}

fun Modifier.animatePlacement(): Modifier = composed {
    val scope = rememberCoroutineScope()
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }
    var animatable by remember {
        mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
    }
    this
        .onPlaced {
            targetOffset = it
                .positionInParent()
                .round()
        }
        .offset {
            val anim = animatable ?: Animatable(targetOffset, IntOffset.VectorConverter)
                .also {
                    animatable = it
                }
            if (anim.targetValue != targetOffset) {
                scope.launch {
                    anim.animateTo(targetOffset, spring(stiffness = Spring.StiffnessMediumLow))
                }
            }
            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        }
}

/**
 * A field the service left empty reads as [fallback], never as a blank line.
 *
 * The fallback is passed in rather than fixed here so the wording stays in the string resources.
 */
fun String?.orAbsent(fallback: String): String = if (isNullOrBlank()) fallback else this

/** The dash the design prints wherever a value is missing. */
const val ABSENT_VALUE = "-"

/** No days at all — distinct from a blank, which only means the service said nothing. */
const val NO_DAYS = "0"

/** A value the service omitted reads as a dash placeholder, the way the design shows it. */
fun String?.orDash(): String = orAbsent(ABSENT_VALUE)

/** Day counts are strings on the wire; an absent one is none, not a blank. */
fun String?.orZero(): String = orAbsent(NO_DAYS)

fun String.iSValidForSearch(): Boolean = this.trim().length > 2

/** True when any of [phrases] appears anywhere in this string. */
fun String.containsAny(phrases: List<String>): Boolean = phrases.any { contains(it) }

/**
 * Rewrites Arabic ي/ك to Persian ی/ک.
 *
 * The same letters to a reader, different code points on the wire, and the services mix them
 * freely — `commission-confrimation` returns «تائيد شده» with an Arabic yeh. Anything that either
 * matches on Persian text or displays it beside Persian text has to fold the variants away first.
 */
fun String.normalizeArabicLetters(): String = replace(ARABIC_YEH, PERSIAN_YEH).replace(ARABIC_KAF, PERSIAN_KAF)

private const val ARABIC_YEH = 'ي'
private const val PERSIAN_YEH = 'ی'
private const val ARABIC_KAF = 'ك'
private const val PERSIAN_KAF = 'ک'

fun String.iSValidForSearchHashtag(): Boolean = this.trim().length > 1

val hashtagRegex = "#[\\p{L}0-9_\\p{M}]+".toRegex()
val stringListRegex = "(\\S+|\\s)".toRegex()
val emojiRegex = """[\uD83C\uDF00-\uD83D\uDFFF\uD83E\uDD00-\uD83E\uDFFF]+""".toRegex()

private const val PERSIAN_THOUSANDS_SEPARATOR = '٬'

/**
 * Groups a run of digits into thousands and renders them in Persian numerals,
 * e.g. "1234567" -> "۱٬۲۳۴٬۵۶۷". Matches how [com.tamin.taminhamrah.util.PersianDateFormatter]
 * renders dates, so amounts and dates read consistently.
 *
 * An optional leading minus is preserved. Empty input yields "۰".
 */
private fun groupThousands(digits: String): String {
    if (digits.isEmpty()) return "۰"
    // Split the sign-off first: grouping it along with the digits inserts a separator
    // straight after the minus whenever the digit count is a multiple of three.
    val isNegative = digits.startsWith('-')
    val magnitude = if (isNegative) digits.substring(1) else digits
    if (magnitude.isEmpty()) return "۰"

    val reversed = magnitude.reversed()
    val builder = StringBuilder()
    for (i in reversed.indices) {
        if (i > 0 && i % 3 == 0) {
            builder.append(PERSIAN_THOUSANDS_SEPARATOR)
        }
        builder.append(reversed[i])
    }
    val grouped = builder.reverse().toString().toPersianDigits()
    return if (isNegative) "-$grouped" else grouped
}

/**
 * Formats a numeric [String] as a Persian thousands-grouped price
 * (e.g. "1234567" -> "۱٬۲۳۴٬۵۶۷").
 *
 * Unlike the previous implementation this does NOT silently strip unexpected characters:
 * if the input contains any non-digit character it is considered invalid and an
 * [IllegalArgumentException] is raised so the caller can surface the problem instead of
 * displaying a corrupted value. An empty string formats to "۰".
 *
 * @throws IllegalArgumentException if [this] contains a non-digit character.
 */
fun String.toPriceFormat(): String {
    require(all { it.isDigit() }) {
        this
    }
    return groupThousands(this)
}

/** Formats a [Long] amount as a thousands-grouped price. */
fun Long.toPriceFormat(): String = groupThousands(this.toString())

/**
 * Normalizes a numeric amount string (digits only) to a plain Long string, defaulting to "0".
 *
 * The services send amounts as free-form text, so this is what makes an amount safe to hand to
 * [toPriceFormat], which rejects anything that is not a digit.
 */
fun String?.toLongStringOrZero(): String = this?.toLongOrNull()?.toString() ?: "0"

/**
 * An amount with its unit, the way every money line in the app reads it: grouped digits, then
 * «ریال».
 *
 * A value the service did not send formats as [fallback] on its own — a missing amount must never
 * read as a real zero.
 */
fun String.toRialAmount(fallback: String = "—"): String =
    toLongOrNull()?.let { "${it.toPriceFormat()} ریال" } ?: fallback

/**
 * Formats a [Double] amount as a thousands-grouped price.
 * Currency amounts in the app are integral (Rial), so the fractional part is dropped.
 */
fun Double.toPriceFormat(): String = groupThousands(this.toLong().toString())

/**
 * The extension of the document this thumbnail URL stands for.
 *
 * This endpoint carries the filename in the query, not the path — a real thumbnail is
 * `…/api/erecords/thumbs?id=0017312213669900959.tif&parent=…&cs=2`, whose path ends in `thumbs`
 * and has no extension of its own. So the `id` parameter is read first, and only a URL not shaped
 * that way falls back to its path.
 *
 * Reading the path first is what sent شناسنامه to the image viewer: every document looked
 * extensionless, and a TIFF cannot be decoded as an image.
 */
fun String.documentExtension(): String {
    val idParameter = substringAfter("id=", missingDelimiterValue = "").substringBefore('&')
    val fileName = idParameter.ifEmpty {
        substringBefore('?').substringBefore('#').substringAfterLast('/')
    }
    return fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
}

private const val THUMBS_SEGMENT = "thumbs"

/**
 * The thumbnail URL pointed at the full document instead.
 *
 * A URL carrying no `thumbs` segment is already whole and is returned untouched, so an unexpected
 * shape degrades to "open what we were given" rather than to a mangled URL.
 */
fun String.forFullDocument(segment: String): String =
    if (THUMBS_SEGMENT in this) replace(THUMBS_SEGMENT, segment) else this

/**
 * Checks if the byte array starts with or contains the %PDF magic header sequence.
 */
fun ByteArray.looksLikePdf(): Boolean {
    if (size < 4) return false
    val pdfMagic = byteArrayOf(0x25, 0x50, 0x44, 0x46) // %PDF
    val limit = minOf(size - 3, 1024)
    for (i in 0 until limit) {
        if (this[i] == pdfMagic[0] &&
            this[i + 1] == pdfMagic[1] &&
            this[i + 2] == pdfMagic[2] &&
            this[i + 3] == pdfMagic[3]
        ) {
            return true
        }
    }
    return false
}

