package com.example.tugaskuliah.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tugaskuliah.data.entity.Task
import com.example.tugaskuliah.data.entity.TaskPriority
import com.example.tugaskuliah.data.entity.TaskStatus
import com.example.tugaskuliah.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter(val label: String) {
    ALL("Semua"),
    BELUM("Belum"),
    PROSES("Proses"),
    SELESAI("Selesai");

    companion object {
        val list: List<TaskFilter> by lazy { entries }
    }
}

data class TaskStats(
    val total: Int = 0,
    val belum: Int = 0,
    val proses: Int = 0,
    val selesai: Int = 0
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<String>(TaskFilter.ALL.label)
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Full list of all tasks from Room (ordered by deadline ASC)
    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered & searched tasks
    val filteredTasks: StateFlow<List<Task>> = combine(
        allTasks,
        _selectedFilter,
        _searchQuery
    ) { tasks, filter, query ->
        tasks.filter { task ->
            val matchesFilter = if (filter == TaskFilter.ALL.label) {
                true
            } else {
                task.status.equals(filter, ignoreCase = true)
            }

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                task.title.contains(query, ignoreCase = true) ||
                task.course.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true)
            }

            matchesFilter && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Task statistics
    val taskStats: StateFlow<TaskStats> = allTasks.combine(_selectedFilter) { tasks, _ ->
        TaskStats(
            total = tasks.size,
            belum = tasks.count { it.status.equals(TaskStatus.BELUM.displayName, ignoreCase = true) },
            proses = tasks.count { it.status.equals(TaskStatus.PROSES.displayName, ignoreCase = true) },
            selesai = tasks.count { it.status.equals(TaskStatus.SELESAI.displayName, ignoreCase = true) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskStats()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedInitialTasksIfEmpty()
        }
    }

    fun setFilter(filterLabel: String) {
        _selectedFilter.value = filterLabel
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getTaskById(id: Int) = repository.getTaskById(id)

    fun insertTask(
        title: String,
        course: String,
        description: String,
        deadline: Long,
        status: String = TaskStatus.BELUM.displayName,
        priority: String = TaskPriority.SEDANG.displayName,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                course = course.trim(),
                description = description.trim(),
                deadline = deadline,
                status = status,
                priority = priority
            )
            repository.insertTask(task)
            _userMessage.emit("Tugas berhasil ditambahkan!")
            onSuccess()
        }
    }

    fun updateTask(
        task: Task,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.updateTask(task)
            _userMessage.emit("Tugas berhasil diperbarui!")
            onSuccess()
        }
    }

    fun updateStatus(taskId: Int, newStatus: String) {
        viewModelScope.launch {
            repository.updateStatus(taskId, newStatus)
            _userMessage.emit("Status diubah ke '$newStatus'")
        }
    }

    fun deleteTask(task: Task, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _userMessage.emit("Tugas '${task.title}' berhasil dihapus")
            onSuccess()
        }
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
