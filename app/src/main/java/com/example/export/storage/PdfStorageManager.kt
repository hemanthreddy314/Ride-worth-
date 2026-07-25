package com.example.export.storage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.export.model.PdfReportMetadata
import com.example.export.model.ReportType
import com.example.export.print.PdfPrintAdapter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfStorageManager(private val context: Context) {

    private val baseDirectoryName = "RideWorth/Reports"

    init {
        // Ensure all folders exist
        ReportType.values().forEach { type ->
            getFolderForType(type).mkdirs()
        }
    }

    fun getFolderForType(reportType: ReportType): File {
        val rootDir = File(context.filesDir, "$baseDirectoryName/${reportType.folderName}")
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
        return rootDir
    }

    fun createOutputFile(reportType: ReportType, prefix: String): File {
        val folder = getFolderForType(reportType)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val cleanPrefix = prefix.replace("[^a-zA-Z0-9_-]".toRegex(), "_").take(30)
        val fileName = "RideWorth_${reportType.folderName}_${cleanPrefix}_$timeStamp.pdf"
        return File(folder, fileName)
    }

    fun getAllReports(): List<PdfReportMetadata> {
        val list = mutableListOf<PdfReportMetadata>()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        ReportType.values().forEach { reportType ->
            val folder = getFolderForType(reportType)
            val files = folder.listFiles { file -> file.isFile && file.extension.equals("pdf", ignoreCase = true) }
            files?.forEach { file ->
                val sizeKb = file.length() / 1024
                val sizeFormatted = if (sizeKb > 1024) {
                    String.format(Locale.US, "%.1f MB", sizeKb / 1024f)
                } else {
                    "$sizeKb KB"
                }

                val title = file.nameWithoutExtension
                    .replace("RideWorth_", "")
                    .replace("_", " ")

                list.add(
                    PdfReportMetadata(
                        id = file.absolutePath,
                        fileName = file.name,
                        reportType = reportType,
                        title = title,
                        subtitle = dateFormat.format(Date(file.lastModified())),
                        fileSizeFormatted = sizeFormatted,
                        fileSizeBytes = file.length(),
                        createdAtTimestamp = file.lastModified(),
                        filePath = file.absolutePath
                    )
                )
            }
        }
        return list.sortedByDescending { it.createdAtTimestamp }
    }

    fun searchReports(query: String): List<PdfReportMetadata> {
        if (query.isBlank()) return getAllReports()
        val lower = query.lowercase(Locale.getDefault())
        return getAllReports().filter {
            it.title.lowercase(Locale.getDefault()).contains(lower) ||
                    it.reportType.displayName.lowercase(Locale.getDefault()).contains(lower) ||
                    it.fileName.lowercase(Locale.getDefault()).contains(lower)
        }
    }

    fun renameReport(file: File, newNameWithoutExt: String): File? {
        if (!file.exists()) return null
        val cleanName = newNameWithoutExt.replace("[^a-zA-Z0-9_ -]".toRegex(), "").trim()
        val targetName = if (cleanName.endsWith(".pdf", ignoreCase = true)) cleanName else "$cleanName.pdf"
        val targetFile = File(file.parentFile, targetName)
        return if (file.renameTo(targetFile)) targetFile else null
    }

    fun deleteReport(file: File): Boolean {
        return if (file.exists()) file.delete() else false
    }

    fun deleteAllReports(): Int {
        var count = 0
        ReportType.values().forEach { reportType ->
            val folder = getFolderForType(reportType)
            val files = folder.listFiles { file -> file.isFile && file.extension.equals("pdf", ignoreCase = true) }
            files?.forEach { file ->
                if (file.delete()) count++
            }
        }
        return count
    }

    fun getUriForFile(file: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    fun openPdf(file: File) {
        val uri = getUriForFile(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Open PDF Report")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun sharePdf(file: File, title: String) {
        val uri = getUriForFile(file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "RideWorth Official Report - $title")
            putExtra(Intent.EXTRA_TEXT, "Attached is the official RideWorth Automotive Report for $title.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share PDF Report")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun printPdf(file: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val printAdapter = PdfPrintAdapter(file)
        printManager.print(
            "RideWorth_${file.nameWithoutExtension}",
            printAdapter,
            null
        )
    }
}
