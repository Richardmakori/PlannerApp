package com.dailyplanner

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddTaskBottomSheet(
    private val existingTask: Task? = null,
    private val onTaskSaved: (Task) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var btnPickTime: MaterialButton
    private lateinit var btnSave: MaterialButton
    private lateinit var rgPriority: RadioGroup
    private lateinit var tvSheetTitle: TextView

    private var selectedTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_add_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.etTaskTitle)
        etDescription = view.findViewById(R.id.etTaskDescription)
        btnPickTime = view.findViewById(R.id.btnPickTime)
        btnSave = view.findViewById(R.id.btnSaveTask)
        rgPriority = view.findViewById(R.id.rgPriority)
        tvSheetTitle = view.findViewById(R.id.tvSheetTitle)

        // Pre-fill if editing
        existingTask?.let { task ->
            tvSheetTitle.text = "Edit Task"
            etTitle.setText(task.title)
            etDescription.setText(task.description)
            selectedTime = task.time
            btnPickTime.text = task.time
            when (task.priority) {
                Priority.LOW -> rgPriority.check(R.id.rbLow)
                Priority.MEDIUM -> rgPriority.check(R.id.rbMedium)
                Priority.HIGH -> rgPriority.check(R.id.rbHigh)
            }
        }

        btnPickTime.setOnClickListener { showTimePicker() }

        btnSave.setOnClickListener {
            val title = etTitle.text?.toString()?.trim()
            if (title.isNullOrBlank()) {
                etTitle.error = "Task title is required"
                return@setOnClickListener
            }
            if (selectedTime.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val priority = when (rgPriority.checkedRadioButtonId) {
                R.id.rbLow -> Priority.LOW
                R.id.rbHigh -> Priority.HIGH
                else -> Priority.MEDIUM
            }

            val task = Task(
                id = existingTask?.id ?: java.util.UUID.randomUUID().toString(),
                title = title,
                description = etDescription.text?.toString()?.trim() ?: "",
                time = selectedTime,
                isCompleted = existingTask?.isCompleted ?: false,
                priority = priority
            )
            onTaskSaved(task)
            dismiss()
        }
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val amPm = if (hour < 12) "AM" else "PM"
                val displayHour = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                selectedTime = String.format("%02d:%02d %s", displayHour, minute, amPm)
                btnPickTime.text = selectedTime
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }
}
