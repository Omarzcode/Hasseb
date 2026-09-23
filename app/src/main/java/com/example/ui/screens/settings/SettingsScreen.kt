package com.example.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.UserSettingsEntity
import com.example.domain.engine.AdaptiveReminderInfo
import com.example.ui.screens.checkin.AddCustomHabitDialog
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.extendedColors
import java.util.Locale

@Composable
fun SettingsScreen(
    userSettings: UserSettingsEntity,
    habits: List<HabitEntity>,
    reminderInfo: AdaptiveReminderInfo,
    onUpdateTier: (Int) -> Unit,
    onAddCustomHabit: (name: String, arabicName: String?, category: HabitCategory, description: String) -> Unit,
    onToggleHabitPause: (String, Boolean) -> Unit,
    onDeleteCustomHabit: (String) -> Unit,
    onUpdateReminder: (Boolean, Int) -> Unit,
    onPopulateSampleData: () -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = MaterialTheme.extendedColors
    var showAddDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }

    var isAdaptive by remember(userSettings.adaptiveReminderEnabled) {
        mutableStateOf(userSettings.adaptiveReminderEnabled)
    }
    var manualMinutes by remember(userSettings.manualReminderMinutes) {
        mutableIntStateOf(userSettings.manualReminderMinutes)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.ScreenHorizontalPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))

        // Title Header
        Text(
            text = "Settings & Routine",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "إعدادات الأوراد والتذكير",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = ArabicFontFamily,
            color = extendedColors.terracottaGold
        )

        // --- Section 1: Spiritual Tier Selection ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tier_settings_card"),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
                    Text(
                        text = "Current Stage Tier",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))
                Text(
                    text = "Switching stages updates your core non-deletable habits while preserving all your custom wirds.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

                TierOptionRow(
                    tierNumber = 1,
                    title = "Stage 1 – Fard Focus",
                    subtitle = "5 daily prayers (Fajr to Isha)",
                    isSelected = userSettings.selectedTier == 1,
                    onSelect = { onUpdateTier(1) }
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                TierOptionRow(
                    tierNumber = 2,
                    title = "Stage 2 – Sunnah & Adhkar",
                    subtitle = "Fard + Rawatib (12 Sunnah) + Morning/Evening Adhkar",
                    isSelected = userSettings.selectedTier == 2,
                    onSelect = { onUpdateTier(2) }
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                TierOptionRow(
                    tierNumber = 3,
                    title = "Stage 3 – Advanced Devotion",
                    subtitle = "Fard + Sunnah + Adhkar + Tahajjud + Daily Quran",
                    isSelected = userSettings.selectedTier == 3,
                    onSelect = { onUpdateTier(3) }
                )
            }
        }

        // --- Section 2: Habit Manager ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Habit & Wird Manager",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Core habits are locked to your tier; custom habits can be edited/paused",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

                habits.forEach { habit ->
                    HabitManagerRow(
                        habit = habit,
                        onTogglePause = { onToggleHabitPause(habit.id, !habit.isActive) },
                        onDelete = { habitToDelete = habit }
                    )
                    Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_add_custom_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(Dimens.CardCornerSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                    Text("Add Custom Habit")
                }
            }
        }

        // --- Section 3: Adaptive Reminder Configuration ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reminder_config_card"),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.IconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
                        Column {
                            Text(
                                text = "Adaptive Reflection Window",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAdaptive) "Auto-adapts to your rolling average check-in time" else "Set to manual static time",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isAdaptive,
                        onCheckedChange = {
                            isAdaptive = it
                            onUpdateReminder(it, manualMinutes)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("adaptive_reminder_switch")
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

                if (isAdaptive) {
                    Surface(
                        shape = RoundedCornerShape(Dimens.CardCornerSmall),
                        color = extendedColors.sageContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(Dimens.SpacingMedium)) {
                            Text(
                                text = "Current In-App Target: ~ ${reminderInfo.formattedTime}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = reminderInfo.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Column {
                        val hours = manualMinutes / 60
                        val mins = manualMinutes % 60
                        val ampm = if (hours >= 12) "PM" else "AM"
                        val displayH = if (hours % 12 == 0) 12 else hours % 12
                        val timeFormatted = String.format(Locale.getDefault(), "%d:%02d %s", displayH, mins, ampm)

                        Text(
                            text = "Manual Time: $timeFormatted",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Slider(
                            value = manualMinutes.toFloat(),
                            onValueChange = {
                                val rounded = (it / 15).toInt() * 15 // snap to 15 min intervals
                                manualMinutes = rounded
                            },
                            onValueChangeFinished = {
                                onUpdateReminder(isAdaptive, manualMinutes)
                            },
                            valueRange = 360f..1425f, // 6:00 AM to 11:45 PM
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // --- Section 4: Demo / Sample Data & Reset ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
                Text(
                    text = "Data & Reset Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Populate realistic sample logs to explore all insight triggers, radar balance wheel, and yearly seasons immediately.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingMedium))

                OutlinedButton(
                    onClick = onPopulateSampleData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("populate_sample_btn"),
                    shape = RoundedCornerShape(Dimens.CardCornerSmall),
                    border = BorderStroke(Dimens.BorderWidthThin, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Dataset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                    Text("Populate 35-Day Sample History", color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

                OutlinedButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_all_data_btn"),
                    shape = RoundedCornerShape(Dimens.CardCornerSmall),
                    border = BorderStroke(Dimens.BorderWidthThin, extendedColors.statusLateText.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        tint = extendedColors.statusLateText,
                        modifier = Modifier.size(Dimens.IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                    Text("Clear All Local Data (Reset)", color = extendedColors.statusLateText)
                }
            }
        }

        // --- Section 5: Local Privacy Assurance ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = OrganicCardShape,
            colors = CardDefaults.cardColors(containerColor = extendedColors.sandCard),
            border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
        ) {
            Row(
                modifier = Modifier.padding(Dimens.CardInnerPaddingCompact),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.IconSizeMedium)
                )
                Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
                Column {
                    Text(
                        text = "100% Local-First & Private",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Zero remote servers, zero analytics, zero accounts. All logs and insights remain entirely on your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingTriple))
    }

    // Dialogs
    if (showAddDialog) {
        AddCustomHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, arabicName, cat, desc ->
                onAddCustomHabit(name, arabicName, cat, desc)
            }
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Reset & Clear All Data?") },
            text = {
                Text("This will permanently remove all logged check-ins, custom wirds, and reset your stage tier on this device. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = extendedColors.statusLateText)
                ) {
                    Text("Reset Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.CardCornerMedium)
        )
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = { habitToDelete = null },
            title = { Text("Delete \"${habit.name}\"?") },
            text = { Text("Are you sure you want to delete this custom habit and its historical logs?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCustomHabit(habit.id)
                        habitToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = extendedColors.statusLateText)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { habitToDelete = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.CardCornerMedium)
        )
    }
}

@Composable
fun TierOptionRow(
    tierNumber: Int,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val extendedColors = MaterialTheme.extendedColors

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(Dimens.CardCornerSmall),
        color = if (isSelected) extendedColors.sageContainer.copy(alpha = 0.5f) else extendedColors.sandCard,
        border = BorderStroke(if (isSelected) 1.5.dp else Dimens.BorderWidthThin, if (isSelected) MaterialTheme.colorScheme.primary else extendedColors.sandBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active Stage",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.IconSizeSmall)
                )
            }
        }
    }
}

@Composable
fun HabitManagerRow(
    habit: HabitEntity,
    onTogglePause: () -> Unit,
    onDelete: () -> Unit
) {
    val extendedColors = MaterialTheme.extendedColors

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardCornerSmall),
        color = if (habit.isActive) extendedColors.sandCard else extendedColors.sandCard.copy(alpha = 0.5f),
        border = BorderStroke(Dimens.BorderWidthThin, extendedColors.sandBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (habit.isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingExtraSmall))
                    if (habit.isCore) {
                        Text(
                            text = "CORE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 8.sp,
                            modifier = Modifier
                                .background(extendedColors.sageContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    } else if (!habit.isActive) {
                        Text(
                            text = "PAUSED",
                            style = MaterialTheme.typography.labelSmall,
                            color = extendedColors.statusLateText,
                            fontSize = 8.sp,
                            modifier = Modifier
                                .background(extendedColors.statusLateText.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                habit.arabicName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = ArabicFontFamily,
                        color = extendedColors.terracottaGold,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pause / Resume button
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier.size(Dimens.TouchTargetMin)
                ) {
                    Icon(
                        imageVector = if (habit.isActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = if (habit.isActive) "Pause" else "Resume",
                        tint = if (habit.isActive) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconSizeSmall)
                    )
                }

                // Delete button (only for custom habits)
                if (!habit.isCore) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(Dimens.TouchTargetMin)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Habit",
                            tint = extendedColors.statusLateText,
                            modifier = Modifier.size(Dimens.IconSizeSmall)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Tier Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(Dimens.IconSizeExtraSmall)
                    )
                }
            }
        }
    }
}
