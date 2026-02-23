package com.example.reminders.domain.repository

import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskSortOrder
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasks(sortOrder: TaskSortOrder): Flow<List<Task>>

    suspend fun getTaskById(id: Long): Task?

    suspend fun insertTask(task: Task): Long

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun getUpcomingTasksForNotifications(): Flow<List<Task>>
}
