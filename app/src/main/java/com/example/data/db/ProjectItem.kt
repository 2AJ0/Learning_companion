package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_items")
data class ProjectItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "", // Core Idea / Description
    val status: String = "IN_PROGRESS", // IDEA, IN_PROGRESS, ON_HOLD, COMPLETED
    val techStack: String = "",
    val repoUrl: String = "",
    val featuresToAdd: String = "", // Features to add checklist
    val platform: String = "Android", // Project platform e.g. Android, Web, iOS, Desktop
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
