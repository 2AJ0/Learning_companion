package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningDao {
    // Learning Concepts
    @Query("SELECT * FROM learning_concepts WHERE isCompleted = 0 AND status = 'LEARNING' ORDER BY orderIndex ASC, createdAt DESC")
    fun getActiveConcepts(): Flow<List<LearningConcept>>

    @Query("SELECT * FROM learning_concepts WHERE isCompleted = 0 AND status = 'TO_ACQUIRE' ORDER BY orderIndex ASC, createdAt DESC")
    fun getSkillsToAcquire(): Flow<List<LearningConcept>>

    @Query("SELECT * FROM learning_concepts WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedConcepts(): Flow<List<LearningConcept>>

    @Query("SELECT * FROM learning_concepts ORDER BY createdAt DESC")
    fun getAllConcepts(): Flow<List<LearningConcept>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcept(concept: LearningConcept): Long

    @Update
    suspend fun updateConcept(concept: LearningConcept)

    @Query("DELETE FROM learning_concepts WHERE id = :id")
    suspend fun deleteConceptById(id: Int)

    @Query("DELETE FROM learning_concepts WHERE isCompleted = 1")
    suspend fun deleteAllCompletedConcepts()

    // Project Items
    @Query("SELECT * FROM project_items WHERE isCompleted = 0 ORDER BY orderIndex ASC, createdAt DESC")
    fun getActiveProjects(): Flow<List<ProjectItem>>

    @Query("SELECT * FROM project_items WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedProjects(): Flow<List<ProjectItem>>

    @Query("SELECT * FROM project_items ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectItem): Long

    @Update
    suspend fun updateProject(project: ProjectItem)

    @Query("DELETE FROM project_items WHERE id = :id")
    suspend fun deleteProjectById(id: Int)

    @Query("DELETE FROM project_items WHERE isCompleted = 1")
    suspend fun deleteAllCompletedProjects()

    @Query("DELETE FROM learning_concepts")
    suspend fun deleteAllConcepts()

    @Query("DELETE FROM project_items")
    suspend fun deleteAllProjects()

    @Query("DELETE FROM project_ideas")
    suspend fun deleteAllIdeas()

    // Project Ideas
    @Query("SELECT * FROM project_ideas WHERE parentId = :parentId AND parentType = :parentType ORDER BY createdAt DESC")
    fun getIdeasForParent(parentId: Int, parentType: String): Flow<List<ProjectIdea>>

    @Query("SELECT COUNT(*) FROM project_ideas WHERE parentId = :parentId AND parentType = :parentType")
    fun getIdeaCountForParent(parentId: Int, parentType: String): Flow<Int>

    @Query("SELECT * FROM project_ideas ORDER BY createdAt DESC")
    fun getAllIdeas(): Flow<List<ProjectIdea>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: ProjectIdea): Long

    @Query("DELETE FROM project_ideas WHERE id = :id")
    suspend fun deleteIdeaById(id: Int)

    // Resume Metadata
    @Query("SELECT * FROM resume_metadata WHERE id = 1")
    fun getResumeMetadata(): Flow<ResumeMetadata?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateResumeMetadata(metadata: ResumeMetadata)
}
