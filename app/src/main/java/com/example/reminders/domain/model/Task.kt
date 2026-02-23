package com.example.reminders.domain.model

import java.util.Date

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val dueDate: Date?,
    val isCompleted: Boolean,
    val createdAt: Date
)

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}
