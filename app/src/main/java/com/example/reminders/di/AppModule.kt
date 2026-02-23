package com.example.reminders.di

import android.content.Context
import com.example.reminders.data.local.database.AppDatabase
import com.example.reminders.data.repository.TaskRepositoryImpl
import com.example.reminders.domain.repository.TaskRepository
import com.example.reminders.domain.usecase.CreateTaskUseCase
import com.example.reminders.domain.usecase.DeleteTaskUseCase
import com.example.reminders.domain.usecase.GetTaskByIdUseCase
import com.example.reminders.domain.usecase.GetTasksUseCase
import com.example.reminders.domain.usecase.GetUpcomingTasksForNotificationsUseCase
import com.example.reminders.domain.usecase.UpdateTaskUseCase

object AppModule {

    private var database: AppDatabase? = null
    private var taskRepository: TaskRepository? = null

    fun init(context: Context) {
        database = AppDatabase.getInstance(context)
        taskRepository = TaskRepositoryImpl(database!!.taskDao())
    }

    private fun getRepository(): TaskRepository {
        return taskRepository ?: error("AppModule not initialized. Call init() in Application.")
    }

    val getTasksUseCase: GetTasksUseCase
        get() = GetTasksUseCase(getRepository())

    val getTaskByIdUseCase: GetTaskByIdUseCase
        get() = GetTaskByIdUseCase(getRepository())

    val createTaskUseCase: CreateTaskUseCase
        get() = CreateTaskUseCase(getRepository())

    val updateTaskUseCase: UpdateTaskUseCase
        get() = UpdateTaskUseCase(getRepository())

    val deleteTaskUseCase: DeleteTaskUseCase
        get() = DeleteTaskUseCase(getRepository())

    val getUpcomingTasksForNotificationsUseCase: GetUpcomingTasksForNotificationsUseCase
        get() = GetUpcomingTasksForNotificationsUseCase(getRepository())
}
