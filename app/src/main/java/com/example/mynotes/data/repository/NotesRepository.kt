package com.example.mynotes.data.repository

import com.example.mynotes.data.local.*
import kotlinx.coroutines.flow.Flow

class NotesRepository(
    private val topicDao: TopicDao,
    private val subTopicDao: SubTopicDao,
    private val noteDao: NoteDao
) {
    // Topics
    fun getAllTopics(): Flow<List<TopicEntity>> = topicDao.getAllTopics()
    fun getTopicById(topicId: Long): Flow<TopicEntity?> = topicDao.getTopicById(topicId)
    suspend fun insertTopic(topic: TopicEntity) = topicDao.insertTopic(topic)

    suspend fun insertFolderTopic(name: String): Long {
        return topicDao.insertTopic(TopicEntity(name = name, isFolder = true))
    }

    suspend fun insertDirectChatTopic(name: String): Long {
        val topicId = topicDao.insertTopic(TopicEntity(name = name, isFolder = false))
        subTopicDao.insertSubTopic(SubTopicEntity(topicId = topicId, name = name))
        return topicId
    }

    suspend fun updateTopic(topic: TopicEntity) {
        topicDao.updateTopic(topic)
        if (!topic.isFolder) {
            val subTopic = subTopicDao.getSubTopicByTopicId(topic.id)
            if (subTopic != null) {
                subTopicDao.updateSubTopic(subTopic.copy(name = topic.name))
            }
        }
    }

    suspend fun deleteTopic(topic: TopicEntity) = topicDao.deleteTopic(topic)

    suspend fun getSubTopicForDirectTopic(topicId: Long): SubTopicEntity? {
        return subTopicDao.getSubTopicByTopicId(topicId)
    }

    // SubTopics
    fun getSubTopics(topicId: Long): Flow<List<SubTopicEntity>> = subTopicDao.getSubTopicsByTopic(topicId)
    fun getSubTopicById(subTopicId: Long): Flow<SubTopicEntity?> = subTopicDao.getSubTopicById(subTopicId)
    suspend fun insertSubTopic(subTopic: SubTopicEntity) = subTopicDao.insertSubTopic(subTopic)
    suspend fun updateSubTopic(subTopic: SubTopicEntity) = subTopicDao.updateSubTopic(subTopic)
    suspend fun deleteSubTopic(subTopic: SubTopicEntity) = subTopicDao.deleteSubTopic(subTopic)

    // Notes
    fun getNotes(subTopicId: Long, tag: String? = null): Flow<List<NoteEntity>> {
        return if (tag == null) {
            noteDao.getNotesBySubTopic(subTopicId)
        } else {
            noteDao.getNotesBySubTopicAndTag(subTopicId, tag)
        }
    }
    fun getTags(subTopicId: Long): Flow<List<String>> = noteDao.getTagsForSubTopic(subTopicId)
    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
}