package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.ProjectItem
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.LearningCompanionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            LearningCompanionTheme(themeMode = themeMode) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val currentScreen by viewModel.currentDrawerScreen.collectAsStateWithLifecycle()
                val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

                val activeConcepts by viewModel.activeConcepts.collectAsStateWithLifecycle()
                val skillsToAcquire by viewModel.skillsToAcquire.collectAsStateWithLifecycle()
                val completedConcepts by viewModel.completedConcepts.collectAsStateWithLifecycle()
                val allConcepts by viewModel.allConcepts.collectAsStateWithLifecycle()

                val activeProjects by viewModel.activeProjects.collectAsStateWithLifecycle()
                val completedProjects by viewModel.completedProjects.collectAsStateWithLifecycle()
                val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()

                val allIdeas by viewModel.allIdeas.collectAsStateWithLifecycle()
                val ideasForParent by viewModel.ideasForSelectedParent.collectAsStateWithLifecycle()
                val resumeMetadata by viewModel.resumeMetadata.collectAsStateWithLifecycle()

                val learningPlatforms by viewModel.learningPlatforms.collectAsStateWithLifecycle()
                val projectPlatforms by viewModel.projectPlatforms.collectAsStateWithLifecycle()

                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
                val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()

                val selectedParentForIdeas by viewModel.selectedParentForIdeas.collectAsStateWithLifecycle()
                val showAddConceptDialog by viewModel.showAddConceptDialog.collectAsStateWithLifecycle()
                val conceptToEdit by viewModel.conceptToEdit.collectAsStateWithLifecycle()

                val showAddProjectDialog by viewModel.showAddProjectDialog.collectAsStateWithLifecycle()
                val projectToEdit by viewModel.projectToEdit.collectAsStateWithLifecycle()

                val showResumeUploadDialog by viewModel.showResumeUploadDialog.collectAsStateWithLifecycle()
                val showPostResumeCleanupDialog by viewModel.showPostResumeCleanupDialog.collectAsStateWithLifecycle()

                var selectedProjectForDetails by remember { mutableStateOf<ProjectItem?>(null) }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawerContent(
                            currentScreen = currentScreen,
                            resumeMetadata = resumeMetadata,
                            onNavigateScreen = { screen ->
                                viewModel.currentDrawerScreen.value = screen
                            },
                            onOpenResumeUpload = {
                                viewModel.showResumeUploadDialog.value = true
                            },
                            onCloseDrawer = {
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("main_scaffold"),
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = when (currentScreen) {
                                            DrawerScreen.HOME -> "Learning Companion"
                                            DrawerScreen.DASHBOARD -> "Progress Dashboard"
                                            DrawerScreen.SKILLS_TO_ACQUIRE -> "Skills To Acquire"
                                            DrawerScreen.COMPLETED_SKILLS -> "Completed Skills"
                                            DrawerScreen.COMPLETED_PROJECTS -> "Completed Projects"
                                            DrawerScreen.KANBAN_BOARD -> "Project Idea Board"
                                            DrawerScreen.RESUME -> "Resume Last Updated"
                                            DrawerScreen.SETTINGS -> "Settings & About"
                                        },
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { scope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("menu_drawer_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Open Collapsible Menu"
                                        )
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            if (currentScreen == DrawerScreen.HOME) {
                                FloatingActionButton(
                                    onClick = {
                                        if (selectedTab == MainTab.LEARNING) {
                                            viewModel.conceptToEdit.value = null
                                            viewModel.showAddConceptDialog.value = true
                                        } else {
                                            viewModel.projectToEdit.value = null
                                            viewModel.showAddProjectDialog.value = true
                                        }
                                    },
                                    modifier = Modifier.testTag("add_item_fab")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Item")
                                }
                            }
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            androidx.compose.animation.Crossfade(targetState = currentScreen) { screen ->
                                if (screen == DrawerScreen.HOME) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        // Top Tabs: Learning & Active Projects
                                        TabRow(
                                            selectedTabIndex = if (selectedTab == MainTab.LEARNING) 0 else 1,
                                            modifier = Modifier.testTag("main_tab_row")
                                        ) {
                                    Tab(
                                        selected = selectedTab == MainTab.LEARNING,
                                        onClick = { viewModel.selectedTab.value = MainTab.LEARNING },
                                        text = {
                                            Text(
                                                "Learning (${activeConcepts.size})",
                                                fontWeight = if (selectedTab == MainTab.LEARNING) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        modifier = Modifier.testTag("learning_tab_button")
                                    )

                                    Tab(
                                        selected = selectedTab == MainTab.PROJECTS,
                                        onClick = { viewModel.selectedTab.value = MainTab.PROJECTS },
                                        text = {
                                            Text(
                                                "Active Projects (${activeProjects.size})",
                                                fontWeight = if (selectedTab == MainTab.PROJECTS) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        modifier = Modifier.testTag("projects_tab_button")
                                    )
                                }

                                androidx.compose.animation.Crossfade(targetState = selectedTab) { tab ->
                                    when (tab) {
                                        MainTab.LEARNING -> {
                                            LearningTabScreen(
                                                concepts = activeConcepts,
                                                searchQuery = searchQuery,
                                                selectedCategory = selectedCategoryFilter,
                                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                                onCategorySelect = { viewModel.selectedCategoryFilter.value = it },
                                                onToggleCompleted = { viewModel.toggleConceptCompleted(it) },
                                                onOpenIdeas = { parent ->
                                                    viewModel.selectedParentForIdeas.value = parent
                                                },
                                                onEditConcept = { concept ->
                                                    viewModel.conceptToEdit.value = concept
                                                    viewModel.showAddConceptDialog.value = true
                                                },
                                                onDeleteConcept = { id ->
                                                    viewModel.deleteConcept(id)
                                                },
                                                onReorderConcept = { concept, moveUp ->
                                                    viewModel.reorderConcept(concept, moveUp)
                                                }
                                            )
                                        }

                                        MainTab.PROJECTS -> {
                                            ProjectsTabScreen(
                                                projects = activeProjects,
                                                searchQuery = searchQuery,
                                                selectedStatus = selectedStatusFilter,
                                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                                onStatusSelect = { viewModel.selectedStatusFilter.value = it },
                                                onToggleCompleted = { viewModel.toggleProjectCompleted(it) },
                                                onUpdateStatus = { project, newStatus ->
                                                    viewModel.updateProjectStatus(project, newStatus)
                                                },
                                                onOpenProjectDetails = { project ->
                                                    selectedProjectForDetails = project
                                                },
                                                onEditProject = { project ->
                                                    viewModel.projectToEdit.value = project
                                                    viewModel.showAddProjectDialog.value = true
                                                },
                                                onDeleteProject = { id ->
                                                    viewModel.deleteProject(id)
                                                },
                                                onReorderProject = { project, moveUp ->
                                                    viewModel.reorderProject(project, moveUp)
                                                }
                                            )
                                        }
                                    }
                                }
                                    } // Close Column inside HOME branch
                                } else {
                                    when (screen) {
                                    DrawerScreen.DASHBOARD -> {
                                        DashboardScreen(
                                            activeConcepts = activeConcepts,
                                            completedConcepts = completedConcepts,
                                            activeProjects = activeProjects,
                                            completedProjects = completedProjects,
                                            ideas = ideasForParent
                                        )
                                    }

                                    DrawerScreen.SKILLS_TO_ACQUIRE -> {
                                        SkillsToAcquireScreen(
                                            skillsToAcquire = skillsToAcquire,
                                            learningPlatforms = learningPlatforms,
                                            onAddSkillToAcquire = { title, platform, priority, notes ->
                                                viewModel.addSkillToAcquire(title, platform, priority, notes)
                                            },
                                            onMoveSkillToLearning = { concept ->
                                                viewModel.moveSkillToLearning(concept)
                                                Toast.makeText(this@MainActivity, "'${concept.title}' moved to Learning tab!", Toast.LENGTH_SHORT).show()
                                            },
                                            onEditSkill = { concept ->
                                                viewModel.conceptToEdit.value = concept
                                                viewModel.showAddConceptDialog.value = true
                                            },
                                            onDeleteSkill = { id ->
                                                viewModel.deleteConcept(id)
                                            },
                                            onReorderSkill = { concept, moveUp ->
                                                viewModel.reorderConcept(concept, moveUp)
                                            }
                                        )
                                    }

                                    DrawerScreen.COMPLETED_SKILLS -> {
                                        CompletedItemsScreen(
                                            showConcepts = true,
                                            completedConcepts = completedConcepts,
                                            completedProjects = completedProjects,
                                            onRestoreConcept = { viewModel.toggleConceptCompleted(it) },
                                            onRestoreProject = { viewModel.toggleProjectCompleted(it) },
                                            onDeleteConcept = { viewModel.deleteConcept(it) },
                                            onDeleteProject = { viewModel.deleteProject(it) }
                                        )
                                    }

                                    DrawerScreen.COMPLETED_PROJECTS -> {
                                        CompletedItemsScreen(
                                            showConcepts = false,
                                            completedConcepts = completedConcepts,
                                            completedProjects = completedProjects,
                                            onRestoreConcept = { viewModel.toggleConceptCompleted(it) },
                                            onRestoreProject = { viewModel.toggleProjectCompleted(it) },
                                            onDeleteConcept = { viewModel.deleteConcept(it) },
                                            onDeleteProject = { viewModel.deleteProject(it) }
                                        )
                                    }

                                    DrawerScreen.KANBAN_BOARD -> {
                                        KanbanBoardScreen(
                                            allProjects = allProjects,
                                            onUpdateProjectStatus = { project, newStatus ->
                                                viewModel.updateProjectStatus(project, newStatus)
                                            },
                                             onOpenProjectDetails = { project ->
                                                selectedProjectForDetails = project
                                            }
                                        )
                                    }

                                    DrawerScreen.RESUME -> {
                                        ResumeScreen(
                                            resumeMetadata = resumeMetadata,
                                            onOpenUploadDialog = {
                                                viewModel.showResumeUploadDialog.value = true
                                            }
                                        )
                                    }

                                    DrawerScreen.SETTINGS -> {
                                        val feedbackReport by viewModel.feedbackReport.collectAsStateWithLifecycle()
                                        SettingsScreen(
                                            themeMode = themeMode,
                                            onThemeModeChange = { viewModel.setThemeMode(it) },
                                            feedbackReport = feedbackReport,
                                            onUpdateFeedbackReport = { viewModel.updateFeedbackReport(it) },
                                            onImportData = { viewModel.importDataFromJson(it) },
                                            learningPlatforms = learningPlatforms,
                                            projectPlatforms = projectPlatforms,
                                            allSkills = allConcepts,
                                            allProjects = allProjects,
                                            allIdeas = allIdeas,
                                            onAddLearningPlatform = { viewModel.addLearningPlatform(it) },
                                            onRemoveLearningPlatform = { viewModel.removeLearningPlatform(it) },
                                            onAddProjectPlatform = { viewModel.addProjectPlatform(it) },
                                            onRemoveProjectPlatform = { viewModel.removeProjectPlatform(it) },
                                            onDeleteAllCompletedSkills = { viewModel.deleteAllCompletedSkills() },
                                            onDeleteAllCompletedProjects = { viewModel.deleteAllCompletedProjects() }
                                        )
                                    }

                                    else -> {}
                                }
                            }
                        } // Close Crossfade
                    } // Close Column
                }

                // Dialogs
                selectedParentForIdeas?.let { parent ->
                    ProjectIdeasDialog(
                        parent = parent,
                        ideas = ideasForParent,
                        onDismiss = { viewModel.selectedParentForIdeas.value = null },
                        onAddIdea = { title, desc, tags ->
                            viewModel.addIdea(title, desc, tags)
                        },
                        onDeleteIdea = { id ->
                            viewModel.deleteIdea(id)
                        }
                    )
                }

                selectedProjectForDetails?.let { project ->
                    val attachedIdeas = remember(allIdeas, project.id) {
                        allIdeas.filter { it.parentId == project.id && it.parentType == "PROJECT" }
                    }

                    ProjectDetailsDialog(
                        project = project,
                        attachedIdeas = attachedIdeas,
                        onDismiss = { selectedProjectForDetails = null },
                        onSaveProjectDetails = { coreIdea, tags, featuresToAdd ->
                            viewModel.updateProject(
                                project.copy(
                                    description = coreIdea,
                                    techStack = tags,
                                    featuresToAdd = featuresToAdd
                                )
                            )
                        },
                        onAddProjectIdea = { ideaTitle, ideaDesc, tags ->
                            viewModel.selectedParentForIdeas.value = IdeaParentSelection(
                                id = project.id,
                                type = "PROJECT",
                                title = project.title,
                                categoryOrStatus = project.status
                            )
                            viewModel.addIdea(ideaTitle, ideaDesc, tags)
                        },
                        onDeleteProjectIdea = { id ->
                            viewModel.deleteIdea(id)
                        }
                    )
                }

                if (showAddConceptDialog) {
                    AddEditConceptDialog(
                        concept = conceptToEdit,
                        onDismiss = { viewModel.showAddConceptDialog.value = false },
                        onSave = { title, platform, priority, ideas, notes ->
                            if (conceptToEdit != null) {
                                viewModel.updateConcept(
                                    conceptToEdit!!.copy(
                                        title = title,
                                        learningPlatform = platform,
                                        priority = priority,
                                        projectIdeas = ideas,
                                        notes = notes
                                    )
                                )
                            } else {
                                viewModel.addConcept(title, platform, priority, ideas, notes)
                            }
                        }
                    )
                }

                if (showAddProjectDialog) {
                    AddEditProjectDialog(
                        project = projectToEdit,
                        onDismiss = { viewModel.showAddProjectDialog.value = false },
                        onSave = { title, desc, status, techStack, repoUrl ->
                            if (projectToEdit != null) {
                                viewModel.updateProject(
                                    projectToEdit!!.copy(
                                        title = title,
                                        description = desc,
                                        status = status,
                                        techStack = techStack,
                                        repoUrl = repoUrl
                                    )
                                )
                            } else {
                                viewModel.addProject(title, desc, status, techStack, repoUrl)
                            }
                        }
                    )
                }

                if (showResumeUploadDialog) {
                    ResumeUploadDialog(
                        currentResume = resumeMetadata,
                        onDismiss = { viewModel.showResumeUploadDialog.value = false },
                        onSave = { fileName, fileUri, notes, fileSize ->
                            viewModel.updateResume(fileName, fileUri, notes, fileSize)
                        }
                    )
                }

                if (showPostResumeCleanupDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.showPostResumeCleanupDialog.value = false },
                        title = { Text("Resume Updated Successfully!") },
                        text = {
                            Text("Would you like to clear completed skills and projects now to reset your workspace for the next month's learning cycle?")
                        },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.clearAllCompletedTasks()
                                viewModel.showPostResumeCleanupDialog.value = false
                                Toast.makeText(this, "Completed skills and projects cleared!", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Clear Completed Tasks")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.showPostResumeCleanupDialog.value = false }) {
                                Text("Keep History")
                            }
                        }
                    )
                }
            }
        }
    }
}
}
