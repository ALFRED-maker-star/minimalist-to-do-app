package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentAmberMedium
import com.example.ui.theme.AccentCoralHigh
import com.example.ui.theme.AccentSlateLow
import com.example.ui.theme.CategoryFinance
import com.example.ui.theme.CategoryHealth
import com.example.ui.theme.CategoryPersonal
import com.example.ui.theme.CategoryShopping
import com.example.ui.theme.CategoryStudy
import com.example.ui.theme.CategoryWork

enum class Priority(val label: String, val color: Color) {
    LOW("Low", AccentSlateLow),
    MEDIUM("Medium", AccentAmberMedium),
    HIGH("High", AccentCoralHigh);

    companion object {
        fun fromString(value: String): Priority {
            return try {
                valueOf(value.uppercase())
            } catch (e: Exception) {
                MEDIUM
            }
        }
    }
}

data class TaskCategory(
    val name: String,
    val color: Color
)

object TaskCategories {
    val Work = TaskCategory("Work", CategoryWork)
    val Personal = TaskCategory("Personal", CategoryPersonal)
    val Health = TaskCategory("Health", CategoryHealth)
    val Study = TaskCategory("Study", CategoryStudy)
    val Finance = TaskCategory("Finance", CategoryFinance)
    val Shopping = TaskCategory("Shopping", CategoryShopping)

    val all = listOf(Work, Personal, Health, Study, Finance, Shopping)

    fun getColor(categoryName: String): Color {
        return all.firstOrNull { it.name.equals(categoryName, ignoreCase = true) }?.color ?: CategoryWork
    }
}

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDateMillis: Long? = null,
    val dueTimeText: String? = null,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: String = "Work",
    val createdAtMillis: Long = System.currentTimeMillis()
)
