package com.mealnote.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mealnote.app.databinding.FragmentSettingsBinding
import com.mealnote.app.data.database.MealNoteDatabase
import com.mealnote.app.data.repository.SettingsRepository
import kotlinx.coroutines.launch
import java.io.File

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Manually create repository
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Create database and repository manually
        val database = MealNoteDatabase.getDatabase(requireContext())
        settingsRepository = SettingsRepository(database.settingsDao())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()
        setupClickListeners()
        setupSeekBarListener()
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            try {
                val settings = settingsRepository.getSettings()
                binding.waterGoalSeekBar.progress = settings.dailyWaterGoalMl
                binding.waterGoalValue.text = "${settings.dailyWaterGoalMl} ml"
                binding.waterReminderSwitch.isChecked = settings.enableWaterReminders
                binding.mealReminderSwitch.isChecked = settings.enableMealReminders
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading settings: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSeekBarListener() {
        binding.waterGoalSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.waterGoalValue.text = "$progress ml"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {
                // Do nothing
            }
        })
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val dailyGoal = binding.waterGoalSeekBar.progress
                    val enableWaterReminders = binding.waterReminderSwitch.isChecked
                    val enableMealReminders = binding.mealReminderSwitch.isChecked

                    // Update settings
                    settingsRepository.updateWaterGoal(dailyGoal)
                    settingsRepository.updateWaterReminderEnabled(enableWaterReminders)
                    settingsRepository.updateMealReminderEnabled(enableMealReminders)

                    // Show success message
                    Snackbar.make(binding.root, "Settings saved successfully!", Snackbar.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Snackbar.make(binding.root, "Error saving: ${e.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Optional: Save when switches are toggled
        binding.waterReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsRepository.updateWaterReminderEnabled(isChecked)
                Snackbar.make(binding.root, "Water reminders ${if (isChecked) "enabled" else "disabled"}", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.mealReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingsRepository.updateMealReminderEnabled(isChecked)
                Snackbar.make(binding.root, "Meal reminders ${if (isChecked) "enabled" else "disabled"}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}