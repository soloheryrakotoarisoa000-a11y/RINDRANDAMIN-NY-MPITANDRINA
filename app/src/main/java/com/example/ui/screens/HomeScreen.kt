package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChurchEvent
import com.example.data.model.Meeting
import com.example.data.model.PastoralTask
import com.example.data.model.Sermon
import com.example.i18n.AppStrings
import com.example.ui.PastoralViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.StatusBadge
import com.example.ui.components.VerseCard
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
fun HomeScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val sermons by viewModel.allSermons.collectAsStateWithLifecycle()
    val meetings by viewModel.allMeetings.collectAsStateWithLifecycle()
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val urgentTasks by viewModel.urgentTasks.collectAsStateWithLifecycle()
    val finances by viewModel.allFinances.collectAsStateWithLifecycle()

    val upcomingSermons = sermons.take(2)
    val upcomingMeetings = meetings.take(2)
    val upcomingEvents = events.take(2)

    val dateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault())
    val todayDateStr = dateFormat.format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Welcome Greeting Banner
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = strings.welcomePastor,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = todayDateStr.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Mandatory Biblical Verse Card (Central on Home screen)
        item {
            VerseCard()
        }

        // 3. Pastoral Summary Progress Metrics
        item {
            PastoralSummarySection(
                sermonCount = sermons.size,
                meetingCount = meetings.size,
                eventCount = events.size,
                pendingTasksCount = tasks.count { !it.isCompleted },
                strings = strings,
                onNavigate = onNavigate
            )
        }

        // 4. Main 8 Access Modules Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = strings.pastoralSummary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                DashboardEightModulesGrid(
                    strings = strings,
                    onNavigate = onNavigate
                )
            }
        }

        // 5. Urgent Tasks & Reminders
        if (urgentTasks.isNotEmpty()) {
            item {
                SectionHeader(
                    title = strings.urgentTasks,
                    actionText = strings.viewAll,
                    onAction = { onNavigate(ScreenDestination.AGENDA) }
                )
            }
            items(urgentTasks) { task ->
                TaskCardItem(
                    task = task,
                    strings = strings,
                    onToggle = { viewModel.toggleTaskCompleted(task) }
                )
            }
        }

        // 6. Upcoming Sermons
        item {
            SectionHeader(
                title = strings.upcomingSermons,
                actionText = strings.viewAll,
                onAction = { onNavigate(ScreenDestination.SERMONS) }
            )
        }
        if (upcomingSermons.isEmpty()) {
            item {
                EmptyPlaceholderCard(text = "Tsy misy toriteny voalahatra") {
                    viewModel.openSermon(null)
                }
            }
        } else {
            items(upcomingSermons) { sermon ->
                SermonCardItem(
                    sermon = sermon,
                    strings = strings,
                    onClick = { viewModel.openSermon(sermon) }
                )
            }
        }

        // 7. Upcoming Meetings
        item {
            SectionHeader(
                title = strings.upcomingMeetings,
                actionText = strings.viewAll,
                onAction = { onNavigate(ScreenDestination.MEETINGS) }
            )
        }
        if (upcomingMeetings.isEmpty()) {
            item {
                EmptyPlaceholderCard(text = "Tsy misy fivoriana voalahatra") {
                    viewModel.openMeeting(null)
                }
            }
        } else {
            items(upcomingMeetings) { meeting ->
                MeetingCardItem(
                    meeting = meeting,
                    strings = strings,
                    onClick = { viewModel.openMeeting(meeting) }
                )
            }
        }

        // 8. Scheduled Events
        if (upcomingEvents.isNotEmpty()) {
            item {
                SectionHeader(
                    title = strings.scheduledEvents,
                    actionText = strings.viewAll,
                    onAction = { onNavigate(ScreenDestination.EVENTS) }
                )
            }
            items(upcomingEvents) { event ->
                EventCardItem(
                    event = event,
                    strings = strings,
                    onClick = { viewModel.openEvent(event) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = actionText,
            color = GoldBase,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onAction() }
        )
    }
}

