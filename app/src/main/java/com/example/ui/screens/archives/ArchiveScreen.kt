package com.example.ui.screens.archives

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.i18n.AppStrings
import com.example.ui.PastoralViewModel
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldLight
import com.example.ui.theme.Navy800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ArchiveItem(
    val id: Long,
    val title: String,
    val subtitle: String,
    val categoryName: String,
    val dateMillis: Long,
    val icon: ImageVector,
    val onOpen: () -> Unit,
    val onExportPdf: (() -> Unit)? = null
)

@Composable
fun ArchiveScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val sermons by viewModel.allSermons.collectAsStateWithLifecycle()
    val meetings by viewModel.allMeetings.collectAsStateWithLifecycle()
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val documents by viewModel.allDocuments.collectAsStateWithLifecycle()
    val finances by viewModel.allFinances.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val categories = listOf(
        "ALL" to strings.all,
        "SERMON" to strings.navSermons,
        "MEETING" to strings.navMeetings,
        "EVENT" to strings.navEvents,
        "DOCUMENT" to strings.adminDocuments,
        "FINANCE" to strings.navFinances
    )

    val allArchiveItems = mutableListOf<ArchiveItem>()

    for (s in sermons) {
        allArchiveItems.add(
            ArchiveItem(
                id = s.id,
                title = s.title,
                subtitle = "${s.biblicalPassage} • ${s.themeAndObjective}",
                categoryName = strings.navSermons,
                dateMillis = s.dateMillis,
                icon = Icons.Default.MenuBook,
                onOpen = { viewModel.openSermon(s) },
                onExportPdf = { viewModel.exportSermonPdf(s) }
            )
        )
    }

    for (m in meetings) {
        allArchiveItems.add(
            ArchiveItem(
                id = m.id,
                title = m.title,
                subtitle = "${m.category} • ${m.location}",
                categoryName = strings.navMeetings,
                dateMillis = m.dateTimeMillis,
                icon = Icons.Default.Groups,
                onOpen = { viewModel.openMeeting(m) },
                onExportPdf = { viewModel.exportMeetingMinutesPdf(m) }
            )
        )
    }

    for (e in events) {
        allArchiveItems.add(
            ArchiveItem(
                id = e.id,
                title = e.title,
                subtitle = "${e.eventType} • ${e.description}",
                categoryName = strings.navEvents,
                dateMillis = e.startDateMillis,
                icon = Icons.Default.Event,
                onOpen = { viewModel.openEvent(e) }
            )
        )
    }

    for (d in documents) {
        allArchiveItems.add(
            ArchiveItem(
                id = d.id,
                title = d.title,
                subtitle = "${d.documentType} • ${d.referenceNumber}",
                categoryName = strings.adminDocuments,
                dateMillis = d.dateMillis,
                icon = Icons.Default.Description,
                onOpen = {}
            )
        )
    }

    for (f in finances) {
        allArchiveItems.add(
            ArchiveItem(
                id = f.id,
                title = f.title,
                subtitle = "${f.transactionType} • %,.0f %s".format(f.amount, currency),
                categoryName = strings.navFinances,
                dateMillis = f.dateMillis,
                icon = Icons.Default.AccountBalance,
                onOpen = {}
            )
        )
    }

    allArchiveItems.sortByDescending { it.dateMillis }

    val filteredItems = allArchiveItems.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.subtitle.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (selectedCategoryFilter) {
            "SERMON" -> item.categoryName == strings.navSermons
            "MEETING" -> item.categoryName == strings.navMeetings
            "EVENT" -> item.categoryName == strings.navEvents
            "DOCUMENT" -> item.categoryName == strings.adminDocuments
            "FINANCE" -> item.categoryName == strings.navFinances
            else -> true
        }

        matchesSearch && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("archive_screen")
    ) {
        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(strings.searchArchivesPlaceholder) },
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldBase,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            singleLine = true
        )

        // Category filter chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { (catKey, catLabel) ->
                FilterChip(
                    selected = selectedCategoryFilter == catKey,
                    onClick = { selectedCategoryFilter = catKey },
                    label = { Text(catLabel, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Navy800,
                        selectedLabelColor = GoldLight
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tsy misy tahiry mifanaraka amin'ny fikarohana",
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
                items(filteredItems) { item ->
                    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val dateStr = df.format(Date(item.dateMillis))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.onOpen() },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(38.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = GoldBase,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.categoryName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBase
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "• $dateStr",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (item.subtitle.isNotBlank()) {
                                    Text(
                                        text = item.subtitle,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                            if (item.onExportPdf != null) {
                                IconButton(
                                    onClick = { item.onExportPdf.invoke() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF",
                                        tint = GoldBase,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
