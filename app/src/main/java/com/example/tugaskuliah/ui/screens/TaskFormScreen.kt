package com.example.tugaskuliah.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugaskuliah.data.entity.Task
import com.example.tugaskuliah.data.entity.TaskPriority
import com.example.tugaskuliah.data.entity.TaskStatus
import com.example.tugaskuliah.ui.components.DateUtils
import com.example.tugaskuliah.ui.theme.DarkBackground
import com.example.tugaskuliah.ui.theme.DarkCardBackground
import com.example.tugaskuliah.ui.theme.DarkCardBorder
import com.example.tugaskuliah.ui.theme.DarkSurface
import com.example.tugaskuliah.ui.theme.OverdueRed
import com.example.tugaskuliah.ui.theme.TextPrimary
import com.example.tugaskuliah.ui.theme.TextSecondary
import com.example.tugaskuliah.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskFormScreen(
    taskId: Int? = null,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isEditMode = taskId != null

    var title by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(TaskStatus.BELUM.displayName) }
    var priority by remember { mutableStateOf(TaskPriority.SEDANG.displayName) }
    var deadlineTimestamp by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 2)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 0)
            }.timeInMillis
        )
    }

    var existingTask by remember { mutableStateOf<Task?>(null) }

    // Validation error states
    var titleError by remember { mutableStateOf<String?>(null) }
    var courseError by remember { mutableStateOf<String?>(null) }
    var deadlineError by remember { mutableStateOf<String?>(null) }

    // Load existing task if in Edit mode
    LaunchedEffect(taskId) {
        if (taskId != null) {
            val task = viewModel.getTaskById(taskId).firstOrNull()
            task?.let {
                existingTask = it
                title = it.title
                course = it.course
                description = it.description
                status = it.status
                priority = it.priority
                deadlineTimestamp = it.deadline
            }
        }
    }

    // Common university course suggestions
    val commonCourses = listOf(
        "Pemrograman Mobile",
        "Sistem Basis Data",
        "Kecerdasan Buatan",
        "Jaringan Komputer",
        "Rekayasa Perangkat Lunak",
        "Algoritma & Struktur Data",
        "Sistem Operasi"
    )

    fun showDateTimePicker() {
        val calendar = Calendar.getInstance().apply { timeInMillis = deadlineTimestamp }
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }

                // Follow up with TimePicker
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        selectedCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedCal.set(Calendar.MINUTE, minute)
                        selectedCal.set(Calendar.SECOND, 0)
                        deadlineTimestamp = selectedCal.timeInMillis
                        deadlineError = null
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun validateAndSave() {
        var isValid = true

        if (title.trim().isEmpty()) {
            titleError = "Judul tugas tidak boleh kosong!"
            isValid = false
        } else if (title.trim().length < 3) {
            titleError = "Judul tugas minimal 3 karakter!"
            isValid = false
        } else {
            titleError = null
        }

        if (course.trim().isEmpty()) {
            courseError = "Nama mata kuliah wajib diisi!"
            isValid = false
        } else {
            courseError = null
        }

        if (deadlineTimestamp <= 0) {
            deadlineError = "Pilih batas waktu deadline tugas!"
            isValid = false
        } else {
            deadlineError = null
        }

        if (isValid) {
            if (isEditMode && existingTask != null) {
                val updated = existingTask!!.copy(
                    title = title.trim(),
                    course = course.trim(),
                    description = description.trim(),
                    deadline = deadlineTimestamp,
                    status = status,
                    priority = priority
                )
                viewModel.updateTask(updated) {
                    onNavigateBack()
                }
            } else {
                viewModel.insertTask(
                    title = title,
                    course = course,
                    description = description,
                    deadline = deadlineTimestamp,
                    status = status,
                    priority = priority
                ) {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Tugas Kuliah" else "Tambah Tugas Baru",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Field 1: Judul Tugas
                    Column {
                        Text(
                            text = "Judul Tugas *",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                if (it.isNotBlank()) titleError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Contoh: Laporan Praktikum Mobile", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Title, contentDescription = null, tint = TextSecondary)
                            },
                            isError = titleError != null,
                            supportingText = {
                                titleError?.let {
                                    Text(it, color = OverdueRed, fontSize = 12.sp)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    // Field 2: Mata Kuliah
                    Column {
                        Text(
                            text = "Mata Kuliah *",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = course,
                            onValueChange = {
                                course = it
                                if (it.isNotBlank()) courseError = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Pilih atau ketik mata kuliah", color = TextSecondary) },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = TextSecondary)
                            },
                            isError = courseError != null,
                            supportingText = {
                                courseError?.let {
                                    Text(it, color = OverdueRed, fontSize = 12.sp)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        // Quick suggestion chips
                        Text(
                            text = "Rekomendasi mata kuliah:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            commonCourses.forEach { item ->
                                SuggestionChip(
                                    onClick = {
                                        course = item
                                        courseError = null
                                    },
                                    label = { Text(item, fontSize = 11.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = DarkSurface,
                                        labelColor = if (course == item) MaterialTheme.colorScheme.primary else TextSecondary
                                    ),
                                    border = SuggestionChipDefaults.suggestionChipBorder(
                                        enabled = true,
                                        borderColor = if (course == item) MaterialTheme.colorScheme.primary else DarkCardBorder
                                    )
                                )
                            }
                        }
                    }

                    // Field 3: Deadline Date & Time
                    Column {
                        Text(
                            text = "Batas Waktu (Deadline) *",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDateTimePicker() },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, if (deadlineError != null) OverdueRed else DarkCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = DateUtils.formatFullDateTime(deadlineTimestamp),
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Ketuk untuk mengubah tanggal & jam",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                        if (deadlineError != null) {
                            Text(
                                text = deadlineError!!,
                                color = OverdueRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }

                    // Field 4: Status (Belum / Proses / Selesai)
                    Column {
                        Text(
                            text = "Status Pengerjaan",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                TaskStatus.BELUM.displayName,
                                TaskStatus.PROSES.displayName,
                                TaskStatus.SELESAI.displayName
                            ).forEach { s ->
                                val isSelected = status == s
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { status = s },
                                    label = { Text(s, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = DarkSurface,
                                        labelColor = TextSecondary,
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = DarkCardBorder,
                                        selectedBorderColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }

                    // Field 5: Prioritas (Rendah / Sedang / Tinggi)
                    Column {
                        Text(
                            text = "Tingkat Prioritas",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                TaskPriority.RENDAH.displayName,
                                TaskPriority.SEDANG.displayName,
                                TaskPriority.TINGGI.displayName
                            ).forEach { p ->
                                val isSelected = priority == p
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { priority = p },
                                    label = { Text(p, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = DarkSurface,
                                        labelColor = TextSecondary,
                                        selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = DarkCardBorder,
                                        selectedBorderColor = MaterialTheme.colorScheme.secondary
                                    )
                                )
                            }
                        }
                    }

                    // Field 6: Deskripsi / Catatan Tambahan (Opsional)
                    Column {
                        Text(
                            text = "Deskripsi / Catatan Tambahan",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = {
                                Text(
                                    "Instruksi tugas, link referensi, catatan dosen, atau detail pengerjaan...",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Description, contentDescription = null, tint = TextSecondary)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            }

            // Save Button
            Button(
                onClick = { validateAndSave() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditMode) "Perbarui Tugas" else "Simpan Tugas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
