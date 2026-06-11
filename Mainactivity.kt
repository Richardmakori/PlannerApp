package com.dailyplanner

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var rvTasks: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var tvEmptyState: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvTaskCount: TextView

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupFab()
        updateHeader()
        loadSampleTasks() // Load demo tasks on first launch
    }

    private fun initViews() {
        rvTasks = findViewById(R.id.rvTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvDate = findViewById(R.id.tvDate)
        tvTaskCount = findViewById(R.id.tvTaskCount)
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskChecked = { task, isChecked -> toggleTaskCompletion(task, isChecked) },
            onTaskDeleted = { task -> confirmDeleteTask(task) },
            onTaskClicked = { task -> openEditSheet(task) }
        )

        rvTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        refreshList()
    }

    private fun setupFab() {
        fabAddTask.setOnClickListener {
            AddTaskBottomSheet(onTaskSaved = { task ->
                taskList.add(task)
                refreshList()
            }).show(supportFragmentManager, "AddTask")
        }
    }

    private fun toggleTaskCompletion(task: Task, isChecked: Boolean) {
        val index = taskList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            taskList[index] = taskList[index].copy(isCompleted = isChecked)
            refreshList()
        }
    }

    private fun confirmDeleteTask(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete \"${task.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                taskList.removeAll { it.id == task.id }
                refreshList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openEditSheet(task: Task) {
        AddTaskBottomSheet(existingTask = task, onTaskSaved = { updatedTask ->
            val index = taskList.indexOfFirst { it.id == updatedTask.id }
            if (index != -1) taskList[index] = updatedTask
            refreshList()
        }).show(supportFragmentManager, "EditTask")
    }

    private fun refreshList() {
        // Sort: incomplete tasks first, then by time, completed at bottom
        val sorted = taskList.sortedWith(compareBy({ it.isCompleted }, { it.time }))
        taskAdapter.submitList(sorted.toList())

        val completedCount = taskList.count { it.isCompleted }
        tvTaskCount.text = "$completedCount/${taskList.size} completed"

        tvEmptyState.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun updateHeader() {
        val sdf = SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault())
        tvDate.text = sdf.format(Date())
    }

    private fun loadSampleTasks() {
        if (taskList.isNotEmpty()) return
        taskList.addAll(
            listOf(
                Task(title = "Morning workout", time = "07:00 AM", priority = Priority.HIGH),
                Task(title = "Team standup meeting", description = "Daily sync with the team", time = "09:30 AM", priority = Priority.HIGH),
                Task(title = "Review pull requests", description = "Check pending PRs on GitHub", time = "11:00 AM", priority = Priority.MEDIUM),
                Task(title = "Lunch break", time = "01:00 PM", priority = Priority.LOW, isCompleted = true),
                Task(title = "Write unit tests", description = "Cover TaskAdapter and ViewModel", time = "02:30 PM", priority = Priority.MEDIUM),
                Task(title = "Read documentation", time = "04:00 PM", priority = Priority.LOW),
            )
        )
        refreshList()
    }
}
