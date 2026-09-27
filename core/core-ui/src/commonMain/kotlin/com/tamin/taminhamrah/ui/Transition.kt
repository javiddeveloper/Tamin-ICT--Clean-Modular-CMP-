/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import kotlin.jvm.JvmSuppressWildcards

typealias EnterTransitionProvider =
    (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)

typealias ExitTransitionProvider =
    (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)

typealias NonNullEnterTransitionProvider =
    (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)

typealias NonNullExitTransitionProvider =
    (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)

const val DEFAULT_FADE_TRANSITION_TIME_MS: Int = 600

const val DEFAULT_SLIDE_TRANSITION_TIME_MS: Int = 450

const val DEFAULT_PUSH_TRANSITION_TIME_MS: Int = 350

val DEFAULT_STAY_TRANSITION_TIME_MS: Int =
    maxOf(
        DEFAULT_FADE_TRANSITION_TIME_MS,
        DEFAULT_SLIDE_TRANSITION_TIME_MS,
        DEFAULT_PUSH_TRANSITION_TIME_MS,
    )

val AnimatedContentTransitionScope<NavBackStackEntry>.isSameGraphNavigation: Boolean
    get() = initialState.destination.parent == targetState.destination.parent

object TransitionProviders {
    object Enter {
        val fadeIn: EnterTransitionProvider = {
            RootTransitionProviders.Enter
                .fadeIn(this)
                .takeIf { isSameGraphNavigation }
        }
        val pushLeft: EnterTransitionProvider = {
            RootTransitionProviders
                .Enter
                .pushLeft(this)
                .takeIf { isSameGraphNavigation }
        }
        val pushRight: EnterTransitionProvider = {
            RootTransitionProviders
                .Enter
                .pushRight(this)
                .takeIf { isSameGraphNavigation }
        }
        val slideUp: EnterTransitionProvider = {
            RootTransitionProviders
                .Enter
                .slideUp(this)
                .takeIf { isSameGraphNavigation }
        }
        val stay: EnterTransitionProvider = {
            RootTransitionProviders
                .Enter
                .stay(this)
                .takeIf { isSameGraphNavigation }
        }
    }
    object Exit {
        val fadeOut: ExitTransitionProvider = {
            RootTransitionProviders
                .Exit
                .fadeOut(this)
                .takeIf { isSameGraphNavigation }
        }
        val pushLeft: ExitTransitionProvider = {
            RootTransitionProviders
                .Exit
                .pushLeft(this)
                .takeIf { isSameGraphNavigation }
        }
        val pushRight: ExitTransitionProvider = {
            RootTransitionProviders
                .Exit
                .pushRight(this)
                .takeIf { isSameGraphNavigation }
        }
        val slideDown: ExitTransitionProvider = {
            RootTransitionProviders
                .Exit
                .slideDown(this)
                .takeIf { isSameGraphNavigation }
        }
        val stay: ExitTransitionProvider = {
            RootTransitionProviders
                .Exit
                .stay(this)
                .takeIf { isSameGraphNavigation }
        }
    }
}
/**
 * The push the nav graph animates a screen change with, as plain transitions.
 *
 * A screen that swaps one body for another in place — a list and the form that replaces it — is a
 * navigation as far as the person tapping is concerned, and `AnimatedContent` cannot take the
 * providers above: those are typed to a [NavBackStackEntry] scope. So the motion itself lives here
 * and both callers read it, rather than the in-page swap growing a near-copy that drifts the first
 * time one of the two is retimed.
 */
object PushTransition {
    private const val HALF = 2

    /** The leaving half starts a beat late, so the two bodies read as one sheet of paper moving. */
    private const val EXIT_DELAY_MS = DEFAULT_PUSH_TRANSITION_TIME_MS / 7

    /** How far a body travels: half the container. */
    internal val halfTravel: (Int) -> Int = { fullSlide -> fullSlide / HALF }

    internal val slideInSpec: FiniteAnimationSpec<IntOffset> =
        tween(durationMillis = DEFAULT_PUSH_TRANSITION_TIME_MS)

    internal val slideOutSpec: FiniteAnimationSpec<IntOffset> = tween(
        durationMillis = DEFAULT_PUSH_TRANSITION_TIME_MS - EXIT_DELAY_MS,
        delayMillis = EXIT_DELAY_MS,
    )

    /** The fade trails the slide, so a body is already moving before it starts to arrive. */
    internal val fadeInLate: EnterTransition = fadeIn(
        animationSpec = tween(
            durationMillis = DEFAULT_PUSH_TRANSITION_TIME_MS / HALF,
            delayMillis = DEFAULT_PUSH_TRANSITION_TIME_MS / HALF,
        ),
    )

    internal val fadeOutLate: ExitTransition = fadeOut(
        animationSpec = tween(
            durationMillis = DEFAULT_PUSH_TRANSITION_TIME_MS / HALF,
            delayMillis = EXIT_DELAY_MS,
        ),
    )

