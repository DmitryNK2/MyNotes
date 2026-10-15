// ChatScreen
//
package com.example.mynotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.data.local.NoteEntity
import com.example.mynotes.data.repository.NotesRepository
import com.example.mynotes.ui.components.MessageBubble
import com.example.mynotes.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    subTopicId: Long,
    repository: NotesRepository,
    onBackClick: () -> Unit
) {
    val viewModel = remember { ChatViewModel(repository, subTopicId) }
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val availableTags by viewModel.availableTags.collectAsStateWithLifecycle()

    // ПОЛУЧАЕМ НАЗВАНИЕ ПОДТЕМЫ
    val subTopic by repository.getSubTopicById(subTopicId).collectAsStateWithLifecycle(initialValue = null)

    val listState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }
    var showTagFilter by remember { mutableStateOf(false) }

    LaunchedEffect(notes.size) {
        if (notes.isNotEmpty()) {
            listState.animateScrollToItem(notes.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // ОТОБРАЖАЕМ НАЗВАНИЕ ПОДТЕМЫ ИЛИ ЗАГЛУШКУ
                    Text(subTopic?.name ?: "💬 Заметки")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { showTagFilter = true }) {
                        Icon(Icons.Default.FilterList, "Фильтр по тегам")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Индикатор активного фильтра
            viewModel.selectedTag?.let { tag ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Фильтр: 🏷️ $tag")
                        TextButton(onClick = { viewModel.updateSelectedTag(null) }) {
                            Text("Сбросить")
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (notes.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(
                                if (viewModel.selectedTag == null) "Нет заметок. Начните писать!"
                                else "Нет заметок с тегом '${viewModel.selectedTag}'",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(
                        items = notes,
                        key = { note: NoteEntity -> note.id }
                    ) { note: NoteEntity ->
                        MessageBubble(
                            note = note,
                            availableTags = availableTags,
                            onEdit = { viewModel.updateNote(it) },
                            onDelete = { viewModel.deleteNote(it) },
                            onEmojiReaction = { n, emoji -> viewModel.setEmojiReaction(n, emoji) },
                            onSetTag = { n, tag -> viewModel.setTag(n, tag) }
                        )
                    }
                }
            }

            // Поле ввода (стиль Telegram)
            Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Новая заметка...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 5
                    )
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.addNote(messageText.trim())
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Отправить",
                            tint = if (messageText.isNotBlank()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showTagFilter) {
        AlertDialog(
            onDismissRequest = { showTagFilter = false },
            title = { Text("Сортировка по тегам") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Выберите тег для отображения:")
                    if (availableTags.isEmpty()) {
                        Text("Нет доступных тегов", color = MaterialTheme.colorScheme.error)
                    } else {
                        availableTags.forEach { tag: String ->
                            FilterChip(
                                selected = viewModel.selectedTag == tag,
                                onClick = {
                                    viewModel.updateSelectedTag(tag)
                                    showTagFilter = false
                                },
                                label = { Text("🏷️ $tag") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateSelectedTag(null)
                    showTagFilter = false
                }) { Text("Показать все") }
            },
            dismissButton = {
                TextButton(onClick = { showTagFilter = false }) { Text("Закрыть") }
            }
        )
    }
}