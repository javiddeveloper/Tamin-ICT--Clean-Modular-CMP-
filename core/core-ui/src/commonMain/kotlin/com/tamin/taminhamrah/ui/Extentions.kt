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

fun String.iSValidForSearch(): Boolean = this.trim().length > 2

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
 * Digits as arithmetic sees them: everything that is not a digit is dropped, and Persian and
 * Arabic-Indic digits are folded onto ASCII first.
 *
 * [Char.isDigit] is true for all three alphabets, so filtering before folding would keep characters
 * that no `toLong` can read.
 */
fun String.digitsOnly(): String = buildString(length) {
    for (char in this@digitsOnly) {
        when (char) {
            in '0'..'9' -> append(char)
            in '۰'..'۹' -> append('0' + (char - '۰'))
            in '٠'..'٩' -> append('0' + (char - '٠'))
        }
    }
}

/**
 * Breaks a run of characters into fixed-size groups the way a card number is printed —
 * left to right, unlike [groupThousands], which counts from the end.
 *
 * The receiver should already be [digitsOnly]: a stray separator would otherwise be counted as one
 * of the four.
 */
fun String.grouped(size: Int = 4, separator: Char = ' '): String {
    if (isEmpty() || size <= 0) return this
    return buildString(length + (length - 1) / size) {
        this@grouped.forEachIndexed { index, char ->
            if (index > 0 && index % size == 0) append(separator)
            append(char)
        }
    }
}

/**
 * Groups from the right in [size]s, folding a short leading group into the first one — the way an
 * account number is printed: 13 digits read 4-3-3-3, 10 read 4-3-3, 9 read 3-3-3.
 *
 * Differs from [grouped], which counts from the left and so leaves the remainder at the end.
 */
fun String.groupedFromEnd(size: Int = 3, separator: Char = ' '): String {
    if (size <= 0 || length <= size) return this
    val remainder = length % size
    val head = if (remainder == 0) size else remainder + size
    return buildString(length + (length - 1) / size) {
        append(this@groupedFromEnd, 0, head)
        var index = head
        while (index < this@groupedFromEnd.length) {
            append(separator)
            append(this@groupedFromEnd, index, index + size)
            index += size
        }
    }
}
