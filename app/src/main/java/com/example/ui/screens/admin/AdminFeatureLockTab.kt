package com.example.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.PageBookViewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FeatureLockConfig

@Composable
fun AdminFeatureLockTab(viewModel: PageBookViewModel) {
    val featureLockConfig by viewModel.featureLockConfig.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("ফিচার লক ও কন্ট্রোল প্যানেল", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("এখানে আপনি নবচেতনার বিভিন্ন ফিচার সমূহ অন/অফ করতে পারবেন।")
        }

        item {
            FeatureToggleRow(
                title = "ভয়েস রুম",
                isEnabled = featureLockConfig.isVoiceRoomEnabled,
                onToggle = { enabled ->
                    viewModel.updateFeatureLockConfig(featureLockConfig.copy(isVoiceRoomEnabled = enabled))
                }
            )
            FeatureToggleRow(
                title = "কল",
                isEnabled = featureLockConfig.isCallEnabled,
                onToggle = { enabled ->
                    viewModel.updateFeatureLockConfig(featureLockConfig.copy(isCallEnabled = enabled))
                }
            )
            FeatureToggleRow(
                title = "মার্কেটপ্লেস",
                isEnabled = featureLockConfig.isMarketplaceEnabled,
                onToggle = { enabled ->
                    viewModel.updateFeatureLockConfig(featureLockConfig.copy(isMarketplaceEnabled = enabled))
                }
            )
            FeatureToggleRow(
                title = "মনিটাইজেশন",
                isEnabled = featureLockConfig.isMonetizationEnabled,
                onToggle = { enabled ->
                    viewModel.updateFeatureLockConfig(featureLockConfig.copy(isMonetizationEnabled = enabled))
                }
            )
        }
    }
}

@Composable
fun FeatureToggleRow(title: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isEnabled) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isEnabled) Color(0xFF2E7D32) else Color.Red
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}
