package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resume_metadata")
data class ResumeMetadata(
    @PrimaryKey val id: Int = 1,
    val fileName: String = "My_Software_Engineer_Resume.pdf",
    val fileUri: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val fileSizeFormatted: String = "1.2 MB",
    val summaryNotes: String = "Updated with recent Kotlin & Android Jetpack projects."
)
