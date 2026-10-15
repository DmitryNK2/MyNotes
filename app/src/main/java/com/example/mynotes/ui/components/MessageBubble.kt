// MessageBubble
//
//
package com.example.mynotes.ui.components

import androidx.compose.foundation.layout.* // Добавляет Arrangement, Column, Row, Box, Spacer, Modifier, FlowRow и т.д.
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mynotes.data.local.NoteEntity

@OptIn(ExperimentalLayoutApi::class) // <-- ДОБАВЛЕНО: Разрешение на использование FlowRow
@Composable
fun MessageBubble(
    note: NoteEntity,
    availableTags: List<String>,
    onEdit: (NoteEntity) -> Unit,
    onDelete: (NoteEntity) -> Unit,
    onEmojiReaction: (NoteEntity, String?) -> Unit,
    onSetTag: (NoteEntity, String?) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val emojis = listOf("✅", "⚡", "🔥", "⭐", "❗", "❓", "💡", "📌")
    val defaultPresets = listOf("Срочно", "Важно", "Идея", "Задача", "Вопрос", "Готово")
    val allTags = (defaultPresets + availableTags).distinct()

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box {
                Card(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            note.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            note.tag?.let { tag ->
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "🏷️ $tag",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            note.emojiReaction?.let { emoji ->
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        emoji,
                                        modifier = Modifier.padding(6.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            if (note.isEdited) {
                                Text(
                                    "✏️ изм.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, "Меню", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("✏️ Редактировать") },
                        onClick = { showMenu = false; showEditDialog = true }
                    )
                    DropdownMenuItem(
                        text = { Text("😀 Реакция / Эмодзи") },
                        onClick = { showMenu = false; showEmojiPicker = true }
                    )
                    DropdownMenuItem(
                        text = { Text("🏷️ Задать тег") },
                        onClick = { showMenu = false; showTagDialog = true }
                    )
                    DropdownMenuItem(
                        text = { Text("🗑️ Удалить", color = Color.Red) },
                        onClick = { showMenu = false; showDeleteDialog = true }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить заметку?") },
            text = { Text("Эта заметка будет удалена безвозвратно.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(note)
                    showDeleteDialog = false
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showEmojiPicker) {
        AlertDialog(
            onDismissRequest = { showEmojiPicker = false },
            title = { Text("Выберите реакцию") },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    emojis.forEach { emoji ->
                        TextButton(onClick = {
                            onEmojiReaction(note, emoji)
                            showEmojiPicker = false
                        }) {
                            Text(emoji, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onEmojiReaction(note, null)
                    showEmojiPicker = false
                }) { Text("Убрать") }
            }
        )
    }

    if (showTagDialog) {
        var customTag by remember { mutableStateOf(note.tag ?: "") }
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("Тег сообщения") },
            text = {
                Column {
                    if (allTags.isNotEmpty()) {
                        // Использование FlowRow для автоматического переноса тегов
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allTags.forEach { tag ->
                                FilterChip(
                                    selected = note.tag == tag,
                                    onClick = {
                                        onSetTag(note, if (note.tag == tag) null else tag)
                                        showTagDialog = false
                                    },
                                    label = { Text(tag, maxLines = 1) }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = customTag,
                        onValueChange = { customTag = it },
                        label = { Text("Свой тег") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (customTag.isNotBlank()) onSetTag(note, customTag.trim())
                    showTagDialog = false
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onSetTag(note, null)
                    showTagDialog = false
                }) { Text("Убрать тег") }
            }
        )
    }

    if (showEditDialog) {
        var editedContent by remember { mutableStateOf(note.content) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Редактировать") },
            text = {
                OutlinedTextField(
                    value = editedContent,
                    onValueChange = { editedContent = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onEdit(note.copy(content = editedContent.trim()))
                    showEditDialog = false
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Отмена") }
            }
        )
    }
}