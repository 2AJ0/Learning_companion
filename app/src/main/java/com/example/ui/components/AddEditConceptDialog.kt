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
import com.example.data.db.LearningConcept

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditConceptDialog(
    concept: LearningConcept? = null,
    availablePlatforms: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (title: String, learningPlatform: String, priority: String, projectIdeas: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf(concept?.title ?: "") }
    var learningPlatform by remember { mutableStateOf(concept?.learningPlatform ?: (availablePlatforms.firstOrNull() ?: "General")) }
    var customPlatformInput by remember { mutableStateOf("") }
    var isCustomPlatform by remember { mutableStateOf(false) }

    var priority by remember { mutableStateOf(concept?.priority ?: "MEDIUM") }
    var projectIdeas by remember { mutableStateOf(concept?.projectIdeas ?: "") }
    var notes by remember { mutableStateOf(concept?.notes ?: "") }

    val priorities = listOf("HIGH", "MEDIUM", "LOW")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_concept_dialog"),
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
                    text = if (concept == null) "Add Skill" else "Edit Skill",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Skill Name *") },
                    placeholder = { Text("Enter skill title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("concept_title_input")
                )

                // Learning Platform Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Learning Platform",
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
                                selected = !isCustomPlatform && learningPlatform == plat,
                                onClick = {
                                    isCustomPlatform = false
                                    learningPlatform = plat
                                },
                                label = { Text(plat) }
                            )
                        }

                        FilterChip(
                            selected = isCustomPlatform,
                            onClick = {
                                isCustomPlatform = true
                            },
                            label = { Text("+ Custom") }
                        )
                    }

                    if (isCustomPlatform || availablePlatforms.isEmpty()) {
                        OutlinedTextField(
                            value = if (isCustomPlatform) customPlatformInput else learningPlatform,
                            onValueChange = {
                                customPlatformInput = it
                                learningPlatform = it
                            },
                            label = { Text("Platform Name") },
                            placeholder = { Text("e.g., Coursera, Book, YouTube") },
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

                OutlinedTextField(
                    value = projectIdeas,
                    onValueChange = { projectIdeas = it },
                    label = { Text("Project Ideas") },
                    placeholder = { Text("Describe application ideas for this skill") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("concept_project_ideas_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes & References") },
                    placeholder = { Text("Key takeaways or reference links") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("concept_notes_input")
                )

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
                            val finalPlatform = if (isCustomPlatform && customPlatformInput.isNotBlank()) customPlatformInput else learningPlatform
                            if (title.isNotBlank()) {
                                onSave(title, finalPlatform, priority, projectIdeas, notes)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("save_concept_button")
                    ) {
                        Text(if (concept == null) "Add" else "Save")
                    }
                }
            }
        }
    }
}
