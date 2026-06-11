package com.dailyplanner

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskDeleted: (Task) -> Unit,
    private val onTaskClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.cbTaskComplete)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTaskTime)
        private val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteTask)
        private val priorityIndicator: View = itemView.findViewById(R.id.viewPriorityIndicator)

        fun bind(task: Task) {
            checkBox.isChecked = task.isCompleted
            tvTitle.text = task.title
            tvTime.text = task.time
            tvPriority.text = task.priority.label

            // Show/hide description
            if (task.description.isNotBlank()) {
                tvDescription.visibility = View.VISIBLE
                tvDescription.text = task.description
            } else {
                tvDescription.visibility = View.GONE
            }

            // Strikethrough effect for completed tasks
            if (task.isCompleted) {
                tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvTitle.alpha = 0.5f
                tvDescription.alpha = 0.5f
                tvTime.alpha = 0.5f
                itemView.alpha = 0.75f
            } else {
                tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                tvTitle.alpha = 1f
                tvDescription.alpha = 1f
                tvTime.alpha = 1f
                itemView.alpha = 1f
            }

            // Priority color
            val priorityColor = ContextCompat.getColor(itemView.context, task.priority.colorRes)
            priorityIndicator.setBackgroundColor(priorityColor)
            tvPriority.setTextColor(priorityColor)

            // Listeners
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = task.isCompleted
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onTaskChecked(task, isChecked)
            }

            btnDelete.setOnClickListener { onTaskDeleted(task) }
            itemView.setOnClickListener { onTaskClicked(task) }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }
}
