// TopicsViewModel
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

    fun addTopic(name: String) {
        viewModelScope.launch {
            repository.insertTopic(TopicEntity(name = name))
        }
    }

    // НОВЫЙ МЕТОД
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