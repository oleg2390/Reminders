package com.example.reminders.domain.usecase

import com.example.reminders.domain.model.Task
import com.example.reminders.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetUpcomingTasksForNotificationsUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> {
        return repository.getUpcomingTasksForNotifications()
    }
}
