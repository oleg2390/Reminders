package com.example.reminders.domain.model

import androidx.compose.runtime.Immutable

@Immutable
enum class TaskSortOrder {
    BY_CREATION_DATE,
    BY_PRIORITY,
    BY_DUE_DATE
}
