package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Priority
import com.example.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDateMillis: Long? = null,
    val dueTimeText: String? = null,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val priority: String = "MEDIUM",
    val category: String = "Work",
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            dueDateMillis = dueDateMillis,
            dueTimeText = dueTimeText,
            isCompleted = isCompleted,
            completedAtMillis = completedAtMillis,
            priority = Priority.fromString(priority),
            category = category,
            createdAtMillis = createdAtMillis
        )
    }

    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                dueDateMillis = task.dueDateMillis,
                dueTimeText = task.dueTimeText,
                isCompleted = task.isCompleted,
                completedAtMillis = task.completedAtMillis,
                priority = task.priority.name,
                category = task.category,
                createdAtMillis = task.createdAtMillis
            )
        }
    }
}
