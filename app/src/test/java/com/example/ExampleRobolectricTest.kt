package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Priority
import com.example.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TaskFlow", appName)
    }

    @Test
    fun `task creation and default state`() {
        val task = Task(
            id = 1,
            title = "Design review",
            priority = Priority.HIGH,
            category = "Work"
        )
        assertFalse(task.isCompleted)
        assertEquals(Priority.HIGH, task.priority)
        assertEquals("Work", task.category)
    }
}
