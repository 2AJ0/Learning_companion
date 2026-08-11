package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.db.LearningConcept
import com.example.ui.IdeaParentSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningTabScreen(
    concepts: List<LearningConcept>,
    searchQuery: String,
    selectedCategory: String?,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onToggleCompleted: (LearningConcept) -> Unit,
    onOpenIdeas: (IdeaParentSelection) -> Unit,
    onEditConcept: (LearningConcept) -> Unit,
    onDeleteConcept: (Int) -> Unit,
    onReorderConcept: (LearningConcept, Boolean) -> Unit
) {
    val platforms = remember(concepts) {
        listOf("All") + concepts.map { it.learningPlatform }.distinct()
    }

    var sortOption by remember { mutableStateOf("ORDER") } // ORDER, PRIORITY, DATE, NAME
    var showSortMenu by remember { mutableStateOf(false) }

    val filteredConcepts = remember(concepts, searchQuery, selectedCategory, sortOption) {
        val filtered = concepts.filter { concept ->
            val matchesQuery = searchQuery.isBlank() ||
                    concept.title.contains(searchQuery, ignoreCase = true) ||
                    concept.notes.contains(searchQuery, ignoreCase = true) ||
                    concept.projectIdeas.contains(searchQuery, ignoreCase = true) ||
                    concept.learningPlatform.contains(searchQuery, ignoreCase = true)

            val matchesPlatform = selectedCategory == null || selectedCategory == "All" || concept.learningPlatform == selectedCategory
            matchesQuery && matchesPlatform
        }

        when (sortOption) {
            "PRIORITY" -> filtered.sortedBy {
                when (it.priority) {
                    "HIGH" -> 0
                    "MEDIUM" -> 1
                    "LOW" -> 2
                    else -> 3
                }
            }
            "DATE" -> filtered.sortedByDescending { it.createdAt }
            "NAME" -> filtered.sortedBy { it.title.lowercase() }
            else -> filtered.sortedBy { it.orderIndex }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("learning_tab_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Filter and Sort Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Learning Platform Filter Chips
            if (platforms.size > 1) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(platforms) { plat ->
                        val isSelected = (selectedCategory == plat) || (selectedCategory == null && plat == "All")
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onCategorySelect(if (plat == "All") null else plat)
                            },
                            label = { Text(plat) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Sort Dropdown Menu Button
            Box {
                IconButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier.testTag("sort_learning_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort Skills",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Manual / Custom Order") },
                        onClick = { sortOption = "ORDER"; showSortMenu = false },
                        leadingIcon = { if (sortOption == "ORDER") Icon(Icons.Default.Check, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Priority (High to Low)") },
                        onClick = { sortOption = "PRIORITY"; showSortMenu = false },
                        leadingIcon = { if (sortOption == "PRIORITY") Icon(Icons.Default.Check, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Date Created (Newest)") },
                        onClick = { sortOption = "DATE"; showSortMenu = false },
                        leadingIcon = { if (sortOption == "DATE") Icon(Icons.Default.Check, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Name (A-Z)") },
                        onClick = { sortOption = "NAME"; showSortMenu = false },
                        leadingIcon = { if (sortOption == "NAME") Icon(Icons.Default.Check, null) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Items list
        if (filteredConcepts.isEmpty()) {
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
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "No skills found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the + button to add a new skill!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredConcepts, key = { it.id }) { concept ->
                    ConceptItemCard(
                        concept = concept,
                        onToggleCompleted = { onToggleCompleted(concept) },
                        onLongPress = {
                            onOpenIdeas(
                                IdeaParentSelection(
                                    id = concept.id,
                                    type = "LEARNING",
                                    title = concept.title,
                                    categoryOrStatus = concept.learningPlatform
                                )
                            )
                        },
                        onEdit = { onEditConcept(concept) },
                        onDelete = { onDeleteConcept(concept.id) },
                        onMoveUp = { onReorderConcept(concept, true) },
                        onMoveDown = { onReorderConcept(concept, false) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConceptItemCard(
    concept: LearningConcept,
    onToggleCompleted: () -> Unit,
    onLongPress: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val priorityColor = when (concept.priority.uppercase()) {
        "HIGH" -> MaterialTheme.colorScheme.error
        "MEDIUM" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onEdit,
                onLongClick = onLongPress
            )
            .testTag("concept_card_${concept.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = concept.isCompleted,
                        onCheckedChange = { onToggleCompleted() },
                        modifier = Modifier.testTag("concept_checkbox_${concept.id}")
                    )

                    Column {
                        Text(
                            text = concept.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (concept.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (concept.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            // Learning Platform Tag
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = concept.learningPlatform,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Priority Tag
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = priorityColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = concept.priority,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = priorityColor
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMoveUp, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onMoveDown, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("concept_edit_btn_${concept.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Skill",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (concept.projectIdeas.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Project Idea: ${concept.projectIdeas}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (concept.notes.isNotBlank()) {
                if (concept.projectIdeas.isBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = concept.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )
            }
        }
    }
}
