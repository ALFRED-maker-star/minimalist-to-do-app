package com.example.MainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Task
import com.example.ui.components.NavTab
import com.example.ui.components.TaskFlowBottomBar
import com.example.ui.screens.AddEditTaskScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CompletedTasksScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TaskDetailScreen
import com.example.ui.theme.TaskFlowTheme
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AppSubScreen {
    NONE,
    ADD_TASK,
    EDIT_TASK,
    TASK_DETAIL
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val taskViewModel: TaskViewModel = viewModel()
            val isDarkThemePref by taskViewModel.isDarkMode.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val effectiveDark = isDarkThemePref

            TaskFlowTheme(darkTheme = effectiveDark) {
                TaskFlowApp(viewModel = taskViewModel)
            }
        }
    }
}

@Composable
fun TaskFlowApp(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(NavTab.HOME) }
    var subScreen by remember { mutableStateOf(AppSubScreen.NONE) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Listen for recently deleted task to offer quick Undo snackbar
    LaunchedEffect(Unit) {
        viewModel.recentlyDeletedTask.collectLatest { deletedTask ->
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Task \"${deletedTask.title}\" deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.restoreTask(deletedTask)
                }
            }
        }
    }

    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    } else {
        // Back Handling
        BackHandler(enabled = subScreen != AppSubScreen.NONE || currentTab != NavTab.HOME) {
            when {
                subScreen != AppSubScreen.NONE -> {
                    subScreen = AppSubScreen.NONE
                }
                currentTab != NavTab.HOME -> {
                    currentTab = NavTab.HOME
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp) // Centered responsive frame
            ) {
                when (subScreen) {
                    AppSubScreen.ADD_TASK -> {
                        AddEditTaskScreen(
                            taskToEdit = null,
                            onSaveTask = { title, description, dueDateMillis, dueTimeText, priority, category ->
                                viewModel.addTask(
                                    title = title,
                                    description = description,
                                    dueDateMillis = dueDateMillis,
                                    dueTimeText = dueTimeText,
                                    priority = priority,
                                    category = category
                                )
                                subScreen = AppSubScreen.NONE
                            },
                            onNavigateBack = { subScreen = AppSubScreen.NONE }
                        )
                    }

                    AppSubScreen.EDIT_TASK -> {
                        AddEditTaskScreen(
                            taskToEdit = selectedTask,
                            onSaveTask = { title, description, dueDateMillis, dueTimeText, priority, category ->
                                selectedTask?.let { original ->
                                    val updated = original.copy(
                                        title = title,
                                        description = description,
                                        dueDateMillis = dueDateMillis,
                                        dueTimeText = dueTimeText,
                                        priority = priority,
                                        category = category
                                    )
                                    viewModel.updateTask(updated)
                                    selectedTask = updated
                                }
                                subScreen = AppSubScreen.TASK_DETAIL
                            },
                            onNavigateBack = { subScreen = AppSubScreen.TASK_DETAIL }
                        )
                    }

                    AppSubScreen.TASK_DETAIL -> {
                        selectedTask?.let { task ->
                            TaskDetailScreen(
                                task = task,
                                onEditClick = {
                                    subScreen = AppSubScreen.EDIT_TASK
                                },
                                onDeleteClick = {
                                    viewModel.deleteTask(task)
                                    subScreen = AppSubScreen.NONE
                                    selectedTask = null
                                },
                                onToggleComplete = {
                                    viewModel.toggleTask(task)
                                    selectedTask = task.copy(isCompleted = !task.isCompleted)
                                },
                                onNavigateBack = {
                                    subScreen = AppSubScreen.NONE
                                    selectedTask = null
                                }
                            )
                        } ?: run {
                            subScreen = AppSubScreen.NONE
                        }
                    }

                    AppSubScreen.NONE -> {
                        Scaffold(
                            bottomBar = {
                                TaskFlowBottomBar(
                                    currentTab = currentTab,
                                    onTabSelected = { tab ->
                                        currentTab = tab
                                    },
                                    onAddClick = {
                                        selectedTask = null
                                        subScreen = AppSubScreen.ADD_TASK
                                    }
                                )
                            },
                            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                            containerColor = MaterialTheme.colorScheme.background
                        ) { innerPadding ->
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "tabTransition",
                                modifier = Modifier.padding(innerPadding)
                            ) { tab ->
                                when (tab) {
                                    NavTab.HOME -> {
                                        HomeScreen(
                                            viewModel = viewModel,
                                            onTaskClick = { task ->
                                                selectedTask = task
                                                subScreen = AppSubScreen.TASK_DETAIL
                                            },
                                            onAddTaskClick = {
                                                selectedTask = null
                                                subScreen = AppSubScreen.ADD_TASK
                                            }
                                        )
                                    }

                                    NavTab.CATEGORIES -> {
                                        CategoriesScreen(
                                            viewModel = viewModel,
                                            onCategorySelected = { categoryName ->
                                                viewModel.setSelectedCategory(categoryName)
                                            },
                                            onTaskClick = { task ->
                                                selectedTask = task
                                                subScreen = AppSubScreen.TASK_DETAIL
                                            },
                                            onAddTaskToCategory = { categoryName ->
                                                viewModel.setSelectedCategory(categoryName)
                                                selectedTask = null
                                                subScreen = AppSubScreen.ADD_TASK
                                            }
                                        )
                                    }

                                    NavTab.COMPLETED -> {
                                        CompletedTasksScreen(
                                            viewModel = viewModel
                                        )
                                    }

                                    NavTab.SETTINGS -> {
                                        SettingsScreen(
                                            viewModel = viewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
