package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.IllDaysCalculateRoute
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.IllDaysIntroRoute
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.IllDaysWizardRoute
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysCalculateScreen
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro.IllDaysIntroScreen
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardScreen

@Composable
fun RequestPaymentForIllDaysNavHost(onExit: () -> Unit) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = IllDaysIntroRoute,
    ) {
        composable<IllDaysIntroRoute> {
            IllDaysIntroScreen(
                onBack = onExit,
                onOpenCalculate = { navController.navigate(IllDaysCalculateRoute) },
                onStartRequest = { navController.navigate(IllDaysWizardRoute) },
            )
        }
        composable<IllDaysCalculateRoute> {
            IllDaysCalculateScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable<IllDaysWizardRoute> {
            IllDaysWizardScreen(
                onBack = { navController.popBackStack() },
                onOpenCalculate = { navController.navigate(IllDaysCalculateRoute) },
            )
        }
    }
}