@Composable
private fun PastoralSummarySection(
    sermonCount: Int,
    meetingCount: Int,
    eventCount: Int,
    pendingTasksCount: Int,
    strings: AppStrings,
    onNavigate: (ScreenDestination) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem(
                count = sermonCount,
                label = strings.navSermons,
                icon = Icons.Default.MenuBook,
                iconColor = Color(0xFF1E3A6E),
                onClick = { onNavigate(ScreenDestination.SERMONS) }
            )
            SummaryItem(
                count = meetingCount,
                label = strings.navMeetings,
                icon = Icons.Default.Groups,
                iconColor = GoldBase,
                onClick = { onNavigate(ScreenDestination.MEETINGS) }
            )
            SummaryItem(
                count = eventCount,
                label = strings.navEvents,
                icon = Icons.Default.Event,
                iconColor = Color(0xFF00796B),
                onClick = { onNavigate(ScreenDestination.EVENTS) }
            )
            SummaryItem(
                count = pendingTasksCount,
                label = strings.tasks,
                icon = Icons.Default.NotificationsActive,
                iconColor = if (pendingTasksCount > 0) StatusRed else StatusGreen,
                onClick = { onNavigate(ScreenDestination.AGENDA) }
            )
        }
    }
}

@Composable
private fun SummaryItem(
    count: Int,
    label: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DashboardEightModulesGrid(
    strings: AppStrings,
    onNavigate: (ScreenDestination) -> Unit
) {
    val modules = listOf(
        Triple(strings.navSermons, Icons.Default.MenuBook, ScreenDestination.SERMONS),
        Triple(strings.navMeetings, Icons.Default.Groups, ScreenDestination.MEETINGS),
        Triple(strings.navEvents, Icons.Default.Event, ScreenDestination.EVENTS),
        Triple(strings.navAgenda, Icons.Default.CalendarMonth, ScreenDestination.AGENDA),
        Triple(strings.navAdmin, Icons.Default.Badge, ScreenDestination.ADMIN),
        Triple(strings.navFinances, Icons.Default.AccountBalance, ScreenDestination.FINANCES),
        Triple(strings.navArchives, Icons.Default.Archive, ScreenDestination.ARCHIVES),
        Triple(strings.navSettings, Icons.Default.Settings, ScreenDestination.SETTINGS)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in modules.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModuleCard(
                    title = modules[i].first,
                    icon = modules[i].second,
                    destination = modules[i].third,
                    onNavigate = onNavigate,
                    modifier = Modifier.weight(1f)
                )
                if (i + 1 < modules.size) {
                    ModuleCard(
                        title = modules[i + 1].first,
                        icon = modules[i + 1].second,
                        destination = modules[i + 1].third,
                        onNavigate = onNavigate,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    icon: ImageVector,
    destination: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable { onNavigate(destination) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Navy800.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = GoldBase,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SermonCardItem(
    sermon: Sermon,
    strings: AppStrings,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sermon.biblicalPassage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBase
                )
                StatusBadge(status = sermon.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sermon.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (sermon.themeAndObjective.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sermon.themeAndObjective,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { sermon.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GoldBase,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${sermon.progressPercent}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MeetingCardItem(
    meeting: Meeting,
    strings: AppStrings,
    onClick: () -> Unit
) {
    val df = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateStr = df.format(Date(meeting.dateTimeMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Navy800.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = Navy800,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meeting.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$dateStr • ${meeting.location.ifBlank { "Fiangonana" }}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(status = meeting.status)
        }
    }
}

@Composable
private fun EventCardItem(
    event: ChurchEvent,
    strings: AppStrings,
    onClick: () -> Unit
) {
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateStr = df.format(Date(event.startDateMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GoldBase.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = GoldBase,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$dateStr • ${event.eventType}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(status = event.status)
        }
    }
}

@Composable
private fun TaskCardItem(
    task: PastoralTask,
    strings: AppStrings,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Radio,
                contentDescription = null,
                tint = if (task.isCompleted) StatusGreen else GoldBase,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onToggle() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(status = task.priority)
        }
    }
}

@Composable
private fun EmptyPlaceholderCard(
    text: String,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = onAdd,
                modifier = Modifier.height(34.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hanampy", fontSize = 12.sp)
            }
        }
    }
}
