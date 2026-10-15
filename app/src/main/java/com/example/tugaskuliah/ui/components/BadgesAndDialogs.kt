package com.example.tugaskuliah.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugaskuliah.data.entity.TaskPriority
import com.example.tugaskuliah.data.entity.TaskStatus
import com.example.tugaskuliah.ui.theme.DarkCardBackground
import com.example.tugaskuliah.ui.theme.DarkCardBorder
import com.example.tugaskuliah.ui.theme.DarkSurface
import com.example.tugaskuliah.ui.theme.OverdueRed
import com.example.tugaskuliah.ui.theme.OverdueRedBg
import com.example.tugaskuliah.ui.theme.PriorityRendah
import com.example.tugaskuliah.ui.theme.PrioritySedang
import com.example.tugaskuliah.ui.theme.PriorityTinggi
import com.example.tugaskuliah.ui.theme.StatusBelumBg
import com.example.tugaskuliah.ui.theme.StatusBelumBorder
import com.example.tugaskuliah.ui.theme.StatusBelumColor
import com.example.tugaskuliah.ui.theme.StatusProsesBg
import com.example.tugaskuliah.ui.theme.StatusProsesBorder
import com.example.tugaskuliah.ui.theme.StatusProsesColor
import com.example.tugaskuliah.ui.theme.StatusSelesaiBg
import com.example.tugaskuliah.ui.theme.StatusSelesaiBorder
import com.example.tugaskuliah.ui.theme.StatusSelesaiColor
import com.example.tugaskuliah.ui.theme.TextPrimary
import com.example.tugaskuliah.ui.theme.TextSecondary

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val (bgColor, textColor, borderColor, icon) = when (status.lowercase()) {
        "proses" -> Quadruple(
            StatusProsesBg,
            StatusProsesColor,
            StatusProsesBorder,
            Icons.Default.HourglassBottom
        )
        "selesai" -> Quadruple(
            StatusSelesaiBg,
            StatusSelesaiColor,
            StatusSelesaiBorder,
            Icons.Default.CheckCircle
        )
        else -> Quadruple(
            StatusBelumBg,
            StatusBelumColor,
            StatusBelumBorder,
            Icons.Default.AccessTime
        )
    }

    Surface(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PriorityBadge(
    priority: String,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (priority.lowercase()) {
        "tinggi" -> Pair(PriorityTinggi, "Tinggi")
        "rendah" -> Pair(PriorityRendah, "Rendah")
        else -> Pair(PrioritySedang, "Sedang")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(0.8.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PriorityHigh,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DeadlineBadge(
    deadlineTimestamp: Long,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val info = DateUtils.getDeadlineInfo(deadlineTimestamp, isCompleted)

    val (bgColor, textColor, borderColor) = when {
        info.isOverdue -> Triple(OverdueRedBg, OverdueRed, OverdueRed.copy(alpha = 0.5f))
        info.isDueSoon -> Triple(StatusBelumBg, StatusBelumColor, StatusBelumBorder)
        else -> Triple(DarkSurface, TextSecondary, DarkCardBorder)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = if (info.isOverdue) Icons.Default.Warning else Icons.Default.AccessTime,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = info.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = if (info.isOverdue || info.isDueSoon) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ChangeStatusDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onStatusSelected: (String) -> Unit
) {
    var selected by remember { mutableStateOf(currentStatus) }
    val statuses = listOf(
        TaskStatus.BELUM.displayName,
        TaskStatus.PROSES.displayName,
        TaskStatus.SELESAI.displayName
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Ubah Status Tugas",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pilih status tugas saat ini:",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                statuses.forEach { status ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selected = status },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected == status) DarkCardBackground else DarkSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selected == status) MaterialTheme.colorScheme.primary else DarkCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (selected == status),
                                    onClick = { selected = status },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary,
                                        unselectedColor = TextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = status,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            StatusBadge(status = status)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStatusSelected(selected)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Simpan Status")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = OverdueRed,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Hapus Tugas?",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = "Apakah Anda yakin ingin menghapus tugas \"$taskTitle\"? Data yang dihapus tidak dapat dipulihkan.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = OverdueRed
                )
            ) {
                Text("Ya, Hapus", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
