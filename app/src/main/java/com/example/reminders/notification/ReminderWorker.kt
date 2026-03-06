package com.example.reminders.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.reminders.domain.usecase.GetUpcomingTasksForNotificationsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getUpcomingTasksForNotificationsUseCase: GetUpcomingTasksForNotificationsUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val tasks = getUpcomingTasksForNotificationsUseCase().first()

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
