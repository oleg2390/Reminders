package com.example.reminders.domain.usecase

import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskSortOrder
import com.example.reminders.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(sortOrder: TaskSortOrder = TaskSortOrder.BY_CREATION_DATE): Flow<List<Task>> {
        return repository.getTasks(sortOrder)
    }
}
