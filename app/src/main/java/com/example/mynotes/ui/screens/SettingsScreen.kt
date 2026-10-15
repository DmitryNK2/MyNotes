package com.example.mynotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle // <-- ДОБАВЛЕН ИМПОРТ
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mynotes.data.preferences.ThemeOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Цветовая тема",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                ThemeOptionCard(
                    themeOption = ThemeOption.SYSTEM_DEFAULT,
                    title = "🎨 Системная тема",
                    description = "Автоматически подстраивается под настройки системы",
                    isSelected = currentTheme == ThemeOption.SYSTEM_DEFAULT,
                    onClick = { onThemeSelected(ThemeOption.SYSTEM_DEFAULT) }
                )
            }

            item {
                ThemeOptionCard(
                    themeOption = ThemeOption.BLUE,
                    title = "💙 Синяя тема",
                    description = "Спокойные синие оттенки",
                    isSelected = currentTheme == ThemeOption.BLUE,
                    previewColors = listOf(Color(0xFF1976D2), Color(0xFFBBDEFB)),
                    onClick = { onThemeSelected(ThemeOption.BLUE) }
                )
            }

            item {
                ThemeOptionCard(
                    themeOption = ThemeOption.GREEN,
                    title = "💚 Зеленая тема",
                    description = "Природные зеленые оттенки",
                    isSelected = currentTheme == ThemeOption.GREEN,
                    previewColors = listOf(Color(0xFF388E3C), Color(0xFFC8E6C9)),
                    onClick = { onThemeSelected(ThemeOption.GREEN) }
                )
            }

            item {
                ThemeOptionCard(
                    themeOption = ThemeOption.ORANGE,
                    title = "🧡 Оранжевая тема",
                    description = "Теплые оранжевые оттенки",
                    isSelected = currentTheme == ThemeOption.ORANGE,
                    previewColors = listOf(Color(0xFFF57C00), Color(0xFFFFE0B2)),
                    onClick = { onThemeSelected(ThemeOption.ORANGE) }
                )
            }

            item {
                ThemeOptionCard(
                    themeOption = ThemeOption.PURPLE,
                    title = "💜 Фиолетовая тема",
                    description = "Элегантные фиолетовые оттенки",
                    isSelected = currentTheme == ThemeOption.PURPLE,
                    previewColors = listOf(Color(0xFF7B1FA2), Color(0xFFE1BEE7)),
                    onClick = { onThemeSelected(ThemeOption.PURPLE) }
                )
            }
        }
    }
}

@Composable
fun ThemeOptionCard(
    themeOption: ThemeOption,
    title: String,
    description: String,
    isSelected: Boolean,
    previewColors: List<Color>? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (previewColors != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    previewColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Выбрано",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp)
                )
            }
        }
    }
}