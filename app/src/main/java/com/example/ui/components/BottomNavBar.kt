package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppStrings
import com.example.ui.ScreenDestination
import com.example.ui.theme.GoldBase
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900

@Composable
fun BottomNavBar(
    currentScreen: ScreenDestination,
    strings: AppStrings,
    onNavigate: (ScreenDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        containerColor = Navy900,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == ScreenDestination.HOME,
            onClick = { onNavigate(ScreenDestination.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = strings.navHome,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navHome,
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = GoldBase,
                indicatorColor = GoldBase,
                unselectedIconColor = IvoryWhite.copy(alpha = 0.7f),
                unselectedTextColor = IvoryWhite.copy(alpha = 0.7f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.SERMONS || currentScreen == ScreenDestination.SERMON_DETAIL,
            onClick = { onNavigate(ScreenDestination.SERMONS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = strings.navSermons,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navSermons,
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.SERMONS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = GoldBase,
                indicatorColor = GoldBase,
                unselectedIconColor = IvoryWhite.copy(alpha = 0.7f),
                unselectedTextColor = IvoryWhite.copy(alpha = 0.7f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.MEETINGS || currentScreen == ScreenDestination.MEETING_DETAIL,
            onClick = { onNavigate(ScreenDestination.MEETINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = strings.navMeetings,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navMeetings,
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.MEETINGS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = GoldBase,
                indicatorColor = GoldBase,
                unselectedIconColor = IvoryWhite.copy(alpha = 0.7f),
                unselectedTextColor = IvoryWhite.copy(alpha = 0.7f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.AGENDA,
            onClick = { onNavigate(ScreenDestination.AGENDA) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = strings.navAgenda,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navAgenda,
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.AGENDA) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = GoldBase,
                indicatorColor = GoldBase,
                unselectedIconColor = IvoryWhite.copy(alpha = 0.7f),
                unselectedTextColor = IvoryWhite.copy(alpha = 0.7f)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.ARCHIVES,
            onClick = { onNavigate(ScreenDestination.ARCHIVES) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Archive,
                    contentDescription = strings.navArchives,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navArchives,
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.ARCHIVES) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = GoldBase,
                indicatorColor = GoldBase,
                unselectedIconColor = IvoryWhite.copy(alpha = 0.7f),
                unselectedTextColor = IvoryWhite.copy(alpha = 0.7f)
            )
        )
    }
}
