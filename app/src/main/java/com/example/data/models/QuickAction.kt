package com.example.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class QuickAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val badge: String? = null
)
