package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.TaskCategories
import com.example.ui.theme.AccentCoralHigh
import com.example.ui.theme.TaskFlowIndigo
import com.example.ui.viewmodel.TaskFilter

@Composable
fun FilterChipsRow(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskFilter.entries.forEach { filter ->
            val isSelected = selectedFilter == filter && selectedCategory == null
            FilterChip(
                selected = isSelected,
                onClick = {
                    onFilterSelected(filter)
                    onCategorySelected(null)
                },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (filter == TaskFilter.HIGH_PRIORITY) AccentCoralHigh else TaskFlowIndigo,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    selectedBorderColor = Color.Transparent,
                    borderWidth = 1.dp
                ),
                modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
            )
        }

        // Category Filter Chips
        TaskCategories.all.forEach { category ->
            val isSelected = selectedCategory.equals(category.name, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        onCategorySelected(null)
                    } else {
                        onCategorySelected(category.name)
                    }
                },
                label = {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = category.color,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    selectedBorderColor = Color.Transparent,
                    borderWidth = 1.dp
                ),
                modifier = Modifier.testTag("filter_category_${category.name.lowercase()}")
            )
        }
    }
}
