package com.example.ui.screens.agenda

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PastoralTask
import com.example.i18n.AppStrings
import com.example.ui.PastoralViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldLight
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AgendaScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    var selectedViewTab by remember { mutableIntStateOf(0) } // 0: All, 1: Appointments, 2: Tasks, 3: Completed
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<PastoralTask?>(null) }

    val viewTabs = listOf(strings.all, strings.appointments, strings.tasks, strings.completed)

    val now = System.currentTimeMillis()
    val filteredTasks = tasks.filter { t ->
        when (selectedViewTab) {
            1 -> t.isAppointment && !t.isCompleted
            2 -> !t.isAppointment && !t.isCompleted
            3 -> t.isCompleted
            else -> !t.isCompleted // Active tasks/appointments
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showAddDialog = true
                },
                containerColor = Navy800,
                contentColor = GoldLight,
                modifier = Modifier.testTag("fab_add_task")
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.newTask)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // View Selector Tabs
            TabRow(
                selectedTabIndex = selectedViewTab,
                containerColor = Navy900,
                contentColor = GoldLight
            ) {
                viewTabs.forEachIndexed { index, tabName ->
                    Tab(
                        selected = selectedViewTab == index,
                        onClick = { selectedViewTab = index },
                        text = {
                            Text(
                                text = tabName,
                                fontSize = 12.sp,
                                color = if (selectedViewTab == index) GoldLight else IvoryWhite.copy(alpha = 0.7f),
                                fontWeight = if (selectedViewTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tsy misy asa na fotoana voalahatra",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks) { task ->
                        val isOverdue = !task.isCompleted && task.dueDateMillis < now
                        AgendaTaskCard(
                            task = task,
                            isOverdue = isOverdue,
                            strings = strings,
                            onToggleCompleted = { viewModel.toggleTaskCompleted(task) },
                            onEdit = {
                                taskToEdit = task
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TaskAddEditDialog(
            task = taskToEdit,
            strings = strings,
            onDismiss = { showAddDialog = false },
            onSave = { savedTask ->
                viewModel.saveTask(savedTask)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AgendaTaskCard(
    task: PastoralTask,
    isOverdue: Boolean,
    strings: AppStrings,
    onToggleCompleted: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dueDateStr = df.format(Date(task.dueDateMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("agenda_task_${task.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isOverdue) BorderStroke(1.dp, StatusRed) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Radio,
                contentDescription = null,
                tint = if (task.isCompleted) StatusGreen else GoldBase,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onToggleCompleted() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = task.priority)
                }

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (task.isAppointment) Icons.Default.EventAvailable else Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isOverdue) StatusRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isOverdue) "$dueDateStr (${strings.overdue})" else dueDateStr,
                        fontSize = 11.sp,
                        color = if (isOverdue) StatusRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                    )

                    if (task.reminderSet) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = strings.setReminder,
                            tint = GoldBase,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = strings.edit, modifier = Modifier.size(18.dp), tint = GoldBase)
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = strings.delete, modifier = Modifier.size(18.dp), tint = StatusRed)
            }
        }
    }
}

@Composable
private fun TaskAddEditDialog(
    task: PastoralTask?,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (PastoralTask) -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: "MOYENNE") }
    var isAppointment by remember { mutableStateOf(task?.isAppointment ?: false) }
    var reminderSet by remember { mutableStateOf(task?.reminderSet ?: false) }

    val priorities = listOf(
        "BASSE" to strings.priorityLow,
        "MOYENNE" to strings.priorityMedium,
        "HAUTE" to strings.priorityHigh,
        "URGENTE" to strings.priorityUrgent
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) strings.newTask else strings.edit) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lohatenin'ny asa / fotoana") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Fanazavana fanampiny") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )

                Text(strings.priority, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(priorities) { (pKey, pLabel) ->
                        FilterChip(
                            selected = priority == pKey,
                            onClick = { priority = pKey },
                            label = { Text(pLabel, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Navy800,
                                selectedLabelColor = GoldLight
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAppointment,
                        onCheckedChange = { isAppointment = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.isAppointment, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = reminderSet,
                        onCheckedChange = { reminderSet = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.setReminder, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val dueMillis = task?.dueDateMillis ?: (System.currentTimeMillis() + 86400000L)
                    val reminderMillis = if (reminderSet) dueMillis - 3600000L else null
                    onSave(
                        PastoralTask(
                            id = task?.id ?: 0L,
                            title = title,
                            description = description,
                            dueDateMillis = dueMillis,
                            isCompleted = task?.isCompleted ?: false,
                            priority = priority,
                            isAppointment = isAppointment,
                            reminderSet = reminderSet,
                            reminderTimeMillis = reminderMillis
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy800,
                    contentColor = GoldLight
                )
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
