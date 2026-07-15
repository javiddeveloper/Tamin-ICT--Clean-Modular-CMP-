package com.tamin.taminhamrah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
expect fun applicationFont(): FontFamily

@Composable
fun taminHamrahTypography(): Typography {
    val fontFamily = applicationFont()
    return Typography(
        // displayLarge = TextStyle(
        //     fontFamily = fontFamily,
        //     fontWeight = FontWeight.Bold,
        //     fontSize = 57.sp,
        //     lineHeight = 64.sp,
        //     letterSpacing = (-0.25).sp
        // ),
        // displayMedium = TextStyle(
        //     fontFamily = fontFamily,
        //     fontWeight = FontWeight.Bold,
        //     fontSize = 45.sp,
        //     lineHeight = 52.sp
        // ),
        // displaySmall = TextStyle(
        //     fontFamily = fontFamily,
        //     fontWeight = FontWeight.Bold,
        //     fontSize = 36.sp,
        //     lineHeight = 44.sp
        // ),
        // headlineLarge = TextStyle(
        //     fontFamily = fontFamily,
        //     fontWeight = FontWeight.Bold,
        //     fontSize = 32.sp,
        //     lineHeight = 40.sp
        // ),
         headlineMedium = TextStyle(
             fontFamily = fontFamily,
             fontWeight = FontWeight.Bold,
             fontSize = 28.sp,
             lineHeight = 36.sp
         ),
        // headlineSmall = TextStyle(
        //     fontFamily = fontFamily,
        //     fontWeight = FontWeight.Bold,
        //     fontSize = 24.sp,
        //     lineHeight = 32.sp
        // ),

        // "حساب کاربری" — screen/app-bar title, 18sp / weight 800
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(800),
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),

        // "نام نویسی شده" — verification card title, 14.5sp / weight 700
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 14.5.sp,
            lineHeight = 20.sp
        ),

        // "اطلاعات هویتی" — settings/profile list item title, 14.5sp / weight 600
        titleSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 14.5.sp,
            lineHeight = 20.sp
        ),

        // bodyLarge = TextStyle(
        //     fontFamily = fontFamily,
        //     fontWeight = FontWeight.Normal,
        //     fontSize = 16.sp,
        //     lineHeight = 24.sp,
        //     letterSpacing = 0.5.sp
        // ),

        // "کد ملی: ..." — secondary account info under title, 13sp / weight 400
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),

        // "حساب شما تأیید و فعال است" — descriptive/subtitle text, 12sp / weight 400
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),

        // "اطلاعات شخصی" — section header label, 13sp / weight 700
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 13.sp,
            lineHeight = 18.sp
        ),

        // "معتبر" — status badge text, 11.5sp / weight 600
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(600),
            fontSize = 11.5.sp,
            lineHeight = 16.sp
        ),

        // "پروفایل" — bottom navigation label, 11sp / weight 700
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 11.sp,
            lineHeight = 14.sp
        )

    )
}
