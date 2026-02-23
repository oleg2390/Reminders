package com.example.reminders.presentation.taskdetail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.reminders.domain.model.TaskPriority
import com.example.reminders.ui.theme.RemindersTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    onBack: () -> Unit,
    taskId: Long?
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
        when {
            uiState.saveSuccess -> {
                Toast.makeText(context, "Task saved", android.widget.Toast.LENGTH_SHORT).show()
                viewModel.clearSaveSuccess()
                onBack()
            }
            uiState.deleteSuccess -> {
                Toast.makeText(context, "Task deleted", android.widget.Toast.LENGTH_SHORT).show()
                viewModel.clearSaveSuccess()
                onBack()
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    var title by remember { mutableStateOf(uiState.task?.title ?: "") }
    var description by remember { mutableStateOf(uiState.task?.description ?: "") }
    var dueDate by remember { mutableStateOf(uiState.task?.dueDate) }
    var priority by remember { mutableStateOf(uiState.task?.priority ?: TaskPriority.MEDIUM) }
    var isCompleted by remember { mutableStateOf(uiState.task?.isCompleted ?: false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.task) {
        uiState.task?.let { task ->
            title = task.title
            description = task.description
            dueDate = task.dueDate
            priority = task.priority
            isCompleted = task.isCompleted
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "New task" else "Edit task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dueDate?.let { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Due date") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    DatePickerButton(
                        initialDate = dueDate,
                        onDateSelected = { dueDate = it }
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Priority", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = priority == TaskPriority.LOW,
                    onClick = { priority = TaskPriority.LOW },
                    label = { Text("Low") }
                )
                FilterChip(
                    selected = priority == TaskPriority.MEDIUM,
                    onClick = { priority = TaskPriority.MEDIUM },
                    label = { Text("Medium") }
                )
                FilterChip(
                    selected = priority == TaskPriority.HIGH,
                    onClick = { priority = TaskPriority.HIGH },
                    label = { Text("High") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.Switch(
                    checked = isCompleted,
                    onCheckedChange = { isCompleted = it }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Completed", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (uiState.task != null) {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.saveTask(title, description, priority, dueDate, isCompleted)
                        } else {
                            Toast.makeText(context, "Enter task title", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete task?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        uiState.task?.let { viewModel.deleteTask(it) }
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "New task")
@Composable
private fun TaskDetailScreenPreviewNew() {
    RemindersTheme {
        TaskDetailScreenPreviewContent(
            title = "",
            description = "",
            dueDate = null,
            priority = TaskPriority.MEDIUM,
            isCompleted = false,
            isNewTask = true
        )
    }
}

@Preview(showBackground = true, name = "Edit task")
@Composable
private fun TaskDetailScreenPreviewEdit() {
    RemindersTheme {
        TaskDetailScreenPreviewContent(
            title = "Buy groceries",
            description = "Milk, bread, eggs, butter",
            dueDate = Date(),
            priority = TaskPriority.HIGH,
            isCompleted = false,
            isNewTask = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailScreenPreviewContent(
    title: String,
    description: String,
    dueDate: Date?,
    priority: TaskPriority,
    isCompleted: Boolean,
    isNewTask: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNewTask) "New task" else "Edit task") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dueDate?.let { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(it) } ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Due date") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Priority", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = priority == TaskPriority.LOW,
                    onClick = { },
                    label = { Text("Low") }
                )
                FilterChip(
                    selected = priority == TaskPriority.MEDIUM,
                    onClick = { },
                    label = { Text("Medium") }
                )
                FilterChip(
                    selected = priority == TaskPriority.HIGH,
                    onClick = { },
                    label = { Text("High") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                androidx.compose.material3.Switch(
                    checked = isCompleted,
                    onCheckedChange = { }
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Completed", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isNewTask) {
                    TextButton(
                        onClick = { },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { }) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun DatePickerButton(
    initialDate: Date?,
    onDateSelected: (Date) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance().apply { initialDate?.let { time = it } } }

    IconButton(
        onClick = {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    onDateSelected(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    ) {
        Icon(
            imageVector = Icons.Default.Event,
            contentDescription = "Select date"
        )
    }
}
