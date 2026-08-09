package com.tamin.taminhamrah

import androidx.compose.runtime.Composable

/**
 * Requests notification permission when the user is logged in ([isLoggedIn] = true).
 */
@Composable
expect fun RequestNotificationPermissionOnLogin(isLoggedIn: Boolean)
