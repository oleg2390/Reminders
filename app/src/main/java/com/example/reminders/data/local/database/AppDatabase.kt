package com.example.reminders.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reminders.data.local.dao.TaskDao
import com.example.reminders.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}
