package com.example.ui.screens.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.HabitCategory
import com.example.ui.theme.ArabicFontFamily
import com.example.ui.theme.Dimens
import com.example.ui.theme.OrganicDialogShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, arabicName: String?, category: HabitCategory, description: String) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var arabicName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(HabitCategory.CUSTOM) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val categories = listOf(
        HabitCategory.CUSTOM,
        HabitCategory.PRAYER_SUNNAH,
        HabitCategory.ADHKAR,
        HabitCategory.QURAN,
        HabitCategory.CHARITY,
        HabitCategory.FASTING,
        HabitCategory.REFLECTION
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Custom Wird",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
            ) {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = {
                        habitName = it
                        if (it.isNotBlank()) isError = false
                    },
                    label = { Text("Habit / Wird Name *") },
                    placeholder = { Text("e.g., Duha Prayer, Surah Al-Mulk") },
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Please enter a habit name") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_habit_name_input"),
                    shape = RoundedCornerShape(Dimens.CardCornerSmall)
                )

                OutlinedTextField(
                    value = arabicName,
                    onValueChange = { arabicName = it },
                    label = { Text("Arabic Name (Optional)") },
                    placeholder = { Text("e.g., صلاة الضحى") },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = ArabicFontFamily),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_habit_arabic_input"),
                    shape = RoundedCornerShape(Dimens.CardCornerSmall)
                )

                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.CardCornerSmall)
                    )

                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    selectedCategory = cat
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Daily Target / Note (Optional)") },
                    placeholder = { Text("e.g., 2 Rak'ahs before Dhuhr") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.CardCornerSmall)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (habitName.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(
                            habitName,
                            arabicName.takeIf { it.isNotBlank() },
                            selectedCategory,
                            description
                        )
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(Dimens.CardCornerSmall),
                modifier = Modifier.testTag("save_custom_habit_btn")
            ) {
                Text("Add Wird")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        shape = OrganicDialogShape,
        containerColor = MaterialTheme.colorScheme.surface
    )
}
