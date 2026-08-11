package com.example.data

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportManager {

    private const val REPORT_FILE_NAME = "reports.md"

    fun appendReport(context: Context, reportText: String): Boolean {
        if (reportText.isBlank()) return false
        return try {
            val file = File(context.filesDir, REPORT_FILE_NAME)
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val formattedEntry = "## Report - $time\n$reportText\n\n---\n\n"
            file.appendText(formattedEntry)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getReportsFile(context: Context): File {
        return File(context.filesDir, REPORT_FILE_NAME)
    }

    fun getReportsText(context: Context): String {
        val file = getReportsFile(context)
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }

    fun getLastUpdatedTimestamp(context: Context): String {
        val file = getReportsFile(context)
        return if (file.exists() && file.length() > 0) {
            val lastMod = file.lastModified()
            SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(lastMod))
        } else {
            "Never updated"
        }
    }
}
