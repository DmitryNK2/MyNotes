// SubTopicsScreen
//
package com.example.mynotes.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.data.local.SubTopicEntity
import com.example.mynotes.data.repository.NotesRepository
import com.example.mynotes.ui.viewmodel.SubTopicsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTopicsScreen(
    topicId: Long,
    repository: NotesRepository,
    onSubTopicClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = remember { SubTopicsViewModel(repository, topicId) }
    val subTopics by viewModel.subTopics.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    // ПОЛУЧАЕМ НАЗВАНИЕ ТЕМЫ
    val topic by repository.getTopicById(topicId).collectAsStateWithLifecycle(initialValue = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // ОТОБРАЖАЕМ НАЗВАНИЕ ТЕМЫ ИЛИ ЗАГЛУШКУ
                    Text(topic?.name ?: "📂 Подтемы")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить подтему")
            }
        }
    ) { padding ->
        if (subTopics.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет подтем. Нажмите + чтобы создать.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subTopics) { subTopic ->
                    SubTopicItem(
                        subTopic = subTopic,
                        onClick = { onSubTopicClick(subTopic.id) },
                        onDelete = { viewModel.deleteSubTopic(subTopic) },
                        onRename = { newName -> viewModel.updateSubTopic(subTopic.copy(name = newName)) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        CreateSubTopicDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name ->
                viewModel.addSubTopic(name)
                showDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubTopicItem(
    subTopic: SubTopicEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(subTopic.name) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(subTopic.name, style = MaterialTheme.typography.titleMedium)
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("✏️ Переименовать") },
                        onClick = {
                            showMenu = false
                            newName = subTopic.name
                            showRenameDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🗑️ Удалить", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить подтему?") },
            text = { Text("Подтема «${subTopic.name}» и все её заметки будут удалены безвозвратно.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
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

    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Переименовать подтему") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Название подтемы") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        onRename(newName.trim())
                        showRenameDialog = false
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun CreateSubTopicDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая подтема") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название подтемы") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) {
                Text("Создать")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}