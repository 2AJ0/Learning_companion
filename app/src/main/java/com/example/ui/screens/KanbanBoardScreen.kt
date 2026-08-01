package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.ProjectItem

@Composable
fun KanbanBoardScreen(
    allProjects: List<ProjectItem>,
    onUpdateProjectStatus: (ProjectItem, String) -> Unit,
    onOpenProjectDetails: (ProjectItem) -> Unit
) {
    val ideaProjects = remember(allProjects) { allProjects.filter { it.status == "IDEA" } }
    val inProgressProjects = remember(allProjects) { allProjects.filter { it.status == "IN_PROGRESS" || it.status == "ON_HOLD" } }
    val completedProjects = remember(allProjects) { allProjects.filter { it.status == "COMPLETED" || it.isCompleted } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ViewColumn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Column {
                    Text(
                        text = "Project Idea Board (Kanban)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Horizontal Scrollable Kanban Columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KanbanColumn(
                title = "Ideas (${ideaProjects.size})",
                statusKey = "IDEA",
                headerColor = MaterialTheme.colorScheme.tertiaryContainer,
                projects = ideaProjects,
                onMoveLeft = null,
                onMoveRight = { project -> onUpdateProjectStatus(project, "IN_PROGRESS") },
                onOpenDetails = onOpenProjectDetails,
                modifier = Modifier.width(280.dp)
            )

            KanbanColumn(
                title = "In Progress (${inProgressProjects.size})",
                statusKey = "IN_PROGRESS",
                headerColor = MaterialTheme.colorScheme.secondaryContainer,
                projects = inProgressProjects,
                onMoveLeft = { project -> onUpdateProjectStatus(project, "IDEA") },
                onMoveRight = { project -> onUpdateProjectStatus(project, "COMPLETED") },
                onOpenDetails = onOpenProjectDetails,
                modifier = Modifier.width(280.dp)
            )

            KanbanColumn(
                title = "Completed / Resume Ready (${completedProjects.size})",
                statusKey = "COMPLETED",
                headerColor = MaterialTheme.colorScheme.primaryContainer,
                projects = completedProjects,
                onMoveLeft = { project -> onUpdateProjectStatus(project, "IN_PROGRESS") },
                onMoveRight = null,
                onOpenDetails = onOpenProjectDetails,
                modifier = Modifier.width(280.dp)
            )
        }
    }
}

@Composable
fun KanbanColumn(
    title: String,
    statusKey: String,
    headerColor: Color,
    projects: List<ProjectItem>,
    onMoveLeft: ((ProjectItem) -> Unit)?,
    onMoveRight: ((ProjectItem) -> Unit)?,
    onOpenDetails: (ProjectItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = headerColor
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                )
            }

            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No projects in this stage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(projects, key = { it.id }) { project ->
                        KanbanProjectCard(
                            project = project,
                            onMoveLeft = onMoveLeft?.let { { it(project) } },
                            onMoveRight = onMoveRight?.let { { it(project) } },
                            onClick = { onOpenDetails(project) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanProjectCard(
    project: ProjectItem,
    onMoveLeft: (() -> Unit)?,
    onMoveRight: (() -> Unit)?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            if (project.description.isNotBlank()) {
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            if (project.techStack.isNotBlank()) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(project.techStack, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.height(26.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onMoveLeft != null) {
                    IconButton(onClick = onMoveLeft, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Move Left",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(28.dp))
                }

                Text(
                    text = "Tap for details",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                if (onMoveRight != null) {
                    IconButton(onClick = onMoveRight, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Move Right",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}
