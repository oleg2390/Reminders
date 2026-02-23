package com.example.reminders.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.reminders.di.AppModule
import kotlinx.coroutines.flow.first

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val useCase = AppModule.getUpcomingTasksForNotificationsUseCase
            val tasks = useCase().first()

            tasks.forEach { task ->
                ReminderNotificationHelper.showReminderNotification(
                    applicationContext,
                    task,
                    task.id.toInt()
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
