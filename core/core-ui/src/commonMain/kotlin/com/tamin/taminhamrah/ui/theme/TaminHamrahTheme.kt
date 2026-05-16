package com.tamin.taminhamrah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80, // رنگ اصلی در حالت تاریک
    onPrimary = Pink80, // رنگ متن و عناصر روی رنگ اصلی
    secondary = PurpleGrey80, // رنگ ثانویه برای بخش‌های کم‌اهمیت‌تر
    onSecondary = Pink80, // رنگ متن روی رنگ ثانویه
    tertiary = Pink80, // رنگ سوم برای تاکید و تنوع
    onTertiary = Pink80, // رنگ متن روی رنگ سوم
    background = Pink80, // رنگ پس‌زمینه اصلی صفحات
    onBackground = Pink80, // رنگ متن روی پس‌زمینه اصلی
    surface = Pink80, // رنگ سطوح (مانند کارت‌ها و دیالوگ‌ها)
    onSurface = Pink80, // رنگ متن روی سطوح
    error = Pink80, // رنگ نمایش خطاها
    onError = Pink80, // رنگ متن روی رنگ خطا
    outline = Pink80 // رنگ خطوط دور و جداکننده‌ها
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40, // رنگ اصلی در حالت روشن
    onPrimary = Color.White, // رنگ متن و عناصر روی رنگ اصلی
    secondary = PurpleGrey40, // رنگ ثانویه
    onSecondary = Color.White, // رنگ متن روی رنگ ثانویه
    tertiary = Pink40, // رنگ سوم
    onTertiary = Color.White, // رنگ متن روی رنگ سوم
    background = Pink80, // رنگ پس‌زمینه اصلی صفحات
    onBackground = Pink80, // رنگ متن روی پس‌زمینه اصلی
    surface = Pink80, // رنگ سطوح (مانند کارت‌ها)
    onSurface = Pink80, // رنگ متن روی سطوح
    error = Pink80, // رنگ نمایش خطاها
    onError = Color.White, // رنگ متن روی رنگ خطا
    outline = Pink80 // رنگ خطوط دور و جداکننده‌ها
)

@Composable
fun TaminHamrahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = taminHamrahTypography(),
        content = content
    )
}
