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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProjectDialog(
    project: ProjectItem? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, status: String, techStack: String, repoUrl: String) -> Unit
) {
    var title by remember { mutableStateOf(project?.title ?: "") }
    var desc by remember { mutableStateOf(project?.description ?: "") }
    var status by remember { mutableStateOf(project?.status ?: "IN_PROGRESS") }
    var techStack by remember { mutableStateOf(project?.techStack ?: "") }
    var repoUrl by remember { mutableStateOf(project?.repoUrl ?: "") }

    val statusList = listOf(
        "IN_PROGRESS" to "In Progress",
        "ON_HOLD" to "On Hold",
        "TESTING" to "Under Test"
    )

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
                    placeholder = { Text("e.g. Learning Companion App") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_title_input")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("What is the goal of this project?") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_desc_input")
                )

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
                            if (title.isNotBlank()) {
                                onSave(title, desc, status, "", "")
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("save_project_button")
                    ) {
                        Text("Save Project")
                    }
                }
            }
        }
    }
}
