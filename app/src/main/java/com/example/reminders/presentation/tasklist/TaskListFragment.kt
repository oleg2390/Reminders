package com.example.reminders.presentation.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.reminders.R
import com.example.reminders.presentation.taskdetail.TaskDetailFragment
import com.example.reminders.ui.theme.RemindersTheme

class TaskListFragment : Fragment() {

    private val viewModel: TaskListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                RemindersTheme {
                    TaskListScreen(
                        viewModel = viewModel,
                        onTaskClick = { taskId ->
                            parentFragmentManager.commit {
                                replace(R.id.fragment_container, TaskDetailFragment.newInstance(taskId))
                                addToBackStack(null)
                            }
                        },
                        onAddTask = {
                            parentFragmentManager.commit {
                                replace(R.id.fragment_container, TaskDetailFragment.newInstance(null))
                                addToBackStack(null)
                            }
                        }
                    )
                }
            }
        }
    }
}
