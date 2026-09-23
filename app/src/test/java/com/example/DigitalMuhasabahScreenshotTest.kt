package com.example

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.DefaultHabits
import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import com.example.data.model.UserSettingsEntity
import com.example.domain.engine.AdaptiveReminderInfo
import com.example.domain.engine.InsightsEngine
import com.example.domain.engine.StatisticsEngine
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppNavigationItem
import com.example.ui.viewmodel.MuhasabahUiState
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class DigitalMuhasabahScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createSampleData(): Triple<List<HabitEntity>, Map<String, HabitLogEntity>, List<HabitLogEntity>> {
        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        val coreHabits = DefaultHabits.getCoreHabitsForTier(1)
        val stage2Habits = listOf(
            HabitEntity(
                id = "prayer_rawatib",
                name = "Sunnah Rawatib",
                arabicName = "السنن الرواتب",
                description = "12 Sunnah rak'ahs daily",
                category = HabitCategory.PRAYER_SUNNAH.name,
                tier = 2,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 6
            ),
            HabitEntity(
                id = "prayer_witr",
                name = "Witr Prayer",
                arabicName = "صلاة الوتر",
                description = "Night closure prayer",
                category = HabitCategory.PRAYER_SUNNAH.name,
                tier = 2,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 7
            ),
            HabitEntity(
                id = "adhkar_morning",
                name = "Morning Adhkar",
                arabicName = "أذكار الصباح",
                description = "Dawn remembrance",
                category = HabitCategory.ADHKAR.name,
                tier = 3,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 8
            ),
            HabitEntity(
                id = "quran_daily",
                name = "Quran Recitation",
                arabicName = "الورد القرآني",
                description = "Daily 1 Hizb or 4 pages",
                category = HabitCategory.QURAN.name,
                tier = 3,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 9
            ),
            HabitEntity(
                id = "custom_duha",
                name = "Duha Prayer",
                arabicName = "صلاة الضحى",
                description = "Mid-morning prayer",
                category = HabitCategory.PRAYER_SUNNAH.name,
                tier = 0,
                isCore = false,
                isPrayer3State = false,
                isActive = true,
                orderIndex = 10
            )
        )

        val allHabits = coreHabits + stage2Habits

        val sampleLogs = mutableListOf<HabitLogEntity>()
        for (i in 30 downTo 0) {
            val dateObj = today.minusDays(i.toLong())
            val dateStr = dateObj.format(DateTimeFormatter.ISO_LOCAL_DATE)

            val prayerIds = listOf("prayer_fajr", "prayer_dhuhr", "prayer_asr", "prayer_maghrib", "prayer_isha")
            for (pId in prayerIds) {
                val status = when {
                    i == 0 && pId == "prayer_fajr" -> LogStatus.ON_TIME
                    i == 0 && pId == "prayer_dhuhr" -> LogStatus.ON_TIME
                    i == 0 && pId == "prayer_asr" -> LogStatus.LATE
                    i == 0 -> LogStatus.UNLOGGED
                    i % 6 == 1 && pId == "prayer_fajr" -> LogStatus.LATE
                    i % 5 == 0 && pId == "prayer_asr" -> LogStatus.LATE
                    else -> LogStatus.ON_TIME
                }
                sampleLogs.add(
                    HabitLogEntity(
                        id = "${dateStr}_$pId",
                        date = dateStr,
                        habitId = pId,
                        status = status.name,
                        completed = status == LogStatus.ON_TIME || status == LogStatus.LATE,
                        loggedAtTimestamp = System.currentTimeMillis() - i * 86400000L
                    )
                )
            }

            // Rawatib
            val rawatibDone = i % 4 != 0
            sampleLogs.add(
                HabitLogEntity(
                    id = "${dateStr}_prayer_rawatib",
                    date = dateStr,
                    habitId = "prayer_rawatib",
                    status = if (rawatibDone) LogStatus.DONE.name else LogStatus.NOT_DONE.name,
                    completed = rawatibDone,
                    loggedAtTimestamp = System.currentTimeMillis() - i * 86400000L
                )
            )

            // Adhkar
            val adhkarDone = i % 3 != 0
            sampleLogs.add(
                HabitLogEntity(
                    id = "${dateStr}_adhkar_morning",
                    date = dateStr,
                    habitId = "adhkar_morning",
                    status = if (adhkarDone) LogStatus.DONE.name else LogStatus.NOT_DONE.name,
                    completed = adhkarDone,
                    loggedAtTimestamp = System.currentTimeMillis() - i * 86400000L
                )
            )

            // Quran
            val quranDone = i % 2 == 0
            sampleLogs.add(
                HabitLogEntity(
                    id = "${dateStr}_quran_daily",
                    date = dateStr,
                    habitId = "quran_daily",
                    status = if (quranDone) LogStatus.DONE.name else LogStatus.NOT_DONE.name,
                    completed = quranDone,
                    loggedAtTimestamp = System.currentTimeMillis() - i * 86400000L
                )
            )
        }

        val logsForToday = sampleLogs.filter { it.date == todayStr }.associateBy { it.habitId }

        return Triple(allHabits, logsForToday, sampleLogs)
    }

    private fun buildUiState(tab: AppNavigationItem): MuhasabahUiState {
        val (habits, logsForToday, allLogs) = createSampleData()
        val weeklyStats = StatisticsEngine.computeWeeklyStats(habits, allLogs)
        val monthlyStats = StatisticsEngine.computeMonthlyStats(habits, allLogs)
        val yearlyStats = StatisticsEngine.computeYearlyStats(habits, allLogs)
        val insights = InsightsEngine.generateInsights(habits, allLogs)
        val reminderInfo = AdaptiveReminderInfo(
            formattedTime = "9:30 PM",
            averageMinutesOfDay = 1290,
            totalCheckInsAnalyzed = 28,
            isCalculatedFromHabits = true,
            explanation = "Calculated from your 28 recorded reflections"
        )

        return MuhasabahUiState(
            isLoading = false,
            selectedTab = tab,
            selectedDate = LocalDate.now(),
            isToday = true,
            hasCompletedOnboarding = true,
            selectedTier = 2,
            habits = habits,
            allHabits = habits,
            logsForDate = logsForToday,
            allLogs = allLogs,
            reminderInfo = reminderInfo,
            insights = insights,
            weeklyStats = weeklyStats,
            monthlyStats = monthlyStats,
            yearlyStats = yearlyStats,
            userSettings = UserSettingsEntity(
                selectedTier = 2,
                hasCompletedOnboarding = true,
                adaptiveReminderEnabled = true,
                manualReminderMinutes = 1290
            )
        )
    }

    @Test
    fun screenshot_01_onboarding() {
        composeTestRule.setContent {
            MyApplicationTheme {
                OnboardingScreen(onSelectTier = {})
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "/app/applet/docs/screenshots/screenshot_onboarding.png")
    }

    @Test
    fun screenshot_02_checkin() {
        val state = buildUiState(AppNavigationItem.CHECK_IN)
        composeTestRule.setContent {
            MyApplicationTheme {
                val snackbar = remember { SnackbarHostState() }
                MainAppScaffold(
                    uiState = state,
                    snackbarHostState = snackbar,
                    onTabSelected = {},
                    onDateNav = {},
                    onDateSelect = {},
                    onSetPrayerStatus = { _, _ -> },
                    onToggleHabitDone = { _, _ -> },
                    onAddCustomHabit = { _, _, _, _ -> },
                    onUpdateTier = {},
                    onToggleHabitPause = { _, _ -> },
                    onDeleteCustomHabit = {},
                    onUpdateReminder = { _, _ -> },
                    onPopulateSampleData = {},
                    onClearAllData = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "/app/applet/docs/screenshots/screenshot_checkin.png")
    }

    @Test
    fun screenshot_03_insights() {
        val state = buildUiState(AppNavigationItem.INSIGHTS)
        composeTestRule.setContent {
            MyApplicationTheme {
                val snackbar = remember { SnackbarHostState() }
                MainAppScaffold(
                    uiState = state,
                    snackbarHostState = snackbar,
                    onTabSelected = {},
                    onDateNav = {},
                    onDateSelect = {},
                    onSetPrayerStatus = { _, _ -> },
                    onToggleHabitDone = { _, _ -> },
                    onAddCustomHabit = { _, _, _, _ -> },
                    onUpdateTier = {},
                    onToggleHabitPause = { _, _ -> },
                    onDeleteCustomHabit = {},
                    onUpdateReminder = { _, _ -> },
                    onPopulateSampleData = {},
                    onClearAllData = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "/app/applet/docs/screenshots/screenshot_insights.png")
    }

    @Test
    fun screenshot_04_dashboard() {
        val state = buildUiState(AppNavigationItem.DASHBOARD)
        composeTestRule.setContent {
            MyApplicationTheme {
                val snackbar = remember { SnackbarHostState() }
                MainAppScaffold(
                    uiState = state,
                    snackbarHostState = snackbar,
                    onTabSelected = {},
                    onDateNav = {},
                    onDateSelect = {},
                    onSetPrayerStatus = { _, _ -> },
                    onToggleHabitDone = { _, _ -> },
                    onAddCustomHabit = { _, _, _, _ -> },
                    onUpdateTier = {},
                    onToggleHabitPause = { _, _ -> },
                    onDeleteCustomHabit = {},
                    onUpdateReminder = { _, _ -> },
                    onPopulateSampleData = {},
                    onClearAllData = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "/app/applet/docs/screenshots/screenshot_dashboard.png")
    }

    @Test
    fun screenshot_05_settings() {
        val state = buildUiState(AppNavigationItem.SETTINGS)
        composeTestRule.setContent {
            MyApplicationTheme {
                val snackbar = remember { SnackbarHostState() }
                MainAppScaffold(
                    uiState = state,
                    snackbarHostState = snackbar,
                    onTabSelected = {},
                    onDateNav = {},
                    onDateSelect = {},
                    onSetPrayerStatus = { _, _ -> },
                    onToggleHabitDone = { _, _ -> },
                    onAddCustomHabit = { _, _, _, _ -> },
                    onUpdateTier = {},
                    onToggleHabitPause = { _, _ -> },
                    onDeleteCustomHabit = {},
                    onUpdateReminder = { _, _ -> },
                    onPopulateSampleData = {},
                    onClearAllData = {}
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "/app/applet/docs/screenshots/screenshot_settings.png")
    }
}
