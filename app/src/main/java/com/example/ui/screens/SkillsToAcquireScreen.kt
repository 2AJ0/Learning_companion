package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.LearningConcept

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsToAcquireScreen(
    skillsToAcquire: List<LearningConcept>,
    learningPlatforms: List<String>,
    onAddSkillToAcquire: (title: String, platform: String, priority: String, notes: String) -> Unit,
    onMoveSkillToLearning: (LearningConcept) -> Unit,
    onEditSkill: (LearningConcept) -> Unit,
    onDeleteSkill: (Int) -> Unit,
    onReorderSkill: (LearningConcept, Boolean) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSkills = remember(skillsToAcquire, searchQuery) {
        if (searchQuery.isBlank()) {
            skillsToAcquire
        } else {
            skillsToAcquire.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.learningPlatform.contains(searchQuery, ignoreCase = true) ||
                        it.notes.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Skills To Acquire",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Search Bar & Add FAB/Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_skills_to_acquire"),
                placeholder = { Text("Search skills to acquire...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_skill_to_acquire_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }

        if (filteredSkills.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No matching skills found" else "No skills in your acquire list yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Tap 'Add' above to start building your wishlist!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredSkills, key = { it.id }) { skill ->
                    SkillToAcquireCard(
                        skill = skill,
                        onMoveToLearning = { onMoveSkillToLearning(skill) },
                        onEdit = { onEditSkill(skill) },
                        onDelete = { onDeleteSkill(skill.id) },
                        onMoveUp = { onReorderSkill(skill, true) },
                        onMoveDown = { onReorderSkill(skill, false) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSkillToAcquireDialog(
            platforms = learningPlatforms,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, platform, priority, notes ->
                onAddSkillToAcquire(title, platform, priority, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SkillToAcquireCard(
    skill: LearningConcept,
    onMoveToLearning: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("skill_to_acquire_card_${skill.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox: Checking moves skill to Learning tab!
            Checkbox(
                checked = false,
                onCheckedChange = { onMoveToLearning() },
                modifier = Modifier.testTag("check_move_to_learning_${skill.id}")
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(skill.learningPlatform, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(12.dp)) },
                        modifier = Modifier.height(26.dp)
                    )

                    val priorityColor = when (skill.priority.uppercase()) {
                        "HIGH" -> MaterialTheme.colorScheme.error
                        "LOW" -> MaterialTheme.colorScheme.outline
                        else -> MaterialTheme.colorScheme.primary
                    }
                    Text(
                        text = "• ${skill.priority}",
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (skill.notes.isNotBlank()) {
                    Text(
                        text = skill.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    IconButton(onClick = onMoveUp, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onMoveDown, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(16.dp))
                    }
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSkillToAcquireDialog(
    platforms: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, platform: String, priority: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf(platforms.firstOrNull() ?: "Documentation") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var notes by remember { mutableStateOf("") }
    var expandedPlatform by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Skill to Acquire Wishlist") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Skill Title") },
                    placeholder = { Text("Enter skill title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_acquire_skill_title_input")
                )

                ExposedDropdownMenuBox(
                    expanded = expandedPlatform,
                    onExpandedChange = { expandedPlatform = !expandedPlatform }
                ) {
                    OutlinedTextField(
                        value = selectedPlatform,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Learning Platform") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlatform) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPlatform,
                        onDismissRequest = { expandedPlatform = false }
                    ) {
                        platforms.forEach { platform ->
                            DropdownMenuItem(
                                text = { Text(platform) },
                                onClick = {
                                    selectedPlatform = platform
                                    expandedPlatform = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("HIGH", "MEDIUM", "LOW").forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Why learn this?") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, selectedPlatform, priority, notes)
                    }
                },
                modifier = Modifier.testTag("save_acquire_skill_button")
            ) {
                Text("Add to Wishlist")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
