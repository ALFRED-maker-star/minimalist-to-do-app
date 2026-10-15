package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TaskDatabase
import com.example.data.TaskRepository
import com.example.model.Priority
import com.example.model.Task
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TaskFilter(val displayName: String) {
    ALL("All"),
    TODAY("Today"),
    HIGH_PRIORITY("High Priority")
}

enum class SortOption(val displayName: String) {
    DUE_DATE("Due Date"),
    PRIORITY("Priority"),
    ALPHABETICAL("Alphabetical"),
    NEWEST("Newest First")
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: StateFlow<List<Task>>

    private val _activeFilter = MutableStateFlow(TaskFilter.ALL)
    val activeFilter: StateFlow<TaskFilter> = _activeFilter.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DUE_DATE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Undo snackbar notification
    private val _recentlyDeletedTask = MutableSharedFlow<Task>()
    val recentlyDeletedTask: SharedFlow<Task> = _recentlyDeletedTask.asSharedFlow()

    init {
        val db = TaskDatabase.getInstance(application)
        repository = TaskRepository(db.taskDao())

        allTasks = repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            // Seed initial sample data if database is empty
            val initial = repository.allTasks.first()
            if (initial.isEmpty()) {
                repository.resetWithSampleData()
            }
        }
    }

    // Filtered and sorted active tasks for Home Screen
    val filteredActiveTasks: StateFlow<List<Task>> = combine(
        allTasks,
        activeFilter,
        selectedCategory,
        sortOption,
        searchQuery
    ) { tasks, filter, category, sort, query ->
        val active = tasks.filter { !it.isCompleted }
        val categoryFiltered = if (category != null) {
            active.filter { it.category.equals(category, ignoreCase = true) }
        } else {
            active
        }

        val chipFiltered = when (filter) {
            TaskFilter.ALL -> categoryFiltered
            TaskFilter.TODAY -> categoryFiltered.filter { isDueToday(it.dueDateMillis) }
            TaskFilter.HIGH_PRIORITY -> categoryFiltered.filter { it.priority == Priority.HIGH }
        }

        val searchFiltered = if (query.isNotBlank()) {
            chipFiltered.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        } else {
            chipFiltered
        }

        sortTasks(searchFiltered, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Completed tasks grouped by date
    val completedTasks: StateFlow<List<Task>> = allTasks.combine(sortOption) { tasks, _ ->
        tasks.filter { it.isCompleted }.sortedByDescending { it.completedAtMillis ?: it.createdAtMillis }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Today's completion stats for the Progress Ring
    val todayStats: StateFlow<Pair<Int, Int>> = allTasks.combine(activeFilter) { tasks, _ ->
        val todayTasks = tasks.filter { isDueToday(it.dueDateMillis) }
        val completed = todayTasks.count { it.isCompleted }
        val total = todayTasks.size
        // If no tasks explicitly for today, compute over all active + completed today
        if (total == 0) {
            val anyCompletedToday = tasks.count { it.isCompleted && isToday(it.completedAtMillis) }
            val uncompleted = tasks.count { !it.isCompleted }
            val overallTotal = anyCompletedToday + uncompleted
            Pair(anyCompletedToday, overallTotal)
        } else {
            Pair(completed, total)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Pair(0, 0)
    )

    fun setFilter(filter: TaskFilter) {
        _activeFilter.value = filter
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun addTask(
        title: String,
        description: String,
        dueDateMillis: Long?,
        dueTimeText: String?,
        priority: Priority,
        category: String
    ) {
        viewModelScope.launch {
            val newTask = Task(
                title = title.trim(),
                description = description.trim(),
                dueDateMillis = dueDateMillis,
                dueTimeText = dueTimeText,
                priority = priority,
                category = category,
                isCompleted = false,
                createdAtMillis = System.currentTimeMillis()
            )
            repository.insertTask(newTask)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _recentlyDeletedTask.emit(task)
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun uncompleteTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = false, completedAtMillis = null))
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
        }
    }

    private fun sortTasks(tasks: List<Task>, sort: SortOption): List<Task> {
        return when (sort) {
            SortOption.DUE_DATE -> tasks.sortedWith(
                compareBy<Task> { it.dueDateMillis ?: Long.MAX_VALUE }
                    .thenBy { it.priority.ordinal * -1 }
            )
            SortOption.PRIORITY -> tasks.sortedByDescending { it.priority.ordinal }
            SortOption.ALPHABETICAL -> tasks.sortedBy { it.title.lowercase(Locale.ROOT) }
            SortOption.NEWEST -> tasks.sortedByDescending { it.createdAtMillis }
        }
    }

    companion object {
        fun isDueToday(dueDateMillis: Long?): Boolean {
            if (dueDateMillis == null) return true // treat unspecified as today for quick access
            return isToday(dueDateMillis)
        }

        fun isToday(timestamp: Long?): Boolean {
            if (timestamp == null) return false
            val cal = Calendar.getInstance()
            val nowYear = cal.get(Calendar.YEAR)
            val nowDay = cal.get(Calendar.DAY_OF_YEAR)

            cal.timeInMillis = timestamp
            return cal.get(Calendar.YEAR) == nowYear && cal.get(Calendar.DAY_OF_YEAR) == nowDay
        }

        fun isYesterday(timestamp: Long?): Boolean {
            if (timestamp == null) return false
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yYear = cal.get(Calendar.YEAR)
            val yDay = cal.get(Calendar.DAY_OF_YEAR)

            cal.timeInMillis = timestamp
            return cal.get(Calendar.YEAR) == yYear && cal.get(Calendar.DAY_OF_YEAR) == yDay
        }

        fun formatDisplayDate(timestamp: Long?): String {
            if (timestamp == null) return "Today"
            return when {
                isToday(timestamp) -> "Today"
                isYesterday(timestamp) -> "Yesterday"
                else -> {
                    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
    }
}
