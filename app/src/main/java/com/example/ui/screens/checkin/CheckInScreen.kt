package com.example.ui.screens.checkin

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.LogStatus
import com.example.domain.engine.AdaptiveReminderInfo
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicCardShape
import com.example.ui.theme.extendedColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CheckInScreen(
    selectedDate: LocalDate,
    isToday: Boolean,
    habits: List<HabitEntity>,
    logsForDate: Map<String, HabitLogEntity>,
    reminderInfo: AdaptiveReminderInfo,
    onDateNav: (Long) -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onSetPrayerStatus: (String, LogStatus) -> Unit,
    onToggleHabitDone: (String, Boolean) -> Unit,
    onAddCustomHabit: (name: String, arabicName: String?, category: HabitCategory, description: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    // Calculate daily completion stats
    val totalActiveHabits = habits.size
    val completedCount = habits.count { habit ->
        logsForDate[habit.id]?.completed == true
    }
    val progressFraction = if (totalActiveHabits > 0) {
        (completedCount.toFloat() / totalActiveHabits).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        label = "dailyProgress"
    )

    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())
    val formattedDate = selectedDate.format(dateFormatter)

    // Spiritual Reflection quotes for mindful encouragement
    val dailyQuotes = listOf(
        "“Take account of yourselves before you are taken to account.” — Umar ibn Al-Khattab (RA)",
        "“The most beloved of deeds to Allah are those that are most consistent, even if small.” — Prophet Muhammad ﷺ",
        "“O you who have believed, remember Allah with much remembrance.” (Quran 33:41)",
        "“Indeed, prayer prohibits immorality and wrongdoing, and the remembrance of Allah is greater.” (Quran 29:45)",
        "“A moment of patient reflection transforms daily habits into sacred devotion.”"
    )
    val quoteIndex = (selectedDate.dayOfYear) % dailyQuotes.size
    val dailyQuote = dailyQuotes[quoteIndex]

    // Group habits into sections
    val fardHabits = habits.filter { it.category == HabitCategory.PRAYER_FARD.name }
    val sunnahHabits = habits.filter { it.category == HabitCategory.PRAYER_SUNNAH.name }
    val adhkarHabits = habits.filter { it.category == HabitCategory.ADHKAR.name }
    val quranHabits = habits.filter { it.category == HabitCategory.QURAN.name }
    val customHabits = habits.filter {
        it.category !in setOf(
            HabitCategory.PRAYER_FARD.name,
            HabitCategory.PRAYER_SUNNAH.name,
            HabitCategory.ADHKAR.name,
            HabitCategory.QURAN.name
        ) || !it.isCore
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
        ) {
            item {
                Spacer(modifier = Modifier.height(Dimens.SpacingExtraSmall))

                // Date Navigation Header
                DateNavigationHeader(
                    selectedDate = selectedDate,
                    formattedDate = formattedDate,
                    isToday = isToday,
                    onPrevDay = { onDateNav(-1) },
                    onNextDay = { onDateNav(1) },
                    onTodayClick = { onDateSelect(LocalDate.now()) }
                )
            }

            // Adaptive Reflection Window Card
            item {
                AdaptiveReminderCard(reminderInfo = reminderInfo)
            }

            // Daily Progress Card
            item {
                DailyProgressCard(
                    completedCount = completedCount,
                    totalCount = totalActiveHabits,
                    progress = animatedProgress
                )
            }

            // --- Section 1: Fard Prayers (5 Daily) ---
            if (fardHabits.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Obligatory Prayers",
                        arabicTitle = "الصلوات الخمس المفروضة",
                        badge = "STAGE 1"
                    )
                }

                items(fardHabits, key = { it.id }) { habit ->
                    HabitItemCard(
                        habit = habit,
                        log = logsForDate[habit.id],
                        onSetPrayerStatus = { status -> onSetPrayerStatus(habit.id, status) },
                        onToggleDone = { done -> onToggleHabitDone(habit.id, done) }
                    )
                }
            }

            // --- Section 2: Sunnah & Rawatib ---
            if (sunnahHabits.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Sunnah & Rawatib",
                        arabicTitle = "السنن الرواتب والنوافل",
                        badge = "STAGE 2"
                    )
                }

                items(sunnahHabits, key = { it.id }) { habit ->
                    HabitItemCard(
                        habit = habit,
                        log = logsForDate[habit.id],
                        onSetPrayerStatus = { status -> onSetPrayerStatus(habit.id, status) },
                        onToggleDone = { done -> onToggleHabitDone(habit.id, done) }
                    )
                }
            }

            // --- Section 3: Adhkar & Remembrance ---
            if (adhkarHabits.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Daily Adhkar",
                        arabicTitle = "الأذكار والأوراد",
                        badge = "STAGE 2"
                    )
                }

                items(adhkarHabits, key = { it.id }) { habit ->
                    HabitItemCard(
                        habit = habit,
                        log = logsForDate[habit.id],
                        onSetPrayerStatus = { status -> onSetPrayerStatus(habit.id, status) },
                        onToggleDone = { done -> onToggleHabitDone(habit.id, done) }
                    )
                }
            }

            // --- Section 4: Quran & Study ---
            if (quranHabits.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Quran & Night Vigil",
                        arabicTitle = "ورد القرآن وقيام الليل",
                        badge = "STAGE 3"
                    )
                }

                items(quranHabits, key = { it.id }) { habit ->
                    HabitItemCard(
                        habit = habit,
                        log = logsForDate[habit.id],
                        onSetPrayerStatus = { status -> onSetPrayerStatus(habit.id, status) },
                        onToggleDone = { done -> onToggleHabitDone(habit.id, done) }
                    )
                }
            }

            // --- Section 5: Personal Wirds ---
            if (customHabits.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Personal Wirds",
                        arabicTitle = "الأوراد الشخصية",
                        badge = "CUSTOM"
                    )
                }

                items(customHabits, key = { it.id }) { habit ->
                    HabitItemCard(
                        habit = habit,
                        log = logsForDate[habit.id],
                        onSetPrayerStatus = { status -> onSetPrayerStatus(habit.id, status) },
                        onToggleDone = { done -> onToggleHabitDone(habit.id, done) }
                    )
                }
            }

            // Add Custom Habit Banner
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.CardCornerLarge))
                        .clickable { showAddDialog = true }
                        .testTag("add_custom_wird_card"),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(Dimens.CardCornerLarge),
                    border = BorderStroke(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.CardInnerPaddingCompact),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Wird",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.IconSizeSmall)
                        )
                        Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
                        Text(
                            text = "Add Custom Habit or Wird",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.5.sp
                        )
                    }
                }
            }

            // Spiritual Reflection Card at Bottom
            item {
                SpiritualInsightFooterCard(quote = dailyQuote)
            }

            item {
                Spacer(modifier = Modifier.height(Dimens.SpacingTriple))
            }
        }
    }

    if (showAddDialog) {
        AddCustomHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, arabicName, category, desc ->
                onAddCustomHabit(name, arabicName, category, desc)
            }
        )
    }
}

