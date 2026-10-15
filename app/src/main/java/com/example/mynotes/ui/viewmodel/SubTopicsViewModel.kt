// SubTopicViewModel
package com.example.mynotes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.local.SubTopicEntity
import com.example.mynotes.data.repository.NotesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubTopicsViewModel(
    private val repository: NotesRepository,
    private val topicId: Long
) : ViewModel() {

    val subTopics = repository.getSubTopics(topicId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSubTopic(name: String) {
        viewModelScope.launch {
            repository.insertSubTopic(SubTopicEntity(topicId = topicId, name = name))
        }
    }

    // НОВЫЙ МЕТОД
    fun updateSubTopic(subTopic: SubTopicEntity) {
        viewModelScope.launch {
            repository.updateSubTopic(subTopic)
        }
    }

    fun deleteSubTopic(subTopic: SubTopicEntity) {
        viewModelScope.launch {
            repository.deleteSubTopic(subTopic)
        }
    }
}