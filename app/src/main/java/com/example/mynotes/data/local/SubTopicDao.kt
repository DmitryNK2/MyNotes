// SubTopicDao
//
package com.example.mynotes.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTopicDao {
    @Query("SELECT * FROM sub_topics WHERE topicId = :topicId ORDER BY createdAt DESC")
    fun getSubTopicsByTopic(topicId: Long): Flow<List<SubTopicEntity>>

    @Query("SELECT * FROM sub_topics WHERE topicId = :topicId LIMIT 1")
    suspend fun getSubTopicByTopicId(topicId: Long): SubTopicEntity?

    @Query("SELECT * FROM sub_topics WHERE id = :subTopicId")
    fun getSubTopicById(subTopicId: Long): Flow<SubTopicEntity?>

    @Insert
    suspend fun insertSubTopic(subTopic: SubTopicEntity): Long

    @Update
    suspend fun updateSubTopic(subTopic: SubTopicEntity)

    @Delete
    suspend fun deleteSubTopic(subTopic: SubTopicEntity)
}