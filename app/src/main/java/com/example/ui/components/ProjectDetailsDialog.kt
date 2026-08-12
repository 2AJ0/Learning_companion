package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.ProjectIdea
import com.example.data.db.ProjectItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsDialog(
    project: ProjectItem,
    attachedIdeas: List<ProjectIdea>,
    onDismiss: () -> Unit,
    onSaveProjectDetails: (coreIdea: String, tags: String, featuresToAdd: String) -> Unit,
    onAddProjectIdea: (ideaTitle: String, ideaDesc: String, tags: String) -> Unit,
    onDeleteProjectIdea: (Int) -> Unit
) {
    var coreIdea by remember(project) { mutableStateOf(project.description) }
    var tags by remember(project) { mutableStateOf(project.techStack) }
    var featuresToAdd by remember(project) { mutableStateOf(project.featuresToAdd) }

    var showAddIdeaDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FolderSpecial,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = coreIdea,
                    onValueChange = { coreIdea = it },
                    label = { Text("Core Idea / Description") },
                    placeholder = { Text("Core concept and purpose of this project") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_core_idea_input"),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags / Tech Stack") },
                    placeholder = { Text("Technologies used") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_tags_input")
                )

                OutlinedTextField(
                    value = featuresToAdd,
                    onValueChange = { featuresToAdd = it },
                    label = { Text("Features to Add / Subtasks") },
                    placeholder = { Text("Enter subtasks or planned features (one per line)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_features_input"),
                    minLines = 2,
                    maxLines = 4
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Attached Project Ideas (${attachedIdeas.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { showAddIdeaDialog = true },
                        modifier = Modifier.testTag("add_attached_project_idea_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Idea",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (attachedIdeas.isEmpty()) {
                    Text(
                        text = "No project ideas attached yet. Tap '+' to brainstorm ideas for this project!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(attachedIdeas, key = { it.id }) { idea ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = idea.ideaTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (idea.ideaDescription.isNotBlank()) {
                                            Text(
                                                text = idea.ideaDescription,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    IconButton(onClick = { onDeleteProjectIdea(idea.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Idea",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveProjectDetails(coreIdea, tags, featuresToAdd)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_project_details_button")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

    if (showAddIdeaDialog) {
        var ideaTitle by remember { mutableStateOf("") }
        var ideaDesc by remember { mutableStateOf("") }
        var ideaTags by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddIdeaDialog = false },
            title = { Text("Add Project Feature / Idea") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ideaTitle,
                        onValueChange = { ideaTitle = it },
                        label = { Text("Idea Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ideaDesc,
                        onValueChange = { ideaDesc = it },
                        label = { Text("Idea Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ideaTags,
                        onValueChange = { ideaTags = it },
                        label = { Text("Tags") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (ideaTitle.isNotBlank()) {
                        onAddProjectIdea(ideaTitle, ideaDesc, ideaTags)
                        showAddIdeaDialog = false
                    }
                }) {
                    Text("Add Idea")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddIdeaDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
