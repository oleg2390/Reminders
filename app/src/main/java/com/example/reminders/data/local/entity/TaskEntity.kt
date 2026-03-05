package com.example.reminders.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskPriority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: String,
    val dueDate: Long?,
    val isCompleted: Boolean,
    val createdAt: Long
) {
    fun toDomain(): Task = Task(
        id = id,
        title = title,
        description = description,
        priority = TaskPriority.valueOf(priority),
        dueDateMillis =  dueDate,
        isCompleted = isCompleted,
        createdAtMillis = createdAt

    )

    companion object {
        fun fromDomain(task: Task): TaskEntity = TaskEntity(
            id = task.id,
            title = task.title,
            description = task.description,
            priority = task.priority.name,
            dueDate = task.dueDateMillis,
            isCompleted = task.isCompleted,
            createdAt = task.createdAtMillis
        )
    }
}
