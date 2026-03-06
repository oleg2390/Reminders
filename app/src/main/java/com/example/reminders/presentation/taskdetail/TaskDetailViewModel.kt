package com.example.reminders.presentation.taskdetail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskPriority
import com.example.reminders.domain.usecase.CreateTaskUseCase
import com.example.reminders.domain.usecase.DeleteTaskUseCase
import com.example.reminders.domain.usecase.GetTaskByIdUseCase
import com.example.reminders.domain.usecase.UpdateTaskUseCase
import com.example.reminders.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val isNewTask: Boolean = true,
    val title: String = "",
    val description: String = "",
    val dueDateMillis: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
) : ViewModel() {

    private val taskId: Long? =
        savedStateHandle.get<Long>(Destination.TaskDetailWithId.ARG_TASK_ID)?.takeIf { it >= 0L }
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
                            title = task?.title.orEmpty(),
                            description = task?.description.orEmpty(),
                            dueDateMillis = task?.dueDateMillis,
                            priority = task?.priority ?: TaskPriority.MEDIUM,
                            isCompleted = task?.isCompleted ?: false,
                            error = null
                        )
                    }
                }
        }
    }

    fun saveTask() {
        viewModelScope.launch {
            val state = uiState.value
            val currentTask = state.task

            if (state.title.isBlank()) {
                _uiState.update { it.copy(error = "TITLE_EMPTY") }
                return@launch
            }

            val taskToSave = if (currentTask != null) {
                currentTask.copy(
                    title = state.title,
                    description = state.description,
                    priority = state.priority,
                    dueDateMillis = state.dueDateMillis,
                    isCompleted = state.isCompleted
                )
            } else {
                Task(
                    title = state.title,
                    description = state.description,
                    priority = state.priority,
                    dueDateMillis = state.dueDateMillis,
                    isCompleted = state.isCompleted,
                    createdAtMillis = System.currentTimeMillis()
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

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onDueDateChange(value: Long?) {
        _uiState.update { it.copy(dueDateMillis = value) }
    }

    fun onPriorityChange(value: TaskPriority) {
        _uiState.update { it.copy(priority = value) }
    }

    fun onCompleteChange(value: Boolean) {
        _uiState.update { it.copy(isCompleted = value) }
    }

    fun onDeleteDialogChange(value: Boolean) {
        _uiState.update { it.copy(showDeleteConfirm = value) }
    }
}
