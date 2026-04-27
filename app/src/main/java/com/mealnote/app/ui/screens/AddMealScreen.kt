package com.mealnote.app.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mealnote.app.ui.viewmodels.Meal
import com.mealnote.app.ui.viewmodels.MealViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    onMealAdded: () -> Unit,
    mealViewModel: MealViewModel = viewModel()
) {
    var mealName by remember { mutableStateOf("") }
    var selectedMealTime by remember { mutableStateOf("Lunch") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var sodium by remember { mutableStateOf("") }
    var fiber by remember { mutableStateOf("") }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var savedPhotoPath by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var tempPhotoFile by remember { mutableStateOf<File?>(null) }

    val context = LocalContext.current
    val mealTimes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    // Function to save bitmap to file
    fun saveBitmapToFile(bitmap: Bitmap): String? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val fileName = "MEAL_$timeStamp.jpg"

            val directory = context.filesDir
            val file = File(directory, fileName)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            android.util.Log.d("AddMeal", "Photo saved to: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            android.util.Log.e("AddMeal", "Error saving photo: ${e.message}")
            null
        }
    }

    // Camera launcher with permanent storage
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoFile?.let { file ->
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    bitmap?.let {
                        photoBitmap = it
                        savedPhotoPath = saveBitmapToFile(it)
                        android.util.Log.d("AddMeal", "Camera photo saved to: $savedPhotoPath")
                    }
                    file.delete()
                }
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                photoBitmap = bitmap
                savedPhotoPath = saveBitmapToFile(bitmap)
                android.util.Log.d("AddMeal", "Gallery photo saved to: $savedPhotoPath")
            }
        }
    }

    // Create temp file for camera photo
    fun createTempPhotoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = context.cacheDir
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            tempPhotoFile = this
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Meal") },
                navigationIcon = {
                    IconButton(onClick = onMealAdded) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Meal Name
            OutlinedTextField(
                value = mealName,
                onValueChange = { mealName = it },
                label = { Text("Meal Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meal Time Selection
            Text("Meal Time", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mealTimes.forEach { time ->
                    FilterChip(
                        selected = selectedMealTime == time,
                        onClick = { selectedMealTime = time },
                        label = { Text(time) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calories (Optional)
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ===== MACROS SECTION =====
            Text("Macros (Optional)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // First row: Protein, Carbs, Fat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Protein (g)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("Carbs (g)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it },
                    label = { Text("Fat (g)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Second row: Sodium, Fiber
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = sodium,
                    onValueChange = { sodium = it },
                    label = { Text("Sodium (mg)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                OutlinedTextField(
                    value = fiber,
                    onValueChange = { fiber = it },
                    label = { Text("Fiber (g)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photo Section
            Text("Meal Photo (Optional)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val photoFile = createTempPhotoFile()
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        cameraLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📸 Take Photo")
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🖼️ Choose Photo")
                }
            }

            // Photo Preview
            photoBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Meal photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Remove photo button
                    IconButton(
                        onClick = {
                            photoBitmap = null
                            savedPhotoPath = null
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Remove")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            if (showError) {
                Text(
                    "Please enter a meal name",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Save Button
            Button(
                onClick = {
                    if (mealName.isBlank()) {
                        showError = true
                    } else {
                        val newMeal = Meal(
                            id = (mealViewModel.meals.value.size + 1),
                            name = mealName,
                            mealTime = selectedMealTime,
                            calories = calories.toIntOrNull(),
                            protein = protein.toIntOrNull(),
                            carbs = carbs.toIntOrNull(),
                            fat = fat.toIntOrNull(),
                            sodium = sodium.toIntOrNull(),
                            fiber = fiber.toIntOrNull(),
                            photoPath = savedPhotoPath,
                            timestamp = System.currentTimeMillis()
                        )
                        mealViewModel.addMeal(newMeal)
                        onMealAdded()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save Meal", modifier = Modifier.padding(8.dp))
            }
        }
    }
}