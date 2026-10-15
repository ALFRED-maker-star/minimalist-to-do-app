package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TaskFlowIndigo

enum class NavTab {
    HOME,
    CATEGORIES,
    COMPLETED,
    SETTINGS
}

@Composable
fun TaskFlowBottomBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Home Tab
                NavItem(
                    label = "Home",
                    icon = if (currentTab == NavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    selected = currentTab == NavTab.HOME,
                    onClick = { onTabSelected(NavTab.HOME) },
                    testTag = "nav_tab_home"
                )

                // Categories Tab
                NavItem(
                    label = "Categories",
                    icon = Icons.Outlined.GridView,
                    selected = currentTab == NavTab.CATEGORIES,
                    onClick = { onTabSelected(NavTab.CATEGORIES) },
                    testTag = "nav_tab_categories"
                )

                // Space for Center FAB
                Spacer(modifier = Modifier.size(52.dp))

                // Completed Tab
                NavItem(
                    label = "Completed",
                    icon = if (currentTab == NavTab.COMPLETED) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    selected = currentTab == NavTab.COMPLETED,
                    onClick = { onTabSelected(NavTab.COMPLETED) },
                    testTag = "nav_tab_completed"
                )

                // Settings Tab
                NavItem(
                    label = "Settings",
                    icon = if (currentTab == NavTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    selected = currentTab == NavTab.SETTINGS,
                    onClick = { onTabSelected(NavTab.SETTINGS) },
                    testTag = "nav_tab_settings"
                )
            }
        }

        // Center Floating "+" Action Button
        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = TaskFlowIndigo,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(54.dp)
                .testTag("center_fab_add_task")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create Task",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = if (selected) TaskFlowIndigo else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = color,
            fontSize = 11.sp
        )
    }
}
