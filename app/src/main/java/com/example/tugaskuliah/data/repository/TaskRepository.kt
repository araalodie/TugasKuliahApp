package com.example.tugaskuliah.data.repository

import com.example.tugaskuliah.data.dao.TaskDao
import com.example.tugaskuliah.data.entity.Task
import com.example.tugaskuliah.data.entity.TaskPriority
import com.example.tugaskuliah.data.entity.TaskStatus
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTasksByStatus(status: String): Flow<List<Task>> {
        return taskDao.getTasksByStatus(status)
    }

    fun getTaskById(id: Int): Flow<Task?> {
        return taskDao.getTaskById(id)
    }

    suspend fun getTaskByIdDirect(id: Int): Task? {
        return taskDao.getTaskByIdDirect(id)
    }

    fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query)
    }

    suspend fun getTaskCount(): Int {
        return try {
            taskDao.getTaskCount()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun seedInitialTasksIfEmpty() {
        try {
            if (taskDao.getTaskCount() == 0) {
                val now = System.currentTimeMillis()
                val oneDay = 24 * 60 * 60 * 1000L

                taskDao.insertTask(
                    Task(
                        title = "Implementasi Room Database & Compose",
                        course = "Pemrograman Mobile",
                        description = "Buat aplikasi CRUD offline lokal menggunakan Jetpack Compose dan Room Database.",
                        deadline = now + 1 * oneDay,
                        status = TaskStatus.PROSES.displayName,
                        priority = TaskPriority.TINGGI.displayName
                    )
                )
                taskDao.insertTask(
                    Task(
                        title = "Analisis Kompleksitas Algoritma Dijkstra",
                        course = "Desain & Analisis Algoritma",
                        description = "Tulis laporan perbandingan efisiensi waktu dan memori antara Dijkstra dan Bellman-Ford.",
                        deadline = now + 3 * oneDay,
                        status = TaskStatus.BELUM.displayName,
                        priority = TaskPriority.SEDANG.displayName
                    )
                )
                taskDao.insertTask(
                    Task(
                        title = "Desain Skema ERD Toko Online",
                        course = "Sistem Basis Data",
                        description = "Normalisasi tabel hingga 3NF beserta query relasi antar tabel transaksi.",
                        deadline = now + 5 * oneDay,
                        status = TaskStatus.BELUM.displayName,
                        priority = TaskPriority.RENDAH.displayName
                    )
                )
                taskDao.insertTask(
                    Task(
                        title = "Resume Paper Kecerdasan Buatan",
                        course = "Kecerdasan Buatan",
                        description = "Meringkas paper terbaru tentang Transformer Architecture.",
                        deadline = now - 1 * oneDay,
                        status = TaskStatus.SELESAI.displayName,
                        priority = TaskPriority.SEDANG.displayName
                    )
                )
            }
        } catch (_: Exception) {
            // Ignore if seeding fails
        }
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task): Int {
        return taskDao.updateTask(task)
    }

    suspend fun updateStatus(id: Int, status: String): Int {
        return taskDao.updateStatus(id, status)
    }

    suspend fun deleteTask(task: Task): Int {
        return taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Int): Int {
        return taskDao.deleteTaskById(id)
    }
}
