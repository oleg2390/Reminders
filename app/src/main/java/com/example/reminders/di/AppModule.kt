package com.example.reminders.di

import android.content.Context
import androidx.room.Room
import com.example.reminders.data.common.DispatcherProvider
import com.example.reminders.data.local.dao.TaskDao
import com.example.reminders.data.local.database.AppDatabase
import com.example.reminders.data.repository.TaskRepositoryImpl
import com.example.reminders.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val DATABASE_NAME = "reminders_db"


    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        dispatchers: DispatcherProvider
    ): TaskRepository {
        return TaskRepositoryImpl(taskDao, dispatchers)
    }
}
