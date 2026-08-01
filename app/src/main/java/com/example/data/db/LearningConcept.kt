package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_concepts")
data class LearningConcept(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val learningPlatform: String = "Documentation",
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val projectIdeas: String = "",
    val notes: String = "",
    val status: String = "LEARNING", // "TO_ACQUIRE", "LEARNING", "COMPLETED"
    val orderIndex: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

