package com.example.ui.screens.finances

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.data.model.FinanceRecord
import com.example.i18n.AppStrings
import com.example.ui.PastoralViewModel
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldLight
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val finances by viewModel.allFinances.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()

    var selectedTypeFilter by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    var recordToEdit by remember { mutableStateOf<FinanceRecord?>(null) }

    val totalRecettes = finances
        .filter { it.transactionType == "RECETTE" || it.transactionType == "DON_CONTRIBUTION" }
        .sumOf { it.amount }
    val totalDepenses = finances
        .filter { it.transactionType == "DEPENSE" }
        .sumOf { it.amount }
    val soldeNet = totalRecettes - totalDepenses

    val typeFilters = listOf(
        "ALL" to strings.all,
        "RECETTE" to strings.revenues,
        "DEPENSE" to strings.expenses,
        "DON_CONTRIBUTION" to strings.catDonation,
        "BUDGET_PREVISIONNEL" to strings.projectedBudgets
    )

    val filteredFinances = finances.filter { f ->
        selectedTypeFilter == "ALL" || f.transactionType == selectedTypeFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    recordToEdit = null
                    showAddDialog = true
                },
                containerColor = Navy800,
                contentColor = GoldLight,
                modifier = Modifier.testTag("fab_add_finance")
            ) {
                Icon(Icons.Default.Add, contentDescription = strings.add)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Balance & Totals Overview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SOLDE NET / TAHIRY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { viewModel.exportFinancesPdf() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = strings.exportPdf,
                                tint = GoldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "%,.0f %s".format(soldeNet, currency),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (soldeNet >= 0) GoldLight else StatusRed
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.revenues, fontSize = 11.sp, color = GoldLight.copy(alpha = 0.8f))
                            }
                            Text(
                                text = "+%,.0f %s".format(totalRecettes, currency),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.expenses, fontSize = 11.sp, color = GoldLight.copy(alpha = 0.8f))
                            }
                            Text(
                                text = "-%,.0f %s".format(totalDepenses, currency),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusRed
                            )
                        }
                    }
                }
            }

            // Type filter chips
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

            if (filteredFinances.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tsy misy hetsika ara-bola voarakitra",
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
                    items(filteredFinances) { record ->
                        FinanceItemCard(
                            record = record,
                            currency = currency,
                            strings = strings,
                            onEdit = {
                                recordToEdit = record
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteFinance(record) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        FinanceAddEditDialog(
            record = recordToEdit,
            currency = currency,
            strings = strings,
            onDismiss = { showAddDialog = false },
            onSave = { saved ->
                viewModel.saveFinance(saved)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun FinanceItemCard(
    record: FinanceRecord,
    currency: String,
    strings: AppStrings,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateStr = df.format(Date(record.dateMillis))

    val isExpense = record.transactionType == "DEPENSE"
    val color = if (isExpense) StatusRed else StatusGreen
    val prefix = if (isExpense) "- " else "+ "

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("finance_item_${record.id}"),
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
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isExpense) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$dateStr • ${record.category}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$prefix%,.0f %s".format(record.amount, currency),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Edit, contentDescription = strings.edit, modifier = Modifier.size(16.dp), tint = GoldBase)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Delete, contentDescription = strings.delete, modifier = Modifier.size(16.dp), tint = StatusRed)
            }
        }
    }
}

@Composable
private fun FinanceAddEditDialog(
    record: FinanceRecord?,
    currency: String,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (FinanceRecord) -> Unit
) {
    var title by remember { mutableStateOf(record?.title ?: "") }
    var transactionType by remember { mutableStateOf(record?.transactionType ?: "RECETTE") }
    var category by remember { mutableStateOf(record?.category ?: "DIME_FANATITRA") }
    var amountText by remember { mutableStateOf(record?.amount?.toString() ?: "") }
    var notes by remember { mutableStateOf(record?.notes ?: "") }

    val types = listOf(
        "RECETTE" to strings.revenues,
        "DEPENSE" to strings.expenses,
        "DON_CONTRIBUTION" to strings.catDonation,
        "BUDGET_PREVISIONNEL" to strings.projectedBudgets
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (record == null) strings.add else strings.edit) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lohahevitra na Anton'ny vola") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text("Karazana", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(types) { (tKey, tLabel) ->
                        FilterChip(
                            selected = transactionType == tKey,
                            onClick = { transactionType = tKey },
                            label = { Text(tLabel, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Navy800,
                                selectedLabelColor = GoldLight
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("${strings.amount} ($currency)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Sokajin'ny vola") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Fanamarihana fanampiny") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    if (title.isBlank()) return@Button
                    onSave(
                        FinanceRecord(
                            id = record?.id ?: 0L,
                            title = title,
                            transactionType = transactionType,
                            category = category,
                            amount = amount,
                            currency = currency,
                            dateMillis = record?.dateMillis ?: System.currentTimeMillis(),
                            notes = notes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Navy800, contentColor = GoldLight)
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
