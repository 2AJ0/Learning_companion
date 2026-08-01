package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class LearningRepository(private val dao: LearningDao) {

    val activeConcepts: Flow<List<LearningConcept>> = dao.getActiveConcepts()
    val skillsToAcquire: Flow<List<LearningConcept>> = dao.getSkillsToAcquire()
    val completedConcepts: Flow<List<LearningConcept>> = dao.getCompletedConcepts()
    val allConcepts: Flow<List<LearningConcept>> = dao.getAllConcepts()

    val activeProjects: Flow<List<ProjectItem>> = dao.getActiveProjects()
    val completedProjects: Flow<List<ProjectItem>> = dao.getCompletedProjects()
    val allProjects: Flow<List<ProjectItem>> = dao.getAllProjects()

    val allIdeas: Flow<List<ProjectIdea>> = dao.getAllIdeas()
    val resumeMetadata: Flow<ResumeMetadata?> = dao.getResumeMetadata()

    fun getIdeasForParent(parentId: Int, parentType: String): Flow<List<ProjectIdea>> {
        return dao.getIdeasForParent(parentId, parentType)
    }

    suspend fun insertConcept(concept: LearningConcept): Long = dao.insertConcept(concept)
    suspend fun updateConcept(concept: LearningConcept) = dao.updateConcept(concept)
    suspend fun deleteConcept(id: Int) = dao.deleteConceptById(id)

    suspend fun moveSkillToLearning(concept: LearningConcept) {
        dao.updateConcept(concept.copy(status = "LEARNING"))
    }

    suspend fun toggleConceptCompleted(concept: LearningConcept) {
        val isNowCompleted = !concept.isCompleted
        val updated = concept.copy(
            isCompleted = isNowCompleted,
            status = if (isNowCompleted) "COMPLETED" else "LEARNING",
            completedAt = if (isNowCompleted) System.currentTimeMillis() else null
        )
        dao.updateConcept(updated)

        // When marking complete, automatically move attached project ideas to Active Projects with tag/status IDEA!
        if (isNowCompleted) {
            val attachedIdeas = dao.getIdeasForParent(concept.id, "LEARNING").first()
            if (attachedIdeas.isNotEmpty()) {
                attachedIdeas.forEach { idea ->
                    dao.insertProject(
                        ProjectItem(
                            title = idea.ideaTitle,
                            description = idea.ideaDescription,
                            techStack = idea.tags.ifBlank { concept.learningPlatform },
                            status = "IDEA"
                        )
                    )
                }
            } else if (concept.projectIdeas.isNotBlank()) {
                dao.insertProject(
                    ProjectItem(
                        title = "${concept.title} Project Idea",
                        description = concept.projectIdeas,
                        techStack = concept.learningPlatform,
                        status = "IDEA"
                    )
                )
            }
        }
    }

    suspend fun insertProject(project: ProjectItem): Long = dao.insertProject(project)
    suspend fun updateProject(project: ProjectItem) = dao.updateProject(project)
    suspend fun deleteProject(id: Int) = dao.deleteProjectById(id)

    suspend fun toggleProjectCompleted(project: ProjectItem) {
        val isNowCompleted = !project.isCompleted
        val updated = project.copy(
            isCompleted = isNowCompleted,
            status = if (isNowCompleted) "COMPLETED" else "IN_PROGRESS",
            completedAt = if (isNowCompleted) System.currentTimeMillis() else null
        )
        dao.updateProject(updated)
    }

    suspend fun deleteAllCompletedConcepts() = dao.deleteAllCompletedConcepts()
    suspend fun deleteAllCompletedProjects() = dao.deleteAllCompletedProjects()

    suspend fun clearAllDataAndRestore(
        concepts: List<LearningConcept>,
        projects: List<ProjectItem>,
        ideas: List<ProjectIdea>
    ) {
        dao.deleteAllConcepts()
        dao.deleteAllProjects()
        dao.deleteAllIdeas()
        concepts.forEach { dao.insertConcept(it) }
        projects.forEach { dao.insertProject(it) }
        ideas.forEach { dao.insertIdea(it) }
    }

    suspend fun insertIdea(idea: ProjectIdea): Long = dao.insertIdea(idea)
    suspend fun deleteIdea(id: Int) = dao.deleteIdeaById(id)

    suspend fun updateResume(metadata: ResumeMetadata) {
        dao.updateResumeMetadata(metadata)
    }

    suspend fun removeSampleTasksIfPresent() {
        val sampleTitles = setOf(
            "Jetpack Compose State & Recomposition",
            "Kotlin Coroutines & Flow Operations",
            "Room Database & KSP Code Gen",
            "Clean Architecture & MVVM Patterns"
        )
        val sampleProjectTitles = setOf(
            "Learning Companion Android App",
            "DevPortfolio CLI Tool",
            "Algorithm Visualizer App"
        )

        val allC = dao.getAllConcepts().first()
        allC.filter { it.title in sampleTitles }.forEach { dao.deleteConceptById(it.id) }

        val allP = dao.getAllProjects().first()
        allP.filter { it.title in sampleProjectTitles }.forEach { dao.deleteProjectById(it.id) }
    }

    suspend fun seedSampleDataIfEmpty() {
        // No sample data seeded as requested - app starts clean.
    }
}
