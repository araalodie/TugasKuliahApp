package com.example.tugaskuliah.ui.components

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val fullDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", Locale("id", "ID"))
    private val shortDateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID"))
    private val dateOnlyFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
    private val timeOnlyFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))

    fun formatFullDateTime(timestamp: Long): String {
        return fullDateFormat.format(Date(timestamp))
    }

    fun formatShortDateTime(timestamp: Long): String {
        return shortDateFormat.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        return dateOnlyFormat.format(Date(timestamp))
    }

    fun formatTimeOnly(timestamp: Long): String {
        return timeOnlyFormat.format(Date(timestamp))
    }

    data class DeadlineStatus(
        val label: String,
        val isOverdue: Boolean,
        val isDueSoon: Boolean
    )

    fun getDeadlineInfo(timestamp: Long, isCompleted: Boolean): DeadlineStatus {
        if (isCompleted) {
            return DeadlineStatus(
                label = "Selesai",
                isOverdue = false,
                isDueSoon = false
            )
        }

        val now = System.currentTimeMillis()
        val diff = timestamp - now

        if (diff < 0) {
            val overdueHours = Math.abs(diff) / (1000 * 60 * 60)
            val overdueDays = overdueHours / 24
            val text = when {
                overdueDays > 0 -> "Terlambat $overdueDays hari"
                overdueHours > 0 -> "Terlambat $overdueHours jam"
                else -> "Baru saja terlewat"
            }
            return DeadlineStatus(label = text, isOverdue = true, isDueSoon = false)
        }

        val hours = diff / (1000 * 60 * 60)
        val days = hours / 24

        val text = when {
            days > 7 -> "${days / 7} minggu lagi"
            days > 0 -> "$days hari lagi"
            hours > 0 -> "$hours jam lagi"
            else -> "Kurang dari 1 jam lagi"
        }

        val isDueSoon = hours <= 24

        return DeadlineStatus(label = text, isOverdue = false, isDueSoon = isDueSoon)
    }
}
