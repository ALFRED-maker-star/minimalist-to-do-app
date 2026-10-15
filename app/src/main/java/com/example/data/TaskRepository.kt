package com.example.data

import com.example.model.Priority
import com.example.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getTaskById(id: Long): Flow<Task?> = taskDao.getTaskById(id).map { it?.toDomain() }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(TaskEntity.fromDomain(task))
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(TaskEntity.fromDomain(task))
    }

    suspend fun toggleTaskCompletion(task: Task) {
        val updated = task.copy(
            isCompleted = !task.isCompleted,
            completedAtMillis = if (!task.isCompleted) System.currentTimeMillis() else null
        )
        taskDao.updateTask(TaskEntity.fromDomain(updated))
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(TaskEntity.fromDomain(task))
    }

    suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    suspend fun resetWithSampleData() {
        taskDao.deleteAllTasks()
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        // Today millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.timeInMillis

        // Yesterday millis
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayMillis = cal.timeInMillis

        val sampleTasks = listOf(
            Task(
                title = "Design mobile app wireframes",
                description = "Prepare Figma mockups for 375x812 viewport and design system tokens.",
                dueDateMillis = todayStart,
                dueTimeText = "10:30 AM",
                isCompleted = false,
                priority = Priority.HIGH,
                category = "Work",
                createdAtMillis = now - 7200000
            ),
            Task(
                title = "Team sprint sync meeting",
                description = "Review weekly blockers, task velocity, and upcoming release milestones.",
                dueDateMillis = todayStart,
                dueTimeText = "1:00 PM",
                isCompleted = true,
                completedAtMillis = todayStart + 3600000,
                priority = Priority.MEDIUM,
                category = "Work",
                createdAtMillis = now - 18000000
            ),
            Task(
                title = "Daily 30-min cardio workout",
                description = "High-intensity interval running or brisk walk in the park.",
                dueDateMillis = todayStart,
                dueTimeText = "5:30 PM",
                isCompleted = false,
                priority = Priority.MEDIUM,
                category = "Health",
                createdAtMillis = now - 10000000
            ),
            Task(
                title = "Quarterly budget review",
                description = "Audit subscription services, credit cards, and monthly savings goals.",
                dueDateMillis = todayStart,
                dueTimeText = "7:00 PM",
                isCompleted = false,
                priority = Priority.HIGH,
                category = "Finance",
                createdAtMillis = now - 5000000
            ),
            Task(
                title = "Read 2 chapters of Clean Architecture",
                description = "Deep dive into domain boundary enforcement and dependency inversion.",
                dueDateMillis = todayStart,
                dueTimeText = "9:00 PM",
                isCompleted = true,
                completedAtMillis = todayStart + 7200000,
                priority = Priority.LOW,
                category = "Study",
                createdAtMillis = now - 25000000
            ),
            Task(
                title = "Refill grocery essentials",
                description = "Almond milk, organic avocados, sourdough bread, and green tea.",
                dueDateMillis = yesterdayMillis,
                dueTimeText = "3:00 PM",
                isCompleted = true,
                completedAtMillis = yesterdayMillis + 54000000,
                priority = Priority.LOW,
                category = "Personal",
                createdAtMillis = yesterdayMillis - 10000000
            ),
            Task(
                title = "Renew domain registration",
                description = "Ensure auto-renew is active on Cloudflare DNS dashboard.",
                dueDateMillis = yesterdayMillis,
                dueTimeText = "11:00 AM",
                isCompleted = true,
                completedAtMillis = yesterdayMillis + 36000000,
                priority = Priority.MEDIUM,
                category = "Work",
                createdAtMillis = yesterdayMillis - 36000000
            )
        )

        taskDao.insertTasks(sampleTasks.map { TaskEntity.fromDomain(it) })
    }
}
