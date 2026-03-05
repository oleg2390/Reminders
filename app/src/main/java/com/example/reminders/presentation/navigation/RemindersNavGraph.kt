package com.example.reminders.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
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
        startDestination = Routes.TASK_LIST
    ) {
        composable(Routes.TASK_LIST) {
            val viewModel: TaskListViewModel = hiltViewModel()
            TaskListScreen(
                viewModel = viewModel,
                onTaskClick = { taskId ->
                    navController.navigate(Routes.detail(taskId))
                },
                onAddTask = {
                    navController.navigate(Routes.TASK_DETAIL)
                }
            )
        }

        composable(Routes.TASK_DETAIL) {
            val viewModel: TaskDetailViewModel = hiltViewModel()
            TaskDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.TASK_DETAIL_WITH_ID,
            arguments = listOf(navArgument(Routes.ARG_TASK_ID) { type = NavType.LongType })
        ) {
            val viewModel: TaskDetailViewModel = hiltViewModel()
            TaskDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}