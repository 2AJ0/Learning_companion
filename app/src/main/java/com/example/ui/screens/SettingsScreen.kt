package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.LearningConcept
import com.example.data.db.ProjectIdea
import com.example.data.db.ProjectItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeMode: String,
    onThemeModeChange: (String) -> Unit,
    feedbackReport: String,
    onUpdateFeedbackReport: (String) -> Unit,
    onImportData: (String) -> Unit,
    learningPlatforms: List<String>,
    projectPlatforms: List<String>,
    allSkills: List<LearningConcept>,
    allProjects: List<ProjectItem>,
    allIdeas: List<ProjectIdea>,
    onAddLearningPlatform: (String) -> Unit,
    onRemoveLearningPlatform: (String) -> Unit,
    onAddProjectPlatform: (String) -> Unit,
    onRemoveProjectPlatform: (String) -> Unit,
    onDeleteAllCompletedSkills: () -> Unit,
    onDeleteAllCompletedProjects: () -> Unit
) {
    val context = LocalContext.current

    var newLearningPlatformInput by remember { mutableStateOf("") }
    var newProjectPlatformInput by remember { mutableStateOf("") }

    var showDeleteSkillsConfirm by remember { mutableStateOf(false) }
    var showDeleteProjectsConfirm by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showSubmitReportDialog by remember { mutableStateOf(false) }
    var importJsonInput by remember { mutableStateOf("") }
    var feedbackInput by remember { mutableStateOf(feedbackReport) }
    var reportDialogText by remember { mutableStateOf(feedbackInput) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings & Customization",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // 0. Dark / Light Theme Preference Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = when (themeMode) {
                                "DARK" -> Icons.Default.DarkMode
                                "LIGHT" -> Icons.Default.LightMode
                                else -> Icons.Default.SettingsSystemDaydream
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "App Theme & Appearance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = themeMode == "SYSTEM",
                            onClick = { onThemeModeChange("SYSTEM") },
                            label = { Text("System") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.SettingsSystemDaydream,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("theme_system_chip")
                        )

                        FilterChip(
                            selected = themeMode == "LIGHT",
                            onClick = { onThemeModeChange("LIGHT") },
                            label = { Text("Light") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LightMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("theme_light_chip")
                        )

                        FilterChip(
                            selected = themeMode == "DARK",
                            onClick = { onThemeModeChange("DARK") },
                            label = { Text("Dark") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("theme_dark_chip")
                        )
                    }
                }
            }
        }

        // 1. Learning Platforms Manager
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Learning Platforms",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newLearningPlatformInput,
                            onValueChange = { newLearningPlatformInput = it },
                            placeholder = { Text("e.g. Pluralsight") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_learning_platform_input")
                        )
                        Button(
                            onClick = {
                                if (newLearningPlatformInput.isNotBlank()) {
                                    onAddLearningPlatform(newLearningPlatformInput)
                                    newLearningPlatformInput = ""
                                }
                            },
                            modifier = Modifier.testTag("add_learning_platform_button")
                        ) {
                            Text("Add")
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        learningPlatforms.forEach { platform ->
                            InputChip(
                                selected = false,
                                onClick = { onRemoveLearningPlatform(platform) },
                                label = { Text(platform) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove $platform",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // 2. Project Platforms & Tech Stacks Manager
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Project Platforms & Tech Stacks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newProjectPlatformInput,
                            onValueChange = { newProjectPlatformInput = it },
                            placeholder = { Text("e.g. Flutter, GraphQL") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_project_platform_input")
                        )
                        Button(
                            onClick = {
                                if (newProjectPlatformInput.isNotBlank()) {
                                    onAddProjectPlatform(newProjectPlatformInput)
                                    newProjectPlatformInput = ""
                                }
                            },
                            modifier = Modifier.testTag("add_project_platform_button")
                        ) {
                            Text("Add")
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        projectPlatforms.forEach { platform ->
                            InputChip(
                                selected = false,
                                onClick = { onRemoveProjectPlatform(platform) },
                                label = { Text(platform) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove $platform",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // 3. Bulk Delete Completed Tasks
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Clear Completed Tasks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDeleteSkillsConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("clear_completed_skills_button")
                        ) {
                            Text("Delete Completed Skills")
                        }

                        OutlinedButton(
                            onClick = { showDeleteProjectsConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("clear_completed_projects_button")
                        ) {
                            Text("Delete Completed Projects")
                        }
                    }
                }
            }
        }

        // 4. Backup & Export Data
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Export & Backup Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_backup_data_button")
                        ) {
                            Icon(imageVector = Icons.Default.IosShare, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export")
                        }

                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("import_data_button")
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import")
                        }
                    }
                }
            }
        }
        // 5. Report Issues & Recommend Features
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Feedback,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Report Issues & Recommend Features",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Submit Report Button (Opens Popup Dialog like in screenshot)
                    Button(
                        onClick = {
                            reportDialogText = feedbackInput
                            showSubmitReportDialog = true
                        },
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Report", style = MaterialTheme.typography.labelLarge)
                    }

                    // Export Reports Button
                    OutlinedButton(
                        onClick = {
                            val reportsContent = com.example.data.ReportManager.getReportsText(context)
                            if (reportsContent.isBlank()) {
                                Toast.makeText(context, "No reports found in reports.md. Tap 'Submit Report' to create one.", Toast.LENGTH_SHORT).show()
                            } else {
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, reportsContent)
                                    type = "text/markdown"
                                }
                                val shareIntent = android.content.Intent.createChooser(sendIntent, "Export Reports (.md)")
                                context.startActivity(shareIntent)
                            }
                        },
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("export_reports_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Reports (.md)", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Data (JSON)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Paste your previously exported backup JSON here to restore your records:")
                    OutlinedTextField(
                        value = importJsonInput,
                        onValueChange = { importJsonInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        placeholder = { Text("{\n  \"skills\": [...],\n  \"projects\": [...]\n}") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onImportData(importJsonInput)
                    importJsonInput = ""
                    showImportDialog = false
                    Toast.makeText(context, "Data imported successfully!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteSkillsConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteSkillsConfirm = false },
            title = { Text("Delete All Completed Skills?") },
            text = { Text("This will permanently remove all mastered skills from your database.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllCompletedSkills()
                        showDeleteSkillsConfirm = false
                        Toast.makeText(context, "Completed skills deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSkillsConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteProjectsConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteProjectsConfirm = false },
            title = { Text("Delete All Completed Projects?") },
            text = { Text("This will permanently remove all completed project records from your database.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAllCompletedProjects()
                        showDeleteProjectsConfirm = false
                        Toast.makeText(context, "Completed projects deleted", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteProjectsConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showExportDialog) {
        var exportFormat by remember { mutableStateOf("MD") } // MD or JSON

        val backupText = remember(allSkills, allProjects, allIdeas, exportFormat) {
            if (exportFormat == "MD") {
                val sb = StringBuilder()
                sb.append("# Developer Companion - Learning Data Export\n\n")
                sb.append("## Skills & Concepts To Learn\n")
                if (allSkills.isEmpty()) {
                    sb.append("*No skills recorded*\n")
                } else {
                    allSkills.forEach {
                        val statusStr = if (it.isCompleted) "[x]" else "[ ]"
                        sb.append("- $statusStr **${it.title}** (${it.learningPlatform}) - Priority: ${it.priority}\n")
                        if (it.notes.isNotBlank()) sb.append("  - Notes: ${it.notes}\n")
                    }
                }

                sb.append("\n## Projects\n")
                if (allProjects.isEmpty()) {
                    sb.append("*No projects recorded*\n")
                } else {
                    allProjects.forEach {
                        val statusStr = if (it.isCompleted) "[x]" else "[ ]"
                        sb.append("- $statusStr **${it.title}** [${it.platform}] - ${it.status}\n")
                        if (it.description.isNotBlank()) sb.append("  - Description: ${it.description}\n")
                        if (it.techStack.isNotBlank()) sb.append("  - Tech Stack: ${it.techStack}\n")
                        if (it.featuresToAdd.isNotBlank()) sb.append("  - Features to Add: ${it.featuresToAdd}\n")
                    }
                }
                sb.toString()
            } else {
                val skillsText = allSkills.joinToString(",\n  ") { "{\"title\":\"${it.title}\", \"platform\":\"${it.learningPlatform}\", \"completed\":${it.isCompleted}}" }
                val projectsText = allProjects.joinToString(",\n  ") { "{\"title\":\"${it.title}\", \"techStack\":\"${it.techStack}\", \"completed\":${it.isCompleted}}" }
                "{\n \"skills\": [\n  $skillsText\n ],\n \"projects\": [\n  $projectsText\n ]\n}"
            }
        }

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Backup & Export Data") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = exportFormat == "MD",
                            onClick = { exportFormat = "MD" },
                            label = { Text("Markdown (.md)") }
                        )
                        FilterChip(
                            selected = exportFormat == "JSON",
                            onClick = { exportFormat = "JSON" },
                            label = { Text("JSON (.json)") }
                        )
                    }

                    Text("Copy this backup text to save or transfer your learning records:")
                    OutlinedTextField(
                        value = backupText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Learning Companion Export", backupText))
                    Toast.makeText(context, "Export ($exportFormat) copied to clipboard!", Toast.LENGTH_SHORT).show()
                    showExportDialog = false
                }) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy to Clipboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showSubmitReportDialog) {
        val lastUpdated = remember(showSubmitReportDialog) {
            com.example.data.ReportManager.getLastUpdatedTimestamp(context)
        }

        AlertDialog(
            onDismissRequest = { showSubmitReportDialog = false },
            title = {
                Column {
                    Text(
                        text = "Submit Report /\nRecommendation",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Last updated on: $lastUpdated",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = reportDialogText,
                        onValueChange = { reportDialogText = it },
                        placeholder = { Text("Write your bug report or feature recommendation here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reportDialogText.isNotBlank()) {
                            val success = com.example.data.ReportManager.appendReport(context, reportDialogText)
                            if (success) {
                                feedbackInput = com.example.data.ReportManager.getReportsText(context)
                                onUpdateFeedbackReport(feedbackInput)
                                Toast.makeText(context, "Report appended to reports.md!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showSubmitReportDialog = false
                    }
                ) {
                    Text("Save & Append")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSubmitReportDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
