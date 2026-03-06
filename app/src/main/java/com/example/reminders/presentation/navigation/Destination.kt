package com.example.reminders.presentation.navigation

sealed interface Destination {
    val route: String

    data object TaskList : Destination {
        override val route: String = "task_list"
    }

    data object TaskDetail : Destination {
        override val route: String = "task_detail"
    }

    data object TaskDetailWithId : Destination {
        const val ARG_TASK_ID = "task_id"
        override val route: String = "task_detail/{$ARG_TASK_ID}"

        fun createRoute(taskId: Long): String = "task_detail/$taskId"
    }
}