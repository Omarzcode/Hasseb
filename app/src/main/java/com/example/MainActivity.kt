package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HabitCategory
import com.example.ui.screens.checkin.CheckInScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.insights.InsightsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.extendedColors
import com.example.ui.viewmodel.AppNavigationItem
import com.example.ui.viewmodel.MuhasabahUiState
import com.example.ui.viewmodel.MuhasabahViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MuhasabahViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(userMessage) {
                    userMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearUserMessage()
                    }
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (!uiState.hasCompletedOnboarding) {
                    OnboardingScreen(
                        onSelectTier = { tier ->
                            viewModel.completeOnboarding(tier)
                        }
                    )
                } else {
                    MainAppScaffold(
                        uiState = uiState,
                        snackbarHostState = snackbarHostState,
                        onTabSelected = { tab -> viewModel.selectTab(tab) },
                        onDateNav = { delta -> viewModel.navigateDateBy(delta) },
                        onDateSelect = { date -> viewModel.selectDate(date) },
                        onSetPrayerStatus = { habitId, status -> viewModel.setPrayerStatus(habitId, status) },
                        onToggleHabitDone = { habitId, done -> viewModel.toggleHabitDone(habitId, done) },
                        onAddCustomHabit = { name, arabic, cat, desc ->
                            viewModel.addCustomHabit(name, arabic, cat, desc)
                        },
                        onUpdateTier = { newTier -> viewModel.updateTier(newTier) },
                        onToggleHabitPause = { habitId, isActive -> viewModel.toggleHabitPause(habitId, isActive) },
                        onDeleteCustomHabit = { habitId -> viewModel.deleteCustomHabit(habitId) },
                        onUpdateReminder = { isAdaptive, manualMins ->
                            viewModel.updateReminderSettings(isAdaptive, manualMins)
                        },
                        onPopulateSampleData = { viewModel.populateSampleHistory() },
                        onClearAllData = { viewModel.clearAllData() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    uiState: MuhasabahUiState,
    snackbarHostState: SnackbarHostState,
    onTabSelected: (AppNavigationItem) -> Unit,
    onDateNav: (Long) -> Unit,
    onDateSelect: (java.time.LocalDate) -> Unit,
    onSetPrayerStatus: (String, com.example.data.model.LogStatus) -> Unit,
    onToggleHabitDone: (String, Boolean) -> Unit,
    onAddCustomHabit: (String, String?, HabitCategory, String) -> Unit,
    onUpdateTier: (Int) -> Unit,
    onToggleHabitPause: (String, Boolean) -> Unit,
    onDeleteCustomHabit: (String) -> Unit,
    onUpdateReminder: (Boolean, Int) -> Unit,
    onPopulateSampleData: () -> Unit,
    onClearAllData: () -> Unit
) {
    val extendedColors = MaterialTheme.extendedColors

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(extendedColors.sageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Digital Muhasabah",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(extendedColors.terracottaGold)
                                    )
                            }
                            Text(
                                text = "STAGE ${uiState.selectedTier} • LOCAL-FIRST ENCRYPTED",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 8.5.sp,
                                letterSpacing = 1.0.sp
                            )
                        }
                    }
                },
                actions = {
                    // Streak Pill
                    val streakDays = uiState.weeklyStats?.checkInStreak ?: 0
                    if (streakDays > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = extendedColors.sageContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, extendedColors.sageContainerBorder),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = extendedColors.terracottaGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$streakDays d",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets.statusBars
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = extendedColors.sandSurface,
                tonalElevation = 0.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .testTag("main_bottom_nav")
                    .border(
                        width = 1.dp,
                        color = extendedColors.sandBorder,
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                    )
            ) {
                // 1. Check-In (Wird)
                NavigationBarItem(
                    selected = uiState.selectedTab == AppNavigationItem.CHECK_IN,
                    onClick = { onTabSelected(AppNavigationItem.CHECK_IN) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.selectedTab == AppNavigationItem.CHECK_IN)
                                Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = "Check-In"
                        )
                    },
                    label = {
                        Text(
                            text = "WIRD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (uiState.selectedTab == AppNavigationItem.CHECK_IN) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.6.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = extendedColors.sageContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_checkin")
                )

                // 2. Insights
                NavigationBarItem(
                    selected = uiState.selectedTab == AppNavigationItem.INSIGHTS,
                    onClick = { onTabSelected(AppNavigationItem.INSIGHTS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.selectedTab == AppNavigationItem.INSIGHTS)
                                Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Insights"
                        )
                    },
                    label = {
                        Text(
                            text = "INSIGHTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (uiState.selectedTab == AppNavigationItem.INSIGHTS) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.6.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = extendedColors.sageContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_insights")
                )

                // 3. Dashboard
                NavigationBarItem(
                    selected = uiState.selectedTab == AppNavigationItem.DASHBOARD,
                    onClick = { onTabSelected(AppNavigationItem.DASHBOARD) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.selectedTab == AppNavigationItem.DASHBOARD)
                                Icons.Default.BarChart else Icons.Outlined.BarChart,
                            contentDescription = "Reports"
                        )
                    },
                    label = {
                        Text(
                            text = "REPORTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (uiState.selectedTab == AppNavigationItem.DASHBOARD) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.6.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = extendedColors.sageContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                // 4. Settings
                NavigationBarItem(
                    selected = uiState.selectedTab == AppNavigationItem.SETTINGS,
                    onClick = { onTabSelected(AppNavigationItem.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.selectedTab == AppNavigationItem.SETTINGS)
                                Icons.Default.Tune else Icons.Outlined.Tune,
                            contentDescription = "Config"
                        )
                    },
                    label = {
                        Text(
                            text = "CONFIG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (uiState.selectedTab == AppNavigationItem.SETTINGS) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.6.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = extendedColors.sageContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = uiState.selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tabTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) { tab ->
            when (tab) {
                AppNavigationItem.CHECK_IN -> CheckInScreen(
                    selectedDate = uiState.selectedDate,
                    isToday = uiState.isToday,
                    habits = uiState.habits,
                    logsForDate = uiState.logsForDate,
                    reminderInfo = uiState.reminderInfo,
                    onDateNav = onDateNav,
                    onDateSelect = onDateSelect,
                    onSetPrayerStatus = onSetPrayerStatus,
                    onToggleHabitDone = onToggleHabitDone,
                    onAddCustomHabit = onAddCustomHabit
                )

                AppNavigationItem.INSIGHTS -> InsightsScreen(
                    insights = uiState.insights,
                    onPauseHabit = { habitId -> onToggleHabitPause(habitId, false) },
                    onNavigateToSettings = { onTabSelected(AppNavigationItem.SETTINGS) },
                    onAddWird = { onTabSelected(AppNavigationItem.CHECK_IN) }
                )

                AppNavigationItem.DASHBOARD -> DashboardScreen(
                    weeklyStats = uiState.weeklyStats,
                    monthlyStats = uiState.monthlyStats,
                    yearlyStats = uiState.yearlyStats
                )

                AppNavigationItem.SETTINGS -> SettingsScreen(
                    userSettings = uiState.userSettings,
                    habits = uiState.allHabits,
                    reminderInfo = uiState.reminderInfo,
                    onUpdateTier = onUpdateTier,
                    onAddCustomHabit = onAddCustomHabit,
                    onToggleHabitPause = onToggleHabitPause,
                    onDeleteCustomHabit = onDeleteCustomHabit,
                    onUpdateReminder = onUpdateReminder,
                    onPopulateSampleData = onPopulateSampleData,
                    onClearAllData = onClearAllData
                )
            }
        }
    }
}
