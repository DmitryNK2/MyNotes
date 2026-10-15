package com.example.mynotes.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mynotes.data.local.TopicEntity
import com.example.mynotes.data.repository.NotesRepository
import com.example.mynotes.ui.viewmodel.TopicsViewModel

enum class CreationType { FOLDER, DIRECT_CHAT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    repository: NotesRepository,
    onTopicClick: (Long) -> Unit,
    onDirectChatClick: (Long) -> Unit,
    onSettingsClick: () -> Unit
) {
    val viewModel = remember { TopicsViewModel(repository) }
    val topics by viewModel.topics.collectAsStateWithLifecycle()
    var showTypeSelectionDialog by remember { mutableStateOf(false) }
    var creationType by remember { mutableStateOf<CreationType?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📝 Мои темы") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTypeSelectionDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить тему")
            }
        }
    ) { padding ->
        if (topics.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет тем. Нажмите + чтобы создать.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(topics) { topic ->
                    TopicItem(
                        topic = topic,
                        onClick = {
                            if (topic.isFolder) {
                                onTopicClick(topic.id)
                            } else {
                                viewModel.openDirectChat(topic.id) { subTopicId ->
                                    onDirectChatClick(subTopicId)
                                }
                            }
                        },
                        onDelete = { viewModel.deleteTopic(topic) },
                        onRename = { newName -> viewModel.updateTopic(topic.copy(name = newName)) }
                    )
                }
            }
        }
    }

    if (showTypeSelectionDialog) {
        SelectCreationTypeDialog(
            onDismiss = { showTypeSelectionDialog = false },
            onSelectType = { type ->
                showTypeSelectionDialog = false
                creationType = type
            }
        )
    }

    creationType?.let { type ->
        CreateTopicDialog(
            type = type,
            onDismiss = { creationType = null },
            onConfirm = { name ->
                if (type == CreationType.FOLDER) {
                    viewModel.addFolderTopic(name)
                } else {
                    viewModel.addDirectChatTopic(name)
                }
                creationType = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopicItem(
    topic: TopicEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(topic.name) }

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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (topic.isFolder) Icons.Default.Folder else Icons.AutoMirrored.Filled.Chat,
                    contentDescription = if (topic.isFolder) "Папка" else "Тема-чат",
                    tint = if (topic.isFolder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                )
                Column {
                    Text(topic.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = if (topic.isFolder) "Папка с темами" else "Тема-чат",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                            newName = topic.name
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
            title = { Text(if (topic.isFolder) "Удалить папку?" else "Удалить тему?") },
            text = {
                Text(
                    if (topic.isFolder)
                        "Папка «${topic.name}» и все её подтемы и заметки будут удалены безвозвратно."
                    else
                        "Тема-чат «${topic.name}» и все её заметки будут удалены безвозвратно."
                )
            },
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
            title = { Text(if (topic.isFolder) "Переименовать папку" else "Переименовать тему") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Название") },
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
fun SelectCreationTypeDialog(
    onDismiss: () -> Unit,
    onSelectType: (CreationType) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Что создать?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    onClick = { onSelectType(CreationType.FOLDER) },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Папка с темами", style = MaterialTheme.typography.titleMedium)
                            Text("Содержит несколько подтем", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Surface(
                    onClick = { onSelectType(CreationType.DIRECT_CHAT) },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Column {
                            Text("Тема-чат", style = MaterialTheme.typography.titleMedium)
                            Text("Отдельный чат для заметок", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}

@Composable
fun CreateTopicDialog(
    type: CreationType,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val titleText = if (type == CreationType.FOLDER) "Новая папка" else "Новая тема-чат"
    val labelText = if (type == CreationType.FOLDER) "Название папки" else "Название темы"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titleText) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(labelText) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}