package com.example.reminders.domain.usecase

import com.example.reminders.domain.model.Task
import com.example.reminders.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long = withContext(Dispatchers.IO) {
        repository.insertTask(task)
    }
}