package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.db.ProjectItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditProjectDialog(
    project: ProjectItem? = null,
    availablePlatforms: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, status: String, techStack: String, repoUrl: String, platform: String, priority: String) -> Unit
) {
    var title by remember { mutableStateOf(project?.title ?: "") }
    var desc by remember { mutableStateOf(project?.description ?: "") }
    var status by remember { mutableStateOf(project?.status ?: "IN_PROGRESS") }
    var platform by remember { mutableStateOf(project?.platform ?: (availablePlatforms.firstOrNull() ?: "Android")) }
    var customPlatformInput by remember { mutableStateOf("") }
    var isCustomPlatform by remember { mutableStateOf(false) }

    var priority by remember { mutableStateOf(project?.priority ?: "MEDIUM") }
    var techStack by remember { mutableStateOf(project?.techStack ?: "") }
    var repoUrl by remember { mutableStateOf(project?.repoUrl ?: "") }

    val statusList = listOf(
        "IN_PROGRESS" to "In Progress",
        "ON_HOLD" to "On Hold",
        "IDEA" to "Idea"
    )

    val priorities = listOf("HIGH", "MEDIUM", "LOW")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_project_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (project == null) "Add Active Project" else "Edit Project",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Name *") },
                    placeholder = { Text("Enter project title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_title_input")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("Describe the project goals and features") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_desc_input")
                )

                // Platform Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Project Platform",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availablePlatforms.forEach { plat ->
                            FilterChip(
                                selected = !isCustomPlatform && platform == plat,
                                onClick = {
                                    isCustomPlatform = false
                                    platform = plat
                                },
                                label = { Text(plat) }
                            )
                        }

                        FilterChip(
                            selected = isCustomPlatform,
                            onClick = { isCustomPlatform = true },
                            label = { Text("+ Custom") }
                        )
                    }

                    if (isCustomPlatform || availablePlatforms.isEmpty()) {
                        OutlinedTextField(
                            value = if (isCustomPlatform) customPlatformInput else platform,
                            onValueChange = {
                                customPlatformInput = it
                                platform = it
                            },
                            label = { Text("Platform / Target") },
                            placeholder = { Text("e.g., Android, Web, iOS") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                }

                // Priority Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        priorities.forEach { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p) }
                            )
                        }
                    }
                }

                // Status Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        statusList.forEach { (code, label) ->
                            FilterChip(
                                selected = status == code,
                                onClick = { status = code },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalPlat = if (isCustomPlatform && customPlatformInput.isNotBlank()) customPlatformInput else platform
                            if (title.isNotBlank()) {
                                onSave(title, desc, status, techStack, repoUrl, finalPlat, priority)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("save_project_button")
                    ) {
                        Text(if (project == null) "Add Project" else "Save Project")
                    }
                }
            }
        }
    }
}
