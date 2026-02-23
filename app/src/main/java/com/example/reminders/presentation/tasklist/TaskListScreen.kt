package com.example.reminders.presentation.tasklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.reminders.domain.model.Task
import com.example.reminders.domain.model.TaskPriority
import com.example.reminders.domain.model.TaskSortOrder
import com.example.reminders.ui.theme.RemindersTheme
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTask: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    TaskListScreenPreviewContent(
        tasks = uiState.tasks,
        sortOrder = uiState.sortOrder,
        onTaskClick = onTaskClick,
        onAddTask = onAddTask,
        onSortOrderChange = { viewModel.setSortOrder(it) },
        onCompleteToggle = { viewModel.toggleTaskComplete(it) }
    )
}

@Preview(showBackground = true, name = "Empty list")
@Composable
private fun TaskListScreenPreviewEmpty() {
    RemindersTheme {
        TaskListScreenPreviewContent(
            tasks = emptyList(),
            sortOrder = TaskSortOrder.BY_CREATION_DATE,
            onTaskClick = {},
            onAddTask = {},
            onSortOrderChange = {},
            onCompleteToggle = {}
        )
    }
}

@Preview(showBackground = true, name = "With tasks")
@Composable
private fun TaskListScreenPreviewWithTasks() {
    RemindersTheme {
        TaskListScreenPreviewContent(
            tasks = listOf(
                Task(1, "Buy groceries", "Milk, bread, eggs", TaskPriority.HIGH, Date(), false, Date()),
                Task(2, "Call mom", "", TaskPriority.MEDIUM, null, true, Date()),
                Task(3, "Read book", "Finish chapter 5", TaskPriority.LOW, Date(), false, Date())
            ),
            sortOrder = TaskSortOrder.BY_PRIORITY,
            onTaskClick = {},
            onAddTask = {},
            onSortOrderChange = {},
            onCompleteToggle = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskListScreenPreviewContent(
    tasks: List<Task>,
    sortOrder: TaskSortOrder,
    onTaskClick: (Long) -> Unit,
    onAddTask: () -> Unit,
    onSortOrderChange: (TaskSortOrder) -> Unit,
    onCompleteToggle: (Task) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortOrder == TaskSortOrder.BY_CREATION_DATE,
                    onClick = { onSortOrderChange(TaskSortOrder.BY_CREATION_DATE) },
                    label = { Text("By date") }
                )
                FilterChip(
                    selected = sortOrder == TaskSortOrder.BY_PRIORITY,
                    onClick = { onSortOrderChange(TaskSortOrder.BY_PRIORITY) },
                    label = { Text("By priority") }
                )
                FilterChip(
                    selected = sortOrder == TaskSortOrder.BY_DUE_DATE,
                    onClick = { onSortOrderChange(TaskSortOrder.BY_DUE_DATE) },
                    label = { Text("By due date") }
                )
            }
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onClick = { onTaskClick(task.id) },
                            onCompleteToggle = { onCompleteToggle(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onCompleteToggle: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onCompleteToggle() }
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                PriorityChip(priority = task.priority)
            }
            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            task.dueDate?.let { date ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: TaskPriority) {
    val (color, text) = when (priority) {
        TaskPriority.HIGH -> MaterialTheme.colorScheme.error to "High"
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary to "Medium"
        TaskPriority.LOW -> MaterialTheme.colorScheme.primary to "Low"
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color
        )
    }
}
