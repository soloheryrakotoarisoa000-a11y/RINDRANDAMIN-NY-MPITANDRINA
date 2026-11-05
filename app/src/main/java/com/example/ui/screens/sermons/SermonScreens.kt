package com.example.ui.screens.sermons

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Sermon
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
fun SermonListScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val sermons by viewModel.allSermons.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    val statusFilters = listOf(
        "ALL" to strings.all,
        "A_COMMENCER" to strings.statusToStart,
        "EN_COURS" to strings.statusInProgress,
        "A_REVISER" to strings.statusToRevise,
        "TERMINEE" to strings.statusFinished,
        "PRECHEE" to strings.statusPreached
    )

    val filteredSermons = sermons.filter { sermon ->
        val matchesSearch = searchQuery.isBlank() ||
                sermon.title.contains(searchQuery, ignoreCase = true) ||
                sermon.biblicalPassage.contains(searchQuery, ignoreCase = true) ||
                sermon.themeAndObjective.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatusFilter == "ALL" || sermon.status == selectedStatusFilter

        matchesSearch && matchesStatus
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openSermon(null) },
                containerColor = Navy800,
                contentColor = GoldLight,
                modifier = Modifier.testTag("fab_add_sermon")
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.newSermon)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
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
                    .testTag("sermon_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldBase,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )

            // Status Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusFilters) { (statusKey, label) ->
                    FilterChip(
                        selected = selectedStatusFilter == statusKey,
                        onClick = { selectedStatusFilter = statusKey },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Navy800,
                            selectedLabelColor = GoldLight
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sermons List
            if (filteredSermons.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tsy misy toriteny hita",
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
                    items(filteredSermons) { sermon ->
                        SermonListItem(
                            sermon = sermon,
                            strings = strings,
                            onClick = { viewModel.openSermon(sermon) },
                            onExportPdf = { viewModel.exportSermonPdf(sermon) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SermonListItem(
    sermon: Sermon,
    strings: AppStrings,
    onClick: () -> Unit,
    onExportPdf: () -> Unit
) {
    val df = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val dateStr = df.format(Date(sermon.dateMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("sermon_item_${sermon.id}"),
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
                    text = sermon.biblicalPassage,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBase
                )
                StatusBadge(status = sermon.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = sermon.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (sermon.themeAndObjective.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sermon.themeAndObjective,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$dateStr • ${sermon.plannedDurationMinutes} min",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = onExportPdf,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = strings.exportPdf,
                        tint = GoldBase,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

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
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SermonDetailEditScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val existingSermon by viewModel.selectedSermon.collectAsStateWithLifecycle()

    var title by remember(existingSermon) { mutableStateOf(existingSermon?.title ?: "") }
    var biblicalPassage by remember(existingSermon) { mutableStateOf(existingSermon?.biblicalPassage ?: "") }
    var themeAndObjective by remember(existingSermon) { mutableStateOf(existingSermon?.themeAndObjective ?: "") }
    var introduction by remember(existingSermon) { mutableStateOf(existingSermon?.introduction ?: "") }
    var historicalContext by remember(existingSermon) { mutableStateOf(existingSermon?.historicalContext ?: "") }
    var verseExegesis by remember(existingSermon) { mutableStateOf(existingSermon?.verseExegesis ?: "") }
    var theologicalAnalysis by remember(existingSermon) { mutableStateOf(existingSermon?.theologicalAnalysis ?: "") }
    var biblicalArguments by remember(existingSermon) { mutableStateOf(existingSermon?.biblicalArguments ?: "") }
    var homileticPlan by remember(existingSermon) { mutableStateOf(existingSermon?.homileticPlan ?: "") }
    var practicalApplications by remember(existingSermon) { mutableStateOf(existingSermon?.practicalApplications ?: "") }
    var illustrations by remember(existingSermon) { mutableStateOf(existingSermon?.illustrations ?: "") }
    var conclusion by remember(existingSermon) { mutableStateOf(existingSermon?.conclusion ?: "") }
    var finalPrayer by remember(existingSermon) { mutableStateOf(existingSermon?.finalPrayer ?: "") }
    var plannedDuration by remember(existingSermon) { mutableIntStateOf(existingSermon?.plannedDurationMinutes ?: 35) }
    var progressPercent by remember(existingSermon) { mutableIntStateOf(existingSermon?.progressPercent ?: 50) }
    var status by remember(existingSermon) { mutableStateOf(existingSermon?.status ?: "EN_COURS") }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val tabs = listOf("Fototra", "Exégèse", "Drafitra & Votoatiny", "Fampiharana")

    val statuses = listOf(
        "A_COMMENCER" to strings.statusToStart,
        "EN_COURS" to strings.statusInProgress,
        "A_REVISER" to strings.statusToRevise,
        "TERMINEE" to strings.statusFinished,
        "PRECHEE" to strings.statusPreached
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
                    if (existingSermon != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = StatusRed)
                        }

                        IconButton(
                            onClick = {
                                val s = existingSermon?.copy(
                                    title = title,
                                    biblicalPassage = biblicalPassage,
                                    themeAndObjective = themeAndObjective,
                                    introduction = introduction,
                                    historicalContext = historicalContext,
                                    verseExegesis = verseExegesis,
                                    homileticPlan = homileticPlan,
                                    practicalApplications = practicalApplications,
                                    conclusion = conclusion,
                                    finalPrayer = finalPrayer,
                                    plannedDurationMinutes = plannedDuration,
                                    progressPercent = progressPercent,
                                    status = status
                                )
                                if (s != null) viewModel.exportSermonPdf(s)
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = strings.exportPdf, tint = GoldBase)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (title.isBlank()) return@Button
                            val sermon = Sermon(
                                id = existingSermon?.id ?: 0L,
                                title = title,
                                dateMillis = existingSermon?.dateMillis ?: System.currentTimeMillis(),
                                biblicalPassage = biblicalPassage,
                                themeAndObjective = themeAndObjective,
                                introduction = introduction,
                                historicalContext = historicalContext,
                                verseExegesis = verseExegesis,
                                theologicalAnalysis = theologicalAnalysis,
                                biblicalArguments = biblicalArguments,
                                homileticPlan = homileticPlan,
                                practicalApplications = practicalApplications,
                                illustrations = illustrations,
                                conclusion = conclusion,
                                finalPrayer = finalPrayer,
                                plannedDurationMinutes = plannedDuration,
                                progressPercent = progressPercent,
                                status = status
                            )
                            viewModel.saveSermon(sermon)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Navy800,
                            contentColor = GoldLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_sermon_button")
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
            // Tab Row
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
                    0 -> { // Basic Details & Status
                        item {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text(strings.sermonTitle) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sermon_title_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = biblicalPassage,
                                onValueChange = { biblicalPassage = it },
                                label = { Text(strings.biblicalPassage) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sermon_passage_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = themeAndObjective,
                                onValueChange = { themeAndObjective = it },
                                label = { Text(strings.themeAndObjective) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            Text(
                                text = strings.status,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
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
                            Text(
                                text = "${strings.progress}: $progressPercent%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = progressPercent.toFloat(),
                                onValueChange = { progressPercent = it.toInt() },
                                valueRange = 0f..100f,
                                steps = 19,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldBase,
                                    activeTrackColor = GoldBase
                                )
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = plannedDuration.toString(),
                                onValueChange = { plannedDuration = it.toIntOrNull() ?: 30 },
                                label = { Text(strings.plannedDuration) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    1 -> { // Exégèse & Context
                        item {
                            OutlinedTextField(
                                value = historicalContext,
                                onValueChange = { historicalContext = it },
                                label = { Text(strings.historicalContext) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = verseExegesis,
                                onValueChange = { verseExegesis = it },
                                label = { Text(strings.verseExegesis) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 6,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = theologicalAnalysis,
                                onValueChange = { theologicalAnalysis = it },
                                label = { Text(strings.theologicalAnalysis) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = biblicalArguments,
                                onValueChange = { biblicalArguments = it },
                                label = { Text(strings.biblicalArguments) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    2 -> { // Plan & Structure
                        item {
                            OutlinedTextField(
                                value = introduction,
                                onValueChange = { introduction = it },
                                label = { Text(strings.introduction) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = homileticPlan,
                                onValueChange = { homileticPlan = it },
                                label = { Text(strings.homileticPlan) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 8,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    3 -> { // Applications & Prayer
                        item {
                            OutlinedTextField(
                                value = practicalApplications,
                                onValueChange = { practicalApplications = it },
                                label = { Text(strings.practicalApplications) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 5,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = illustrations,
                                onValueChange = { illustrations = it },
                                label = { Text(strings.illustrations) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = conclusion,
                                onValueChange = { conclusion = it },
                                label = { Text(strings.conclusion) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = finalPrayer,
                                onValueChange = { finalPrayer = it },
                                label = { Text(strings.finalPrayer) },
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
                        existingSermon?.let { viewModel.deleteSermon(it) }
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
