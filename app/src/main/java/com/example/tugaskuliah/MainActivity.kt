package com.example.tugaskuliah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tugaskuliah.data.database.TaskDatabase
import com.example.tugaskuliah.data.repository.TaskRepository
import com.example.tugaskuliah.ui.screens.TaskDetailScreen
import com.example.tugaskuliah.ui.screens.TaskFormScreen
import com.example.tugaskuliah.ui.screens.TaskListScreen
import com.example.tugaskuliah.ui.theme.DarkBackground
import com.example.tugaskuliah.ui.theme.TugasKuliahTheme
import com.example.tugaskuliah.viewmodel.TaskViewModel
import com.example.tugaskuliah.viewmodel.TaskViewModelFactory

sealed interface AppScreen {
    object TaskList : AppScreen
    object AddTask : AppScreen
    data class EditTask(val taskId: Int, val fromDetail: Boolean = false) : AppScreen
    data class TaskDetail(val taskId: Int) : AppScreen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = TaskRepository(TaskDatabase.getDatabase(applicationContext).taskDao())

        setContent {
            TugasKuliahTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val viewModel: TaskViewModel = viewModel(
                        factory = TaskViewModelFactory(repository)
                    )
                    TugasKuliahApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TugasKuliahApp(viewModel: TaskViewModel) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.TaskList) }

    // Handle back button behavior cleanly
    BackHandler(enabled = currentScreen != AppScreen.TaskList) {
        currentScreen = when (val screen = currentScreen) {
            is AppScreen.EditTask -> {
                if (screen.fromDetail) AppScreen.TaskDetail(screen.taskId) else AppScreen.TaskList
            }
            is AppScreen.TaskDetail -> AppScreen.TaskList
            is AppScreen.AddTask -> AppScreen.TaskList
            AppScreen.TaskList -> AppScreen.TaskList
        }
    }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            is AppScreen.TaskList -> {
                TaskListScreen(
                    viewModel = viewModel,
                    onNavigateToAddTask = {
                        currentScreen = AppScreen.AddTask
                    },
                    onNavigateToEditTask = { taskId ->
                        currentScreen = AppScreen.EditTask(taskId, fromDetail = false)
                    },
                    onNavigateToDetail = { taskId ->
                        currentScreen = AppScreen.TaskDetail(taskId)
                    }
                )
            }
            is AppScreen.AddTask -> {
                TaskFormScreen(
                    taskId = null,
                    viewModel = viewModel,
                    onNavigateBack = {
                        currentScreen = AppScreen.TaskList
                    }
                )
            }
            is AppScreen.EditTask -> {
                TaskFormScreen(
                    taskId = screen.taskId,
                    viewModel = viewModel,
                    onNavigateBack = {
                        currentScreen = if (screen.fromDetail) {
                            AppScreen.TaskDetail(screen.taskId)
                        } else {
                            AppScreen.TaskList
                        }
                    }
                )
            }
            is AppScreen.TaskDetail -> {
                TaskDetailScreen(
                    taskId = screen.taskId,
                    viewModel = viewModel,
                    onNavigateBack = {
                        currentScreen = AppScreen.TaskList
                    },
                    onNavigateToEdit = { taskId ->
                        currentScreen = AppScreen.EditTask(taskId, fromDetail = true)
                    }
                )
            }
        }
    }
}