@Composable
fun DateNavigationHeader(
    selectedDate: LocalDate,
    formattedDate: String,
    isToday: Boolean,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onTodayClick: () -> Unit
) {
    val extendedColors = MaterialTheme.extendedColors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpacingMedium, vertical = Dimens.SpacingSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevDay,
                modifier = Modifier
                    .size(Dimens.TouchTargetMin)
                    .clip(CircleShape)
                    .background(extendedColors.toggleIdleBg)
                    .border(Dimens.BorderWidthThin, extendedColors.toggleIdleBorder, CircleShape)
                    .testTag("prev_date_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Day",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.IconSizeSmall)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onTodayClick() }
            ) {
                Text(
                    text = "DIGITAL MUHASABAH",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp,
                    letterSpacing = 1.6.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall)
                ) {
                    Text(
                        text = if (isToday) "Today, $formattedDate" else formattedDate,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 19.sp
                    )

                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(extendedColors.terracottaGold)
                    )
                }
            }

            IconButton(
                onClick = onNextDay,
                modifier = Modifier
                    .size(Dimens.TouchTargetMin)
                    .clip(CircleShape)
                    .background(extendedColors.toggleIdleBg)
                    .border(Dimens.BorderWidthThin, extendedColors.toggleIdleBorder, CircleShape)
                    .testTag("next_date_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Day",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.IconSizeSmall)
                )
            }
        }
    }
}

@Composable
fun DailyProgressCard(
    completedCount: Int,
    totalCount: Int,
    progress: Float
) {
    val extendedColors = MaterialTheme.extendedColors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_progress_card"),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY COMPLETION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$completedCount of $totalCount wirds recorded",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = extendedColors.progressRingActive,
                trackColor = extendedColors.progressRingTrack,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun SpiritualInsightFooterCard(quote: String) {
    val extendedColors = MaterialTheme.extendedColors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = OrganicCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.CardInnerPadding)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingExtraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(extendedColors.terracottaGold)
                )
                Text(
                    text = "SPIRITUAL INSIGHT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                    fontSize = 9.5.sp
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))

            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    arabicTitle: String,
    badge: String
) {
    val extendedColors = MaterialTheme.extendedColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.SpacingSmall, bottom = Dimens.SpacingExtraSmall, start = 2.dp, end = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
            Text(
                text = arabicTitle,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = ArabicFontFamily,
                color = extendedColors.terracottaGold,
                fontSize = 12.sp
            )
        }

        Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp,
            letterSpacing = 0.8.sp,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(20.dp))
                .border(Dimens.BorderWidthThin, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
