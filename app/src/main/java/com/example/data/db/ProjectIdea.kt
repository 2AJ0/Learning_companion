package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_ideas")
data class ProjectIdea(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parentId: Int,
    val parentType: String, // "LEARNING" or "PROJECT"
    val ideaTitle: String,
    val ideaDescription: String = "",
    val tags: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
