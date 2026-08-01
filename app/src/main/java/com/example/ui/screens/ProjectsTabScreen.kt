package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.db.ProjectItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsTabScreen(
    projects: List<ProjectItem>,
    searchQuery: String,
    selectedStatus: String?,
    onSearchQueryChange: (String) -> Unit,
    onStatusSelect: (String?) -> Unit,
    onToggleCompleted: (ProjectItem) -> Unit,
    onUpdateStatus: (ProjectItem, String) -> Unit,
    onOpenProjectDetails: (ProjectItem) -> Unit,
    onEditProject: (ProjectItem) -> Unit,
    onDeleteProject: (Int) -> Unit,
    onReorderProject: (ProjectItem, Boolean) -> Unit
) {
    val statuses = listOf(
        "ALL" to "All",
        "IDEA" to "Ideas",
        "IN_PROGRESS" to "In Progress",
        "ON_HOLD" to "On Hold"
    )

    val filteredProjects = remember(projects, searchQuery, selectedStatus) {
        projects.filter { project ->
            val matchesQuery = searchQuery.isBlank() ||
                    project.title.contains(searchQuery, ignoreCase = true) ||
                    project.description.contains(searchQuery, ignoreCase = true) ||
                    project.techStack.contains(searchQuery, ignoreCase = true) ||
                    project.featuresToAdd.contains(searchQuery, ignoreCase = true)

            val matchesStatus = selectedStatus == null || selectedStatus == "ALL" || project.status == selectedStatus
            matchesQuery && matchesStatus
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("projects_tab_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(statuses) { (code, label) ->
                val isSelected = (selectedStatus == code) || (selectedStatus == null && code == "ALL")
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onStatusSelect(if (code == "ALL") null else code)
                    },
                    label = { Text(label) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (filteredProjects.isEmpty()) {
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
                        imageVector = Icons.Default.FolderZip,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "No active projects found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the + button to add a project or long-press to edit details!",
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
                items(filteredProjects, key = { it.id }) { project ->
                    ProjectItemCard(
                        project = project,
                        onToggleCompleted = { onToggleCompleted(project) },
                        onUpdateStatus = { newStatus -> onUpdateStatus(project, newStatus) },
                        onLongPressOrClick = { onOpenProjectDetails(project) },
                        onEdit = { onEditProject(project) },
                        onDelete = { onDeleteProject(project.id) },
                        onMoveUp = { onReorderProject(project, true) },
                        onMoveDown = { onReorderProject(project, false) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectItemCard(
    project: ProjectItem,
    onToggleCompleted: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onLongPressOrClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    val (statusLabel, statusColor) = when (project.status) {
        "IDEA" -> "Idea" to MaterialTheme.colorScheme.tertiary
        "IN_PROGRESS" -> "In Progress" to MaterialTheme.colorScheme.primary
        "ON_HOLD" -> "On Hold" to MaterialTheme.colorScheme.secondary
        "COMPLETED" -> "Completed" to MaterialTheme.colorScheme.outline
        else -> project.status to MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onLongPressOrClick,
                onLongClick = onLongPressOrClick
            )
            .testTag("project_card_${project.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        checked = project.isCompleted,
                        onCheckedChange = { onToggleCompleted() },
                        modifier = Modifier.testTag("project_checkbox_${project.id}")
                    )

                    Column {
                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = if (project.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )

                        // Status Badge with Dropdown Switcher
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = statusColor.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clickable { showStatusMenu = true }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = statusLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Change Status",
                                        tint = statusColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showStatusMenu,
                                onDismissRequest = { showStatusMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Idea") },
                                    onClick = {
                                        onUpdateStatus("IDEA")
                                        showStatusMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("In Progress") },
                                    onClick = {
                                        onUpdateStatus("IN_PROGRESS")
                                        showStatusMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("On Hold") },
                                    onClick = {
                                        onUpdateStatus("ON_HOLD")
                                        showStatusMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Mark Completed") },
                                    onClick = {
                                        onUpdateStatus("COMPLETED")
                                        showStatusMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Action icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMoveUp, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onMoveDown, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onLongPressOrClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("project_details_btn_${project.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Edit Core Idea & Features",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
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

            if (project.description.isNotBlank()) {
                Text(
                    text = "Core Idea: ${project.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (project.techStack.isNotBlank()) {
                Text(
                    text = "Tech Stack / Tags: ${project.techStack}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (project.featuresToAdd.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Features: ${project.featuresToAdd}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}
