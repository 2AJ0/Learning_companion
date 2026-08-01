package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.db.ResumeMetadata
import com.example.ui.DrawerScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppDrawerContent(
    currentScreen: DrawerScreen,
    resumeMetadata: ResumeMetadata?,
    onNavigateScreen: (DrawerScreen) -> Unit,
    onOpenResumeUpload: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    val lastUpdatedStr = resumeMetadata?.lastUpdated?.let { dateFormat.format(Date(it)) } ?: "Never"

    ModalDrawerSheet(
        modifier = Modifier.testTag("app_navigation_drawer"),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Learning Companion",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Navigation Items
            DrawerMenuItem(
                label = "Learning & Projects",
                icon = Icons.Default.Dashboard,
                isSelected = currentScreen == DrawerScreen.HOME,
                onClick = {
                    onNavigateScreen(DrawerScreen.HOME)
                    onCloseDrawer()
                },
                tag = "drawer_item_home"
            )

            DrawerMenuItem(
                label = "Skills to Acquire",
                icon = Icons.Default.BookmarkAdd,
                isSelected = currentScreen == DrawerScreen.SKILLS_TO_ACQUIRE,
                onClick = {
                    onNavigateScreen(DrawerScreen.SKILLS_TO_ACQUIRE)
                    onCloseDrawer()
                },
                tag = "drawer_item_skills_to_acquire"
            )

            DrawerMenuItem(
                label = "Project Idea Board (Kanban)",
                icon = Icons.Default.ViewColumn,
                isSelected = currentScreen == DrawerScreen.KANBAN_BOARD,
                onClick = {
                    onNavigateScreen(DrawerScreen.KANBAN_BOARD)
                    onCloseDrawer()
                },
                tag = "drawer_item_kanban"
            )

            DrawerMenuItem(
                label = "Progress Dashboard",
                icon = Icons.Default.Analytics,
                isSelected = currentScreen == DrawerScreen.DASHBOARD,
                onClick = {
                    onNavigateScreen(DrawerScreen.DASHBOARD)
                    onCloseDrawer()
                },
                tag = "drawer_item_dashboard"
            )

            DrawerMenuItem(
                label = "Completed Skills",
                icon = Icons.Default.CheckCircle,
                isSelected = currentScreen == DrawerScreen.COMPLETED_SKILLS,
                onClick = {
                    onNavigateScreen(DrawerScreen.COMPLETED_SKILLS)
                    onCloseDrawer()
                },
                tag = "drawer_item_completed_skills"
            )

            DrawerMenuItem(
                label = "Completed Projects",
                icon = Icons.Default.FolderZip,
                isSelected = currentScreen == DrawerScreen.COMPLETED_PROJECTS,
                onClick = {
                    onNavigateScreen(DrawerScreen.COMPLETED_PROJECTS)
                    onCloseDrawer()
                },
                tag = "drawer_item_completed_projects"
            )

            DrawerMenuItem(
                label = "Resume Tracker",
                icon = Icons.Default.Description,
                isSelected = currentScreen == DrawerScreen.RESUME,
                onClick = {
                    onNavigateScreen(DrawerScreen.RESUME)
                    onCloseDrawer()
                },
                tag = "drawer_item_resume"
            )

            DrawerMenuItem(
                label = "Settings & Data Management",
                icon = Icons.Default.Settings,
                isSelected = currentScreen == DrawerScreen.SETTINGS,
                onClick = {
                    onNavigateScreen(DrawerScreen.SETTINGS)
                    onCloseDrawer()
                },
                tag = "drawer_item_settings"
            )

            Spacer(modifier = Modifier.weight(1f))

            // Resume Quick Widget inside drawer footer
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Article,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Resume Last Updated",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = resumeMetadata?.fileName ?: "No resume record",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )

                    Text(
                        text = "Updated: $lastUpdatedStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedButton(
                        onClick = {
                            onOpenResumeUpload()
                            onCloseDrawer()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drawer_update_resume_button"),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Update Resume", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .testTag(tag),
        shape = RoundedCornerShape(12.dp)
    )
}
