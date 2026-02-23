package com.example.reminders.presentation.taskdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.reminders.ui.theme.RemindersTheme

class TaskDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val taskId = arguments?.getLong(ARG_TASK_ID, -1L)?.takeIf { it >= 0 }
        val viewModel: TaskDetailViewModel = ViewModelProvider(
            this,
            TaskDetailViewModelFactory(taskId)
        )[TaskDetailViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RemindersTheme {
                    TaskDetailScreen(
                        viewModel = viewModel,
                        onBack = { parentFragmentManager.popBackStack() },
                        taskId = taskId
                    )
                }
            }
        }
    }

    companion object {
        private const val ARG_TASK_ID = "task_id"

        fun newInstance(taskId: Long?): TaskDetailFragment {
            return TaskDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TASK_ID, taskId ?: -1L)
                }
            }
        }
    }
}
