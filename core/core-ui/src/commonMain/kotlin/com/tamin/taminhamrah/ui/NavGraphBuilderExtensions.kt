/*
 * author Javid Sattar *(javiddeveloper@gmail.com)
 */
package com.tamin.taminhamrah.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType


inline fun <reified T : Any> NavGraphBuilder.composableWithSlideTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    this.composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = TransitionProviders.Enter.slideUp,
        exitTransition = TransitionProviders.Exit.stay,
        popEnterTransition = TransitionProviders.Enter.stay,
        popExitTransition = TransitionProviders.Exit.slideDown,
        sizeTransform = null,
        content = content,
    )
}

inline fun <reified T : Any> NavGraphBuilder.composableWithStayTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    this.composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = TransitionProviders.Enter.stay,
        exitTransition = TransitionProviders.Exit.stay,
        popEnterTransition = TransitionProviders.Enter.stay,
        popExitTransition = TransitionProviders.Exit.stay,
        sizeTransform = null,
        content = content,
    )
}

inline fun <reified T : Any> NavGraphBuilder.composableWithFadeTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    this.composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = RootTransitionProviders.Enter.fadeIn,
        exitTransition = RootTransitionProviders.Exit.fadeOut,
        popEnterTransition = RootTransitionProviders.Enter.fadeIn,
        popExitTransition = RootTransitionProviders.Exit.fadeOut,
        sizeTransform = null,
        content = content,
    )
}

inline fun <reified T : Any> NavGraphBuilder.composableWithPushTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    this.composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = TransitionProviders.Enter.pushLeft,
        exitTransition = TransitionProviders.Exit.stay,
        popEnterTransition = TransitionProviders.Enter.stay,
        popExitTransition = TransitionProviders.Exit.pushRight,
        sizeTransform = null,
        content = content,
    )
}

inline fun <reified T : Any> NavGraphBuilder.composableWithRootPushTransitions(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    this.composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = TransitionProviders.Enter.stay,
        exitTransition = TransitionProviders.Exit.pushLeft,
        popEnterTransition = TransitionProviders.Enter.pushRight,
        popExitTransition = TransitionProviders.Exit.fadeOut,
        sizeTransform = null,
        content = content,
    )
}

