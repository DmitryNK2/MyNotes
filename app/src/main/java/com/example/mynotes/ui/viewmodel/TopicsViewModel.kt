package com.example.mynotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.local.TopicEntity
import com.example.mynotes.data.repository.NotesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopicsViewModel(private val repository: NotesRepository) : ViewModel() {

    val topics = repository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFolderTopic(name: String) {
        viewModelScope.launch {
            repository.insertFolderTopic(name)
        }
    }

    fun addDirectChatTopic(name: String) {
        viewModelScope.launch {
            repository.insertDirectChatTopic(name)
        }
    }

    fun openDirectChat(topicId: Long, onSubTopicFound: (Long) -> Unit) {
        viewModelScope.launch {
            val subTopic = repository.getSubTopicForDirectTopic(topicId)
            if (subTopic != null) {
                onSubTopicFound(subTopic.id)
            }
        }
    }

    fun updateTopic(topic: TopicEntity) {
        viewModelScope.launch {
            repository.updateTopic(topic)
        }
    }

    fun deleteTopic(topic: TopicEntity) {
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }
}