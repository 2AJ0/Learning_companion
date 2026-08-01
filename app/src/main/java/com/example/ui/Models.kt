package com.example.ui

data class IdeaParentSelection(
    val id: Int,
    val type: String, // "LEARNING" or "PROJECT"
    val title: String,
    val categoryOrStatus: String
)

enum class DrawerScreen {
    HOME,
    SKILLS_TO_ACQUIRE,
    KANBAN_BOARD,
    DASHBOARD,
    COMPLETED_SKILLS,
    COMPLETED_PROJECTS,
    RESUME,
    SETTINGS
}

enum class MainTab {
    LEARNING,
    PROJECTS
}
