package com.example.reminders.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val dueDateMillis: Long?,
    val isCompleted: Boolean,
    val createdAtMillis: Long
)

@Immutable
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}
