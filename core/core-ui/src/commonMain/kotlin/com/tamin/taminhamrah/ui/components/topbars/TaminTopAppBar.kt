package com.tamin.taminhamrah.ui.components.topbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * A custom top bar component that aligns title to the exact middle,
 * navigation icon to the start (left side), and action icon to the end (right side).
 * Even if any of the components are null, the alignment and spacing of the other
 * elements remain completely unchanged.
 */
@Composable
fun TaminTopAppBar(
    title: @Composable (() -> Unit)? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actionIcon: @Composable (() -> Unit)? = null,
    onNavigationClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LocalTaminColors.current.bgSurface,
    contentColor: Color = LocalTaminColors.current.textPrimary
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        color = backgroundColor,
        contentColor = contentColor,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp)
        ) {
            // Navigation Icon (Start / Left in LTR)
            navigationIcon?.let { iconCompos ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .then(
                            if (onNavigationClick != null) {
                                Modifier.clickable(onClick = onNavigationClick)
                            } else Modifier
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    iconCompos()
                }
            }

            // Title (Middle)
            title?.let { titleCompos ->
                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    titleCompos()
                }
            }

            // Action Icon (End / Right in LTR)
            actionIcon?.let { actionCompos ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .then(
                            if (onActionClick != null) {
                                Modifier.clickable(onClick = onActionClick)
                            } else Modifier
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    actionCompos()
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarTitleOnlyPreview() {
    PreviewRtlThemeContent {
        TaminTopAppBar(
            title = {
                Text(
                    text = "فقط عنوان در وسط",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarTitleAndNavigationPreview() {
    PreviewRtlThemeContent {
        TaminTopAppBar(
            title = {
                Text(
                    text = "عنوان و دکمه بازگشت",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت"
                )
            },
            onNavigationClick = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarTitleAndActionPreview() {
    PreviewRtlThemeContent {
        TaminTopAppBar(
            title = {
                Text(
                    text = "عنوان و دکمه بیشتر",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            actionIcon = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "بیشتر"
                )
            },
            onActionClick = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarFullPreview() {
    PreviewRtlThemeContent {
        TaminTopAppBar(
            title = {
                Text(
                    text = "طراحی کامل نوار بالا",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت"
                )
            },
            actionIcon = {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "بیشتر"
                )
            },
            onNavigationClick = {},
            onActionClick = {}
        )
    }
}
