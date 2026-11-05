package com.example.ui.screens.admin

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AdminDocument
import com.example.data.model.AdminMember
import com.example.i18n.AppStrings
import com.example.ui.PastoralViewModel
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
fun AdminScreen(
    viewModel: PastoralViewModel,
    strings: AppStrings,
    modifier: Modifier = Modifier
) {
    val members by viewModel.allMembers.collectAsStateWithLifecycle()
    val documents by viewModel.allDocuments.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Members/Bureau, 1: Documents
    var showMemberDialog by remember { mutableStateOf(false) }
    var showDocDialog by remember { mutableStateOf(false) }
    var memberToEdit by remember { mutableStateOf<AdminMember?>(null) }
    var docToEdit by remember { mutableStateOf<AdminDocument?>(null) }

    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        memberToEdit = null
                        showMemberDialog = true
                    } else {
                        docToEdit = null
                        showDocDialog = true
                    }
                },
                containerColor = Navy800,
                contentColor = GoldLight,
                modifier = Modifier.testTag("fab_add_admin")
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
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Navy900,
                contentColor = GoldLight
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = strings.adminBureauMembers,
                            fontSize = 12.sp,
                            color = if (selectedTab == 0) GoldLight else IvoryWhite.copy(alpha = 0.7f),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = strings.adminDocuments,
                            fontSize = 12.sp,
                            color = if (selectedTab == 1) GoldLight else IvoryWhite.copy(alpha = 0.7f),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedTab == 0) {
                if (members.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tsy misy mpikambana voarakitra",
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
                        items(members) { member ->
                            MemberCard(
                                member = member,
                                strings = strings,
                                onCall = {
                                    if (member.phone.isNotBlank()) {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phone}"))
                                        context.startActivity(intent)
                                    }
                                },
                                onEmail = {
                                    if (member.email.isNotBlank()) {
                                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${member.email}"))
                                        context.startActivity(intent)
                                    }
                                },
                                onEdit = {
                                    memberToEdit = member
                                    showMemberDialog = true
                                },
                                onDelete = { viewModel.deleteMember(member) }
                            )
                        }
                    }
                }
            } else {
                if (documents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tsy misy tahirin-tsoratra voarakitra",
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
                        items(documents) { doc ->
                            DocumentCard(
                                document = doc,
                                strings = strings,
                                onEdit = {
                                    docToEdit = doc
                                    showDocDialog = true
                                },
                                onDelete = { viewModel.deleteDocument(doc) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showMemberDialog) {
        MemberAddEditDialog(
            member = memberToEdit,
            strings = strings,
            onDismiss = { showMemberDialog = false },
            onSave = { savedMember ->
                viewModel.saveMember(savedMember)
                showMemberDialog = false
            }
        )
    }

    if (showDocDialog) {
        DocumentAddEditDialog(
            document = docToEdit,
            strings = strings,
            onDismiss = { showDocDialog = false },
            onSave = { savedDoc ->
                viewModel.saveDocument(savedDoc)
                showDocDialog = false
            }
        )
    }
}

@Composable
private fun MemberCard(
    member: AdminMember,
    strings: AppStrings,
    onCall: () -> Unit,
    onEmail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_member_${member.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GoldBase.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = GoldBase, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = member.roleFunction,
                        fontSize = 12.sp,
                        color = GoldBase,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = strings.edit, modifier = Modifier.size(18.dp), tint = GoldBase)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = strings.delete, modifier = Modifier.size(18.dp), tint = StatusRed)
                }
            }

            if (member.responsibilities.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = member.responsibilities,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (member.phone.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCall() }
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Navy800, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(member.phone, fontSize = 12.sp, color = Navy800, fontWeight = FontWeight.Medium)
                    }
                }
                if (member.email.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onEmail() }
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = Navy800, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(member.email, fontSize = 12.sp, color = Navy800, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(
    document: AdminDocument,
    strings: AppStrings,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateStr = df.format(Date(document.dateMillis))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_doc_${document.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Navy800.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Navy800, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = document.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${document.documentType} • ${document.referenceNumber.ifBlank { dateStr }}",
                        fontSize = 11.sp,
                        color = GoldBase,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = strings.edit, modifier = Modifier.size(18.dp), tint = GoldBase)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = strings.delete, modifier = Modifier.size(18.dp), tint = StatusRed)
                }
            }

            if (document.contentOrSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = document.contentOrSummary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MemberAddEditDialog(
    member: AdminMember?,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (AdminMember) -> Unit
) {
    var fullName by remember { mutableStateOf(member?.fullName ?: "") }
    var roleFunction by remember { mutableStateOf(member?.roleFunction ?: "") }
    var entityType by remember { mutableStateOf(member?.entityType ?: "BUREAU") }
    var phone by remember { mutableStateOf(member?.phone ?: "") }
    var email by remember { mutableStateOf(member?.email ?: "") }
    var address by remember { mutableStateOf(member?.address ?: "") }
    var responsibilities by remember { mutableStateOf(member?.responsibilities ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (member == null) strings.add else strings.edit) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(strings.fullName) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = roleFunction,
                    onValueChange = { roleFunction = it },
                    label = { Text(strings.roleFunction) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(strings.phone) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(strings.email) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = responsibilities,
                    onValueChange = { responsibilities = it },
                    label = { Text(strings.responsibilities) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isBlank()) return@Button
                    onSave(
                        AdminMember(
                            id = member?.id ?: 0L,
                            fullName = fullName,
                            roleFunction = roleFunction,
                            entityType = entityType,
                            phone = phone,
                            email = email,
                            address = address,
                            responsibilities = responsibilities
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

@Composable
private fun DocumentAddEditDialog(
    document: AdminDocument?,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (AdminDocument) -> Unit
) {
    var title by remember { mutableStateOf(document?.title ?: "") }
    var documentType by remember { mutableStateOf(document?.documentType ?: "REGLEMENT") }
    var referenceNumber by remember { mutableStateOf(document?.referenceNumber ?: "") }
    var contentOrSummary by remember { mutableStateOf(document?.contentOrSummary ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (document == null) strings.add else strings.edit) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.docTitle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = documentType,
                    onValueChange = { documentType = it },
                    label = { Text(strings.docType) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text(strings.docRef) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = contentOrSummary,
                    onValueChange = { contentOrSummary = it },
                    label = { Text(strings.docContent) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    onSave(
                        AdminDocument(
                            id = document?.id ?: 0L,
                            title = title,
                            documentType = documentType,
                            dateMillis = document?.dateMillis ?: System.currentTimeMillis(),
                            referenceNumber = referenceNumber,
                            contentOrSummary = contentOrSummary
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
