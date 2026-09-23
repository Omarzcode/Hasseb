package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import com.example.data.model.UserSettingsEntity
import com.example.data.repository.MuhasabahRepository
import com.example.domain.engine.AdaptiveReminderEngine
import com.example.domain.engine.AdaptiveReminderInfo
import com.example.domain.engine.InsightCardModel
import com.example.domain.engine.InsightsEngine
import com.example.domain.engine.MonthlyStats
import com.example.domain.engine.StatisticsEngine
import com.example.domain.engine.WeeklyStats
import com.example.domain.engine.YearlyStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class AppNavigationItem(val title: String) {
    CHECK_IN("Check-In"),
    INSIGHTS("Insights"),
    DASHBOARD("Dashboard"),
    SETTINGS("Settings")
}

data class MuhasabahUiState(
    val isLoading: Boolean = true,
    val selectedTab: AppNavigationItem = AppNavigationItem.CHECK_IN,
    val selectedDate: LocalDate = LocalDate.now(),
    val isToday: Boolean = true,
    val hasCompletedOnboarding: Boolean = false,
    val selectedTier: Int = 1,
    val habits: List<HabitEntity> = emptyList(),
    val allHabits: List<HabitEntity> = emptyList(),
    val logsForDate: Map<String, HabitLogEntity> = emptyMap(),
    val allLogs: List<HabitLogEntity> = emptyList(),
    val reminderInfo: AdaptiveReminderInfo = AdaptiveReminderInfo("9:30 PM", 1290, 0, false, ""),
    val insights: List<InsightCardModel> = emptyList(),
    val weeklyStats: WeeklyStats? = null,
    val monthlyStats: MonthlyStats? = null,
    val yearlyStats: YearlyStats? = null,
    val userSettings: UserSettingsEntity = UserSettingsEntity()
)

private data class HabitAndLogData(
    val activeHabits: List<HabitEntity>,
    val allHabits: List<HabitEntity>,
    val logsForDate: List<HabitLogEntity>,
    val allLogs: List<HabitLogEntity>,
    val settings: UserSettingsEntity
)

class MuhasabahViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = MuhasabahRepository(
        habitDao = database.habitDao(),
        habitLogDao = database.habitLogDao(),
        userSettingsDao = database.userSettingsDao()
    )

    private val _selectedTab = MutableStateFlow(AppNavigationItem.CHECK_IN)
    val selectedTab: StateFlow<AppNavigationItem> = _selectedTab.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val logsForSelectedDateFlow = _selectedDate.flatMapLatest { date ->
        val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        repository.getLogsForDate(dateStr)
    }

    private val habitAndLogDataFlow = combine(
        repository.activeHabits,
        repository.allHabits,
        logsForSelectedDateFlow,
        repository.allLogs,
        repository.settings
    ) { activeHabits, allHabits, logsForDateList, allLogsList, settings ->
        HabitAndLogData(
            activeHabits = activeHabits,
            allHabits = allHabits,
            logsForDate = logsForDateList,
            allLogs = allLogsList,
            settings = settings ?: UserSettingsEntity()
        )
    }

    val uiState: StateFlow<MuhasabahUiState> = combine(
        habitAndLogDataFlow,
        _selectedTab,
        _selectedDate
    ) { data, tab, date ->
        val userSettings = data.settings
        val isToday = date == LocalDate.now()
        val logsMap = data.logsForDate.associateBy { it.habitId }

        val reminderInfo = AdaptiveReminderEngine.computeReminderTime(
            logs = data.allLogs,
            isAdaptiveEnabled = userSettings.adaptiveReminderEnabled,
            manualMinutes = userSettings.manualReminderMinutes
        )

        val insights = InsightsEngine.generateInsights(
            habits = data.allHabits,
            allLogs = data.allLogs
        )

        val weeklyStats = StatisticsEngine.computeWeeklyStats(
            habits = data.allHabits,
            allLogs = data.allLogs
        )

        val monthlyStats = StatisticsEngine.computeMonthlyStats(
            habits = data.allHabits,
            allLogs = data.allLogs
        )

        val yearlyStats = StatisticsEngine.computeYearlyStats(
            habits = data.allHabits,
            allLogs = data.allLogs
        )

        MuhasabahUiState(
            isLoading = false,
            selectedTab = tab,
            selectedDate = date,
            isToday = isToday,
            hasCompletedOnboarding = userSettings.hasCompletedOnboarding,
            selectedTier = userSettings.selectedTier,
            habits = data.activeHabits,
            allHabits = data.allHabits,
            logsForDate = logsMap,
            allLogs = data.allLogs,
            reminderInfo = reminderInfo,
            insights = insights,
            weeklyStats = weeklyStats,
            monthlyStats = monthlyStats,
            yearlyStats = yearlyStats,
            userSettings = userSettings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MuhasabahUiState()
    )

    fun selectTab(tab: AppNavigationItem) {
        _selectedTab.value = tab
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun navigateDateBy(days: Long) {
        _selectedDate.value = _selectedDate.value.plusDays(days)
    }

    fun completeOnboarding(tier: Int) {
        viewModelScope.launch {
            repository.initializeWithTier(tier)
            _selectedTab.value = AppNavigationItem.CHECK_IN
        }
    }

    fun setPrayerStatus(habitId: String, status: LogStatus) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val isCompleted = status == LogStatus.ON_TIME || status == LogStatus.LATE
            repository.logHabit(
                date = dateStr,
                habitId = habitId,
                status = status,
                completed = isCompleted
            )
        }
    }

    fun toggleHabitDone(habitId: String, isDone: Boolean) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val status = if (isDone) LogStatus.DONE else LogStatus.NOT_DONE
            repository.logHabit(
                date = dateStr,
                habitId = habitId,
                status = status,
                completed = isDone
            )
        }
    }

    fun updateTier(newTier: Int) {
        viewModelScope.launch {
            repository.updateTier(newTier)
            _userMessage.value = "Updated to Stage $newTier"
        }
    }

    fun addCustomHabit(name: String, arabicName: String?, category: HabitCategory, description: String = "") {
        viewModelScope.launch {
            repository.addCustomHabit(name, arabicName, category, description)
            _userMessage.value = "Added \"$name\" to your wirds"
        }
    }

    fun toggleHabitPause(habitId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleHabitActiveState(habitId, isActive)
            _userMessage.value = if (isActive) "Habit resumed" else "Habit paused"
        }
    }

    fun deleteCustomHabit(habitId: String) {
        viewModelScope.launch {
            repository.deleteCustomHabit(habitId)
            _userMessage.value = "Habit removed"
        }
    }

    fun updateReminderSettings(adaptiveEnabled: Boolean, manualMinutes: Int) {
        viewModelScope.launch {
            repository.updateReminderSettings(adaptiveEnabled, manualMinutes)
            _userMessage.value = "Reminder preference updated"
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _userMessage.value = "All data cleared"
        }
    }

    fun populateSampleHistory() {
        viewModelScope.launch {
            repository.populateSampleHistory()
            _userMessage.value = "Sample history loaded"
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