    /** Entering from the trailing edge, the way a screen pushed onto the stack arrives. */
    val enterPushingLeft: EnterTransition = slideInHorizontally(
        animationSpec = slideInSpec,
        initialOffsetX = { fullWidth -> halfTravel(fullWidth) },
    ) + fadeInLate

    /** …and from the leading edge, the way the screen underneath is uncovered on the way back. */
    val enterPushingRight: EnterTransition = slideInHorizontally(
        animationSpec = slideInSpec,
        initialOffsetX = { fullWidth -> -halfTravel(fullWidth) },
    ) + fadeInLate

    val exitPushingLeft: ExitTransition = slideOutHorizontally(
        animationSpec = slideOutSpec,
        targetOffsetX = { fullWidth -> -halfTravel(fullWidth) },
    ) + fadeOutLate

    val exitPushingRight: ExitTransition = slideOutHorizontally(
        animationSpec = slideOutSpec,
        targetOffsetX = { fullWidth -> halfTravel(fullWidth) },
    ) + fadeOutLate
}

/**
 * The crossfade every route in the nav graph changes screens with, as plain transitions — for the
 * same reason [PushTransition] exists: a page that swaps in a body the person reads as a screen of
 * its own should arrive the way the app's screens do, and read the motion from here rather than
 * keep a copy of it.
 */
object FadeTransition {
    val enter: EnterTransition = fadeIn(tween(DEFAULT_FADE_TRANSITION_TIME_MS))
    val exit: ExitTransition = fadeOut(tween(DEFAULT_FADE_TRANSITION_TIME_MS))
}

/** One body replacing another the way the nav graph replaces one screen with the next. */
fun navigationFade(): ContentTransform =
    FadeTransition.enter togetherWith FadeTransition.exit

/**
 * One body replacing another inside a page — a list and the form that takes its place, a wizard's
 * next step, a row and its detail.
 *
 * Same 350ms, same half-container travel, same trailing fade as the nav push above, so stepping
 * *into* a page and stepping *within* one read as the same gesture. Unlike the providers above it
 * is direction-aware: `slideIntoContainer` resolves `Start` against the reading direction, so on a
 * Persian page the new body arrives from the left instead of being mirrored into the wrong edge.
 * The nav push is still physical — the two agree under LTR, and correcting nav is a change for
 * every screen at once, not one to smuggle in beside a page's own animation.
 */
fun <S> AnimatedContentTransitionScope<S>.pushForward(): ContentTransform = pushTowards(
    AnimatedContentTransitionScope.SlideDirection.Start,
)

/** …and the way back out of it. */
fun <S> AnimatedContentTransitionScope<S>.pushBack(): ContentTransform = pushTowards(
    AnimatedContentTransitionScope.SlideDirection.End,
)

private fun <S> AnimatedContentTransitionScope<S>.pushTowards(
    direction: AnimatedContentTransitionScope.SlideDirection,
): ContentTransform =
    slideIntoContainer(
        towards = direction,
        animationSpec = PushTransition.slideInSpec,
        initialOffset = PushTransition.halfTravel,
    ) + PushTransition.fadeInLate togetherWith
        slideOutOfContainer(
            towards = direction,
            animationSpec = PushTransition.slideOutSpec,
            targetOffset = PushTransition.halfTravel,
        ) + PushTransition.fadeOutLate

object RootTransitionProviders {
    object Enter {
        val fadeIn: NonNullEnterTransitionProvider = { FadeTransition.enter }
        val none: NonNullEnterTransitionProvider = {
            EnterTransition.None
        }
        val pushLeft: NonNullEnterTransitionProvider = { PushTransition.enterPushingLeft }
        val pushRight: NonNullEnterTransitionProvider = { PushTransition.enterPushingRight }
        val slideUp: NonNullEnterTransitionProvider = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(DEFAULT_SLIDE_TRANSITION_TIME_MS),
            )
        }
        val stay: NonNullEnterTransitionProvider = {
            fadeIn(
                animationSpec = tween(DEFAULT_STAY_TRANSITION_TIME_MS),
                initialAlpha = 1f,
            )
        }
    }
    object Exit {
        val fadeOut: NonNullExitTransitionProvider = { FadeTransition.exit }
        val none: NonNullExitTransitionProvider = {
            ExitTransition.None
        }
        val pushLeft: NonNullExitTransitionProvider = { PushTransition.exitPushingLeft }
        val pushRight: NonNullExitTransitionProvider = { PushTransition.exitPushingRight }
        val slideDown: NonNullExitTransitionProvider = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(DEFAULT_SLIDE_TRANSITION_TIME_MS),
            )
        }
        val stay: NonNullExitTransitionProvider = {
            fadeOut(
                animationSpec = tween(DEFAULT_STAY_TRANSITION_TIME_MS),
                targetAlpha = 0.99f,
            )
        }
    }
}

