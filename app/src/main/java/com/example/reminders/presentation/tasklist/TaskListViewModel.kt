package com.example.reminders.presentation.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskSortOrder
import com.example.reminders.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val sortOrder: TaskSortOrder = TaskSortOrder.BY_CREATION_DATE,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TaskListViewModel : ViewModel() {

    private val getTasksUseCase = AppModule.getTasksUseCase
    private val updateTaskUseCase = AppModule.updateTaskUseCase

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks(sortOrder: TaskSortOrder? = null) {
        val order = sortOrder ?: _uiState.value.sortOrder
        viewModelScope.launch {
            getTasksUseCase(order)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { tasks ->
                    _uiState.update {
                        it.copy(
                            tasks = tasks,
                            sortOrder = order,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
        _uiState.update { it.copy(sortOrder = order, isLoading = true) }
    }

    fun setSortOrder(sortOrder: TaskSortOrder) {
        loadTasks(sortOrder)
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task.copy(isCompleted = !task.isCompleted))
        }
    }
}
