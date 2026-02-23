package com.example.reminders.data.repository

import com.example.reminders.data.local.dao.TaskDao
import com.example.reminders.data.local.entity.TaskEntity
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskSortOrder
import com.example.reminders.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(sortOrder: TaskSortOrder): Flow<List<Task>> {
        return when (sortOrder) {
            TaskSortOrder.BY_CREATION_DATE -> taskDao.getTasksByCreationDate()
            TaskSortOrder.BY_PRIORITY -> taskDao.getTasksByPriority()
            TaskSortOrder.BY_DUE_DATE -> taskDao.getTasksByDueDate()
        }.map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insert(TaskEntity.fromDomain(task))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(TaskEntity.fromDomain(task))
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteById(task.id)
    }

    override fun getUpcomingTasksForNotifications(): Flow<List<Task>> {
        val minTime = System.currentTimeMillis()
        val maxTime = minTime + (24 * 60 * 60 * 1000) // 24 hours ahead
        return taskDao.getUpcomingTasksForNotifications(minTime, maxTime)
            .map { entities -> entities.map { it.toDomain() } }
    }
}
