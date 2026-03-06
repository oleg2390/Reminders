package com.example.reminders.presentation.tasklist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskSortOrder
import com.example.reminders.domain.usecase.GetTasksUseCase
import com.example.reminders.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TaskListUiState(
    val tasks: PersistentList<Task> = persistentListOf(),
    val sortOrder: TaskSortOrder = TaskSortOrder.BY_CREATION_DATE,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

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
                            tasks = tasks.toPersistentList(),
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
