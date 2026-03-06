package com.example.reminders.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reminders.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getTasksByCreationDate(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks ORDER BY 
        CASE priority 
            WHEN 'HIGH' THEN 1 
            WHEN 'MEDIUM' THEN 2 
            WHEN 'LOW' THEN 3 
        END,
        createdAt DESC
    """)
    fun getTasksByPriority(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC, createdAt DESC
    """)
    fun getTasksByDueDate(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("""
        SELECT * FROM tasks 
        WHERE isCompleted = 0 AND dueDate IS NOT NULL 
        AND dueDate >= :minTime AND dueDate <= :maxTime
        ORDER BY dueDate ASC
    """)
    fun getUpcomingTasksForNotifications(minTime: Long, maxTime: Long): Flow<List<TaskEntity>>
}
