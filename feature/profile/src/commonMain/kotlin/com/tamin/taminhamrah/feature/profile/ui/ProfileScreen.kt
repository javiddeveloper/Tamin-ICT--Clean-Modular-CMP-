package com.tamin.taminhamrah.feature.profile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import taminx.core.core_ui.Res
//import taminx.core.core_ui.ic_clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String?,
    onBack: () -> Unit
) {
    val listState = rememberLazyListState()
    Scaffold(
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                state = listState,
            ) {
                item(key = "__displayProfileHeader") {
                    Spacer(modifier = Modifier.requiredSize(15.dp))
                    Text(
                        text = "پروفایل",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(modifier = Modifier.requiredSize(15.dp))
                }
                item(key = "__identityInformationItem") {
                    UserAccountItem(
                        icon = null,
                        shape = topListItemShape,
                        headlineText = "اطلاعات هویتی",
                        supportingText = "مشاهده و ویرایش",
                        onItemClicked = {}
                    )
                }
                item(key = "__peopleCoveredItem") {
                    UserAccountItem(
                        icon = null,
                        shape = middleListItemShape,
                        headlineText = "افراد تحت پوشش",
                        supportingText = "",
                        onItemClicked = {}
                    )
                }
                item(key = "__bankAccountItem") {
                    UserAccountItem(
                        icon = null,
                        shape = middleListItemShape,
                        headlineText = "حساب بانکی",
                        supportingText = "",
                        onItemClicked = {}
                    )
                }
                item(key = "__relationsUserItem") {
                    UserAccountItem(
                        icon = null,
                        shape = middleListItemShape,
                        headlineText = "ارتباط با ما",
                        supportingText = "",
                        onItemClicked = {}
                    )
                }
                item(key = "__editMobileNumberClickedItem") {
                    UserAccountItem(
                        icon = null,
                        shape = middleListItemShape,
                        headlineText = "ویرایش شماره موبایل",
                        supportingText = "",
                        onItemClicked = {}
                    )
                }
                item(key = "__settingsClickedItem") {
                    UserAccountItem(
                        icon = null,
                        shape = bottomListItemShape,
                        headlineText = "تنظیمات",
                        supportingText = "",
                        onItemClicked = {}
                    )
                }
                item(key = "__accountHeader") {
                    Spacer(modifier = Modifier.requiredSize(15.dp))
                    Text(
                        text = "حساب کاربری",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(modifier = Modifier.requiredSize(15.dp))
                }
                item(key = "__logoutClickedItem") {
                    UserAccountItem(
                        icon = null,
                        applyTintOnDarkTheme = false,
                        shape = singleListItemShape,
                        headlineText = "خروج",
                        onItemClicked = {}
                    )
                }
            }
        }
    }
}


@Composable
fun UserAccountItem(
    modifier: Modifier = Modifier,
    icon: DrawableResource? = null,
    shape: RoundedCornerShape,
    headlineText: String,
    supportingText: String? = null,
    applyTintOnDarkTheme: Boolean = true,
    onItemClicked: (() -> Unit)?
) {
    ListItem(
        leadingContent = {
            icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = headlineText,
                    modifier = Modifier.requiredSize(16.dp),
                    tint = if (isSystemInDarkTheme() && applyTintOnDarkTheme) Color.White else Color.Unspecified
                )
            }
        }, colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            headlineColor = MaterialTheme.colorScheme.onSurface,
            supportingColor = MaterialTheme.colorScheme.onSurfaceVariant
        ), headlineContent = {
            Text(headlineText)

        }, supportingContent = supportingText?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }, modifier = (onItemClicked?.let {
            modifier
                .clip(shape)
                .clickable(onClick = it)
        } ?: modifier.clip(shape))
    )
}

// تعریف مقادیر پیش‌فرض
val cornerSize = 12.dp

val topListItemShape = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize)
val middleListItemShape = RoundedCornerShape(0.dp) // مستطیل ساده برای وسط لیست
val bottomListItemShape = RoundedCornerShape(bottomStart = cornerSize, bottomEnd = cornerSize)
val singleListItemShape = RoundedCornerShape(cornerSize) // گرد برای هر چهار طرف
