package com.tamin.taminhamrah.ui.motion

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Single source of truth برای هر نوع Scroll-Motion در یک صفحه.
 *
 * [rawScrollOffsetPx] مقدار اسکرولی است که خود سیستم موشن (نه لیست داخلی) "مصرف" کرده،
 * محدود به بازه [0, maxMotionDistancePx].
 *
 * [progress] همان مقدار نرمالایز‌شده بین 0f تا 1f است که همه‌ی Modifier های این پکیج
 * (motionFade / motionScale / motionParallax / ...) از آن می‌خوانند.
 *
 * این کلاس کاملاً commonMain-safe است (فقط ریاضیات Float ساده)، پس بدون هیچ expect/actual
 * روی Android و iOS به یک شکل کار می‌کند.
 */
@Stable
class ScrollMotionState(
    val maxMotionDistancePx: Float
) {
    var rawScrollOffsetPx by mutableFloatStateOf(0f)
        private set

    /**
     * derivedStateOf تضمین می‌کند خواننده‌های progress فقط زمانی invalidate می‌شوند که
     * خروجی نرمالایز و کلمپ‌شده واقعاً تغییر کند — نه به ازای هر پیکسل تغییر rawScrollOffsetPx
     * که ممکن است در یک Fling ده‌ها بار در هر فریم رخ دهد. این اولین لایه‌ی جلوگیری از
     * Recomposition اضافی است.
     */
    val progress: Float by derivedStateOf {
        if (maxMotionDistancePx <= 0f) 0f
        else (rawScrollOffsetPx / maxMotionDistancePx).coerceIn(0f, 1f)
    }

    /**
     * [delta] پیکسل را در بازه [0, maxMotionDistancePx] مصرف می‌کند و مقدار واقعاً
     * مصرف‌شده را برمی‌گرداند؛ باقی‌مانده باید برای Scrollable داخلی (مثل LazyColumn) بماند.
     */
    internal fun consumeScroll(delta: Float): Float {
        val old = rawScrollOffsetPx
        val new = (old + delta).coerceIn(0f, maxMotionDistancePx)
        rawScrollOffsetPx = new
        return new - old
    }

    /**
     * NestedScrollConnection همراه با خود State ساخته می‌شود و به‌عنوان یک property ساده
     * expose می‌شود، پس استفاده‌اش فقط همین است:
     * Modifier.nestedScroll(motionState.nestedScrollConnection)
     */
    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {

        // قبل از اینکه فرزند (مثلاً LazyColumn) اسکرول را مصرف کند فراخوانی می‌شود.
        // دلتای رو به بالا (اسکرول کردن به پایین لیست) را اول خودمان می‌گیریم تا هدر
        // جمع شود، بعد نوبت به اسکرول لیست برسد.
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val dy = available.y
            return if (dy < 0f) Offset(0f, consumeScroll(dy)) else Offset.Zero
        }

        // بعد از اینکه فرزند هرچه می‌توانسته مصرف کرده فراخوانی می‌شود. اگر لیست از قبل
        // در بالاترین نقطه باشد و کاربر همچنان رو به پایین بکشد (over-scroll)، همین‌جا
        // هدر دوباره باز می‌شود.
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            val dy = available.y
            return if (dy > 0f) Offset(0f, consumeScroll(dy)) else Offset.Zero
        }
    }

    /**
     * منبع جایگزین برای [nestedScrollConnection]: برای صفحاتی که هدر یک Slot واقعی و
     * جمع‌شونده است (مثلاً topBar در یک Scaffold که ارتفاعش کم می‌شود) نه یک هدر شناور
     * روی محتوا — و فقط لازم است موقعیت اسکرول همان لیست را "تماشا" کند، نه اینکه
     * اسکرول را از آن بگیرد. باید داخل یک LaunchedEffect صدا زده شود:
     *
     * LaunchedEffect(motionState, lazyListState) {
     *     motionState.observeLazyListState(lazyListState)
     * }
     */
    suspend fun observeLazyListState(lazyListState: LazyListState) {
        snapshotFlow {
            if (lazyListState.firstVisibleItemIndex > 0) {
                maxMotionDistancePx
            } else {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            }
        }.collect { offsetPx ->
            rawScrollOffsetPx = offsetPx.coerceIn(0f, maxMotionDistancePx)
        }
    }

    fun reset() {
        rawScrollOffsetPx = 0f
    }
}

/**
 * [ScrollMotionState] را در scope کامپوزیشن جاری remember می‌کند و [maxMotionDistance]
 * (به Dp) را فقط یک‌بار با density فعلی به px تبدیل می‌کند.
 */
@Composable
fun rememberScrollMotionState(
    maxMotionDistance: Dp = 200.dp
): ScrollMotionState {
    val density = LocalDensity.current
    val maxPx = remember(maxMotionDistance, density) {
        with(density) { maxMotionDistance.toPx() }
    }
    return remember(maxPx) { ScrollMotionState(maxMotionDistancePx = maxPx) }
}
