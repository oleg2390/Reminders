package com.example.reminders.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.reminders.presentation.taskdetail.TaskDetailScreen
import com.example.reminders.presentation.taskdetail.TaskDetailViewModel
import com.example.reminders.presentation.tasklist.TaskListScreen
import com.example.reminders.presentation.tasklist.TaskListViewModel

@Composable
fun RemindersNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.TaskList.route
    ) {
        composable(Destination.TaskList.route) {
            val viewModel: TaskListViewModel = hiltViewModel()
            TaskListScreen(
                viewModel = viewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Destination.TaskDetailWithId.createRoute(taskId))
                },
                onAddTask = {
                    navController.navigate(Destination.TaskDetail.route)
                }
            )
        }

        composable(Destination.TaskDetail.route) {
            val viewModel: TaskDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TaskDetailScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onDueDateChange = viewModel::onDueDateChange,
                onPriorityChange = viewModel::onPriorityChange,
                onCompleteChange = viewModel::onCompleteChange,
                onDeleteDialogChange = viewModel::onDeleteDialogChange,
                onSaveClick = viewModel::saveTask,
                onDeleteClick = {
                    uiState.task?.let { task ->
                        viewModel.deleteTask(task)
                    }
                }
            )
        }

        composable(
            Destination.TaskDetailWithId.route,
            arguments = listOf(
                navArgument(Destination.TaskDetailWithId.ARG_TASK_ID) {
                    type = NavType.LongType
                })
        ) {
            val viewModel: TaskDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TaskDetailScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() },
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onDueDateChange = viewModel::onDueDateChange,
                onPriorityChange = viewModel::onPriorityChange,
                onCompleteChange = viewModel::onCompleteChange,
                onDeleteDialogChange = viewModel::onDeleteDialogChange,
                onSaveClick = viewModel::saveTask,
                onDeleteClick = {
                    uiState.task?.let { task ->
                        viewModel.deleteTask(task)
                    }
                }
            )
        }
    }
}