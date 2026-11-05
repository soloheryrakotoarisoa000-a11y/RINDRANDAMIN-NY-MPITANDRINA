package com.example.ui.screens.meetings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Meeting
import com.example.i18n.AppStrings
import com.example.ui.PastoralViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldLight
import com.example.ui.theme.IvoryWhite
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MeetingListScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val meetings by viewModel.allMeetings.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val categoryFilters = listOf(
        "ALL" to strings.all,
        "BUREAU" to strings.catBureau,
        "RESPONSABLES" to strings.catLeaders,
        "COMMISSION" to strings.catCommission,
        "DEPARTEMENT" to strings.catDepartment,
        "AUTRE" to strings.catOther
    )

    val filteredMeetings = meetings.filter { m ->
        val matchesSearch = searchQuery.isBlank() ||
                m.title.contains(searchQuery, ignoreCase = true) ||
                m.participants.contains(searchQuery, ignoreCase = true) ||
                m.agenda.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategoryFilter == "ALL" || m.category == selectedCategoryFilter

        matchesSearch && matchesCategory
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openMeeting(null) },
                containerColor = Navy800,
                contentColor = GoldLight,
                modifier = Modifier.testTag("fab_add_meeting")
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.newMeeting)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(strings.search) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldBase) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("meeting_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldBase,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )

            // Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryFilters) { (catKey, label) ->
                    FilterChip(
                        selected = selectedCategoryFilter == catKey,
                        onClick = { selectedCategoryFilter = catKey },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Navy800,
                            selectedLabelColor = GoldLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredMeetings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tsy misy fivoriana hita",
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
                    items(filteredMeetings) { meeting ->
                        MeetingListItem(
                            meeting = meeting,
                            strings = strings,
                            onClick = { viewModel.openMeeting(meeting) },
                            onExportPdf = { viewModel.exportMeetingMinutesPdf(meeting) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MeetingListItem(
    meeting: Meeting,
    strings: AppStrings,
    onClick: () -> Unit,
    onExportPdf: () -> Unit
) {
    val df = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = df.format(Date(meeting.dateTimeMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("meeting_item_${meeting.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = meeting.category,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBase
                )
                StatusBadge(status = meeting.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = meeting.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (meeting.objective.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meeting.objective,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$dateStr • ${meeting.location.ifBlank { "Fiangonana" }}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = onExportPdf,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = strings.generateMinutesPdf,
                        tint = GoldBase,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MeetingDetailEditScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val existingMeeting by viewModel.selectedMeeting.collectAsStateWithLifecycle()

    var title by remember(existingMeeting) { mutableStateOf(existingMeeting?.title ?: "") }
    var category by remember(existingMeeting) { mutableStateOf(existingMeeting?.category ?: "BUREAU") }
    var location by remember(existingMeeting) { mutableStateOf(existingMeeting?.location ?: "") }
    var participants by remember(existingMeeting) { mutableStateOf(existingMeeting?.participants ?: "") }
    var objective by remember(existingMeeting) { mutableStateOf(existingMeeting?.objective ?: "") }
    var agenda by remember(existingMeeting) { mutableStateOf(existingMeeting?.agenda ?: "") }
    var discussionPoints by remember(existingMeeting) { mutableStateOf(existingMeeting?.discussionPoints ?: "") }
    var decisionsTaken by remember(existingMeeting) { mutableStateOf(existingMeeting?.decisionsTaken ?: "") }
    var designatedLeaders by remember(existingMeeting) { mutableStateOf(existingMeeting?.designatedLeaders ?: "") }
    var deadlines by remember(existingMeeting) { mutableStateOf(existingMeeting?.deadlines ?: "") }
    var minutesNotes by remember(existingMeeting) { mutableStateOf(existingMeeting?.minutesNotes ?: "") }
    var observations by remember(existingMeeting) { mutableStateOf(existingMeeting?.observations ?: "") }
    var status by remember(existingMeeting) { mutableStateOf(existingMeeting?.status ?: "PREVUE") }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val tabs = listOf("Fampahalalana", "Ordre du jour", "Fanapahan-kevitra", "Procès-verbal (PV)")

    val categories = listOf(
        "BUREAU" to strings.catBureau,
        "RESPONSABLES" to strings.catLeaders,
        "COMMISSION" to strings.catCommission,
        "DEPARTEMENT" to strings.catDepartment,
        "AUTRE" to strings.catOther
    )

    val statuses = listOf(
        "PREVUE" to strings.meetingPlanned,
        "EN_COURS" to strings.meetingOngoing,
        "CLOTUREE" to strings.meetingClosed,
        "REPORTEE" to strings.meetingPostponed
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (existingMeeting != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = StatusRed)
                        }

                        IconButton(
                            onClick = {
                                val m = existingMeeting?.copy(
                                    title = title,
                                    category = category,
                                    location = location,
                                    participants = participants,
                                    objective = objective,
                                    agenda = agenda,
                                    discussionPoints = discussionPoints,
                                    decisionsTaken = decisionsTaken,
                                    designatedLeaders = designatedLeaders,
                                    deadlines = deadlines,
                                    minutesNotes = minutesNotes,
                                    observations = observations,
                                    status = status
                                )
                                if (m != null) viewModel.exportMeetingMinutesPdf(m)
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = strings.generateMinutesPdf, tint = GoldBase)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (title.isBlank()) return@Button
                            val meeting = Meeting(
                                id = existingMeeting?.id ?: 0L,
                                title = title,
                                category = category,
                                dateTimeMillis = existingMeeting?.dateTimeMillis ?: System.currentTimeMillis(),
                                location = location,
                                participants = participants,
                                objective = objective,
                                agenda = agenda,
                                discussionPoints = discussionPoints,
                                decisionsTaken = decisionsTaken,
                                designatedLeaders = designatedLeaders,
                                deadlines = deadlines,
                                minutesNotes = minutesNotes,
                                observations = observations,
                                status = status
                            )
                            viewModel.saveMeeting(meeting)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Navy800,
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_meeting_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.save, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Navy900,
                contentColor = GoldLight,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldBase
                    )
                }
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tabTitle,
                                color = if (selectedTab == index) GoldLight else IvoryWhite.copy(alpha = 0.7f),
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                when (selectedTab) {
                    0 -> { // General Info
                        item {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text(strings.meetingTitle) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("meeting_title_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            Text(
                                text = strings.meetingCategory,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(categories) { (catKey, catLabel) ->
                                    FilterChip(
                                        selected = category == catKey,
                                        onClick = { category = catKey },
                                        label = { Text(catLabel, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Navy800,
                                            selectedLabelColor = GoldLight
                                        )
                                    )
                                }
                            }
                        }
                        item {
                            Text(
                                text = strings.status,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(statuses) { (stKey, stLabel) ->
                                    FilterChip(
                                        selected = status == stKey,
                                        onClick = { status = stKey },
                                        label = { Text(stLabel, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Navy800,
                                            selectedLabelColor = GoldLight
                                        )
                                    )
                                }
                            }
                        }
                        item {
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                label = { Text(strings.location) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = participants,
                                onValueChange = { participants = it },
                                label = { Text(strings.participants) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = objective,
                                onValueChange = { objective = it },
                                label = { Text(strings.meetingObjective) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    1 -> { // Agenda & Discussion
                        item {
                            OutlinedTextField(
                                value = agenda,
                                onValueChange = { agenda = it },
                                label = { Text(strings.agenda) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 6,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = discussionPoints,
                                onValueChange = { discussionPoints = it },
                                label = { Text(strings.discussionPoints) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 6,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    2 -> { // Decisions & Leaders
                        item {
                            OutlinedTextField(
                                value = decisionsTaken,
                                onValueChange = { decisionsTaken = it },
                                label = { Text(strings.decisionsTaken) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 5,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = designatedLeaders,
                                onValueChange = { designatedLeaders = it },
                                label = { Text(strings.designatedLeaders) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = deadlines,
                                onValueChange = { deadlines = it },
                                label = { Text(strings.deadlines) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    3 -> { // Minutes (PV) & Observations
                        item {
                            OutlinedTextField(
                                value = minutesNotes,
                                onValueChange = { minutesNotes = it },
                                label = { Text(strings.minutesNotes) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 8,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = observations,
                                onValueChange = { observations = it },
                                label = { Text(strings.observations) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(strings.confirmDelete) },
            text = { Text(strings.deletePrompt) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        existingMeeting?.let { viewModel.deleteMeeting(it) }
                    }
                ) {
                    Text(strings.yes, color = StatusRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(strings.no)
                }
            }
        )
    }
}
