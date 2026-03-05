package com.example.reminders.presentation.navigation

object Routes {
    const val TASK_LIST = "task_list"
    const val TASK_DETAIL = "task_detail"
    const val TASK_DETAIL_WITH_ID = "task_detail/{task_id}"
    const val ARG_TASK_ID = "task_id"

    fun detail(taskId: Long): String = "task_detail/$taskId"
}