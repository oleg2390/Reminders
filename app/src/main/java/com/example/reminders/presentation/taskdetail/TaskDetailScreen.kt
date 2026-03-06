package com.example.reminders.presentation.taskdetail

import android.app.DatePickerDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reminders.R
import com.example.reminders.domain.model.TaskPriority
import com.example.reminders.ui.theme.RemindersTheme
import com.example.reminders.utils.DateTextFormatter
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onCompleteChange: (Boolean) -> Unit,
    onDeleteDialogChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess) {
        when {
            uiState.saveSuccess -> {
                Toast.makeText(context, R.string.task_saved, Toast.LENGTH_SHORT).show()
                onBack()
            }

            uiState.deleteSuccess -> {
                Toast.makeText(context, R.string.task_deleted, Toast.LENGTH_SHORT).show()
                onBack()
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isNewTask) stringResource(R.string.new_task)
                        else stringResource(R.string.edit_task)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.task_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text(stringResource(R.string.task_description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = DateTextFormatter.date(uiState.dueDateMillis).orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.task_due_date)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    DatePickerButton(
                        initialDate = uiState.dueDateMillis?.let { Date(it) },
                        onDateSelected = { onDueDateChange(it.time) }
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.task_priority),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.priority == TaskPriority.LOW,
                    onClick = { onPriorityChange(TaskPriority.LOW) },
                    label = { Text(stringResource(R.string.priority_low)) }
                )
                FilterChip(
                    selected = uiState.priority == TaskPriority.MEDIUM,
                    onClick = { onPriorityChange(TaskPriority.MEDIUM) },
                    label = { Text(stringResource(R.string.priority_medium)) }
                )
                FilterChip(
                    selected = uiState.priority == TaskPriority.HIGH,
                    onClick = { onPriorityChange(TaskPriority.HIGH) },
                    label = { Text(stringResource(R.string.priority_high)) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = uiState.isCompleted,
                    onCheckedChange = onCompleteChange
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(R.string.completed),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (uiState.task != null) {
                    TextButton(
                        onClick = { onDeleteDialogChange(true) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (uiState.title.isNotBlank()) {
                            onSaveClick()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.enter_task_title),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }

    if (uiState.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { onDeleteDialogChange(false) },
            title = { Text(stringResource(R.string.delete_task)) },
            text = { Text(stringResource(R.string.this_action_cannot_be_undone)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        onDeleteDialogChange(false)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { onDeleteDialogChange(false) }) {
                    Text(stringResource(R.string.cancel))
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
                title = {
                    Text(
                        if (isNewTask) stringResource(R.string.new_task)
                        else stringResource(R.string.edit_task)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
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
                label = { Text(stringResource(R.string.task_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { },
                label = { Text(stringResource(R.string.task_description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = DateTextFormatter.date(dueDate?.time).orEmpty(),
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.task_due_date)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.task_priority),
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = priority == TaskPriority.LOW,
                    onClick = { },
                    label = { Text(stringResource(R.string.priority_low)) }
                )
                FilterChip(
                    selected = priority == TaskPriority.MEDIUM,
                    onClick = { },
                    label = { Text(stringResource(R.string.priority_medium)) }
                )
                FilterChip(
                    selected = priority == TaskPriority.HIGH,
                    onClick = { },
                    label = { Text(stringResource(R.string.priority_high)) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
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
                        Text(stringResource(R.string.delete))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { }) {
                    Text(stringResource(R.string.save))
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
            DatePickerDialog(
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
            contentDescription = stringResource(R.string.select_date)
        )
    }
}
