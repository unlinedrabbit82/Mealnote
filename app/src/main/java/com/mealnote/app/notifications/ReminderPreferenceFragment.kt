package com.mealnote.app.notifications

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.mealnote.app.R
import com.mealnote.app.data.database.MealNoteDatabase
import com.mealnote.app.data.repository.SettingsRepository
import com.mealnote.app.databinding.FragmentReminderPreferenceBinding
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReminderPreferenceFragment : Fragment() {

    private var _binding: FragmentReminderPreferenceBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var reminderScheduler: ReminderScheduler
    private var currentSettings: com.mealnote.app.data.database.entities.UserSettings? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderPreferenceBinding.inflate(inflater, container, false)

        // Initialize repository and scheduler
        val database = MealNoteDatabase.getDatabase(requireContext())
        settingsRepository = SettingsRepository(database.settingsDao())
        reminderScheduler = ReminderScheduler(requireContext())

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()
        setupClickListeners()
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            try {
                currentSettings = settingsRepository.getSettings()
                currentSettings?.let { settings ->
                    binding.waterReminderSwitch.isChecked = settings.enableWaterReminders
                    binding.mealReminderSwitch.isChecked = settings.enableMealReminders
                    binding.waterIntervalText.text = "Every ${settings.waterReminderIntervalHours} hours"
                    binding.waterIntervalSlider.value = settings.waterReminderIntervalHours.toFloat()

                    updateMealTimesList(settings.mealReminderTimes)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading settings: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupClickListeners() {
        binding.waterReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                try {
                    currentSettings?.let { settings ->
                        settings.enableWaterReminders = isChecked
                        settingsRepository.saveSettings(settings)
                        if (isChecked) {
                            reminderScheduler.scheduleWaterReminders(settings)
                            Snackbar.make(binding.root, "Water reminders enabled", Snackbar.LENGTH_SHORT).show()
                        } else {
                            reminderScheduler.cancelAllReminders()
                            Snackbar.make(binding.root, "Water reminders disabled", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.mealReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                try {
                    currentSettings?.let { settings ->
                        settings.enableMealReminders = isChecked
                        settingsRepository.saveSettings(settings)
                        if (isChecked) {
                            reminderScheduler.scheduleMealReminders(settings)
                            Snackbar.make(binding.root, "Meal reminders enabled", Snackbar.LENGTH_SHORT).show()
                        } else {
                            reminderScheduler.cancelAllReminders()
                            Snackbar.make(binding.root, "Meal reminders disabled", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.waterIntervalSlider.addOnChangeListener { _, value, _ ->
            val hours = value.toInt()
            binding.waterIntervalText.text = "Every $hours hours"
            lifecycleScope.launch {
                try {
                    currentSettings?.let { settings ->
                        settings.waterReminderIntervalHours = hours
                        settingsRepository.saveSettings(settings)
                        if (settings.enableWaterReminders) {
                            reminderScheduler.scheduleWaterReminders(settings)
                        }
                    }
                } catch (e: Exception) {
                    // Handle error silently
                }
            }
        }

        binding.addMealTimeButton.setOnClickListener {
            showTimePicker()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Meal Reminder Time")
            .build()

        picker.addOnPositiveButtonClickListener {
            val time = LocalTime.of(picker.hour, picker.minute)
            val timeString = time.format(DateTimeFormatter.ofPattern("HH:mm"))

            lifecycleScope.launch {
                try {
                    currentSettings?.let { settings ->
                        val newTimes = settings.mealReminderTimes.toMutableList()
                        if (!newTimes.contains(timeString)) {
                            newTimes.add(timeString)
                            newTimes.sort()
                            settings.mealReminderTimes = newTimes
                            settingsRepository.saveSettings(settings)
                            updateMealTimesList(newTimes)
                            if (settings.enableMealReminders) {
                                reminderScheduler.scheduleMealReminders(settings)
                            }
                            Snackbar.make(binding.root, "Added reminder at $timeString", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(binding.root, "Reminder already exists", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        picker.show(parentFragmentManager, "time_picker")
    }

    private fun updateMealTimesList(times: List<String>) {
        binding.mealTimesContainer.removeAllViews()

        if (times.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "No meal reminders set. Tap + to add."
                textSize = 14f
                setPadding(0, 16, 0, 16)
            }
            binding.mealTimesContainer.addView(emptyText)
            return
        }

        times.forEach { time ->
            val timeView = layoutInflater.inflate(
                R.layout.item_meal_time,
                binding.mealTimesContainer,
                false
            )

            val timeText = timeView.findViewById<TextView>(R.id.timeText)
            val deleteButton = timeView.findViewById<ImageButton>(R.id.deleteButton)

            timeText.text = time

            deleteButton.setOnClickListener {
                lifecycleScope.launch {
                    try {
                        currentSettings?.let { settings ->
                            val newTimes = settings.mealReminderTimes.toMutableList()
                            newTimes.remove(time)
                            settings.mealReminderTimes = newTimes
                            settingsRepository.saveSettings(settings)
                            updateMealTimesList(newTimes)
                            if (settings.enableMealReminders) {
                                reminderScheduler.scheduleMealReminders(settings)
                            }
                            Snackbar.make(binding.root, "Removed reminder at $time", Snackbar.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            binding.mealTimesContainer.addView(timeView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}