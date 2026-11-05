package com.example.ui.screens.events

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
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.ChurchEvent
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
fun EventListScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("ALL") }

    val typeFilters = listOf(
        "ALL" to strings.all,
        "CONFERENCE" to strings.typeConference,
        "SEMINAIRE" to strings.typeSeminar,
        "FORMATION" to strings.typeTraining,
        "EVANGELISATION" to strings.typeEvangelism,
        "CULTE_SPECIAL" to strings.typeSpecialService,
        "PRIERE" to strings.typePrayerDay,
        "ENSEIGNEMENT_FOI" to strings.typeFaithTeaching,
        "MARIAGE" to strings.typeWedding,
        "FUNERAILLES" to strings.typeFuneral
    )

    val filteredEvents = events.filter { e ->
        val matchesSearch = searchQuery.isBlank() ||
                e.title.contains(searchQuery, ignoreCase = true) ||
                e.description.contains(searchQuery, ignoreCase = true) ||
                e.speakers.contains(searchQuery, ignoreCase = true)

        val matchesType = selectedTypeFilter == "ALL" || e.eventType == selectedTypeFilter

        matchesSearch && matchesType
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openEvent(null) },
                containerColor = Navy800,
                contentColor = GoldLight,
                modifier = Modifier.testTag("fab_add_event")
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.newEvent)
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
                    .testTag("event_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldBase,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )

            // Types filter
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(typeFilters) { (typeKey, label) ->
                    FilterChip(
                        selected = selectedTypeFilter == typeKey,
                        onClick = { selectedTypeFilter = typeKey },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Navy800,
                            selectedLabelColor = GoldLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tsy misy hetsika hita",
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
                    items(filteredEvents) { event ->
                        EventListItem(
                            event = event,
                            currency = currency,
                            onClick = { viewModel.openEvent(event) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventListItem(
    event: ChurchEvent,
    currency: String,
    onClick: () -> Unit
) {
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val startStr = df.format(Date(event.startDateMillis))
    val endStr = df.format(Date(event.endDateMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("event_item_${event.id}"),
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
                    text = event.eventType,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBase
                )
                StatusBadge(status = event.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$startStr - $endStr",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (event.plannedBudget > 0.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tetibola: %,.0f %s • Lany: %,.0f %s".format(
                        event.plannedBudget, currency,
                        event.realizedExpenses, currency
                    ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EventDetailEditScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val existingEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    var title by remember(existingEvent) { mutableStateOf(existingEvent?.title ?: "") }
    var eventType by remember(existingEvent) { mutableStateOf(existingEvent?.eventType ?: "CONFERENCE") }
    var description by remember(existingEvent) { mutableStateOf(existingEvent?.description ?: "") }
    var scheduleProgram by remember(existingEvent) { mutableStateOf(existingEvent?.scheduleProgram ?: "") }
    var speakers by remember(existingEvent) { mutableStateOf(existingEvent?.speakers ?: "") }
    var leaders by remember(existingEvent) { mutableStateOf(existingEvent?.leaders ?: "") }
    var tasksTodo by remember(existingEvent) { mutableStateOf(existingEvent?.tasksTodo ?: "") }
    var materialNeeds by remember(existingEvent) { mutableStateOf(existingEvent?.materialNeeds ?: "") }
    var plannedBudget by remember(existingEvent) { mutableDoubleStateOf(existingEvent?.plannedBudget ?: 0.0) }
    var realizedExpenses by remember(existingEvent) { mutableDoubleStateOf(existingEvent?.realizedExpenses ?: 0.0) }
    var finalSummaryReview by remember(existingEvent) { mutableStateOf(existingEvent?.finalSummaryReview ?: "") }
    var status by remember(existingEvent) { mutableStateOf(existingEvent?.status ?: "PLANIFIE") }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val tabs = listOf("Fampahalalana", "Fandaharana & Mpandaha-teny", "Asa & Fitaovana", "Teti-bola & Bilan")

    val types = listOf(
        "CONFERENCE" to strings.typeConference,
        "SEMINAIRE" to strings.typeSeminar,
        "FORMATION" to strings.typeTraining,
        "EVANGELISATION" to strings.typeEvangelism,
        "CULTE_SPECIAL" to strings.typeSpecialService,
        "PRIERE" to strings.typePrayerDay,
        "ENSEIGNEMENT_FOI" to strings.typeFaithTeaching,
        "MARIAGE" to strings.typeWedding,
        "FUNERAILLES" to strings.typeFuneral
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
                    if (existingEvent != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = StatusRed)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (title.isBlank()) return@Button
                            val churchEvent = ChurchEvent(
                                id = existingEvent?.id ?: 0L,
                                title = title,
                                eventType = eventType,
                                description = description,
                                startDateMillis = existingEvent?.startDateMillis ?: System.currentTimeMillis(),
                                endDateMillis = existingEvent?.endDateMillis ?: System.currentTimeMillis(),
                                scheduleProgram = scheduleProgram,
                                speakers = speakers,
                                leaders = leaders,
                                tasksTodo = tasksTodo,
                                materialNeeds = materialNeeds,
                                plannedBudget = plannedBudget,
                                realizedExpenses = realizedExpenses,
                                finalSummaryReview = finalSummaryReview,
                                status = status
                            )
                            viewModel.saveEvent(churchEvent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Navy800,
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_event_button")
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
                                label = { Text(strings.eventTitle) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("event_title_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            Text(
                                text = strings.eventType,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(types) { (tKey, tLabel) ->
                                    FilterChip(
                                        selected = eventType == tKey,
                                        onClick = { eventType = tKey },
                                        label = { Text(tLabel, fontSize = 12.sp) },
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
                                value = description,
                                onValueChange = { description = it },
                                label = { Text(strings.description) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    1 -> { // Schedule & Speakers
                        item {
                            OutlinedTextField(
                                value = scheduleProgram,
                                onValueChange = { scheduleProgram = it },
                                label = { Text(strings.scheduleProgram) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 6,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = speakers,
                                onValueChange = { speakers = it },
                                label = { Text(strings.speakers) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = leaders,
                                onValueChange = { leaders = it },
                                label = { Text(strings.eventLeaders) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    2 -> { // Tasks & Material Needs
                        item {
                            OutlinedTextField(
                                value = tasksTodo,
                                onValueChange = { tasksTodo = it },
                                label = { Text(strings.eventTasks) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 6,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = materialNeeds,
                                onValueChange = { materialNeeds = it },
                                label = { Text(strings.materialNeeds) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 5,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    3 -> { // Budget & Final Summary
                        item {
                            OutlinedTextField(
                                value = plannedBudget.toString(),
                                onValueChange = { plannedBudget = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("${strings.plannedBudget} ($currency)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = realizedExpenses.toString(),
                                onValueChange = { realizedExpenses = it.toDoubleOrNull() ?: 0.0 },
                                label = { Text("${strings.realizedExpenses} ($currency)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = finalSummaryReview,
                                onValueChange = { finalSummaryReview = it },
                                label = { Text(strings.finalSummaryReview) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 5,
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
                        existingEvent?.let { viewModel.deleteEvent(it) }
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
