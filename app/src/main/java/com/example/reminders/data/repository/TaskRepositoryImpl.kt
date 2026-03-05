package com.example.reminders.data.repository

import com.example.reminders.data.common.DispatcherProvider
import com.example.reminders.data.local.dao.TaskDao
import com.example.reminders.data.local.entity.TaskEntity
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskSortOrder
import com.example.reminders.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val dispatchers: DispatcherProvider
) : TaskRepository {

    override fun getTasks(sortOrder: TaskSortOrder): Flow<List<Task>> {
        return when (sortOrder) {
            TaskSortOrder.BY_CREATION_DATE -> taskDao.getTasksByCreationDate()
            TaskSortOrder.BY_PRIORITY -> taskDao.getTasksByPriority()
            TaskSortOrder.BY_DUE_DATE -> taskDao.getTasksByDueDate()
        }.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getTaskById(id: Long): Task? = withContext(dispatchers.io) {
        taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun insertTask(task: Task): Long = withContext(dispatchers.io) {
        taskDao.insert(TaskEntity.fromDomain(task))
    }

    override suspend fun updateTask(task: Task) = withContext(dispatchers.io) {
        taskDao.update(TaskEntity.fromDomain(task))
    }

    override suspend fun deleteTask(task: Task) = withContext(dispatchers.io){
        taskDao.deleteById(task.id)
    }

    override fun getUpcomingTasksForNotifications(): Flow<List<Task>> {
        val minTime = System.currentTimeMillis()
        val maxTime = minTime + (24 * 60 * 60 * 1000) // 24 hours ahead
        return taskDao.getUpcomingTasksForNotifications(minTime, maxTime)
            .map { entities -> entities.map { it.toDomain() } }
    }
}
