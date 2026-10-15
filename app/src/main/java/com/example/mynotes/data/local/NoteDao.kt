// NoteDao
package com.example.mynotes.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE subTopicId = :subTopicId ORDER BY createdAt ASC")
    fun getNotesBySubTopic(subTopicId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE subTopicId = :subTopicId AND tag = :tag ORDER BY createdAt ASC")
    fun getNotesBySubTopicAndTag(subTopicId: Long, tag: String): Flow<List<NoteEntity>>

    @Query("SELECT DISTINCT tag FROM notes WHERE subTopicId = :subTopicId AND tag IS NOT NULL")
    fun getTagsForSubTopic(subTopicId: Long): Flow<List<String>>

    @Insert
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)
}