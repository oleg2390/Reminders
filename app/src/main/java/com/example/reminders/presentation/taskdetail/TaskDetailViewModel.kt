package com.example.reminders.presentation.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskPriority
import com.example.reminders.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val isNewTask: Boolean = true,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val error: String? = null
)

class TaskDetailViewModel(
    private val taskId: Long?
) : ViewModel() {

    private val getTaskByIdUseCase = AppModule.getTaskByIdUseCase
    private val createTaskUseCase = AppModule.createTaskUseCase
    private val updateTaskUseCase = AppModule.updateTaskUseCase
    private val deleteTaskUseCase = AppModule.deleteTaskUseCase

    private val _uiState = MutableStateFlow(TaskDetailUiState(isNewTask = taskId == null))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        taskId?.let { loadTask(it) }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTaskByIdUseCase(id)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = e.message, isLoading = false)
                    }
                }
                .collect { task ->
                    _uiState.update {
                        it.copy(
                            task = task,
                            isNewTask = false,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun saveTask(
        title: String,
        description: String,
        priority: TaskPriority,
        dueDate: java.util.Date?,
        isCompleted: Boolean
    ) {
        viewModelScope.launch {
            val currentTask = _uiState.value.task
            val taskToSave = if (currentTask != null) {
                currentTask.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    isCompleted = isCompleted
                )
            } else {
                Task(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDate = dueDate,
                    isCompleted = isCompleted,
                    createdAt = java.util.Date()
                )
            }

            try {
                if (currentTask != null) {
                    updateTaskUseCase(taskToSave)
                } else {
                    createTaskUseCase(taskToSave)
                }
                _uiState.update { it.copy(saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(task)
                _uiState.update { it.copy(deleteSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false, deleteSuccess = false) }
    }
}
