package com.example.reminders.presentation.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TaskDetailViewModelFactory(
    private val taskId: Long?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(taskId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
