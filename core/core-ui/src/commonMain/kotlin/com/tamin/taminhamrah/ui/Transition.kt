/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
object RootTransitionProviders {
    object Enter {
        val fadeIn: NonNullEnterTransitionProvider = {
            fadeIn(tween(DEFAULT_FADE_TRANSITION_TIME_MS))
        }
        val none: NonNullEnterTransitionProvider = {
            EnterTransition.None
        }
        val pushLeft: NonNullEnterTransitionProvider = {
            val totalTransitionDurationMs = DEFAULT_PUSH_TRANSITION_TIME_MS
            slideInHorizontally(
                animationSpec = tween(durationMillis = totalTransitionDurationMs),
                initialOffsetX = { fullWidth -> fullWidth / 2 },
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = totalTransitionDurationMs / 2,
                    delayMillis = totalTransitionDurationMs / 2,
                ),
            )
        }
        val pushRight: NonNullEnterTransitionProvider = {
            val totalTransitionDurationMs = DEFAULT_PUSH_TRANSITION_TIME_MS
            slideInHorizontally(
                animationSpec = tween(durationMillis = totalTransitionDurationMs),
                initialOffsetX = { fullWidth -> -fullWidth / 2 },
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = totalTransitionDurationMs / 2,
                    delayMillis = totalTransitionDurationMs / 2,
                ),
            )
        }
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
        val fadeOut: NonNullExitTransitionProvider = {
            fadeOut(tween(DEFAULT_FADE_TRANSITION_TIME_MS))
        }
        val none: NonNullExitTransitionProvider = {
            ExitTransition.None
        }
        val pushLeft: NonNullExitTransitionProvider = {
            val totalTransitionDurationMs = DEFAULT_PUSH_TRANSITION_TIME_MS
            val delayMs = totalTransitionDurationMs / 7
            val slideWithoutDelayMs = totalTransitionDurationMs - delayMs
            slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = slideWithoutDelayMs,
                    delayMillis = delayMs,
                ),
                targetOffsetX = { fullWidth -> -fullWidth / 2 },
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = totalTransitionDurationMs / 2,
                    delayMillis = delayMs,
                ),
            )
        }
        val pushRight: NonNullExitTransitionProvider = {
            val totalTransitionDurationMs = DEFAULT_PUSH_TRANSITION_TIME_MS
            val delayMs = totalTransitionDurationMs / 7
            val slideWithoutDelayMs = totalTransitionDurationMs - delayMs
            slideOutHorizontally(
                animationSpec = tween(
                    durationMillis = slideWithoutDelayMs,
                    delayMillis = delayMs,
                ),
                targetOffsetX = { fullWidth -> fullWidth / 2 },
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = totalTransitionDurationMs / 2,
                    delayMillis = delayMs,
                ),
            )
        }
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

