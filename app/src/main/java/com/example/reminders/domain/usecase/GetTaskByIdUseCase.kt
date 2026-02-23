package com.example.reminders.domain.usecase

import com.example.reminders.domain.model.Task
import com.example.reminders.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTaskByIdUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(id: Long): Flow<Task?> = flow {
        emit(repository.getTaskById(id))
    }
}