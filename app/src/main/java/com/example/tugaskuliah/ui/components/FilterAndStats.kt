package com.example.tugaskuliah.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugaskuliah.ui.theme.DarkCardBackground
import com.example.tugaskuliah.ui.theme.DarkCardBorder
import com.example.tugaskuliah.ui.theme.DarkSurface
import com.example.tugaskuliah.ui.theme.DarkSurfaceVariant
import com.example.tugaskuliah.ui.theme.StatusBelumColor
import com.example.tugaskuliah.ui.theme.StatusProsesColor
import com.example.tugaskuliah.ui.theme.StatusSelesaiColor
import com.example.tugaskuliah.ui.theme.TextPrimary
import com.example.tugaskuliah.ui.theme.TextSecondary
import com.example.tugaskuliah.viewmodel.TaskFilter
import com.example.tugaskuliah.viewmodel.TaskStats

@Composable
fun TaskStatsSummary(
    stats: TaskStats,
    onFilterClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                count = stats.total,
                label = "Total",
                color = MaterialTheme.colorScheme.primary,
                onClick = { onFilterClick(TaskFilter.ALL.label) }
            )
            StatDivider()
            StatItem(
                count = stats.belum,
                label = "Belum",
                color = StatusBelumColor,
                onClick = { onFilterClick(TaskFilter.BELUM.label) }
            )
            StatDivider()
            StatItem(
                count = stats.proses,
                label = "Proses",
                color = StatusProsesColor,
                onClick = { onFilterClick(TaskFilter.PROSES.label) }
            )
            StatDivider()
            StatItem(
                count = stats.selesai,
                label = "Selesai",
                color = StatusSelesaiColor,
                onClick = { onFilterClick(TaskFilter.SELESAI.label) }
            )
        }
    }
}

@Composable
private fun StatItem(
    count: Int,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatDivider() {
    Surface(
        modifier = Modifier
            .height(28.dp)
            .width(1.dp),
        color = DarkCardBorder
    ) {}
}

@Composable
fun FilterChipBar(
    selectedFilter: String,
    stats: TaskStats,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskFilter.list.forEach { filter ->
            val count = when (filter) {
                TaskFilter.ALL -> stats.total
                TaskFilter.BELUM -> stats.belum
                TaskFilter.PROSES -> stats.proses
                TaskFilter.SELESAI -> stats.selesai
            }

            val isSelected = selectedFilter == filter.label

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter.label) },
                label = {
                    Text(
                        text = "${filter.label} ($count)",
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
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
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text("Cari judul, mata kuliah, catatan...", color = TextSecondary, fontSize = 14.sp)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cari",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Hapus",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkSurface,
            unfocusedContainerColor = DarkSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}
