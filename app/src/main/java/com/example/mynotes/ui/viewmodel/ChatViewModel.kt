// ChatViewModel
package com.example.mynotes.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.local.NoteEntity
import com.example.mynotes.data.repository.NotesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: NotesRepository,
    private val subTopicId: Long
) : ViewModel() {

    var selectedTag by mutableStateOf<String?>(null)
        private set // Сеттер приватный, конфликтует только с публичными методами

    val availableTags = repository.getTags(subTopicId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = snapshotFlow { selectedTag }
        .flatMapLatest { tag: String? ->
            repository.getNotes(subTopicId, tag)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ИСПРАВЛЕНО: Переименовано во избежание Platform declaration clash
    fun updateSelectedTag(tag: String?) {
        selectedTag = tag
    }

    fun addNote(content: String, tag: String? = null) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(subTopicId = subTopicId, content = content, tag = tag)
            )
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isEdited = true))
        }
    }

    fun setEmojiReaction(note: NoteEntity, emoji: String?) {
        viewModelScope.launch {
            repository.updateNote(note.copy(emojiReaction = emoji))
        }
    }

    fun setTag(note: NoteEntity, tag: String?) {
        viewModelScope.launch {
            repository.updateNote(note.copy(tag = tag))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}