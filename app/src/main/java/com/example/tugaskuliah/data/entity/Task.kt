package com.example.tugaskuliah.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskStatus(val displayName: String) {
    BELUM("Belum"),
    PROSES("Proses"),
    SELESAI("Selesai");

    companion object {
        fun fromDisplayName(name: String): TaskStatus {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: BELUM
        }
    }
}

enum class TaskPriority(val displayName: String) {
    RENDAH("Rendah"),
    SEDANG("Sedang"),
    TINGGI("Tinggi");

    companion object {
        fun fromDisplayName(name: String): TaskPriority {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: SEDANG
        }
    }
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val course: String,
    val description: String = "",
    val deadline: Long, // Epoch timestamp in milliseconds
    val status: String = TaskStatus.BELUM.displayName,
    val priority: String = TaskPriority.SEDANG.displayName,
    val createdAt: Long = System.currentTimeMillis()
)
