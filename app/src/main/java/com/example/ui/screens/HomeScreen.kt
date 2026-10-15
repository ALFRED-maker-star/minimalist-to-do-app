package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.ProgressRingCard
import com.example.ui.components.TaskCard
import com.example.ui.theme.TaskFlowIndigo
import com.example.ui.viewmodel.TaskFilter
import com.example.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTasks by viewModel.filteredActiveTasks.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var isSearchExpanded by remember { mutableStateOf(false) }

    // Dynamic greeting based on current time of day
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    val currentDateText = remember {
        val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        sdf.format(Date())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_tasks_list"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Header Section: Greeting & Date & Search Action
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = greeting,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentDateText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                isSearchExpanded = !isSearchExpanded
                                if (!isSearchExpanded) viewModel.setSearchQuery("")
                            },
                            modifier = Modifier.testTag("home_search_toggle")
                        ) {
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search tasks",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Search Field
                    if (isSearchExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search by title, description, or category...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_search_input"),
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = TaskFlowIndigo,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Progress Ring Card showing % tasks completed today
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    ProgressRingCard(
                        completedCount = todayStats.first,
                        totalCount = todayStats.second
                    )
                }
            }

            // Filter Chips (All / Today / High Priority + Category Chips)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                FilterChipsRow(
                    selectedFilter = activeFilter,
                    onFilterSelected = { viewModel.setFilter(it) },
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.setSelectedCategory(it) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Task List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory != null) "$selectedCategory Tasks" else "${activeFilter.displayName} Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "${activeTasks.size} remaining",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Task List or Empty State
            if (activeTasks.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    EmptyStateView(
                        title = if (searchQuery.isNotBlank()) "No matching tasks" else "No tasks yet — add your first one!",
                        subtitle = if (searchQuery.isNotBlank()) "Try searching for a different keyword" else "Tap below or use the + button to create a task.",
                        buttonText = if (searchQuery.isNotBlank()) null else "Add New Task",
                        onButtonClick = onAddTaskClick
                    )
                }
            } else {
                items(activeTasks, key = { it.id }) { task ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        TaskCard(
                            task = task,
                            onTaskClick = { onTaskClick(task) },
                            onToggleComplete = { viewModel.toggleTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }

        // Floating "+" Button Bottom-Right (as explicitly requested: "Floating "+" button bottom-right")
        FloatingActionButton(
            onClick = onAddTaskClick,
            shape = CircleShape,
            containerColor = TaskFlowIndigo,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp)
                .size(56.dp)
                .testTag("floating_add_task_button")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Task",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
