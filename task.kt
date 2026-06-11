package com.dailyplanner

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val time: String,
    var isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
)

enum class Priority(val label: String, val colorRes: Int) {
    LOW("Low", R.color.priority_low),
    MEDIUM("Medium", R.color.priority_medium),
    HIGH("High", R.color.priority_high)
}
