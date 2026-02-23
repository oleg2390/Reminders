package com.example.reminders

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.reminders.di.AppModule
import com.example.reminders.notification.ReminderWorker
import java.util.concurrent.TimeUnit

class RemindersApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppModule.init(this)
        scheduleReminderWork()
    }

    private fun scheduleReminderWork() {
        val reminderWork = PeriodicWorkRequestBuilder<ReminderWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "reminder_check",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }
}
