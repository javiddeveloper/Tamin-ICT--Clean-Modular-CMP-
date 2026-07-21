package com.tamin.taminhamrah.utils.extentions

/**
 * @author  : Javid
 * @summary : ImageView
 */
import android.widget.ImageView
import coil.load
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun ImageView.animateChange(
    newImageResId: Int,
    scale: Float = 0.9f,
    rotation: Float = 5f,
    animDuration: Long = 150L,
    crossFadeDuration: Int = 100,
    crossfadeEnable: Boolean = true
) {
    suspendCancellableCoroutine { cont ->
        this.animate()
            .scaleX(scale)
            .scaleY(scale)
            .rotation(rotation)
            .setDuration(animDuration)
            .withEndAction {
                if (cont.isActive) cont.resume(Unit)
            }
            .start()
    }


    this.load(newImageResId) {
        crossfade(crossfadeEnable)
        crossfade(crossFadeDuration)
    }

    suspendCancellableCoroutine { cont ->
        this.animate()
            .scaleX(1f)
            .scaleY(1f)
            .rotation(0f)
            .setDuration(animDuration + 50)
            .withEndAction {
                if (cont.isActive) cont.resume(Unit)
            }
            .start()
    }
}
