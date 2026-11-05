package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.preferences.AppThemeMode
import com.example.ui.PastoralViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopHeader
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.agenda.AgendaScreen
import com.example.ui.screens.archives.ArchiveScreen
import com.example.ui.screens.events.EventDetailEditScreen
import com.example.ui.screens.events.EventListScreen
import com.example.ui.screens.finances.FinanceScreen
import com.example.ui.screens.lock.PinLockScreen
import com.example.ui.screens.meetings.MeetingDetailEditScreen
import com.example.ui.screens.meetings.MeetingListScreen
import com.example.ui.screens.sermons.SermonDetailEditScreen
import com.example.ui.screens.sermons.SermonListScreen
import com.example.ui.screens.settings.AboutScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PastoralViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemDark
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: PastoralViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()
    val isPinEnabled by viewModel.isPinEnabled.collectAsStateWithLifecycle()
    val strings by viewModel.strings.collectAsStateWithLifecycle()

    if (isPinEnabled && !isUnlocked) {
        PinLockScreen(
            viewModel = viewModel,
            strings = strings
        )
        return
    }

    // Handle Back Press for custom state navigation
    BackHandler(enabled = currentScreen != ScreenDestination.HOME) {
        viewModel.navigateBack()
    }

    val (screenTitle, screenSubtitle) = when (currentScreen) {
        ScreenDestination.HOME -> "RINDRANDAMIN'NY MPITANDRINA" to strings.welcomePastor
        ScreenDestination.SERMONS -> strings.navSermons to "Fanomanana sy firaketana ny toriteny"
        ScreenDestination.SERMON_DETAIL -> "Fanomanana Toriteny" to "Drafitra sy exégèse"
        ScreenDestination.MEETINGS -> strings.navMeetings to "Biraom-piangonana sy komity"
        ScreenDestination.MEETING_DETAIL -> "Fivoriana sy Tatitra" to "Procès-verbal"
        ScreenDestination.EVENTS -> strings.navEvents to "Fandaminana ny hetsika pastoraly"
        ScreenDestination.EVENT_DETAIL -> "Antsipirian'ny hetsika" to "Fandaharana sy tetibola"
        ScreenDestination.AGENDA -> strings.navAgenda to "Asa sy fotoana voalahatra"
        ScreenDestination.ADMIN -> strings.navAdmin to "Birao, tompon'andraikitra sy taratasy"
        ScreenDestination.FINANCES -> strings.navFinances to "Bilan, teti-bola sy fanatitra"
        ScreenDestination.ARCHIVES -> strings.navArchives to "Fitahirizana sy fikarohana"
        ScreenDestination.SETTINGS -> strings.navSettings to "Fiteny, fisehoana sy tahiry"
        ScreenDestination.ABOUT -> strings.aboutTitle to "Solohery RAKOTOARISOA"
    }

    val isTopLevelScreen = currentScreen == ScreenDestination.HOME ||
            currentScreen == ScreenDestination.SERMONS ||
            currentScreen == ScreenDestination.MEETINGS ||
            currentScreen == ScreenDestination.AGENDA ||
            currentScreen == ScreenDestination.ARCHIVES

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopHeader(
                title = screenTitle,
                subtitle = screenSubtitle,
                showBack = !isTopLevelScreen,
                onBack = { viewModel.navigateBack() },
                showLock = isPinEnabled,
                onLock = { viewModel.lockApp() },
                onAboutClick = { viewModel.navigateTo(ScreenDestination.ABOUT) },
                onSettingsClick = { viewModel.navigateTo(ScreenDestination.SETTINGS) }
            )
        },
        bottomBar = {
            if (isTopLevelScreen) {
                BottomNavBar(
                    currentScreen = currentScreen,
                    strings = strings,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenDestination.HOME -> HomeScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                ScreenDestination.SERMONS -> SermonListScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                ScreenDestination.SERMON_DETAIL -> SermonDetailEditScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenDestination.MEETINGS -> MeetingListScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                ScreenDestination.MEETING_DETAIL -> MeetingDetailEditScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenDestination.EVENTS -> EventListScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                ScreenDestination.EVENT_DETAIL -> EventDetailEditScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onBack = { viewModel.navigateBack() }
                )
                ScreenDestination.AGENDA -> AgendaScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                ScreenDestination.ADMIN -> AdminScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                ScreenDestination.FINANCES -> FinanceScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                ScreenDestination.ARCHIVES -> ArchiveScreen(
                    viewModel = viewModel,
                    strings = strings
                )
                ScreenDestination.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    strings = strings,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                ScreenDestination.ABOUT -> AboutScreen(
                    strings = strings,
                    onBack = { viewModel.navigateBack() }
                )
            }
        }
    }
}
