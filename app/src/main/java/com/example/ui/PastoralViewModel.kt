package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AdminDocument
import com.example.data.model.AdminMember
import com.example.data.model.ChurchEvent
import com.example.data.model.FinanceRecord
import com.example.data.model.Meeting
import com.example.data.model.PastoralTask
import com.example.data.model.Sermon
import com.example.data.preferences.AppPreferences
import com.example.data.preferences.AppThemeMode
import com.example.data.repository.PastoralRepository
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.i18n.Translations
import com.example.util.BackupManager
import com.example.util.NotificationHelper
import com.example.util.PdfExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
    HOME,
    SERMONS,
    SERMON_DETAIL,
    MEETINGS,
    MEETING_DETAIL,
    EVENTS,
    EVENT_DETAIL,
    AGENDA,
    ADMIN,
    FINANCES,
    ARCHIVES,
    SETTINGS,
    ABOUT
}

class PastoralViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = PastoralRepository(database)
    val preferences = AppPreferences(application)

    // Current navigation state
    private val _currentScreen = MutableStateFlow(ScreenDestination.HOME)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Screen navigation stack
    private val screenStack = mutableListOf(ScreenDestination.HOME)

    // Selected item for detail/editing
    val selectedSermon = MutableStateFlow<Sermon?>(null)
    val selectedMeeting = MutableStateFlow<Meeting?>(null)
    val selectedEvent = MutableStateFlow<ChurchEvent?>(null)

    // Reactive Data Flows
    val allSermons = repository.allSermons.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allMeetings = repository.allMeetings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allEvents = repository.allEvents.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allTasks = repository.allTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val urgentTasks = repository.urgentTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allMembers = repository.allMembers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allDocuments = repository.allDocuments.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allFinances = repository.allFinances.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // App language and active translation strings
    val language: StateFlow<AppLanguage> = preferences.language
    val themeMode: StateFlow<AppThemeMode> = preferences.themeMode
    val currency: StateFlow<String> = preferences.currency
    val isPinEnabled: StateFlow<Boolean> = preferences.isPinEnabled

    val strings: StateFlow<AppStrings> = combine(language) { (lang) ->
        Translations.getStrings(lang)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Translations.getStrings(preferences.language.value)
    )

    // Security Unlock State
    private val _isUnlocked = MutableStateFlow(!preferences.isPinEnabled.value)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    fun unlockWithPin(pin: String): Boolean {
        if (preferences.checkPin(pin)) {
            _isUnlocked.value = true
            return true
        }
        return false
    }

    fun lockApp() {
        if (preferences.isPinEnabled.value) {
            _isUnlocked.value = false
        }
    }

    // Navigation
    fun navigateTo(screen: ScreenDestination) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            val previous = screenStack.last()
            _currentScreen.value = previous
            return true
        }
        return false
    }

    // Sermon Actions
    fun openSermon(sermon: Sermon?) {
        selectedSermon.value = sermon
        navigateTo(ScreenDestination.SERMON_DETAIL)
    }

    fun saveSermon(sermon: Sermon) {
        viewModelScope.launch {
            if (sermon.id == 0L) {
                repository.insertSermon(sermon)
            } else {
                repository.updateSermon(sermon.copy(updatedAt = System.currentTimeMillis()))
            }
            navigateBack()
        }
    }

    fun deleteSermon(sermon: Sermon) {
        viewModelScope.launch {
            repository.deleteSermon(sermon)
            if (_currentScreen.value == ScreenDestination.SERMON_DETAIL) {
                navigateBack()
            }
        }
    }

    fun exportSermonPdf(sermon: Sermon) {
        PdfExporter.exportSermonPdf(getApplication(), sermon)
    }

    // Meeting Actions
    fun openMeeting(meeting: Meeting?) {
        selectedMeeting.value = meeting
        navigateTo(ScreenDestination.MEETING_DETAIL)
    }

    fun saveMeeting(meeting: Meeting) {
        viewModelScope.launch {
            if (meeting.id == 0L) {
                repository.insertMeeting(meeting)
            } else {
                repository.updateMeeting(meeting)
            }
            navigateBack()
        }
    }

    fun deleteMeeting(meeting: Meeting) {
        viewModelScope.launch {
            repository.deleteMeeting(meeting)
            if (_currentScreen.value == ScreenDestination.MEETING_DETAIL) {
                navigateBack()
            }
        }
    }

    fun exportMeetingMinutesPdf(meeting: Meeting) {
        PdfExporter.exportMeetingMinutesPdf(getApplication(), meeting)
    }

    // Event Actions
    fun openEvent(event: ChurchEvent?) {
        selectedEvent.value = event
        navigateTo(ScreenDestination.EVENT_DETAIL)
    }

    fun saveEvent(event: ChurchEvent) {
        viewModelScope.launch {
            if (event.id == 0L) {
                repository.insertEvent(event)
            } else {
                repository.updateEvent(event)
            }
            navigateBack()
        }
    }

    fun deleteEvent(event: ChurchEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            if (_currentScreen.value == ScreenDestination.EVENT_DETAIL) {
                navigateBack()
            }
        }
    }

    // Task Actions
    fun saveTask(task: PastoralTask) {
        viewModelScope.launch {
            val id = if (task.id == 0L) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
                task.id
            }

            if (task.reminderSet && task.reminderTimeMillis != null) {
                NotificationHelper.scheduleReminder(
                    getApplication(),
                    id,
                    task.title,
                    task.description.ifBlank { "Fampahatsiahivana ny asa pastoraly" },
                    task.reminderTimeMillis
                )
            } else if (!task.reminderSet) {
                NotificationHelper.cancelReminder(getApplication(), id)
            }
        }
    }

    fun toggleTaskCompleted(task: PastoralTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: PastoralTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            NotificationHelper.cancelReminder(getApplication(), task.id)
        }
    }

    // Admin Member Actions
    fun saveMember(member: AdminMember) {
        viewModelScope.launch {
            if (member.id == 0L) {
                repository.insertMember(member)
            } else {
                repository.updateMember(member)
            }
        }
    }

    fun deleteMember(member: AdminMember) {
        viewModelScope.launch {
            repository.deleteMember(member)
        }
    }

    // Admin Document Actions
    fun saveDocument(document: AdminDocument) {
        viewModelScope.launch {
            if (document.id == 0L) {
                repository.insertDocument(document)
            } else {
                repository.updateDocument(document)
            }
        }
    }

    fun deleteDocument(document: AdminDocument) {
        viewModelScope.launch {
            repository.deleteDocument(document)
        }
    }

    // Finance Actions
    fun saveFinance(record: FinanceRecord) {
        viewModelScope.launch {
            if (record.id == 0L) {
                repository.insertFinance(record)
            } else {
                repository.updateFinance(record)
            }
        }
    }

    fun deleteFinance(record: FinanceRecord) {
        viewModelScope.launch {
            repository.deleteFinance(record)
        }
    }

    fun exportFinancesPdf() {
        val list = allFinances.value
        PdfExporter.exportFinancialReportPdf(getApplication(), list, currency.value)
    }

    // Preferences & Backup
    fun setLanguage(lang: AppLanguage) = preferences.setLanguage(lang)
    fun setThemeMode(mode: AppThemeMode) = preferences.setThemeMode(mode)
    fun setCurrency(curr: String) = preferences.setCurrency(curr)
    fun setPinCode(pin: String?) = preferences.setPinCode(pin)

    fun exportBackup(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val file = BackupManager.createBackupJson(getApplication(), database)
                BackupManager.shareBackup(getApplication(), file)
                onResult(true, file.name)
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Hadisoana")
            }
        }
    }

    fun restoreBackup(jsonString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = BackupManager.restoreFromJsonString(jsonString, database)
            onResult(success)
        }
    }
}
