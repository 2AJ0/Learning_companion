package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.repository.LearningRepository
import com.example.notification.ReminderManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LearningRepository

    val activeConcepts: StateFlow<List<LearningConcept>>
    val skillsToAcquire: StateFlow<List<LearningConcept>>
    val completedConcepts: StateFlow<List<LearningConcept>>
    val allConcepts: StateFlow<List<LearningConcept>>

    val activeProjects: StateFlow<List<ProjectItem>>
    val completedProjects: StateFlow<List<ProjectItem>>
    val allProjects: StateFlow<List<ProjectItem>>

    val allIdeas: StateFlow<List<ProjectIdea>>
    val resumeMetadata: StateFlow<ResumeMetadata?>

    // Theme Mode ("SYSTEM", "LIGHT", "DARK")
    val themeMode = MutableStateFlow("SYSTEM")

    fun setThemeMode(mode: String) {
        themeMode.value = mode
    }

    // Custom Platforms Management
    private val prefs = application.getSharedPreferences("companion_prefs", android.content.Context.MODE_PRIVATE)

    val learningPlatforms = MutableStateFlow<List<String>>(emptyList())
    val projectPlatforms = MutableStateFlow<List<String>>(emptyList())

    // Feedback Report
    val feedbackReport = MutableStateFlow("")

    fun updateFeedbackReport(report: String) {
        feedbackReport.value = report
    }

    fun importDataFromJson(jsonString: String) {
        viewModelScope.launch {
            try {
                val jsonObject = org.json.JSONObject(jsonString)
                val skills = jsonObject.optJSONArray("skills")
                if (skills != null) {
                    for (i in 0 until skills.length()) {
                        val skillObj = skills.getJSONObject(i)
                        val title = skillObj.optString("title", "")
                        val platform = skillObj.optString("platform", "Unknown")
                        val isCompleted = skillObj.optBoolean("completed", false)
                        if (title.isNotEmpty()) {
                            val concept = LearningConcept(
                                title = title,
                                learningPlatform = platform,
                                isCompleted = isCompleted
                            )
                            repository.insertConcept(concept)
                        }
                    }
                }
                val projects = jsonObject.optJSONArray("projects")
                if (projects != null) {
                    for (i in 0 until projects.length()) {
                        val projectObj = projects.getJSONObject(i)
                        val title = projectObj.optString("title", "")
                        val techStack = projectObj.optString("techStack", "")
                        val isCompleted = projectObj.optBoolean("completed", false)
                        if (title.isNotEmpty()) {
                            val project = ProjectItem(
                                title = title,
                                techStack = techStack,
                                isCompleted = isCompleted
                            )
                            repository.insertProject(project)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Filter, Search and Sorting states
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedStatusFilter = MutableStateFlow<String?>(null)
    val sortOption = MutableStateFlow("DATE_DESC") // DATE_DESC, TITLE_ASC, PRIORITY_HIGH

    // UI Navigation & Dialog states
    val currentDrawerScreen = MutableStateFlow(DrawerScreen.HOME)
    val selectedTab = MutableStateFlow(MainTab.LEARNING)

    val selectedParentForIdeas = MutableStateFlow<IdeaParentSelection?>(null)

    val showAddConceptDialog = MutableStateFlow(false)
    val conceptToEdit = MutableStateFlow<LearningConcept?>(null)

    val showAddProjectDialog = MutableStateFlow(false)
    val projectToEdit = MutableStateFlow<ProjectItem?>(null)

    val showResumeUploadDialog = MutableStateFlow(false)
    val showPostResumeCleanupDialog = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ideasForSelectedParent: StateFlow<List<ProjectIdea>> = selectedParentForIdeas
        .flatMapLatest { parent ->
            if (parent != null) {
                repository.getIdeasForParent(parent.id, parent.type)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LearningRepository(database.learningDao())

        // Load saved platforms from SharedPreferences
        val savedLearning = prefs.getStringSet("learning_platforms", null)
        learningPlatforms.value = if (savedLearning != null) savedLearning.toList() else listOf("YouTube", "Udemy", "Coursera", "Documentation")

        val savedProject = prefs.getStringSet("project_platforms", null)
        projectPlatforms.value = if (savedProject != null) savedProject.toList() else listOf("Android", "Web", "iOS", "Flutter", "Desktop")

        // Remove any pre-seeded sample data
        viewModelScope.launch {
            repository.removeSampleTasksIfPresent()
            repository.seedSampleDataIfEmpty()
        }

        activeConcepts = repository.activeConcepts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        skillsToAcquire = repository.skillsToAcquire.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        completedConcepts = repository.completedConcepts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allConcepts = repository.allConcepts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        activeProjects = repository.activeProjects.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        completedProjects = repository.completedProjects.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allProjects = repository.allProjects.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        allIdeas = repository.allIdeas.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        resumeMetadata = repository.resumeMetadata.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )
    }

    // Platform Management Actions
    fun addLearningPlatform(platform: String) {
        val clean = platform.trim()
        if (clean.isNotBlank() && !learningPlatforms.value.contains(clean)) {
            val updated = learningPlatforms.value + clean
            learningPlatforms.value = updated
            prefs.edit().putStringSet("learning_platforms", updated.toSet()).apply()
        }
    }

    fun removeLearningPlatform(platform: String) {
        val updated = learningPlatforms.value.filter { it != platform }
        learningPlatforms.value = updated
        prefs.edit().putStringSet("learning_platforms", updated.toSet()).apply()
    }

    fun addProjectPlatform(platform: String) {
        val clean = platform.trim()
        if (clean.isNotBlank() && !projectPlatforms.value.contains(clean)) {
            val updated = projectPlatforms.value + clean
            projectPlatforms.value = updated
            prefs.edit().putStringSet("project_platforms", updated.toSet()).apply()
        }
    }

    fun removeProjectPlatform(platform: String) {
        val updated = projectPlatforms.value.filter { it != platform }
        projectPlatforms.value = updated
        prefs.edit().putStringSet("project_platforms", updated.toSet()).apply()
    }

    // Skills To Acquire & Concept Actions
    fun addSkillToAcquire(title: String, learningPlatform: String, priority: String, notes: String) {
        viewModelScope.launch {
            repository.insertConcept(
                LearningConcept(
                    title = title,
                    learningPlatform = if (learningPlatform.isBlank()) "General" else learningPlatform,
                    priority = priority,
                    notes = notes,
                    status = "TO_ACQUIRE"
                )
            )
        }
    }

    fun moveSkillToLearning(concept: LearningConcept) {
        viewModelScope.launch {
            repository.moveSkillToLearning(concept)
        }
    }

    fun addConcept(title: String, learningPlatform: String, priority: String, projectIdeas: String, notes: String) {
        viewModelScope.launch {
            repository.insertConcept(
                LearningConcept(
                    title = title,
                    learningPlatform = if (learningPlatform.isBlank()) "General" else learningPlatform,
                    priority = priority,
                    projectIdeas = projectIdeas,
                    notes = notes,
                    status = "LEARNING"
                )
            )
        }
    }

    fun updateConcept(concept: LearningConcept) {
        viewModelScope.launch {
            repository.updateConcept(concept)
        }
    }

    fun toggleConceptCompleted(concept: LearningConcept) {
        viewModelScope.launch {
            repository.toggleConceptCompleted(concept)
        }
    }

    fun deleteConcept(id: Int) {
        viewModelScope.launch {
            repository.deleteConcept(id)
        }
    }

    fun reorderConcept(concept: LearningConcept, moveUp: Boolean) {
        viewModelScope.launch {
            val list = activeConcepts.value
            val index = list.indexOfFirst { it.id == concept.id }
            if (index == -1) return@launch
            val targetIndex = if (moveUp) index - 1 else index + 1
            if (targetIndex in list.indices) {
                val currentOrder = list.mapIndexed { idx, item -> item.copy(orderIndex = idx) }
                val itemA = currentOrder[index]
                val itemB = currentOrder[targetIndex]
                repository.updateConcept(itemA.copy(orderIndex = itemB.orderIndex))
                repository.updateConcept(itemB.copy(orderIndex = itemA.orderIndex))
            }
        }
    }

    // Project Actions
    fun addProject(
        title: String,
        description: String,
        status: String,
        techStack: String,
        repoUrl: String,
        featuresToAdd: String = "",
        platform: String = "Android",
        priority: String = "MEDIUM"
    ) {
        viewModelScope.launch {
            repository.insertProject(
                ProjectItem(
                    title = title,
                    description = description,
                    status = status,
                    techStack = techStack,
                    repoUrl = repoUrl,
                    featuresToAdd = featuresToAdd,
                    platform = if (platform.isBlank()) "Android" else platform,
                    priority = priority
                )
            )
        }
    }

    fun updateProject(project: ProjectItem) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }

    fun toggleProjectCompleted(project: ProjectItem) {
        viewModelScope.launch {
            repository.toggleProjectCompleted(project)
        }
    }

    fun updateProjectStatus(project: ProjectItem, newStatus: String) {
        viewModelScope.launch {
            val isComp = newStatus == "COMPLETED"
            val updated = project.copy(
                status = newStatus,
                isCompleted = isComp,
                completedAt = if (isComp) System.currentTimeMillis() else null
            )
            repository.updateProject(updated)
        }
    }

    fun deleteProject(id: Int) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    fun reorderProject(project: ProjectItem, moveUp: Boolean) {
        viewModelScope.launch {
            val list = activeProjects.value
            val index = list.indexOfFirst { it.id == project.id }
            if (index == -1) return@launch
            val targetIndex = if (moveUp) index - 1 else index + 1
            if (targetIndex in list.indices) {
                val currentOrder = list.mapIndexed { idx, item -> item.copy(orderIndex = idx) }
                val itemA = currentOrder[index]
                val itemB = currentOrder[targetIndex]
                repository.updateProject(itemA.copy(orderIndex = itemB.orderIndex))
                repository.updateProject(itemB.copy(orderIndex = itemA.orderIndex))
            }
        }
    }

    // Bulk Actions
    fun deleteAllCompletedSkills() {
        viewModelScope.launch {
            repository.deleteAllCompletedConcepts()
        }
    }

    fun deleteAllCompletedProjects() {
        viewModelScope.launch {
            repository.deleteAllCompletedProjects()
        }
    }

    fun clearAllCompletedTasks() {
        viewModelScope.launch {
            repository.deleteAllCompletedConcepts()
            repository.deleteAllCompletedProjects()
        }
    }

    // Idea Actions
    fun addIdea(ideaTitle: String, ideaDescription: String, tags: String) {
        val parent = selectedParentForIdeas.value ?: return
        viewModelScope.launch {
            repository.insertIdea(
                ProjectIdea(
                    parentId = parent.id,
                    parentType = parent.type,
                    ideaTitle = ideaTitle,
                    ideaDescription = ideaDescription,
                    tags = tags
                )
            )
        }
    }

    fun deleteIdea(id: Int) {
        viewModelScope.launch {
            repository.deleteIdea(id)
        }
    }

    // Resume Actions
    fun updateResume(fileName: String, fileUri: String, notes: String, fileSize: String = "1.5 MB") {
        viewModelScope.launch {
            repository.updateResume(
                ResumeMetadata(
                    id = 1,
                    fileName = fileName,
                    fileUri = fileUri,
                    lastUpdated = System.currentTimeMillis(),
                    fileSizeFormatted = fileSize,
                    summaryNotes = notes
                )
            )
            showPostResumeCleanupDialog.value = true
        }
    }
}
