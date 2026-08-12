package com.example.ui.components

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.db.ResumeMetadata
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeUploadDialog(
    currentResume: ResumeMetadata?,
    onDismiss: () -> Unit,
    onSave: (fileName: String, fileUri: String, notes: String, fileSize: String) -> Unit
) {
    val context = LocalContext.current

    var fileName by remember { mutableStateOf(currentResume?.fileName ?: "Developer_Resume.pdf") }
    var notes by remember { mutableStateOf(currentResume?.summaryNotes ?: "") }
    var fileSize by remember { mutableStateOf(currentResume?.fileSizeFormatted ?: "1.2 MB") }
    var localPath by remember { mutableStateOf(currentResume?.fileUri ?: "") }
    var pdfSelected by remember { mutableStateOf(false) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { inputUri ->
            try {
                // Determine file name and size
                var name = "Resume.pdf"
                var bytes: Long = 0
                context.contentResolver.query(inputUri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIdx != -1) name = cursor.getString(nameIdx)
                        if (sizeIdx != -1) bytes = cursor.getLong(sizeIdx)
                    }
                }

                // Copy PDF file to local private app storage
                val destFile = File(context.filesDir, "saved_resume.pdf")
                context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                    destFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                fileName = name
                localPath = destFile.absolutePath
                val mb = if (bytes > 0) String.format("%.1f MB", bytes.toDouble() / (1024 * 1024)) else "1.2 MB"
                fileSize = mb
                pdfSelected = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("resume_upload_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Update Resume Document",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Select your PDF resume from device storage. A private copy will be kept in app storage for easy offline download.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Resume File Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("resume_file_name_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Revision / Summary Notes") },
                    placeholder = { Text("Enter revision notes") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("resume_notes_input")
                )

                OutlinedButton(
                    onClick = { pdfPickerLauncher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select PDF from Device Storage")
                }

                if (pdfSelected || localPath.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "✓ Ready to save: $fileName ($fileSize)",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fileName.isNotBlank()) {
                                onSave(fileName, localPath, notes, fileSize)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.testTag("save_resume_button")
                    ) {
                        Text("Update Record & Save")
                    }
                }
            }
        }
    }
}
